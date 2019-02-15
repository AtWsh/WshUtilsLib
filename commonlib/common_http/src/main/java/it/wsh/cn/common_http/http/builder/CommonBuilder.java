package it.wsh.cn.common_http.http.builder;

import android.arch.lifecycle.LifecycleOwner;
import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.HttpClient;
import it.wsh.cn.common_http.http.HttpClientManager;
import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.HttpConstants;
import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.utils.HttpLog;
import it.wsh.cn.common_http.http.utils.HttpUrlEncodeUtils;
import it.wsh.cn.common_http.http.utils.HttpUtils;
import okhttp3.RequestBody;


/**
 * author: wenshenghui
 * created on: 2018/8/7 10:15
 * description:
 */
public abstract class CommonBuilder<T> {

    private String TAG = "CommonBuilder";

    protected Map<String, String> mHttpParams;
    protected Map<String, String> mHttpHeader;


    protected abstract String getPath();
    protected abstract String getBaseUrl();
    protected abstract @HttpMethod.IMethed
    String getMethod();
    private Object mBodyObj;
    protected HttpConfig mHttpCustomConfig;
    protected int mRetryTimes;
    protected int mRetryDelayMillis;
    protected int mKey = -1;
    protected Object mTag;

    /**
     * 设置Rxjava重试次数和延迟时间,默认失败不重试
     * @param retryTimes
     * @param retryDelayMillis 毫秒
     * @return
     */
    public CommonBuilder<T> setRetryTimes(int retryTimes, int retryDelayMillis){
        mRetryTimes = retryTimes;
        mRetryDelayMillis = retryDelayMillis;
        return this;
    }


    /**
     * 批量添加http查询参数
     */
    public CommonBuilder<T> addParamsMap(Map<String, String> params){
        if (mHttpParams == null) {
            mHttpParams = new HashMap<>();
        }
        if (params != null) {
            mHttpParams.putAll(params);
        }
        return this;
    }

    /**
     * 逐个添加http查询参数
     */
    public CommonBuilder<T> addParam(String key, String value){
        if (mHttpParams == null) {
            mHttpParams = new HashMap<>();
        }
        if (!TextUtils.isEmpty(key)) {
            mHttpParams.put(key, value);
        }
        return this;
    }

    /**
     * 覆盖http查询参数，注意，两次调用该方法添加参数，后来添加的会将前面添加的清空
     */
    public CommonBuilder<T> setParamsMap(Map<String, String> params){
        mHttpParams = params;
        return this;
    }

    /**
     * bodyObj会被Gson转换为Json数据，可以直接传JsonObject对象
     * @param bodyObj
     * @return
     */
    public CommonBuilder<T> addBodyObj(Object bodyObj){
        mBodyObj = bodyObj;
        return this;
    }

