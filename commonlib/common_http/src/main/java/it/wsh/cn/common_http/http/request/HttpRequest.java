package it.wsh.cn.common_http.http.request;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;

import java.util.Map;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.HttpClient;
import it.wsh.cn.common_http.http.HttpClientManager;
import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.utils.HttpLog;
import it.wsh.cn.common_http.http.utils.HttpUtils;
import okhttp3.RequestBody;

/**
 * author: wenshenghui
 * created on: 2018/12/27 15:23
 * description:
 */
public class HttpRequest<Req extends BaseReqInfo, Rsp> {

    private String TAG = "HttpRequest";
    protected final int ERROR_KEY = -1; //传参出错导致发起请求失败


    protected HttpConfig mHttpCustomConfig; //为了调用dispose，所以需要存下。
    protected int mRetryTimes;
    protected int mRetryDelayMillis;
    protected int mKey = -1;
    protected Object mTag;
    protected int mTagHash;

    protected String mPath;
    protected String mBaseUrl;
    protected String mMethed;

    /**
     * 如果要绑定生命周期，界面销毁时取消请求，
     * 则tag需要传Activity或者Fragmeng对象   需要继承AppCompatActivity才生效
     *
     * @param tag
     * @return
     */
    public HttpRequest setTag(Object tag) {
        mTag = tag;
        mTagHash = tag == null ? TAG.hashCode() : tag.hashCode();
        return this;
    }

    /**
     * 设置Rxjava重试次数和延迟时间,默认失败不重试
     *
     * @param retryTimes
     * @param retryDelayMillis 毫秒
     * @return
     */
    public HttpRequest setRetryTimes(int retryTimes, int retryDelayMillis) {
        mRetryTimes = retryTimes;
        mRetryDelayMillis = retryDelayMillis;
        return this;
    }

    /**
     * 开始一个请求，回调默认在主线程
     *
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    public int start(Req req, HttpCallBack<Rsp> callback) {
        return start(true, req, callback);
    }

    /**
     * 创建一个请求，并指定回调是否在UI线程
     *
     * @param onUiCallBack
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    public int start(boolean onUiCallBack, Req req, HttpCallBack<Rsp> callback) {
        Object bodyObj = null;
        Map<String, String> httpParams = null;
        Map<String, String> httpHeader = null;
        HttpConfig httpCustomConfig = null;
        if (req != null) {
            bodyObj = req.mBodyObj == null ? req : req.mBodyObj;
            httpParams = req.mHttpParams;
            httpHeader = req.mHttpHeader;
            httpCustomConfig = req.mHttpCustomConfig;
            mHttpCustomConfig = req.mHttpCustomConfig;
            mPath = req.getPath();
            mBaseUrl = req.getBaseUrl();
            mMethed = req.getMethod();
        }
        return request(onUiCallBack, bodyObj, httpParams, httpHeader, httpCustomConfig,
                null, callback);
    }

    /**
     * @param onUiCallBack
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    protected int request(boolean onUiCallBack, Object bodyObj, Map<String, String> httpParams,
                          Map<String, String> httpHeader, HttpConfig httpCustomConfig,
                          Map<String, RequestBody> mPartMap,
                          HttpCallBack<Rsp> callback) {

        int httpKey = HttpUtils.getHttpKey(getPath(), httpHeader, httpParams, bodyObj, httpCustomConfig);
        int key = ERROR_KEY;
        switch (getMethod()) {
            case HttpMethod.POST:
                key = processPostQuest(onUiCallBack, httpKey, bodyObj, httpParams, httpHeader,
                        httpCustomConfig, callback);
                break;
            case HttpMethod.GET:
                key = processGetQuest(onUiCallBack, httpKey, bodyObj, httpParams, httpHeader,
                        httpCustomConfig, callback);
                break;
        }
        registerLifecycle(httpCustomConfig);
        mKey = key;
        return key;
    }

    /**
     * @param httpCustomConfig
     */
    private void registerLifecycle(HttpConfig httpCustomConfig) {
        if (mTag == null) {
            HttpLog.e("CommonBuilder: registerLifecycle, mTag == null");
            return;
        }
        LifecycleOwner owner;
        if (mTag instanceof LifecycleOwner) {
            HttpClient cacheHttpClient = HttpClientManager.getInstance().getCacheHttpClient(getBaseUrl(), httpCustomConfig);
            if (cacheHttpClient == null) {
                HttpLog.e("CommonBuilder: registerLifecycle, cacheHttpClient == null");
                return;
            }
            HttpLog.i("CommonBuilder: registerLifecycle, Success!");
            owner = (LifecycleOwner) mTag;
            owner.getLifecycle().addObserver(cacheHttpClient);
        }
    }

