package it.wsh.cn.wshlibrary.http.upload;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
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
import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpConstants;
import it.wsh.cn.wshlibrary.http.HttpServices;
import it.wsh.cn.wshlibrary.http.HttpStateCode;
import it.wsh.cn.wshlibrary.http.ProgressRequestBody;
import it.wsh.cn.wshlibrary.http.RetryFunction;
import it.wsh.cn.wshlibrary.http.converter.ConvertFactory;
import it.wsh.cn.wshlibrary.http.ssl.SslContextFactory;
import it.wsh.cn.wshlibrary.http.utils.HttpLog;
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

public class UploadTask implements GenericLifecycleObserver {

    private Context mContext;
    private Retrofit mCurrentRetrofit;
    private HttpServices mCurrentServices;
    private Gson mGson;
    //由tagKey 和httpKey 共同维护的mDisposableCache
    private Disposable mDisposable;

    private String mBaseUrl = "";
    private ProgressRequestBody mProgressRequestBody;

    public UploadTask(Context context, String baseUrl, HttpConfig httpConfig, Gson gson, HttpCallBack<String> callback) {
        mContext = context;
        if (!TextUtils.isEmpty(baseUrl)) {
            mBaseUrl = baseUrl;
        }
        if (httpConfig == null) {
            httpConfig = HttpConfig.getDefault();
        }
        mGson = gson;
        init(httpConfig,callback);
    }

    public void init(HttpConfig httpConfig, HttpCallBack<String> callback) {
        OkHttpClient client = getOkHttpClient(httpConfig, callback);
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

    private OkHttpClient getOkHttpClient(HttpConfig httpConfig, HttpCallBack<String> callback) {
        if (mContext == null) {
            HttpLog.e("UploadTask: getOkHttpClient, mContext == null");
            return null;
        }

        //缓存路径
        String cacheFile = mContext.getCacheDir() + "/retrofit";
        Cache cache = new Cache(new File(cacheFile), HttpConstants.SIZE_OF_CACHE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(getHttpInterceptor(httpConfig, callback))
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
                HttpLog.d("UploadTask: request or response data =  " + message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }

    private Interceptor getHttpInterceptor(final HttpConfig config, final HttpCallBack<String> callback) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request okHttpRequest = chain.request();
                RequestBody body = okHttpRequest.body();
                Request.Builder builder;
                if (body != null) {
                    mProgressRequestBody = new ProgressRequestBody(body);
                    mProgressRequestBody.setProgressListener(callback);
                    builder = okHttpRequest.newBuilder().method(okHttpRequest.method(), mProgressRequestBody);
                } else {
                    builder = okHttpRequest.newBuilder();
                }

                if (!config.getHeaders().isEmpty()) {
                    Set<Map.Entry<String, String>> entrySet = config.getHeaders().entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        builder.addHeader(entry.getKey(), entry.getValue());
                    }
                }

                boolean networkAvailable = isNetworkAvailable(mContext);
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

    //上传
    public int start(String path, int httpKey, Map<String, String> mapHeader,
                      Map<String, RequestBody> partMap, int tagHash, int retryTimes,
                      int retryDelayMillis, final HttpCallBack<String> callback) {
        if (mCurrentServices == null) {
            return -1;
        }
        Observable<Response<String>> observable = mCurrentServices.upload(path, mapHeader, partMap);
        return doSubscribe(httpKey, tagHash, observable, retryTimes, retryDelayMillis,
                true, callback);
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
                            final boolean onUiCallBack, final HttpCallBack<String> callback) {
        HttpLog.d("UploadTask: doSubscribe, doSubscribe");
        if (callback != null) {
            HttpLog.d("UploadTask: doSubscribe, onStart");
            callback.onStart();
        }
        Observable<String> mapObservable =
                observable.retryWhen(new RetryFunction(retryTimes, retryDelayMillis))
                        .map(new Function<Response<String>, String>() {
                            @Override
                            public String apply(Response<String> response) throws Exception {
                                String msg = "";
                                if (response.isSuccessful()) {
                                    String data = response.body();
                                    if (data != null) {
                                        if (!onUiCallBack) { //子线程返回
                                            callback.onSuccess(data);
                                        }
                                        return data;
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

                                HttpLog.e("UploadTask: apply, msg");
                                if (!onUiCallBack) {
                                    callback.onError(HttpStateCode.ERROR_SUBSCRIBE_ERROR, msg);
                                }
                                return msg;
                            }
                        });

        mapObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(String data) {
                        if (!onUiCallBack) {
                            return;
                        }
                        if (data != null) {
                            callback.onSuccess(data);
                        } else {
                            callback.onError(HttpStateCode.ERROR_ONNEXT_NULL, data);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                        if (onUiCallBack) {
                            callback.onError(HttpStateCode.ERROR_SUBSCRIBE_ERROR, throwable == null
                                    ? "" : throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                        callback.onComplete();
                    }
                });

        return httpKey;
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        if (source.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            HttpLog.d("UploadTask: onStateChanged LifecycleOwner = " + source.toString());
            source.getLifecycle().removeObserver(this);
            if (mDisposable != null) {
                mDisposable.dispose();
            }
            mProgressRequestBody.clear();
        }
    }
}
