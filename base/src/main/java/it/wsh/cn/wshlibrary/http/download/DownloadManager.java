package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;
import it.wsh.cn.wshlibrary.database.daohelper.DownloadInfoDaoHelper;
import it.wsh.cn.wshlibrary.http.HttpStateCode;


/**
 * author: wenshenghui
 * created on: 2018/8/24 15:04
 * description:
 */
public class DownloadManager {

    private DownloadManager(){}
    private Context mContext;

    private static volatile DownloadManager sInstance;
    private HashMap<Integer, DownloadTask> mDownloadTasks = new HashMap<>();

    public static DownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new DownloadManager();
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
     * 设置下载保存的根路径(初始化时设置) APP通用下载位置
     * @param saveFile
     */
    public void initSaveFile(String saveFile) {
        if (TextUtils.isEmpty(saveFile)) {
            return;
        }
        DownloadPathConfig.setDownloadPath(saveFile);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int start(String downloadUrl) {
        return start(downloadUrl, "", "",null);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int start(String downloadUrl, IDownloadListener callBack) {
        return start(downloadUrl, "", "", callBack);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int startWithName(String downloadUrl, String fileName, IDownloadListener callBack) {
        return start(downloadUrl, fileName, "",callBack);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int startWithName(String downloadUrl, String fileName) {
        return start(downloadUrl, fileName, "",null);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int startWithPath(String downloadUrl, String savePath) {
        return start(downloadUrl, "", savePath, null);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int startWithPath(String downloadUrl, String savePath, IDownloadListener callBack) {
        return start(downloadUrl, "", savePath, callBack);
    }

    /**
     * 开始下载提供文件名
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int start(String downloadUrl, String fileName, String savePath) {
        return start(downloadUrl, fileName, savePath, null);
    }

    /**
     * 开始下载提供文件名
     * @param downloadUrl
     * @param callBack
     * @return
     */
    public int start(String downloadUrl, String fileName, String savePath, IDownloadListener callBack) {

        //1.构建DownloadInfo
        DownloadInfo info = getDownloadInfo(downloadUrl, fileName, savePath);
        if (info == null) {
            return -1;
        }

        //2.创建DownloadObserver
        DownloadObserver downloadObserver = new DownloadObserver(info.getKey());
        downloadObserver.addListener(callBack);

        //3..创建DownloadTask
        DownloadTask task = getDownloadTask(mContext, info, downloadObserver);
        if (task == null) {
            return -1;
        }
        //4.保存DownloadTask并开始下载
        int key = info.getKey();
        task.start();
        saveDownloadTask(key, task);
        return key;
    }

    /**
     * 注册下载监听
     * @param downloadUrl
     * @param processListener
     */
    public void addDownloadListener(String downloadUrl, IDownloadListener processListener) {
        addDownloadListener(downloadUrl, "", "", processListener);
    }



    /**
     * 注册下载监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean addDownloadListener(String downloadUrl, String fileName,
                                    String savePath, IDownloadListener processListener) {
        if (processListener == null) {
            return false;
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        fileName = getRealFileName(fileName, downloadUrl);
        savePath = getRealSavePath(fileName, savePath);
        int key = getDownloadKey(downloadUrl, fileName, savePath);
        return addDownloadListener(key, processListener);
    }

    /**
     * 注册下载监听
     * @param key
     * @param processListener
     */
    public boolean addDownloadListener(int key, IDownloadListener processListener) {
        DownloadTask downloadTask = mDownloadTasks.get(key);
        if (downloadTask != null) {
            return downloadTask.addListener(processListener);
        }
        return false;
    }

    /**
     * 注销进度监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean removeDownloadListener(String downloadUrl, IDownloadListener processListener) {
        return removeDownloadListener(downloadUrl,"", "", processListener);
    }

    /**
     * 注销进度监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean removeDownloadListener(String downloadUrl, String fileName,
                                          String savePath, IDownloadListener processListener) {
        if (TextUtils.isEmpty(downloadUrl) || processListener == null) {
            return false;
        }

        fileName = getRealFileName(fileName, downloadUrl);
        savePath = getRealSavePath(fileName, savePath);
        int key = getDownloadKey(downloadUrl, fileName, savePath);
        return removeDownloadListener(key, processListener);
    }

    /**
     * 注销进度监听
     * @param key
     * @param processListener
     */
    public boolean removeDownloadListener(int key, IDownloadListener processListener) {
        DownloadTask downloadTask = mDownloadTasks.get(key);
        if (downloadTask != null) {
            return downloadTask.removeProcessListener(processListener);
        }else {
            return false;
        }
    }

    public boolean stop(String url) {
        return stop(url, "", "");
    }

    /**
     * 暂停下载
     * @param downloadUrl
     * @return true为暂停成功，否则为失败
     */
    public boolean stop(String downloadUrl, String fileName, String savePath) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }

        fileName = getRealFileName(fileName, downloadUrl);
        savePath = getRealSavePath(fileName, savePath);
        int key = getDownloadKey(downloadUrl, fileName, savePath);
        return stop(key);
    }

    public boolean stop(int key) {
        DownloadTask task = mDownloadTasks.get(key);
        if (task != null) {
            task.exit();
            removeDownloadTask(key);
            return true;
        }
        return false;
    }

    /**
     * 删除下载任务和文件
     * @param url
     * @return
     */
    public boolean delete(String url) {
        return delete(url, "", "");
    }

    /**
     * 删除下载任务和文件
     * @param url
     * @return
     */
    public boolean delete(String url, String fileName, String savePath) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        fileName = getRealFileName(fileName, url);
        savePath = getRealSavePath(fileName, savePath);
        int key = getDownloadKey(url, fileName, savePath);
        return delete(key);
    }

    /**
     * 删除下载任务和文件
     * @param key
     * @return
     */
    public boolean delete(int key) {
        stop(key);
        return clearDownloadData(key);
    }

    /**
     * 获取DownloadTask
     * @param mContext
     * @param info
     * @param observer
     * @return
     */
    private DownloadTask getDownloadTask(Context mContext, DownloadInfo info, DownloadObserver observer) {
        if (mContext == null || info == null) {
            return null;
        }

        DownloadTask task = mDownloadTasks.get(info.getKey());
        if (task != null ) { //在下载中
            return null;
        }
        task = new DownloadTask(mContext, info, observer);
        return task;
    }

    /**
     * 下载之前获取DownloadInfo，
     *
     * @param downloadUrl
     * @param fileName
     * @param downloadUrl
     * @return
     */
    private DownloadInfo getDownloadInfo(String downloadUrl, String fileName, String savePath) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return null;
        }
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(downloadUrl);

        fileName = getRealFileName(fileName, downloadUrl);
        downloadInfo.setFileName(fileName);

        savePath = getRealSavePath(fileName, savePath);
        downloadInfo.setSavePath(savePath);

        //判断是否在下载中
        int key = getDownloadKey(downloadUrl, fileName, savePath);
        downloadInfo.setKey(key);
        return downloadInfo;
    }

    private String getRealFileName(String fileName, String downloadUrl) {
        if (!TextUtils.isEmpty(fileName)) {
            return fileName;
        }
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        if (TextUtils.isEmpty(fileName)) {
            fileName = downloadUrl.hashCode() + "";
        }
        return fileName;
    }

    private String getRealSavePath(String fileName, String savePath) {
        if (!TextUtils.isEmpty(savePath)) {
            if (!savePath.endsWith("/")) {
                savePath = savePath + "/";
            }
            return savePath + fileName;
        }
        return DownloadPathConfig.getDownloadPath(mContext) + fileName;
    }

    private int getDownloadKey(String downloadUrl, String fileName, String savePath) {
        int key = -1;
        if (TextUtils.isEmpty(downloadUrl)) {
            return key;
        }
        StringBuilder builder = new StringBuilder(downloadUrl);
        builder.append(fileName).append(savePath);
        key = builder.toString().hashCode();
        return key;
    }

    /**
     * 清除下载记录和下载文件
     * @param key
     * @return
     */
    private boolean clearDownloadData(int key) {
        DownloadInfo info = DownloadInfoDaoHelper.queryTask(key);
        if (info != null) {
            String saveFile = info.getSavePath();
            DownloadInfoDaoHelper.deleteInfo(key);
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
     * 保存正在下载的task
     * @param key
     * @param task
     */
    private void saveDownloadTask(int key, DownloadTask task) {
        if (key == -1 || task == null) {
            return;
        }
        mDownloadTasks.put(key, task);
    }

    /**
     * 删除task
     * @param key
     */
    public void removeDownloadTask(int key) {
        if (mDownloadTasks.containsKey(key)) {
            mDownloadTasks.remove(key);
        }
    }

    public class DownloadObserver implements Observer<DownloadInfo> {

        private int mKey;
        private List<IDownloadListener> mListeners = new ArrayList<>();

        public DownloadObserver(int key) {
            mKey = key;
        }

        protected Disposable d;//可以用于取消注册的监听者

        @Override
        public void onSubscribe(Disposable d) {
            if (d != null) {
                this.d = d;
            }
        }

        @Override
        public void onNext(DownloadInfo downloadInfo) {
            long downloadedLength = downloadInfo.getDownloadPosition();
            long totalSize = downloadInfo.getTotalSize();
            int progress = (int) (downloadedLength * 100 / totalSize);
            downloadInfo.setProcess(progress);
            notifyProcessUpdate(downloadInfo);
        }

        @Override
        public void onError(Throwable e) {
            if (e != null && IDownloadListener.PAUSE_STATE.equals(e.getMessage())) {
                notifyPause();
            } else {
                notifyError(e);
            }
            removeDownloadTask(mKey);
        }

        @Override
        public void onComplete() {
            removeDownloadTask(mKey);
        }

        /**
         * 添加下载监听
         *
         * @param listener
         * @return true： 添加成功
         */
        public boolean addListener(IDownloadListener listener) {
            if (listener == null) {
                return false;
            }
            if (mListeners.contains(listener)) {
                return false;
            }

            return mListeners.add(listener);
        }

        /**
         * 删除下载监听
         *
         * @param listener
         */
        public boolean removeListener(IDownloadListener listener) {
            if (listener == null || mListeners.size() == 0) {
                return false;
            }
            return mListeners.remove(listener);
        }

        /**
         * 通知所有的DownloadProcessListener更新
         *
         * @param downloadInfo
         */
        private void notifyProcessUpdate(DownloadInfo downloadInfo) {
            if (mListeners == null || mListeners.size() == 0) {
                return;
            }
            for (IDownloadListener listener : mListeners) {
                listener.onProgress(downloadInfo);
            }
        }

        /**
         * 通知所有的DownloadProcessListener出错
         *
         * @param e
         */
        private void notifyError(Throwable e) {
            if (mListeners == null || mListeners.size() == 0) {
                return;
            }
            for (IDownloadListener listener : mListeners) {
                listener.onComplete(IDownloadListener.ERROR_DOWNLOAD_RETROFIT, e.getMessage());
            }
        }

        /**
         * 通知所有的DownloadProcessListener下载结束
         */
        private void notifyPause() {
            if (mListeners == null || mListeners.size() == 0) {
                return;
            }
            for (IDownloadListener listener : mListeners) {
                listener.onComplete(IDownloadListener.PAUSE, "");
            }
        }
    }
}