    private int processPostQuest(boolean onUiCallBack, int httpKey, Object bodyObj,
                                 Map<String, String> httpParams, Map<String, String> httpHeader,
                                 HttpConfig httpCustomConfig, HttpCallBack<Rsp> callback) {
        HttpClientManager clientManager = HttpClientManager.getInstance();
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = httpParams == null;
        boolean bodyObjEmpty = bodyObj == null;
        boolean mapHeaderEmpty = (httpHeader == null || httpHeader.size() <= 0);
        if (isPathEmpty) {
            HttpLog.e("CommonBuilder：Error! processPostQuest: Path is empty !");
            return ERROR_KEY;
        }
        if (paramsEmpty && bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.post(getBaseUrl(), getPath(), httpKey, getTagHash(), mRetryTimes, mRetryDelayMillis,
                    onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.postWithParamsMap(getBaseUrl(), getPath(), httpKey, httpParams, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (paramsEmpty && !bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.post(getBaseUrl(), getPath(), httpKey, bodyObj, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (paramsEmpty && bodyObjEmpty && !mapHeaderEmpty) {
            return clientManager.postWithHeaderMap(getBaseUrl(), getPath(), httpKey, httpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && !bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.postParamsAndObj(getBaseUrl(), getPath(), httpKey, httpParams, bodyObj,
                    mRetryTimes, mRetryDelayMillis, getTagHash(), onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && bodyObjEmpty && !mapHeaderEmpty) {
            return clientManager.post(getBaseUrl(), getPath(), httpKey, httpParams, httpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (paramsEmpty && !bodyObjEmpty && !mapHeaderEmpty) {
            return clientManager.postMapHeaderAndObj(getBaseUrl(), getPath(), httpKey, httpHeader, bodyObj,
                    mRetryTimes, mRetryDelayMillis, getTagHash(), onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && !bodyObjEmpty && !mapHeaderEmpty) {
            return clientManager.post(getBaseUrl(), getPath(), httpKey, httpParams, httpHeader, bodyObj,
                    mRetryTimes, mRetryDelayMillis, getTagHash(), onUiCallBack, httpCustomConfig, callback);
        } else {
            HttpLog.e("CommonBuilder：Error! processPostQuest : not support request !");
            return ERROR_KEY;
        }
    }

    private int processGetQuest(boolean onUiCallBack, int httpKey, Object bodyObj,
                                Map<String, String> httpParams, Map<String, String> httpHeader,
                                HttpConfig httpCustomConfig, HttpCallBack<Rsp> callback) {
        HttpClientManager clientManager = HttpClientManager.getInstance();
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = httpParams == null;
        boolean mapHeaderEmpty = (httpHeader == null || httpHeader.size() <= 0);
        if (isPathEmpty) {
            HttpLog.e("CommonBuilder：Error! processGetQuest : Path is empty !");
            return ERROR_KEY;
        }

        if (paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            return clientManager.get(getBaseUrl(), getPath(), httpKey, getTagHash(), mRetryTimes, mRetryDelayMillis,
                    onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            return clientManager.getWithParamsMap(getBaseUrl(), getPath(), httpKey, httpParams, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (paramsEmpty && !mapHeaderEmpty) {
            //
            return clientManager.getWithHeaderMap(getBaseUrl(), getPath(), httpKey, httpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else if (!paramsEmpty && !mapHeaderEmpty) {
            //
            return clientManager.get(getBaseUrl(), getPath(), httpKey, httpParams, httpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, httpCustomConfig, callback);
        } else {
            HttpLog.e("CommonBuilder：Error! processGetQuest : not support request !");
            return ERROR_KEY;
        }
    }

    protected int getTagHash() {
        if (mTagHash == 0) {
            return TAG.hashCode();
        } else {
            return mTagHash;
        }
    }

    /**
     * 上层主动取消请求
     */
    public boolean dispose() {
        return HttpClientManager.getInstance().dispose(getBaseUrl(), getTagHash(), mKey, mHttpCustomConfig);
    }

    protected String getPath() {
        return mPath;
    }

    ;

    protected String getBaseUrl() {
        return mBaseUrl;
    }

    ;

    protected @HttpMethod.IMethed
    String getMethod() {
        return mMethed;
    }

    ;
}
