package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpStateCode;


/**
 * author: wenshenghui
 * created on: 2018/8/24 15:04
 * description:
 */
public class XLDownloadManager {

    private XLDownloadManager(){}
    private Context mContext;

    private static volatile XLDownloadManager sInstance;

    public static XLDownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (XLDownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new XLDownloadManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        if (context == null) {
            return;
        }
        mContext = context.getApplicationContext();
    }

    /**
     * 设置下载保存的根路径
     * @param saveFile
     */
    public void setSaveFile(String saveFile) {
        if (TextUtils.isEmpty(saveFile)) {
            return;
        }
        DownloadPathConfig.setDownloadPath(saveFile);
    }

    /**
     * 开始下载，不提供文件名
     * @param downloadUrl
     * @param callBack
     */
    public void startDownload(String downloadUrl, HttpCallBack<String> callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        startDownload(downloadUrl, "", callBack);
    }

    /**
     * 开始下载提供文件名
     * @param downloadUrl
     * @param fileName
     * @param callBack
     */
    public void startDownload(String downloadUrl, String fileName, final HttpCallBack<String> callBack) {

        //1.判断下载条件并获取key
        int key = checkDownloadConditionAndGetKey(downloadUrl, callBack);
        if (key == -1) {
            return;
        }

        //2.获取DownloadCallBack
        DownloadCallBack<String> downloadCallBack = getDownloadCallBack(key, callBack);

        //3.创建DownloadTask，并初始化retrofit
        DownloadTask task = new DownloadTask(mContext);
        task.init(downloadUrl);
        //4.保存DownloadTask并开始下载
        DownloadTaskCache.getInstance().save(key, task);
        task.downloadFile(downloadUrl, fileName, downloadCallBack);
    }

    /**
     * 获取DownloadCallBack
     * @param key
     * @param callBack
     * @return
     */
    private DownloadCallBack<String> getDownloadCallBack(int key, final HttpCallBack<String> callBack) {
        DownloadCallBack<String> downloadCallBack = new DownloadCallBack<String>() {

            @Override
            public void onStart() {
                if (callBack != null) {
                    callBack.onStart();
                }
            }

            @Override
            void onPause() {
                DownloadTaskCache.getInstance().remove(key);
            }

            @Override
            public void onSuccess(String s) {
                if (callBack != null) {
                    callBack.onSuccess(s);
                }
                DownloadTaskCache.getInstance().remove(key);
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                if (callBack != null) {
                    callBack.onError(stateCode, errorInfo);
                }
                DownloadTaskCache.getInstance().remove(key);
            }
        };
        return downloadCallBack;
    }

    /**
     * 判断是否能继续执行下载
     * @param downloadUrl
     * @param callBack
     * @return
     */
    private int checkDownloadConditionAndGetKey(String downloadUrl, HttpCallBack<String> callBack) {
        if (TextUtils.isEmpty(downloadUrl)) {
            if (callBack != null) {
                callBack.onError(HttpStateCode.ERROR_DOWNLOAD_URL_IS_NULL, null);
            }
            return -1;
        }

        if (mContext == null) {
            if (callBack != null) {
                callBack.onError(HttpStateCode.ERROR_DOWNLOAD_CONTEXT_IS_NULL, null);
            }
            return -1;
        }

        //判断是否在下载中
        int key = downloadUrl.hashCode();
        DownloadTask task = DownloadTaskCache.getInstance().get(key);
        if (task != null ) {
            if (callBack != null) {
                callBack.onError(HttpStateCode.ERROR_IS_DOWNLOADING, null);
            }
            return -1;
        }
        return key;
    }

    /**
     * 暂停下载
     * @param downloadUrl
     */
    public void stopDownload(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        int key = downloadUrl.hashCode();
        DownloadTask task = DownloadTaskCache.getInstance().get(key);
        if (task != null) {
            task.exit();
        }

        DownloadTaskCache.getInstance().remove(key);
    }

    /**
     * 删除下载任务和文件
     * @param url
     * @return
     */
    public boolean deleteDownloadTask(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        stopDownload(url);
        return clearDownloadData(url);

    }

    /**
     * 清除下载记录和下载文件
     * @param url
     * @return
     */
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

    /**
     * 注册下载监听
     * @param downloadUrl
     * @param processListener
     */
    public void registDownloadProcessListener(String downloadUrl, DownloadProcessListener processListener) {
        if (processListener == null) {
            return;
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            processListener.onError(HttpStateCode.ERROR_DOWNLOAD_URL_IS_NULL, "");
            return;
        }

        int key = downloadUrl.hashCode();
        DownloadTask downloadTask = DownloadTaskCache.getInstance().get(key);
        if (downloadTask != null) {
            downloadTask.addProcessListener(processListener);
        }else {
            processListener.onError(HttpStateCode.ERROR_IS_NOT_DOWNLOADING, "");
        }
    }

    /**
     * 注销进度监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean unRegistDownloadProcessListener(String downloadUrl, DownloadProcessListener processListener) {
        if (TextUtils.isEmpty(downloadUrl) || processListener == null) {
            return false;
        }

        int key = downloadUrl.hashCode();
        DownloadTask downloadTask = DownloadTaskCache.getInstance().get(key);
        if (downloadTask != null) {
            return downloadTask.removeProcessListener(processListener);
        }else {
            return false;
        }
    }

}
