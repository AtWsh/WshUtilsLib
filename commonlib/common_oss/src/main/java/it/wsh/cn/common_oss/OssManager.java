package it.wsh.cn.common_oss;

import android.content.Context;
import android.text.TextUtils;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_oss.bean.OssConfigInfo;
import it.wsh.cn.common_oss.bean.StsConfigResponse;
import it.wsh.cn.common_oss.task.OssUploadTask;

public class OssManager {

    private static Context sContext;
    private static String sStsTokenBaseUrl = "";
    private static String sPath = "";

    public static void init(Context context, String baseUrl, String path) {
        if (context == null) {
            return;
        }
        sContext = context.getApplicationContext();
        sStsTokenBaseUrl = baseUrl;
        sPath = path;
        OssDownloadManager.getInstance().init(sContext);
    }

    public static String getStsTokenBaseUrl() {
        return sStsTokenBaseUrl;
    }

    public static String getPath() {
        return sPath;
    }

    /**
     * 上传
     *
     * @param objectKey
     * @param localPath
     * @param listener
     * @return
     */
    public static boolean startUpload(final String objectKey, final String localPath, final IProcessListener listener) {
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
                startUpload(objectKey, localPath, configInfo, listener);
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

    /**
     * @param objectKey
     * @param localPath
     * @param listener
     * @return
     */
    private static void startUpload(String objectKey, String localPath, OssConfigInfo configInfo, IProcessListener listener) {
        //创建OssUploadTask
        OssUploadTask task = new OssUploadTask(sContext, objectKey, localPath, configInfo);
        UploadCacheHelper.getInstance().save(objectKey, task);  //缓存OssUploadTask
        task.start(listener);
    }

    /**
     * 注册下载监听
     *
     * @param objectKey
     * @param processListener
     */
    public static boolean addUploadListener(String objectKey, IProcessListener processListener) {
        OssUploadTask task = UploadCacheHelper.getInstance().get(objectKey);
        if (task == null) {
            return false;
        }
        return task.addProcessListener(processListener);
    }

    /**
     * 注销进度监听
     *
     * @param objectKey
     * @param processListener
     */
    public static boolean removeUploadListener(String objectKey,
                                        IProcessListener processListener) {
        OssUploadTask task = UploadCacheHelper.getInstance().get(objectKey);
        if (task == null) {
            return false;
        }
        return task.removeListener(processListener);
    }

    /**
     * 界面销毁，清除数据
     *
     * @param objectKey
     */
    public static void clear(String objectKey) {
        OssUploadTask task = UploadCacheHelper.getInstance().get(objectKey);
        if (task == null) {
            return;
        }
        task.clear();
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
    public static boolean startDownload(final String downloadUrl, final String fileName, final String savePath, final IProcessListener callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        getOssConfigInfo(new QueryTokenCallBack() {
            @Override
            public void success(OssConfigInfo ossConfigInfo) {
                OssDownloadManager.getInstance().startOss(downloadUrl, fileName, savePath, ossConfigInfo, callBack);
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
    public static boolean startDownloadPic(final String downloadUrl, final InputStreamCallBack callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        getOssConfigInfo(new QueryTokenCallBack() {
            @Override
            public void success(OssConfigInfo ossConfigInfo) {
                OssDownloadManager.getInstance().startOss(downloadUrl, ossConfigInfo, callBack);
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
    public static void getOssConfigInfo(final QueryTokenCallBack callBack) {
        if (sContext == null) {
            return;
        }
        OssConfigInfo ossConfigInfo = OssPreferHelper.getOssConfigInfo(sContext);
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
                    OssPreferHelper.saveStsToken(sContext, configInfo);
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
