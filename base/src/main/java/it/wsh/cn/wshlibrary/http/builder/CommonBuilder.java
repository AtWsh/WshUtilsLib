package it.wsh.cn.wshlibrary.http.builder;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpClient;
import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.HttpStateCode;
import it.wsh.cn.wshlibrary.http.HttpUtils;


/**
 * author: wenshenghui
 * created on: 2018/8/7 10:15
 * description:
 */
public abstract class CommonBuilder<T> {

    private String TAG = "AsyncBuilder";

    protected Map<String, String> mHttpParams;
    protected Map<String, String> mHttpHeader;


    protected abstract String getPath();
    protected abstract String getBaseUrl();
    protected abstract @HttpMethod.IMethed
    String getMethod();
    private Object mBodyObj;
    protected HttpConfig mHttpCustomConfig;
    protected String mDisposableCacheKey;

    /**
     *  Function: addJsonQuery()
     *          添加http查询参数，注意，两次调用该方法添加参数，后来添加的会将前面添加的清空
     *
     **/
    public CommonBuilder<T> addParamsMap(Map<String, String> params){
        mHttpParams = params;
        return this;
    }

    public CommonBuilder<T> addBodyObj(Object bodyObj){
        mBodyObj = bodyObj;
        return this;
    }

    public CommonBuilder<T> addBodyJsonObject(JsonObject jsonObject){
        mBodyObj = jsonObject;
        return this;
    }


    public CommonBuilder<T> addBodyMap(Map<String, String> mapValue){
        if(mapValue == null || mapValue.size() <= 0){
            Log.d(TAG, "Error! input param mapValue = " + mapValue);
            return this;
        }
        JsonObject jsonObject = new JsonObject();

        for (String key : mapValue.keySet()) {
            try {
                jsonObject.addProperty(key, mapValue.get(key));
            }catch (Exception e){
                Log.d(TAG, "Error! When get JSONObject.");
                jsonObject = null;
            }
        }

        mBodyObj = jsonObject;
        return this;
    }

    /**
     * 完全覆盖Header
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> setHeader(Map<String, String> mapValue){
        if (mapValue == null) {
            return this;
        }
        mHttpHeader = mapValue;
        return this;
    }

    /**
     * 添加Header信息，不覆盖
     * @param mapValue
     * @return
     */
    public CommonBuilder<T> addHeader(Map<String, String> mapValue){
        if (mapValue == null || mapValue.size() <= 0) {
            return this;
        }
        if (mHttpHeader == null) {
            mHttpHeader = new HashMap<>();
        }

        mHttpHeader.putAll(mapValue);
        return this;
    }

    /**
     * 添加Header信息，不覆盖
     * @param key
     * @param value
     * @return
     */
    public CommonBuilder<T> addHeader(String key, String value){
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return this;
        }
        if (mHttpHeader == null) {
            mHttpHeader = new HashMap<>();
        }

        mHttpHeader.put(key, value);
        return this;
    }

    /**
     * 设置HttpConfig信息
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
     **/
    protected Map<String, String> getParams(){
        return mHttpParams;
    }

    /**
     * 创建一个请求，回调默认在主线程
     * @param callback
     */
    final public void build(HttpCallBack<T> callback){
        build(true, callback);
    }

    /**
     * 创建一个请求，并指定回调是否在UI线程
     * @param onUiCallBack
     * @param callback
     */
    final public void build(boolean onUiCallBack, HttpCallBack<T> callback){
        request(onUiCallBack, callback);
    }

    protected void request(boolean onUiCallBack, HttpCallBack<T> callback){

        HttpClient client = getHttpClient(callback);
        if (client == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        callback.onStart();
        mDisposableCacheKey = HttpUtils.getDisposableCacheKey(getPath(), mHttpHeader, getParams(), mBodyObj, mHttpCustomConfig);
        switch (getMethod()){
            case HttpMethod.POST:
                processPostQuest(client, onUiCallBack, callback);
                break;
            case HttpMethod.GET:
                processGetQuest(client, onUiCallBack, callback);
                break;
        }
    }

    private void processPostQuest(HttpClient client, boolean onUiCallBack, HttpCallBack<T> callback) {
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = getParams() == null;
        boolean bodyObEmpty = mBodyObj == null;
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);
        if (isPathEmpty) {
            callback.onError(HttpStateCode.ERROR_PATH_EMPTY, null);
            return;
        }

        if (paramsEmpty && bodyObEmpty && mapHeaderEmpty) {
            client.post(getPath(), mDisposableCacheKey, getTagHash(), onUiCallBack, callback);
        }else if (!paramsEmpty && bodyObEmpty && mapHeaderEmpty) {
            client.postWithParamsMap(getPath(), mDisposableCacheKey, getParams(), getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !bodyObEmpty && mapHeaderEmpty){
            client.post(getPath(), mDisposableCacheKey, mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && bodyObEmpty && !mapHeaderEmpty){
            client.postWithHeaderMap(getPath(), mDisposableCacheKey, mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !bodyObEmpty && mapHeaderEmpty){
            client.postParamsAndObj(getPath(), mDisposableCacheKey, getParams(), mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && bodyObEmpty && !mapHeaderEmpty){
            client.post(getPath(), mDisposableCacheKey, getParams(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !bodyObEmpty && !mapHeaderEmpty){
            client.postMapHeaderAndObj(getPath(), mDisposableCacheKey, mHttpHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !bodyObEmpty && !mapHeaderEmpty){
            client.post(getPath(), mDisposableCacheKey, getParams(), mHttpHeader, mBodyObj, getTagHash(), onUiCallBack, callback);
        }
    }

    private void processGetQuest(HttpClient client, boolean onUiCallBack, HttpCallBack<T> callback) {
        boolean isPathEmpty = TextUtils.isEmpty(getPath());
        boolean paramsEmpty = getParams() == null;
        boolean mapHeaderEmpty = (mHttpHeader == null || mHttpHeader.size() <= 0);
        if (isPathEmpty) {
            callback.onError(HttpStateCode.ERROR_PATH_EMPTY, null);
            return;
        }

        if (paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            client.get(getPath(), mDisposableCacheKey, getTagHash(), onUiCallBack, callback);
        }else if (!paramsEmpty && mapHeaderEmpty) {
            //验证Ok
            client.getWithParamsMap(getPath(), mDisposableCacheKey, getParams(), getTagHash(), onUiCallBack, callback);
        }else if(paramsEmpty && !mapHeaderEmpty){
            //
            client.getWithHeaderMap(getPath(), mDisposableCacheKey, mHttpHeader, getTagHash(), onUiCallBack, callback);
        }else if(!paramsEmpty && !mapHeaderEmpty){
            //
            client.get(getPath(), mDisposableCacheKey, getParams(), mHttpHeader, getTagHash(), onUiCallBack, callback);
        }
    }

    protected HttpClient getHttpClient(HttpCallBack<T> callBack) {
        if (mHttpCustomConfig == null) {
            return HttpClient.getInstance().refresh(getBaseUrl(), callBack);
        }

        return HttpClient.getInstance().refresh(getBaseUrl(), mHttpCustomConfig, callBack);
    }

    protected int getTagHash() {
        return TAG.hashCode();
    }

    /**
     * 上层主动取消请求
     */
    public boolean dispose() {
        return HttpClient.getInstance().dispose(getTagHash(), mDisposableCacheKey);
    }
}
