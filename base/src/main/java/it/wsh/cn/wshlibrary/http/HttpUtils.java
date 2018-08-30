package it.wsh.cn.wshlibrary.http;

import android.text.TextUtils;

import java.util.Map;

/**
 * author: wenshenghui
 * created on: 2018/8/17 11:10
 * description:
 */
public class HttpUtils {

    /**
     * 拼接存储Disposable的key值
     * @param path
     * @param params
     * @param mHttpCustomConfig
     * @return
     */
    public static String getDisposableCacheKey(String path, Map<String, String> mapHeader, Map<String, String> params, Object bodyObject, HttpConfig mHttpCustomConfig) {
        StringBuffer keyBuffer = new StringBuffer("");
        if (!TextUtils.isEmpty(path)) {
            keyBuffer.append(path);
        }

        if (mapHeader != null && mapHeader.size() > 0) {
            keyBuffer.append(mapHeader.toString());
        }
        if (params != null && params.size() > 0) {
            keyBuffer.append(params.toString());
        }
        if (bodyObject != null) {
            keyBuffer.append(bodyObject.toString());
        }

        if (mHttpCustomConfig != null) {
            keyBuffer.append(mHttpCustomConfig.toString());
        }

        return keyBuffer.toString();
    }
}