    /**
     * 以map形式添加表单类型数据
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> addFormDataMap(Map<String, String> mapValue) {
        if (mapValue == null || mapValue.size() <= 0) {
            HttpLog.e("CommonBuilder：Error! input addFormDataMap mapValue = " + mapValue);
            return this;
        }
        StringBuilder sb = new StringBuilder();
        for (String key : mapValue.keySet()) {
            sb.append(key + "="  + mapValue.get(key) + "&");
        }
        String content = sb.toString();
        content = content.substring(0, content.length() - 1);
        RequestBody requestBody = RequestBody.create(HttpConstants.FORM_TYPE, content);
        mBodyObj = requestBody;
        return this;
    }

    /**
     * 以map的形式添加Body，map将转换为JsonObject
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> addBodyMap(Map<String, String> mapValue){
        if(mapValue == null){
            HttpLog.e("CommonBuilder：Error! input addBodyMap mapValue == null");
            return this;
        }
        JsonObject jsonObject = new JsonObject();

        for (String key : mapValue.keySet()) {
            try {
                jsonObject.addProperty(key, mapValue.get(key));
            }catch (Exception e){
                HttpLog.w(e);
                jsonObject = null;
            }
        }

        mBodyObj = jsonObject;
        return this;
    }

    /**
     * 完全覆盖除默认Header以外的Header
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> setHeader(Map<String, String> mapValue){
        if (mapValue == null) {
            HttpLog.e("CommonBuilder：Error! input setHeader mapValue = " + mapValue);
            return this;
        }
        mHttpHeader = mapValue;
        return this;
    }

    /**
     * 批量添加除默认Header以外的Header信息，不覆盖
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> addHeader(Map<String, String> mapValue){
        if (mapValue == null || mapValue.size() <= 0) {
            HttpLog.e("CommonBuilder：Error! input addHeader mapValue = " + mapValue);
            return this;
        }
        if (mHttpHeader == null) {
            mHttpHeader = new HashMap<>();
        }

        mHttpHeader.putAll(mapValue);
        return this;
    }

    /**
     * 逐条添加除默认Header以外的Header信息，不覆盖
     * @param key
     * @param value
     * @return
     */
    public CommonBuilder<T> addHeader(String key, String value){
        if (TextUtils.isEmpty(key)) {
            HttpLog.e("CommonBuilder：Error! input addHeader key = " + key);
            return this;
        }
        if (mHttpHeader == null) {
            mHttpHeader = new HashMap<>();
        }

        mHttpHeader.put(key, value);
        return this;
    }

    /**
     * 设置HttpConfig信息，里面包含默认Header信息以及超时和缓存配置
     * @param httpConfig
     * @return
     */
    public CommonBuilder<T> setHttpCustomConfig(HttpConfig httpConfig) {
        mHttpCustomConfig = httpConfig;
        return this;
    }

    /**
     *  Function: getParams()
     *  NOTE: 该方法可以被重写。如果不重写，则默认使用addJsonQuery()调用时设置的参数。如果重写，则是
     *      添加通用参数，需要创建新的Map<String, String>，在添加通用参数的同时，将mHttpParams中的参数
     *      也填写进去，切不可直接在mHttpParams中直接添加通用参数并返回。
     *
     */
    protected Map<String, String> getParams(){
        if (mHttpParams == null || mHttpParams.size() <= 0) {
            return null;
        }
        Map<String, String> encodeMap = new HashMap<>();
        for (Map.Entry<String, String> entry : mHttpParams.entrySet()) {
            String value = entry.getValue();
            String encodeValue = "";
            if (!TextUtils.isEmpty(value)) {
                encodeValue = HttpUrlEncodeUtils.urlEncode(value);
            }
            encodeMap.put(entry.getKey(), encodeValue);
        }
        return encodeMap;
    }

    /**
     * 创建一个请求，回调默认在主线程
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    final public int build(HttpCallBack<T> callback){
        return build(true, callback);
    }

    /**
     * 创建一个请求，并指定回调是否在UI线程
     * @param onUiCallBack
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    final public int build(boolean onUiCallBack, HttpCallBack<T> callback){
        return request(onUiCallBack, callback);
    }

    /**
     *
     * @param onUiCallBack
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    protected int request(boolean onUiCallBack, HttpCallBack<T> callback){

        int httpKey = HttpUtils.getHttpKey(getPath(), mHttpHeader, getParams(), mBodyObj, mHttpCustomConfig);
        int key = -1;
        switch (getMethod()){
            case HttpMethod.POST:
                key = processPostQuest(onUiCallBack, callback, httpKey);
                break;
            case HttpMethod.GET:
                key = processGetQuest(onUiCallBack, callback, httpKey);
                break;
        }
        registerLifecycle();
        mKey = key;
        return key;
    }

    /**
     *
     */
    public void registerLifecycle() {
        if (mTag == null) {
            HttpLog.e("CommonBuilder: registerLifecycle, mTag == null");
            return;
        }
        LifecycleOwner owner;
        if (mTag instanceof LifecycleOwner) {
            HttpClient cacheHttpClient = HttpClientManager.getInstance().getCacheHttpClient(getBaseUrl(), mHttpCustomConfig);
            if (cacheHttpClient == null) {
                HttpLog.e("CommonBuilder: registerLifecycle, cacheHttpClient == null");
                return;
            }
            HttpLog.d("CommonBuilder: registerLifecycle, Success!");
            owner = (LifecycleOwner) mTag;
            owner.getLifecycle().addObserver(cacheHttpClient);
        }
    }

