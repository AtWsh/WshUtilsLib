package it.wsh.cn.common_http.http;

import java.util.Map;

import okhttp3.RequestBody;

public interface IHttpClient<T> {

    int post(String baseurl, String path, int httpKey, int tagHash, int retryTimes,
             int retryDelayMillis, boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int postWithParamsMap(String baseurl, String path, int httpKey, Map<String, String> params,
                          int tagHash, int retryTimes, int retryDelayMillis,
                          boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int post(String baseurl, String path, int httpKey, Object bodyJson, int tagHash,
             int retryTimes, int retryDelayMillis, boolean onUiCallBack, HttpConfig httpConfig,
             HttpCallBack<T> callback);

    int postWithHeaderMap(String baseurl, String path, int httpKey, Map mapHeader,
                          int tagHash, int retryTimes, int retryDelayMillis,
                          boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int postParamsAndObj(String baseurl, String path, int httpKey, Map<String, String> params,
                         Object bodyJson, int tagHash, int retryTimes, int retryDelayMillis,
                         boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int post(String baseurl, String path, int httpKey, Map<String, String> params,
             Map<String, String> mapHeader, int tagHash, int retryTimes,
             int retryDelayMillis, boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int postMapHeaderAndObj(String baseurl, String path, int httpKey,
                            Map<String, String> mapHeader, Object bodyJson, int tagHash,
                            int retryTimes, int retryDelayMillis, boolean onUiCallBack,
                            HttpConfig httpConfig, HttpCallBack<T> callback);

    int post(String baseurl, String path, int httpKey, Map<String, String> params,
             Map<String, String> mapHeader, Object bodyJson, int tagHash,
             int retryTimes, int retryDelayMillis, boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    /**
     * @param path
     * @param callback
     */
    int get(String baseurl, String path, int httpKey, int tagHash, int retryTimes,
            int retryDelayMillis, boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int getWithParamsMap(String baseurl, String path, int httpKey, Map<String, String> params,
                         int tagHash, int retryTimes, int retryDelayMillis,
                         boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    int getWithHeaderMap(String baseurl, String path, int httpKey, Map<String, String> mapHeader,
                         int tagHash, int retryTimes, int retryDelayMillis, boolean onUiCallBack,
                         HttpConfig httpConfig, HttpCallBack<T> callback);

    int get(String baseurl, String path, int httpKey, Map<String, String> params,
            Map<String, String> authHeader, int tagHash, int retryTimes, int retryDelayMillis,
            boolean onUiCallBack, HttpConfig httpConfig, HttpCallBack<T> callback);

    //上传
    int upload(String baseurl, String path, int httpKey, Map<String, String> mapHeader,
               Map<String, RequestBody> partMap, int tagHash, int retryTimes,
               int retryDelayMillis, HttpConfig httpConfig, HttpCallBack<String> callback);
}
