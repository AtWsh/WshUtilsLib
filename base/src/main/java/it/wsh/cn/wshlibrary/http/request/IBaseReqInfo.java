package it.wsh.cn.wshlibrary.http.request;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpConstants;
import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.utils.HttpLog;
import okhttp3.RequestBody;

/**
 * author: wenshenghui
 * created on: 2018/12/27 14:41
 * description:
 */
public abstract class IBaseReqInfo {

    //需要再url后面拼接的信息
    protected transient Map<String, String> mHttpParams;
    //请求body
    protected Object mBodyObj;
    //默认头信息和配置信息
    protected transient HttpConfig mHttpCustomConfig;
    //其他额外的头部信息
    protected transient Map<String, String> mHttpHeader;

    /**
     * 覆盖http查询参数，注意，两次调用该方法添加参数，后来添加的会将前面添加的清空
     */
    public IBaseReqInfo addParamsMap(Map<String, String> params){
        mHttpParams = params;
        return this;
    }

    /**
     * 以map形式添加表单类型数据
     * 注意：调用了此方法，继承本类的bean自定义的字段将不会作为请求参数。所有请求参数以mBodyObj为准
     * @param mapValue
     * @return
     */
    public IBaseReqInfo addFormDataMap(Map<String, String> mapValue) {
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
     * 注意：调用了此方法，继承本类的bean自定义的字段将不会作为请求参数。所有请求参数以mBodyObj为准
     * @param mapValue
     * @return
     */
    public IBaseReqInfo addBodyMap(Map<String, String> mapValue){
        if(mapValue == null){
            HttpLog.e("CommonBuilder：Error! input addBodyMap mapValue == null");
            return this;
        }
        JsonObject jsonObject = new JsonObject();

        for (String key : mapValue.keySet()) {
            try {
                jsonObject.addProperty(key, mapValue.get(key));
            }catch (Exception e){
                HttpLog.e(e);
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
    public IBaseReqInfo setHeader(Map<String, String> mapValue){
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
    public IBaseReqInfo addHeader(Map<String, String> mapValue){
        if (mapValue == null || mapValue.size() <= 0) {
            HttpLog.i("CommonBuilder：Error! input addHeader mapValue = " + mapValue);
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
    public IBaseReqInfo addHeader(String key, String value){
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
    public IBaseReqInfo setHttpCustomConfig(HttpConfig httpConfig) {
        mHttpCustomConfig = httpConfig;
        return this;
    }

    protected abstract String getPath();

    protected abstract String getBaseUrl();

    protected abstract @HttpMethod.IMethed String getMethod();
}