    private int processPostQuest(boolean onUiCallBack, HttpCallBack<T> callback, int httpKey) {
        HttpClientManager clientManager = HttpClientManager.getInstance();
        boolean paramsEmpty = getParams() == null;
        boolean bodyObjEmpty = mBodyObj == null;
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);

        if (paramsEmpty && bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.post(getBaseUrl(), getPath(), httpKey, getTagHash(), mRetryTimes, mRetryDelayMillis,
                    onUiCallBack, mHttpCustomConfig, callback);
        }else if (!paramsEmpty && bodyObjEmpty && mapHeaderEmpty) {
            return clientManager.postWithParamsMap(getBaseUrl(), getPath(), httpKey, getParams(), getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(paramsEmpty && !bodyObjEmpty && mapHeaderEmpty){
            return clientManager.post(getBaseUrl(), getPath(), httpKey, mBodyObj, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(paramsEmpty && bodyObjEmpty && !mapHeaderEmpty){
            return clientManager.postWithHeaderMap(getBaseUrl(), getPath(), httpKey, mHttpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(!paramsEmpty && !bodyObjEmpty && mapHeaderEmpty){
            return clientManager.postParamsAndObj(getBaseUrl(), getPath(), httpKey, getParams(), mBodyObj,
                    getTagHash(), mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(!paramsEmpty && bodyObjEmpty && !mapHeaderEmpty){
            return clientManager.post(getBaseUrl(), getPath(), httpKey, getParams(), mHttpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(paramsEmpty && !bodyObjEmpty && !mapHeaderEmpty){
            return clientManager.postMapHeaderAndObj(getBaseUrl(), getPath(), httpKey, mHttpHeader, mBodyObj,
                    getTagHash(), mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(!paramsEmpty && !bodyObjEmpty && !mapHeaderEmpty){
            return clientManager.post(getBaseUrl(), getPath(), httpKey, getParams(), mHttpHeader, mBodyObj,
                    getTagHash(), mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else {
            HttpLog.e("CommonBuilder：Error! processPostQuest : not support request !" );
            return -1;
        }
    }

    private int processGetQuest(boolean onUiCallBack, HttpCallBack<T> callback, int httpKey) {
        HttpClientManager clientManager = HttpClientManager.getInstance();
        boolean paramsEmpty = getParams() == null;
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);

        if (paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            return clientManager.get(getBaseUrl(), getPath(), httpKey, getTagHash(), mRetryTimes, mRetryDelayMillis,
                    onUiCallBack, mHttpCustomConfig, callback);
        }else if (!paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            return clientManager.getWithParamsMap(getBaseUrl(), getPath(), httpKey, getParams(), getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(paramsEmpty && !mapHeaderEmpty){
            //
            return clientManager.getWithHeaderMap(getBaseUrl(), getPath(), httpKey, mHttpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else if(!paramsEmpty && !mapHeaderEmpty){
            //
            return clientManager.get(getBaseUrl(), getPath(), httpKey, getParams(), mHttpHeader, getTagHash(),
                    mRetryTimes, mRetryDelayMillis, onUiCallBack, mHttpCustomConfig, callback);
        }else {
            HttpLog.e("CommonBuilder：Error! processGetQuest : not support request !" );
            return -1;
        }
    }

    protected int getTagHash() {
        return TAG.hashCode();
    }

    /**
     * 上层主动取消请求
     */
    public boolean dispose() {
        return HttpClientManager.getInstance().dispose(getBaseUrl(), getTagHash(), mKey, mHttpCustomConfig);
    }
}
