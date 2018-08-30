package it.wsh.cn.wshlibrary.http;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import io.reactivex.android.schedulers.AndroidSchedulers;

import com.google.gson.Gson;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import it.wsh.cn.wshlibrary.http.converter.ConvertFactory;
import it.wsh.cn.wshlibrary.http.https.SslContextFactory;
import it.wsh.cn.wshlibrary.utils.NetWorkUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * author: wenshenghui
 * created on: 2018/8/2 12:01
 * description:
 */
public class HttpClient<T> implements GenericLifecycleObserver {
    private static final String TAG = "HttpClient";

    private Context mContext;
    private Retrofit mCurrentRetrofit;
    private HttpServices mCurrentServices;
    private static volatile HttpClient sInstance;
    private final HashMap<Integer, List<Pair<Integer, Disposable>>> mDisposableCache = new HashMap<>();
    private HashMap<Integer, Retrofit> mRetrofitMap = new HashMap<>();
    private final static int MAX_CACHE_SIZE = 100;
    private final Gson mGson = new Gson();

    protected HttpClient() {
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        mContext = context.getApplicationContext();
    }

    /**
     * @param url
     * @return
     */
    public HttpClient refresh(String url, HttpCallBack<T> callBack) {
        return refresh(url, null, callBack);
    }

