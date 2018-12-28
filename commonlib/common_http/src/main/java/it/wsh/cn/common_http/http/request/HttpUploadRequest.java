package it.wsh.cn.common_http.http.request;

import java.util.Map;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.HttpClientManager;
import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.utils.HttpUtils;
import okhttp3.RequestBody;

/**
 * author: wenshenghui
 * created on: 2018/12/27 16:19
 * description:
 */
public class HttpUploadRequest<Req extends BaseUploadReqInfo, Rsp extends String> extends HttpRequest<Req, Rsp> {

    /**
     * 创建一个请求，并指定回调是否在UI线程
     *
     * @param onUiCallBack
     * @param callback
     * @return key -1为判断条件失败，并未成功发起请求
     */
    @Override
    public int start(boolean onUiCallBack, Req req, HttpCallBack<Rsp> callback) {
        Object bodyObj = null;
        Map<String, String> httpHeader = null;
        HttpConfig httpCustomConfig = null;
        Map<String, RequestBody> partMap = null;
        if (req != null) {
            partMap = req.mPartMap;
            httpCustomConfig = req.mHttpCustomConfig;
            mHttpCustomConfig = req.mHttpCustomConfig;
            mPath = req.getPath();
            mBaseUrl = req.getBaseUrl();
            mMethed = req.getMethod();
        }
        return request(onUiCallBack, bodyObj, null, httpHeader, httpCustomConfig,
                partMap, callback);
    }

    @Override
    protected int request(boolean onUiCallBack, Object bodyObj, Map<String, String> httpParams,
                          Map<String, String> httpHeader, HttpConfig httpCustomConfig,
                          Map<String, RequestBody> mPartMap,
                          HttpCallBack<Rsp> callback) {
        if (mPartMap == null) {
            return ERROR_KEY;
        }
        HttpClientManager clientManager = HttpClientManager.getInstance();
        callback.onStart();
        int disposableCacheKey = HttpUtils.getHttpKey(getPath(), httpHeader, null, mPartMap, mHttpCustomConfig);

        return clientManager.upload(
                getBaseUrl(), getPath(), disposableCacheKey, httpHeader, mPartMap, getTagHash(),
                mRetryTimes, mRetryDelayMillis, mHttpCustomConfig, callback);
    }

    @Override
    protected @HttpMethod.IMethed
    String getMethod() {
        return HttpMethod.POST;
    }
}
