package it.wsh.cn.common_oss.task;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.util.ArrayList;
import java.util.List;

import it.wsh.cn.common_http.http.HttpConstants;
import it.wsh.cn.common_http.http.IProcessInfo;
import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.utils.MainThreadExecutor;
import it.wsh.cn.common_oss.UploadCacheHelper;
import it.wsh.cn.common_oss.bean.OssConfigInfo;
import it.wsh.cn.common_oss.bean.UploadInfo;


public class OssUploadTask implements GenericLifecycleObserver {

    private Context mContext;
    private String mObjectKey;
    private String mLocalPath;
    private OssConfigInfo mOssConfigInfo;
    private OSSClient mOss;
    private OSSAsyncTask mOSSAsyncTask;
    private MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private List<IProcessListener> mListeners = new ArrayList<>();
    private UploadInfo mUploadInfo = new UploadInfo();


    public OssUploadTask(Context context, String objectKey, String localPath,
                         OssConfigInfo ossConfigInfo) {
        mContext = context;
        mObjectKey = objectKey;
        mLocalPath = localPath;
        mOssConfigInfo = ossConfigInfo;
        mUploadInfo.setUrl(objectKey);
        mUploadInfo.setLocalPath(localPath);
        init();
    }

    /**
     * 初始化OssClient
     */
    private void init() {
        //BHLog.i(HttpConstants.LOG_TAG,"OssUploadTask : init");
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        //OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SD_path\OSSLog\logs.csv
        OSSFederationToken stsToken = new OSSFederationToken(mOssConfigInfo.AccessKeyId,
                mOssConfigInfo.AccessKeySecret, mOssConfigInfo.SecurityToken,
                mOssConfigInfo.Expiration);
        OSSStsTokenCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                stsToken.getTempAK(), stsToken.getTempSK(), stsToken.getSecurityToken());
        mOss = new OSSClient(mContext, mOssConfigInfo.mEndpoint, credentialProvider, conf);
    }

    /**
     * 将文件上传到阿里服务器，不支持断点续传，适合小文件
     */
    public void start(IProcessListener listener) {

        addProcessListener(listener);
        notifyStart();
        if (mOssConfigInfo == null) {
            notifyError(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR, "OssConfigInfo == null");
            return;
        }
        PutObjectRequest request = new PutObjectRequest(mOssConfigInfo.mBucketName, mObjectKey, mLocalPath);

        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //BHLog.i(HttpConstants.LOG_TAG,"ossUploadFile onProgress request: " + mObjectKey + " currentSize: " + currentSize + " totalSize: " + totalSize);
                mUploadInfo.setTotalSize(totalSize);
                mUploadInfo.setCurrentPosition(currentSize);
                notifyProcessUpdate(mUploadInfo);
            }
        });

        mOSSAsyncTask = mOss.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                //BHLog.i(HttpConstants.LOG_TAG,"OssUploadTask: ossUploadFile onSuccess request: " + mObjectKey);
                notifySuccess(mObjectKey);
                clear();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {

                //BHLog.e(HttpConstants.LOG_TAG,"OssUploadTask: ossUploadFile onFailure request: " + mObjectKey);
                // 请求异常
                if (clientException != null) {
                    // 本地异常如网络异常等
                    //BHLog.e(HttpConstants.LOG_TAG,"OssUploadTask: ossUploadFile onFailure");
                    notifyError(IProcessListener.ERRCODE_UPLOAD_LOCAL_ERR, "local error");
                    clear();
                    return;
                }
                if (serviceException != null) {
                    // 服务异常
                    String errCode = serviceException.getErrorCode();
                    String errMsg = serviceException.getRawMessage();
                    //BHLog.e(HttpConstants.LOG_TAG,"OssUploadTask: ossUploadFile onFailure ErrorCode: " + errCode + " RequestId: "
                            //+ serviceException.getRequestId() + " HostId: "
                            //+ serviceException.getHostId() + " RawMessage: " + errMsg);
                    notifyError(IProcessListener.ERRCODE_UPLOAD_SERVER_ERR, errMsg);
                    clear();
                    return;
                }
                notifyError(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR, "unknown error");
                clear();
            }
        });
    }

    public boolean addProcessListener(IProcessListener listener) {
        if (listener == null) {
            return false;
        }
        if (mListeners.contains(listener)) {
            return false;
        }

        return mListeners.add(listener);
    }

    /**
     * 删除监听
     * @param listener
     */
    public boolean removeListener(IProcessListener listener) {
        if (listener == null || mListeners.size() == 0) {
            return false;
        }
        return mListeners.remove(listener);
    }

    /**
     * 通知所有的DownloadProcessListener开始
     */
    private void notifyStart() {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            mMainThreadExecutor.onStart(listener);
        }
    }

    /**
     * 通知所有的DownloadProcessListener更新
     *
     * @param processInfo
     */
    private void notifyProcessUpdate(IProcessInfo processInfo) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            mMainThreadExecutor.onProgress(listener, processInfo);
        }
    }

    /**
     * 通知所有的DownloadProcessListener出错
     *
     * @param msg
     */
    private void notifyError(int stateCode, String msg) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            mMainThreadExecutor.onError(listener, stateCode, msg);
        }
    }

    /**
     * 通知所有的DownloadProcessListener出错
     *
     * @param msg
     */
    private void notifySuccess(String msg) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            mMainThreadExecutor.onSuccess(listener, msg);
        }
    }

    /**
     * 清除各种缓存
     */
    public void clear() {
        mListeners.clear();
        UploadCacheHelper.getInstance().remove(mObjectKey);
    }

    public void cancel() {
        if (mOSSAsyncTask != null) {
            mOSSAsyncTask.cancel();
        }
        UploadCacheHelper.getInstance().remove(mObjectKey);
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        clear();
    }
}