    /**
     * @param url
     * @param headerConfig
     * @return
     */
    public HttpClient refresh(String url, HttpConfig headerConfig, HttpCallBack<T> callback) {

        //获取缓存Retrofit
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        int key = getHashKey(url, headerConfig);
        mCurrentRetrofit = getCacheRetrofit(key);
        if (mCurrentRetrofit != null) {
            mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
            return this;
        }
        OkHttpClient client = getOkHttpClient(url, headerConfig, callback);
        try {
            mCurrentRetrofit = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(new ConvertFactory(mGson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();

        } catch (Exception e) {
            mCurrentRetrofit = null;
            e.printStackTrace();
            return null;
        }

        mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
        putCacheRetrofit(key, mCurrentRetrofit);
        return this;
    }

    /**
     * 生成缓存Retrofit用的key
     * 如果url一样，headerConfig不一样，需要判定为不一样的请求，所以需要两个参数共同生成key值
     *
     * @param url
     * @param headerConfig
     */
    private int getHashKey(String url, HttpConfig headerConfig) {
        if (headerConfig == null) {
            return url.hashCode();
        }
        return (url + headerConfig.toString()).hashCode();
    }

    private OkHttpClient getOkHttpClient(String url, HttpConfig headerConfig, final HttpCallBack<T> callback) {
        if (mContext == null) {
            return null;
        }
        if (headerConfig == null) {
            headerConfig = HttpConfig.create(true);

        }
        final HttpConfig config = headerConfig;
        //缓存容量

        //缓存路径
        String cacheFile = mContext.getCacheDir() + "/retrofit";
        Cache cache = new Cache(new File(cacheFile), HttpConstants.SIZE_OF_CACHE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(getHttpInterceptor(headerConfig, callback))
                .cache(cache);

        //测试用  跳过所有认证
        if (url.startsWith(HttpConstants.HTTPS)) {
            //SSLSocketFactory sslSocketFactory = new SslContextFactory().getSslSocket(mContext).getSocketFactory();
            //builder.sslSocketFactory(sslSocketFactory);
            builder.sslSocketFactory(new SslContextFactory().createSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        return builder.build();
    }

    private Interceptor getHttpInterceptor(final HttpConfig config, final HttpCallBack<T> callback) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request okHttpRequest = chain.request();
                RequestBody body = okHttpRequest.body();
                if (body != null) {
                    Log.i(TAG, "upload intercept: " + body.getClass().getSimpleName());
                    ProgressRequestBody prb = new ProgressRequestBody(body);
                    prb.setProgressListener(callback);
                    Request.Builder builder = okHttpRequest.newBuilder().method(okHttpRequest.method(), prb);
                    if (!config.getHeaders().isEmpty()) {
                        Set<Map.Entry<String, String>> entrySet = config.getHeaders().entrySet();
                        for (Map.Entry<String, String> entry : entrySet) {
                            builder.addHeader(entry.getKey(), entry.getValue());
                        }
                    }

                    boolean networkAvailable = NetWorkUtils.isNetworkAvailable(mContext);
                    if (networkAvailable && config.isNeedNetWorkCache()) { //有网络连接，看是否有配置，没配置，则走默认不缓存
                        CacheControl cacheControl = new CacheControl.Builder()
                                .maxAge(config.getNetWorkCacheTimeout(), TimeUnit.SECONDS)
                                .build();

                        builder.addHeader(HttpConstants.CACHE_CONTROL, cacheControl.toString());
                    } else if (!networkAvailable && config.isNeedNoNetWorkCache()) { //离线缓存
                        CacheControl cacheControl = new CacheControl.Builder()
                                .maxAge(config.getNoNetWorkCacheTimeout(), TimeUnit.SECONDS)
                                .build();

                        builder.addHeader(HttpConstants.CACHE_CONTROL, cacheControl.toString());
                    }
                    okHttpRequest = builder.build();
                }
                return chain.proceed(okHttpRequest);
            }
        };
        return interceptor;
    }


    /**
     * 获取缓存的Retrofit对象，如果没有缓存，则会返回null
     *
     * @param key
     * @return
     */
    private Retrofit getCacheRetrofit(int key) {
        return mRetrofitMap.get(key);
    }

    /**
     * 缓存Retrofit对象，超过MAX_CACHE_SIZE则清空
     *
     * @param key
     * @param retrofit
     */
    private void putCacheRetrofit(int key, Retrofit retrofit) {
        if (retrofit == null) {
            return;
        }

        if (mRetrofitMap == null) {
            mRetrofitMap = new HashMap<>();
        }

        if (mRetrofitMap.size() > MAX_CACHE_SIZE) {
            mRetrofitMap.clear();
        }

        mRetrofitMap.put(key, retrofit);
    }

    public void post(String path, String disposableCacheKey, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void postWithParamsMap(String path, String disposableCacheKey, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.postWithParamsMap(path, params);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, String disposableCacheKey, Object bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void postWithHeaderMap(String path, String disposableCacheKey, Map mapHeader, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.postWithHeaderMap(path, mapHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void postParamsAndObj(String path, String disposableCacheKey, Map<String, String> params, Object bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, params, bodyJson);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, String disposableCacheKey, Map<String, String> params, Map<String, String> mapHeader, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, params, mapHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void postMapHeaderAndObj(String path, String disposableCacheKey, Map<String, String> mapHeader, Object bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.post(path, bodyJson, mapHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void post(String path, String disposableCacheKey, Map<String, String> params, Map<String, String> mapHeader, Object bodyJson, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.post(path, params, bodyJson, mapHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    /**
     * @param path
     * @param callback
     */
    public void get(String path, String disposableCacheKey, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.get(path);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void getWithParamsMap(String path, String disposableCacheKey, Map<String, String> params, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.getWithParamsMap(path, params);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void getWithHeaderMap(String path, String disposableCacheKey, Map<String, String> mapHeader, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.getWithHeaderMap(path, mapHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    public void get(String path, String disposableCacheKey, Map<String, String> params, Map<String, String> authHeader, int tagHash, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }

        Observable<Response<String>> observable = mCurrentServices.get(path, params, authHeader);
        doSubscribe(disposableCacheKey, tagHash, observable, onUiCallBack, callback);
    }

    //上传
    public void upload(String path, String disposableCacheKey, Map<String, String> mapHeader, Map<String, RequestBody> partMap, int tagHash, final HttpCallBack<T> callback) {
        Observable<Response<String>> observable = mCurrentServices.upload(path, mapHeader, partMap);
        doSubscribe(disposableCacheKey, tagHash, observable, true, callback);
    }

    /**
     * 处理请求，并回调结果
     *
     * @param observable
     * @param callback
     */
    private void doSubscribe(final String pathKey, final int tagHash, Observable<Response<String>> observable, final boolean onUiCallBack, final HttpCallBack<T> callback) {

        Observable<Pair<String, T>> mapObservable = observable.map(new Function<Response<String>, Pair<String, T>>() {
            @Override
            public Pair<String, T> apply(retrofit2.Response<String> response) throws Exception {
                int code;
                String msg = "";
                Pair<String, T> pair;
                code = response.code();
                if (code == HttpStateCode.RESULT_OK) {
                    String data = response.body();
                    if (data != null) {
                        Class<T> cls = getParameterizedTypeClass(callback);
                        T t = mGson.fromJson(data, cls);
                        if (t != null) {
                            pair = new Pair<>(data, t);
                        } else {
                            msg = "json parse fail";
                            pair = new Pair<>(msg, null);
                        }
                        if (!onUiCallBack && t != null){ //子线程返回
                            callback.onSuccess(t);
                        }else if (!onUiCallBack && t == null){
                            callback.onError(HttpStateCode.ERROR_SUBSCRIBE_ERROR, msg);
                        }
                        return pair;
                    } else {
                        msg = "response body is null";
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody == null) {
                        msg = "errorBody is null";
                    } else {
                        msg = errorBody.string();
                    }
                }
                Log.e(TAG, "apply: " + msg);
                pair = new Pair<>(msg, null);
                if (!onUiCallBack){
                    callback.onError(HttpStateCode.ERROR_SUBSCRIBE_ERROR, msg);
                }
                return pair;
            }
        });

        mapObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<String, T>>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        cacheDisposableIfNeed(disposable, tagHash, pathKey);
                    }

                    @Override
                    public void onNext(Pair<String, T> pair) {
                        if (!onUiCallBack) {
                            return;
                        }
                        T t = pair.second;
                        if (t != null) {
                            callback.onSuccess(t);
                        } else {
                            callback.onError(HttpStateCode.ERROR_ONNEXT_NULL, null);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dispose(tagHash, pathKey);
                        if (onUiCallBack) {
                            callback.onError(HttpStateCode.ERROR_SUBSCRIBE_ERROR, throwable == null ? "" : throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        dispose(tagHash, pathKey);
                    }
                });
    }

    /**
     * 缓存Disposable
     *
     * @param disposable
     * @param hash
     * @param key
     */
    private void cacheDisposableIfNeed(Disposable disposable, int hash, String key) {
        if (disposable == null) {
            return;
        }
        Pair<Integer, Disposable> pair = Pair.create(key.hashCode(), disposable);
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(hash);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(pair);
        mDisposableCache.put(hash, list);
    }

    private Class<T> getParameterizedTypeClass(Object obj) {
        ParameterizedType pt = (ParameterizedType) obj.getClass().getGenericSuperclass();
        Type[] atr = pt.getActualTypeArguments();
        if (atr != null && atr.length > 0) {
            return (Class<T>) atr[0];
        }
        return null;
    }

    /**
     * @param tag 请求时传入的tag
     */
    public void cancel(Object tag) {
        if (tag == null) return;
        List<Pair<Integer, Disposable>> disposableList;
        disposableList = mDisposableCache.get(tag.hashCode());
        if (disposableList != null) {
            for (Pair<Integer, Disposable> pair : disposableList) {
                pair.second.dispose();
            }
            mDisposableCache.remove(tag.hashCode());
        }
    }

    /**
     *
     * @param hash
     * @param key
     * @return 返回是否成功删除
     */
    public boolean dispose(int hash, @NonNull String key) {
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(hash);
        Pair<Integer, Disposable> removePair = null;
        if (list != null) {
            for (Pair<Integer, Disposable> pair : list) {
                if (key.hashCode() == pair.first) {
                    pair.second.dispose();
                    removePair = pair;
                    break;
                }
            }
        }
        if (list != null && removePair != null) {
            list.remove(removePair);
            return true;
        }
        return false;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (source.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            source.getLifecycle().removeObserver(this);
            cancel(source);
        }
    }
}
