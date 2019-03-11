package it.wsh.cn.common_http.http;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import it.wsh.cn.common_http.http.converter.ConvertFactory;
import it.wsh.cn.common_http.http.ssl.SslContextFactory;
import it.wsh.cn.common_http.http.utils.HttpLog;
import it.wsh.cn.common_http.http.utils.MainThreadExecutor;
import it.wsh.cn.common_http.http.utils.SubThreadExecutor;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class HttpClient<T> implements GenericLifecycleObserver {

    private Context mContext;
    private Retrofit mCurrentRetrofit;
    private HttpServices mCurrentServices;
    private Gson mGson;
    //由tagKey 和httpKey 共同维护的mDisposableCache
    private final HashMap<Integer, List<Pair<Integer, Disposable>>> mDisposableCache = new HashMap<>();

    private String mBaseUrl = "";
    private HttpConfig mHttpConfig;

    public HttpClient(Context context, String baseUrl, HttpConfig httpConfig, Gson gson) {
        mContext = context;
        if (!TextUtils.isEmpty(baseUrl)) {
            mBaseUrl = baseUrl;
        }
        mGson = gson;
        init(httpConfig);
    }

    /**
     * 更新配置
     */
    public void updateConfit(HttpConfig httpConfig) {
        mHttpConfig = httpConfig;
    }

    private void init(HttpConfig httpConfig) {
        OkHttpClient client = getOkHttpClient(httpConfig);
        try {
            mCurrentRetrofit = new Retrofit.Builder()
                    .client(client)
                    .addConverterFactory(new ConvertFactory(mGson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(mBaseUrl)
                    .build();

        } catch (Exception e) {
            mCurrentRetrofit = null;
            HttpLog.e(e);
        }

        mCurrentServices = mCurrentRetrofit.create(HttpServices.class);
    }

    private OkHttpClient getOkHttpClient(HttpConfig httpConfig) {
        if (mContext == null) {
            HttpLog.e("HttpClient: getOkHttpClient, mContext == null");
            return null;
        }

        int connectTimeout;
        int readTimeout;
        int writeTimeout;
        if (httpConfig == null) {
            connectTimeout = HttpConfig.CONNECT_TIME_OUT_DEFAULT;
            readTimeout = HttpConfig.READ_TIME_OUT_DEFAULT;
            writeTimeout = HttpConfig.WRITE_TIME_OUT_DEFAULT;
        }else {
            connectTimeout = httpConfig.getConnectTimeout();
            readTimeout = httpConfig.getReadTimeout();
            writeTimeout = httpConfig.getWriteTimeout();
        }

        //缓存路径
        String cacheFile = mContext.getCacheDir() + "/retrofit";
        Cache cache = new Cache(new File(cacheFile), HttpConstants.SIZE_OF_CACHE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addInterceptor(getHttpInterceptor(httpConfig))
                .addInterceptor(getLogInterceptor())
                .cache(cache);

        //测试用  跳过所有认证
        if (mBaseUrl.startsWith(HttpConstants.HTTPS)) {
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

    /**
     * Log interceptor
     *
     * @return
     */
    private Interceptor getLogInterceptor() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                HttpLog.d("HttpClient: request or response data =  " + message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }

    private Interceptor getHttpInterceptor(HttpConfig config) {
        mHttpConfig = config;
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request okHttpRequest = chain.request();
                RequestBody body = okHttpRequest.body();
                Request.Builder builder;
                if (body != null) {
                    ProgressRequestBody prb = new ProgressRequestBody(body);
                    //prb.setProgressListener(callback);
                    builder = okHttpRequest.newBuilder().method(okHttpRequest.method(), prb);
                    //chachProgressRequestBody(prb, tagHash, tagHash);
                } else {
                    builder = okHttpRequest.newBuilder();
                }

                HttpConfig realConfig;
                if (mHttpConfig == null) {
                    realConfig = HttpConfig.create(true);
                }else {
                    realConfig = mHttpConfig;
                }

                if (!realConfig.getHeaders().isEmpty()) {
                    Set<Map.Entry<String, String>> entrySet = realConfig.getHeaders().entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        builder.addHeader(entry.getKey(), entry.getValue());
                    }
                }

                boolean networkAvailable = isNetworkAvailable(mContext);
                if (networkAvailable && realConfig.isNeedNetWorkCache()) { //有网络连接，看是否有配置，没配置，则走默认不缓存
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(realConfig.getNetWorkCacheTimeout(), TimeUnit.SECONDS)
                            .build();

                    builder.addHeader(HttpConstants.CACHE_CONTROL, cacheControl.toString());
                } else if (!networkAvailable && realConfig.isNeedNoNetWorkCache()) { //离线缓存
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(realConfig.getNoNetWorkCacheTimeout(), TimeUnit.SECONDS)
                            .build();

                    builder.addHeader(HttpConstants.CACHE_CONTROL, cacheControl.toString());
                }
                okHttpRequest = builder.build();
                return chain.proceed(okHttpRequest);
            }
        };
        return interceptor;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }


    public int post(String path, int httpKey, int tagHash, int retryTimes,
                    int retryDelayMillis, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }


    public int postWithParamsMap(String path, int httpKey, Map<String, String> params,
                                 int tagHash, int retryTimes, int retryDelayMillis,
                                 boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.postWithParamsMap(path, params);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }


    public int post(String path, int httpKey, Object bodyJson, int tagHash,
                    int retryTimes, int retryDelayMillis, boolean onUiCallBack,
                    HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, bodyJson);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }


    public int postWithHeaderMap(String path, int httpKey, Map mapHeader,
                                 int tagHash, int retryTimes, int retryDelayMillis,
                                 boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.postWithHeaderMap(path, mapHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }


    public int postParamsAndObj(String path, int httpKey, Map<String, String> params,
                                Object bodyJson, int tagHash, int retryTimes, int retryDelayMillis,
                                boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, params, bodyJson);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }


    public int post(String path, int httpKey, Map<String, String> params,
                    Map<String, String> mapHeader, int tagHash, int retryTimes,
                    int retryDelayMillis, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.post(path, params, mapHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    public int postMapHeaderAndObj(String path, int httpKey,
                                   Map<String, String> mapHeader, Object bodyJson, int tagHash,
                                   int retryTimes, int retryDelayMillis, boolean onUiCallBack,
                                   HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.post(path, bodyJson, mapHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    public int post(String path, int httpKey, Map<String, String> params,
                    Map<String, String> mapHeader, Object bodyJson, int tagHash,
                    int retryTimes, int retryDelayMillis, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.post(path, params, bodyJson, mapHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    /**
     * @param path
     * @param callback
     */
    public int get(String path, int httpKey, int tagHash, int retryTimes,
                   int retryDelayMillis, boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.get(path);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    public int getWithParamsMap(String path, int httpKey, Map<String, String> params,
                                int tagHash, int retryTimes, int retryDelayMillis,
                                boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.getWithParamsMap(path, params);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis, onUiCallBack, callback);
    }

    public int getWithHeaderMap(String path, int httpKey, Map<String, String> mapHeader,
                                int tagHash, int retryTimes, int retryDelayMillis, boolean onUiCallBack,
                                HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.getWithHeaderMap(path, mapHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    public int get(String path, int httpKey, Map<String, String> params,
                   Map<String, String> authHeader, int tagHash, int retryTimes, int retryDelayMillis,
                   boolean onUiCallBack, HttpCallBack<T> callback) {
        if (mCurrentServices == null) {
            return -1;
        }

        Observable<Response<String>> observable = mCurrentServices.get(path, params, authHeader);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                onUiCallBack, callback);
    }

    /**
     * 处理请求，并回调结果
     *
     * @param observable
     * @param callback
     */
    private int doSubscribe(final int httpKey, final int tagHash,
                            Observable<Response<String>> observable,
                            int retryTimes, int retryDelayMillis,
                            final boolean onUiCallBack, final HttpCallBack<T> callback) {
        HttpLog.d("HttpClient: doSubscribe, doSubscribe");
        if (callback != null) {
            HttpLog.d("HttpClient: doSubscribe, onStart");
            callback.onStart();
        }
        Observable<Pair<String, T>> mapObservable =
                observable.retryWhen(new RetryFunction(retryTimes, retryDelayMillis))
                        .map(new Function<Response<String>, Pair<String, T>>() {
                            @Override
                            public Pair<String, T> apply(Response<String> response) throws Exception {
                                String msg = "";
                                Pair<String, T> pair;
                                if (response.isSuccessful()) {
                                    String data = response.body();
                                    if (data != null) {
                                        Type type = getParameterizedTypeClass(callback);
                                        T t = mGson.fromJson(data, type);
                                        if (t != null) {
                                            pair = new Pair<>(data, t);
                                        } else {
                                            msg = "json parse fail";
                                            pair = new Pair<>(msg, null);
                                        }
                                        if (!onUiCallBack && t != null) { //子线程返回
                                            callback.onSuccess(t);
                                        } else if (!onUiCallBack && t == null) {
                                            callback.onError(ExceptionHandle.ERROR.PARSE_ERROR, ExceptionHandle.STR_PARSE_ERROR);
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
                                        //msg = "{\"date\":\"20190311\",\"stories\":[{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-f7527ba9195f5efb40dac46b288daf2c.jpg\"],\"type\":0,\"id\":9708715,\"ga_prefix\":\"031109\",\"title\":\"问世半个世纪的巨著《百年孤独》，终于要搬上荧幕了\"},{\"images\":[\"https:\\/\\/pic3.zhimg.com\\/v2-110daa0f4ae2f985048452964d53c792.jpg\"],\"type\":0,\"id\":9708699,\"ga_prefix\":\"031108\",\"title\":\"不只杨幂刘雯，那些闯荡顶级时装周的中国人\"},{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-6f47a6a1ed1f3108566362e46ddfe964.jpg\"],\"type\":0,\"id\":9708340,\"ga_prefix\":\"031107\",\"title\":\"HIV 是如何在中国暴增到百万人规模的？\"},{\"images\":[\"https:\\/\\/pic4.zhimg.com\\/v2-a8171ccec008f3af809d601035d85a1b.jpg\"],\"type\":0,\"id\":9708705,\"ga_prefix\":\"031106\",\"title\":\"瞎扯 · 如何正确地吐槽\"}],\"top_stories\":[{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-3331984a954d0fc1ef59e0ec4f89662d.jpg\",\"type\":0,\"id\":9708667,\"ga_prefix\":\"031021\",\"title\":\"吃到《黑豹》甜头的漫威，就这么如法炮制了《惊奇队长》\"},{\"image\":\"https:\\/\\/pic1.zhimg.com\\/v2-e8f8cd35f19f714e3dcc3e45c7023fa8.jpg\",\"type\":0,\"id\":9708684,\"ga_prefix\":\"031008\",\"title\":\"为了微信的一个 bug，蔡徐坤的粉丝们差点跟腾讯势不两立\"},{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-53f622cd066036eaffa39c5d9be534b9.jpg\",\"type\":0,\"id\":9708663,\"ga_prefix\":\"030818\",\"title\":\"这盘开胃菜，记得看《惊奇队长》之前享用（无剧透）\"},{\"image\":\"https:\\/\\/pic3.zhimg.com\\/v2-2e3aaa3b1b34273062e5a06822a984ce.jpg\",\"type\":0,\"id\":9708658,\"ga_prefix\":\"030907\",\"title\":\"在科学家的眼里，黄金放在地下金库里那绝对是浪费\"},{\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-92ae00bf0f4b7fcbd3217ca302a27f2f.jpg\",\"type\":0,\"id\":9708474,\"ga_prefix\":\"030809\",\"title\":\"游戏直播间「双击送游艇」的老铁们，会一直氪下去吗？\"}]}";
                                        //msg = "{\"stories\":[{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-f7527ba9195f5efb40dac46b288daf2c.jpg\"],\"type\":0,\"id\":9708715,\"ga_prefix\":\"031109\",\"title\":\"问世半个世纪的巨著《百年孤独》，终于要搬上荧幕了\"},{\"images\":[\"https:\\/\\/pic3.zhimg.com\\/v2-110daa0f4ae2f985048452964d53c792.jpg\"],\"type\":0,\"id\":9708699,\"ga_prefix\":\"031108\",\"title\":\"不只杨幂刘雯，那些闯荡顶级时装周的中国人\"},{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-6f47a6a1ed1f3108566362e46ddfe964.jpg\"],\"type\":0,\"id\":9708340,\"ga_prefix\":\"031107\",\"title\":\"HIV 是如何在中国暴增到百万人规模的？\"},{\"images\":[\"https:\\/\\/pic4.zhimg.com\\/v2-a8171ccec008f3af809d601035d85a1b.jpg\"],\"type\":0,\"id\":9708705,\"ga_prefix\":\"031106\",\"title\":\"瞎扯 · 如何正确地吐槽\"}],\"top_stories\":[{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-3331984a954d0fc1ef59e0ec4f89662d.jpg\",\"type\":0,\"id\":9708667,\"ga_prefix\":\"031021\",\"title\":\"吃到《黑豹》甜头的漫威，就这么如法炮制了《惊奇队长》\"},{\"image\":\"https:\\/\\/pic1.zhimg.com\\/v2-e8f8cd35f19f714e3dcc3e45c7023fa8.jpg\",\"type\":0,\"id\":9708684,\"ga_prefix\":\"031008\",\"title\":\"为了微信的一个 bug，蔡徐坤的粉丝们差点跟腾讯势不两立\"},{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-53f622cd066036eaffa39c5d9be534b9.jpg\",\"type\":0,\"id\":9708663,\"ga_prefix\":\"030818\",\"title\":\"这盘开胃菜，记得看《惊奇队长》之前享用（无剧透）\"},{\"image\":\"https:\\/\\/pic3.zhimg.com\\/v2-2e3aaa3b1b34273062e5a06822a984ce.jpg\",\"type\":0,\"id\":9708658,\"ga_prefix\":\"030907\",\"title\":\"在科学家的眼里，黄金放在地下金库里那绝对是浪费\"},{\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-92ae00bf0f4b7fcbd3217ca302a27f2f.jpg\",\"type\":0,\"id\":9708474,\"ga_prefix\":\"030809\",\"title\":\"游戏直播间「双击送游艇」的老铁们，会一直氪下去吗？\"}]}";
                                        //msg = "{\\\"datee\\\":\\\"20190311\\\",\"stories\":[{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-f7527ba9195f5efb40dac46b288daf2c.jpg\"],\"type\":0,\"id\":9708715,\"ga_prefix\":\"031109\",\"title\":\"问世半个世纪的巨著《百年孤独》，终于要搬上荧幕了\"},{\"images\":[\"https:\\/\\/pic3.zhimg.com\\/v2-110daa0f4ae2f985048452964d53c792.jpg\"],\"type\":0,\"id\":9708699,\"ga_prefix\":\"031108\",\"title\":\"不只杨幂刘雯，那些闯荡顶级时装周的中国人\"},{\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-6f47a6a1ed1f3108566362e46ddfe964.jpg\"],\"type\":0,\"id\":9708340,\"ga_prefix\":\"031107\",\"title\":\"HIV 是如何在中国暴增到百万人规模的？\"},{\"images\":[\"https:\\/\\/pic4.zhimg.com\\/v2-a8171ccec008f3af809d601035d85a1b.jpg\"],\"type\":0,\"id\":9708705,\"ga_prefix\":\"031106\",\"title\":\"瞎扯 · 如何正确地吐槽\"}],\"top_stories\":[{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-3331984a954d0fc1ef59e0ec4f89662d.jpg\",\"type\":0,\"id\":9708667,\"ga_prefix\":\"031021\",\"title\":\"吃到《黑豹》甜头的漫威，就这么如法炮制了《惊奇队长》\"},{\"image\":\"https:\\/\\/pic1.zhimg.com\\/v2-e8f8cd35f19f714e3dcc3e45c7023fa8.jpg\",\"type\":0,\"id\":9708684,\"ga_prefix\":\"031008\",\"title\":\"为了微信的一个 bug，蔡徐坤的粉丝们差点跟腾讯势不两立\"},{\"image\":\"https:\\/\\/pic2.zhimg.com\\/v2-53f622cd066036eaffa39c5d9be534b9.jpg\",\"type\":0,\"id\":9708663,\"ga_prefix\":\"030818\",\"title\":\"这盘开胃菜，记得看《惊奇队长》之前享用（无剧透）\"},{\"image\":\"https:\\/\\/pic3.zhimg.com\\/v2-2e3aaa3b1b34273062e5a06822a984ce.jpg\",\"type\":0,\"id\":9708658,\"ga_prefix\":\"030907\",\"title\":\"在科学家的眼里，黄金放在地下金库里那绝对是浪费\"},{\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-92ae00bf0f4b7fcbd3217ca302a27f2f.jpg\",\"type\":0,\"id\":9708474,\"ga_prefix\":\"030809\",\"title\":\"游戏直播间「双击送游艇」的老铁们，会一直氪下去吗？\"}]}";
                                        //msg = "msg";
                                        T t = null;
                                        try{
                                            Type type = getParameterizedTypeClass(callback);
                                            t = mGson.fromJson(msg, type);
                                        }catch (Exception e){
                                            pair = new Pair<>(msg, null);
                                            if (!onUiCallBack ) {
                                                callback.onError(response.code(), msg);
                                            }
                                            return pair;
                                        }

                                        if (t != null) {
                                            pair = new Pair<>(msg, t);
                                        } else {
                                            pair = new Pair<>(msg, null);
                                            if (!onUiCallBack ) {
                                                callback.onError(response.code(), msg);
                                            }
                                        }
                                        return pair;
                                    }
                                }

                                HttpLog.e("HttpClient: apply, msg");
                                if (!onUiCallBack) {
                                    callback.onError(response.code(), msg);
                                }
                                pair = new Pair<>(msg, null);
                                return pair;
                            }
                        });

        mapObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<String, T>>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        cacheDisposableIfNeed(disposable, tagHash, httpKey);
                    }

                    @Override
                    public void onNext(Pair<String, T> pair) {
                        if (!onUiCallBack) {
                            return;
                        }
                        T t = pair.second;
                        String info = pair.first;
                        if (t != null) {
                            callback.onSuccess(t);
                        } else {
                            callback.onError(ExceptionHandle.ERROR.UNKNOWN, info);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dispose(tagHash, httpKey);
                        ExceptionHandle.ResponeThrowable responeThrowable = ExceptionHandle.handleException(throwable);
                        if (onUiCallBack) {
                            callback.onError(responeThrowable.code, throwable == null
                                    ? "" : throwable.getMessage());
                        }else { //需要在子线程回调
                            SubThreadExecutor threadExecutorHelper = new SubThreadExecutor();
                            threadExecutorHelper.onError(callback, responeThrowable.code, throwable == null
                                    ? "" : throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        dispose(tagHash, httpKey);
                        callback.onComplete();
                    }
                });

        return httpKey;
    }

    /**
     * 缓存Disposable
     *
     * @param disposable
     * @param tagHash
     * @param httpKey
     */
    private void cacheDisposableIfNeed(Disposable disposable, int tagHash, int httpKey) {
        if (disposable == null) {
            HttpLog.e("HttpClient: cacheDisposableIfNeed, disposable == null");
            return;
        }
        HttpLog.d("HttpClient: cacheDisposableIfNeed");
        Pair<Integer, Disposable> pair = Pair.create(httpKey, disposable);
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(tagHash);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(pair);
        mDisposableCache.put(tagHash, list);
    }

    public Type getParameterizedTypeClass(Object obj) {
        ParameterizedType pt = (ParameterizedType) obj.getClass().getGenericSuperclass();
        Type[] atr = pt.getActualTypeArguments();
        try {
            if (atr != null && atr.length > 0) {
                return  atr[0];
            }
        }catch (Exception e) {
            HttpLog.d("getParameterizedTypeClass error");
        }
        return null;
    }

    /**
     * @param tag 请求时传入的tag
     */
    public void cancel(Object tag) {
        if (tag == null) return;
        HttpLog.d("HttpClient: cancel  tag = " + tag);
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
     * @param tagHash
     * @param httpKey
     * @return 返回是否成功删除
     */
    public boolean dispose(int tagHash, @NonNull int httpKey) {
        HttpLog.d("HttpClient: cancel  dispose ");
        List<Pair<Integer, Disposable>> list = mDisposableCache.get(tagHash);
        Pair<Integer, Disposable> removePair = null;
        if (list != null) {
            for (Pair<Integer, Disposable> pair : list) {
                if (httpKey == pair.first) {
                    pair.second.dispose();
                    removePair = pair;
                    break;
                }
            }
        }
        if (list != null && removePair != null) {
            list.remove(removePair);
            HttpLog.d("HttpClient: cancel  list.remove(removePair); ");
            return true;
        }
        return false;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (source.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            HttpLog.d("HttpClient: onStateChanged LifecycleOwner = " + source.toString());
            source.getLifecycle().removeObserver(this);
            cancel(source);
        }
    }
}
