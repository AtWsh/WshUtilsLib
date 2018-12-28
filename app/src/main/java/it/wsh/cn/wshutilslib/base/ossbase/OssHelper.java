package it.wsh.cn.wshutilslib.base.ossbase;

import android.text.TextUtils;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.download.DownloadManager;
import it.wsh.cn.common_http.http.oss.InputStreamCallBack;
import it.wsh.cn.common_http.http.oss.OssConfigInfo;
import it.wsh.cn.common_http.http.upload.UploadCacheHelper;
import it.wsh.cn.common_http.http.upload.UploadManager;
import it.wsh.cn.wshutilslib.MianApplication;

public class OssHelper {

    /**
     * 设置后缀
     *
     * @param suffix
     */
    public static void setSuffix(String suffix) {
        if (TextUtils.isEmpty(suffix)) {
            return;
        }
        OssConfig.setSuffix(suffix);
        return;
    }

    /**
     * 上传
     *
     * @param objectKey
     * @param localPath
     * @param listener
     * @return
     */
    public static boolean startUpload(String objectKey, String localPath, IProcessListener listener) {
        if (TextUtils.isEmpty(objectKey) || TextUtils.isEmpty(localPath)) {
            return false;
        }

        final UploadCacheHelper cacheHelper = UploadCacheHelper.getInstance();
        if (cacheHelper.exist(objectKey)) {
            return false;
        }

        //2.获取未过期的StrToken
        getOssConfigInfo(new QueryTokenCallBack() {
            @Override
            public void success(OssConfigInfo configInfo) {
                UploadManager.getInstance().start(objectKey, localPath, configInfo, listener);
            }

            @Override
            public void error(int stateCode, String errorInfo) {
                if (listener != null) {
                    listener.onComplete(stateCode, errorInfo);
                }
            }
        });
        return true;
    }

    public static boolean startDownload(String downloadUrl) {
        return startDownload(downloadUrl, "", "", null);
    }

    public static boolean startDownload(String downloadUrl, IProcessListener callBack) {
        return startDownload(downloadUrl, "", "", callBack);
    }

    public static boolean startDownloadWithName(String downloadUrl, String fileName, IProcessListener callBack) {
        return startDownload(downloadUrl, fileName, "", callBack);
    }

    public static boolean startDownloadWithName(String downloadUrl, String fileName) {
        return startDownload(downloadUrl, fileName, "", null);
    }

    public static boolean startDownloadWithPath(String downloadUrl, String savePath) {
        return startDownload(downloadUrl, "", savePath, null);
    }

    public static boolean startDownloadWithPath(String downloadUrl, String savePath, IProcessListener callBack) {
        return startDownload(downloadUrl, "", savePath, callBack);
    }

    public static boolean startDownload(String downloadUrl, String fileName, String savePath) {
        return startDownload(downloadUrl, fileName, savePath, null);
    }

    /**
     * 开始Oss下载
     *
     * @param downloadUrl
     * @param callBack
     */
    public static boolean startDownload(String downloadUrl, String fileName, String savePath, IProcessListener callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        getOssConfigInfo(new QueryTokenCallBack() {
            @Override
            public void success(OssConfigInfo ossConfigInfo) {
                DownloadManager.getInstance().startOss(downloadUrl, fileName, savePath, ossConfigInfo, callBack);
            }

            @Override
            public void error(int stateCode, String errorInfo) {
                if (callBack != null) {
                    callBack.onComplete(stateCode, errorInfo);
                }
            }
        });
        return true;
    }

    /**
     * 开始Oss下载图片
     *
     * @param downloadUrl
     * @param callBack
     */
    public static boolean startDownloadPic(String downloadUrl, InputStreamCallBack callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        getOssConfigInfo(new QueryTokenCallBack() {
            @Override
            public void success(OssConfigInfo ossConfigInfo) {
                DownloadManager.getInstance().startOss(downloadUrl, ossConfigInfo, callBack);
            }

            @Override
            public void error(int stateCode, String errorInfo) {
                if (callBack != null) {
                    callBack.onError();
                }
            }
        });
        return true;
    }

    /**
     * 获取未过期的配置信息
     *
     * @param callBack
     */
    public static void getOssConfigInfo(QueryTokenCallBack callBack) {
        String suffix = OssConfig.getSuffix();
        OssConfigInfo ossConfigInfo = OssPreferHelper.getOssConfigInfo(MianApplication.getContext(), suffix);
        long expiration = 0;
        if (ossConfigInfo != null) {
            expiration = ossConfigInfo.Expiration;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if (expiration <= currentTime) {
            new StsConfigResponse.AsyncQuery().build(new HttpCallBack<StsConfigResponse>() {
                @Override
                public void onSuccess(StsConfigResponse stsConfigResponse) {
                    OssConfigInfo configInfo = new OssConfigInfo();
                    configInfo.AccessKeyId = stsConfigResponse.result.AccessKeyId;
                    configInfo.AccessKeySecret = stsConfigResponse.result.AccessKeySecret;
                    configInfo.SecurityToken = stsConfigResponse.result.SecurityToken;
                    configInfo.Expiration = stsConfigResponse.result.Expiration;
                    configInfo.mBucketName = stsConfigResponse.result.bucket;
                    configInfo.mEndpoint = stsConfigResponse.result.endpoint;
                    OssPreferHelper.saveStsToken(MianApplication.getContext(), suffix, configInfo);
                    callBack.success(configInfo);
                }

                @Override
                public void onError(int stateCode, String errorInfo) {
                    callBack.error(stateCode, errorInfo);
                }
            });
        } else {
            callBack.success(ossConfigInfo);
        }
    }

    public interface QueryTokenCallBack {
        void success(OssConfigInfo ossConfigInfo);

        void error(int stateCode, String errorInfo);
    }
}
