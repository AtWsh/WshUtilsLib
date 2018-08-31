package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpStateCode;


/**
 * author: wenshenghui
 * created on: 2018/8/24 15:04
 * description:
 */
public class XLDownloadManager {

    private XLDownloadManager(Context context){mContext = context;}
    private Context mContext;

    private static volatile XLDownloadManager sInstance;
    private HashMap<Integer, DownloadTask> mDownloadTasks;

    public static XLDownloadManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (XLDownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new XLDownloadManager(context);
                }
            }
        }
        return sInstance;
    }

    public void setSaveFile(String saveFile) {
        if (TextUtils.isEmpty(saveFile)) {
            return;
        }
        DownloadPathConfig.setDownloadPath(saveFile);
    }

    public void startDownload(String downloadUrl, HttpCallBack<String> callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        DownloadInfo info = DownloadInfoDaoHelper.queryTask(downloadUrl);
        String fileName = "";
        if (info == null) {
            fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            if (TextUtils.isEmpty(fileName)) {
                fileName = downloadUrl.hashCode() + "";
            }
        }else {
            fileName = info.getFileName();
        }

        startDownload(downloadUrl, fileName, callBack);
    }

    public void startDownload(String downloadUrl, String fileName, final HttpCallBack<String> callBack) {
        if (callBack == null) {
            return;
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            callBack.onError(HttpStateCode.ERROR_DOWNLOAD, null);
            return;
        }

        if (TextUtils.isEmpty(fileName)) {
            fileName = downloadUrl;
        }

        if (mContext == null) {
            callBack.onError(HttpStateCode.ERROR_DOWNLOAD, null);
            return;
        }

        final int key = downloadUrl.hashCode();
        if (mDownloadTasks != null) {
            DownloadTask task = mDownloadTasks.get(key);
            if (task != null && task.isDownloading()) {
                return;
            }
        }

        DownloadTask task = new DownloadTask(mContext);
        DownloadCallBack<String> downloadCallBack = new DownloadCallBack<String>() {

            @Override
            public void onStart() {
                callBack.onStart();
            }

            @Override
            void onPause() {
                if (mDownloadTasks != null) {
                    mDownloadTasks.remove(key);
                }
            }

            @Override
            public void onSuccess(String s) {
                callBack.onSuccess(s);
                if (mDownloadTasks != null) {
                    mDownloadTasks.remove(key);
                }
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                callBack.onError(stateCode, errorInfo);
                if (mDownloadTasks != null) {
                    mDownloadTasks.remove(key);
                }
            }

            @Override
            public void onDownloaded(byte[] file) {
                callBack.onDownloaded(file);
            }

            @Override
            public void onProgress(int progress) {
                callBack.onProgress(progress);
            }
        };
        task.init(downloadUrl);
        task.downloadFile(downloadUrl, fileName, downloadCallBack);
        if (mDownloadTasks == null) {
            mDownloadTasks = new HashMap<>();
        }
        mDownloadTasks.put(key, task);

    }

    public void stopDownload(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        if (mDownloadTasks == null) {
            return;
        }
        int key = downloadUrl.hashCode();
        DownloadTask task = mDownloadTasks.get(key);
        if (task != null) {
            task.exit();
        }

        mDownloadTasks.remove(key);
    }

    public boolean deleteDownloadTask(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        stopDownload(url);
        return clearDownloadData(url);

    }

    private boolean clearDownloadData(String url) {
        DownloadInfo info = DownloadInfoDaoHelper.queryTask(url);
        if (info != null) {
            String saveFile = info.getSavePath();
            DownloadInfoDaoHelper.deleteInfo(url);
            if (TextUtils.isEmpty(saveFile)) {
                return false;
            }
            File file = new File(saveFile);
            if (file != null && file.exists()) {
                file.delete();
                return true;
            }
        }
        return false;
    }

}
