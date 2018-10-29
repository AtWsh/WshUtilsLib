package it.wsh.cn.wshlibrary.http.oss;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import java.util.HashMap;

import it.wsh.cn.wshlibrary.http.HttpCallBack;

public class OssUploadManager {

    private static volatile OssUploadManager sInstance;
    private Context mContext;
    private HashMap<Integer, OssTask> mOssTasks = new HashMap<>();

    private OssUploadManager() {
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        mContext = context.getApplicationContext();
    }

    public static OssUploadManager getInstance() {
        if (sInstance == null) {
            synchronized (OssUploadManager.class) {
                if (sInstance == null) {
                    sInstance = new OssUploadManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 将文件上传到阿里服务器，不支持断点续传，适合小文件
     *
     * @param localPath The local file path to upload from.
     * @return the taskKey to cancel the upload task.
     */
    public void upload(final String remotePath, final String localPath, final HttpCallBack<String> callback) {

        Log.i("wsh_log", "startOssUpload remotePath:" + remotePath + " localPath:" + localPath + " callback:" + callback);
        //1.检查StrToken
        OssConfigHelper.getInstance().getOssStsToken(mContext, new OssConfigHelper.QueryTokenCallBack(){
            @Override
            public void success(OSSFederationToken token) {
                //去重
                OssTask ossTask = new OssTask();
                ossTask.startUpload();
                //缓存
            }

            @Override
            public void error(int stateCode, String errorInfo) {
                if (callback != null) {
                    callback.onError(stateCode, errorInfo);
                }
            }
        });
    }
}
