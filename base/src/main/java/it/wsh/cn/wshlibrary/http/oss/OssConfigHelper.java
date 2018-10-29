package it.wsh.cn.wshlibrary.http.oss;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import it.wsh.cn.wshlibrary.http.HttpCallBack;

/**
 * author: wenshenghui
 * created on: 2018/10/15 11:06
 * description: 检测OSS信息是否过期的辅助类
 */
public class OssConfigHelper {
    private static volatile OssConfigHelper sInstance = null;
    public static final int ERROR_HTTP = 2001;

    public static OssConfigHelper getInstance() {
        if (sInstance == null) {
            synchronized (OssConfigHelper.class) {
                if (sInstance == null) {
                    sInstance = new OssConfigHelper();
                }
            }
        }
        return sInstance;
    }

    private OssConfigHelper() {}

    public void getOssStsToken(Context context, QueryTokenCallBack callBack) {
        String suffix = OssConfig.getSuffix();
        OssConfigInfo ossConfigInfo = OssPreferHelper.getOssConfigInfo(context, suffix);
        OSSFederationToken stsToken = ossConfigInfo.mStsToken;
        long currentTime = System.currentTimeMillis() / 1000;
        if (stsToken == null || stsToken.getExpiration() <= currentTime) {
            new StsConfigResponse.AsyncQuery().build(new HttpCallBack<StsConfigResponse>() {
                @Override
                public void onSuccess(StsConfigResponse stsConfigResponse) {
                    //todo 这里的数据结构根据具体的解析来做
                    if (stsConfigResponse == null) {
                        callBack.error(ERROR_HTTP, "StsConfigResponse == null");
                    }
                    OSSFederationToken ossFederationToken = new OSSFederationToken(stsConfigResponse.AccessKeyId,
                            stsConfigResponse.AccessKeySecret, stsConfigResponse.SecurityToken,
                            stsConfigResponse.Expiration);
                    OssConfigInfo configInfo = new OssConfigInfo();
                    configInfo.mStsToken = ossFederationToken;
                    configInfo.mBucketName = stsConfigResponse.bucket;
                    configInfo.mEndpoint = stsConfigResponse.endpoint;
                    OssPreferHelper.saveStsToken(context, suffix, configInfo);
                }

                @Override
                public void onError(int stateCode, String errorInfo) {
                    callBack.error(stateCode, errorInfo);
                }
            });
        }else {
            callBack.success(stsToken);
        }
    }

    public interface QueryTokenCallBack{
        void success(OSSFederationToken token);
        void error(int stateCode, String errorInfo);
    }
}
