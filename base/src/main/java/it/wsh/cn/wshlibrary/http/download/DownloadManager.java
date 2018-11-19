package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;
import it.wsh.cn.wshlibrary.database.bean.OssInfo;
import it.wsh.cn.wshlibrary.database.daohelper.DownloadInfoDaoHelper;
import it.wsh.cn.wshlibrary.database.daohelper.OssInfoDaoHelper;
import it.wsh.cn.wshlibrary.http.IProcessListener;
import it.wsh.cn.wshlibrary.http.IProcessInfo;
import it.wsh.cn.wshlibrary.http.oss.InputStreamCallBack;
import it.wsh.cn.wshlibrary.http.oss.OssConfigInfo;
import it.wsh.cn.wshlibrary.http.oss.OssDownloadTask;


/**
 * author: wenshenghui
 * created on: 2018/8/24 15:04
 * description:
 */
public class DownloadManager{

    private DownloadManager(){}
    private Context mContext;

    private static volatile DownloadManager sInstance;
    private HashMap<Integer, IDownloadTask> mDownloadTasks = new HashMap<>();

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
    public DownloadManager setSaveFile(String saveFile) {
        if (TextUtils.isEmpty(saveFile)) {
            return this;
        }
        DownloadPathConfig.setDownloadPath(saveFile);
        return this;
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
    public int start(String downloadUrl, IProcessListener callBack) {
        return start(downloadUrl, "", "", callBack);
    }

    /**
     * 开始下载
     * @param downloadUrl
     * @return true为开启下载成功，false为不满足下载条件
     */
    public int startWithName(String downloadUrl, String fileName, IProcessListener callBack) {
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
    public int startWithPath(String downloadUrl, String savePath, IProcessListener callBack) {
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
     * 开始Oss下载
     * @param downloadUrl
     * @param callBack
     */
    public boolean startOss(String downloadUrl, String fileName, String savePath, OssConfigInfo configInfo,IProcessListener callBack) {

        if (TextUtils.isEmpty(downloadUrl) || configInfo == null) {
            return false;
        }
        //1.构建IDownloadInfo
        OssInfo info = getOssInfo(downloadUrl, fileName, savePath);
        if (info == null) {
            return false;
        }
        info.setStsToken(configInfo.mStsToken);
        info.setBucketName(configInfo.mBucketName);
        info.setEndpoint(configInfo.mEndpoint);
        //2.创建OssTask 下载用Task
        OssDownloadTask task = getOssTask(mContext, info, callBack);
        if (task == null) {
            return false;
        }
        //3.保存OssTask并开始下载
        int key = info.getKey();
        saveDownloadTask(key, task);
        task.start();
        return true;
    }

    /**
     * 开始Oss下载 图片
     * @param downloadUrl
     * @param callBack
     */
    public boolean startOss(String downloadUrl, OssConfigInfo configInfo, InputStreamCallBack callBack) {

        if (TextUtils.isEmpty(downloadUrl) || configInfo == null) {
            return false;
        }
        //1.构建IDownloadInfo
        OssInfo info = getOssInfo(downloadUrl);
        if (info == null) {
            return false;
        }
        info.setBucketName(configInfo.mBucketName);
        info.setEndpoint(configInfo.mEndpoint);
        info.setStsToken(configInfo.mStsToken);
        //2.创建OssTask 下载用Task
        OssDownloadTask task = getOssTask(mContext, info, callBack);
        if (task == null) {
            return false;
        }
        task.startDownloadPic();
        return true;
    }


    /**
     * @param downloadUrl
     * @param callBack
     * @return
     */
    public int start(String downloadUrl, String fileName, String savePath, IProcessListener callBack) {
        //1.构建DownloadInfo
        DownloadInfo info = getDownloadInfo(downloadUrl, fileName, savePath);
        if (info == null) {
            return -1;
        }

        //2.创建DownloadTask
        DownloadTask task = getDownloadTask(mContext, info, callBack);
        if (task == null) {
            return -1;
        }
        //3.保存DownloadTask并开始下载
        int key = info.getKey();
        saveDownloadTask(key, task);
        task.start();
        return key;
    }

    /**
     * 注册下载监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean addDownloadListener(String downloadUrl, IProcessListener processListener) {
        return addDownloadListener(downloadUrl, "", "", processListener);
    }



    /**
     * 注册下载监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean addDownloadListener(String downloadUrl, String fileName, String savePath,
                                       IProcessListener processListener) {
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
    public boolean addDownloadListener(int key, IProcessListener processListener) {
        IDownloadTask downloadTask = mDownloadTasks.get(key);
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
    public boolean removeDownloadListener(String downloadUrl,
                                          IProcessListener processListener) {
        return removeDownloadListener(downloadUrl,"", "", processListener);
    }

    /**
     * 注销进度监听
     * @param downloadUrl
     * @param processListener
     */
    public boolean removeDownloadListener(String downloadUrl, String fileName, String savePath,
                                          IProcessListener processListener) {
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
    public boolean removeDownloadListener(int key, IProcessListener processListener) {
        IDownloadTask downloadTask = mDownloadTasks.get(key);
        if (downloadTask != null) {
            return downloadTask.removeProcessListener(processListener);
        }else {
            return false;
        }
    }

    public boolean stop(String url, boolean isOss) {
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
        IDownloadTask task = mDownloadTasks.get(key);
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
        IDownloadTask task = mDownloadTasks.get(key);
        if (task != null) {
            task.delete();
            removeDownloadTask(key);
        }
        return clearDownloadData(key);
    }

    /**
     * 获取OssTask
     * @param mContext
     * @param info
     * @return
     */
    private OssDownloadTask getOssTask(Context mContext, OssInfo info,
                                       InputStreamCallBack callBack) {
        if (mContext == null || info == null) {
            return null;
        }
        OssDownloadTask task = new OssDownloadTask(mContext, info, callBack);
        return task;
    }

    /**
     * 获取OssTask
     * @param mContext
     * @param info
     * @return
     */
    private OssDownloadTask getOssTask(Context mContext, OssInfo info,
                                       IProcessListener callBack) {
        if (mContext == null || info == null) {
            return null;
        }
        IDownloadTask cacheTask = mDownloadTasks.get(info.getKey());
        if (cacheTask != null ) { //在下载中
            return null;
        }
        OssDownloadTask task = new OssDownloadTask(mContext, info, callBack);
        return task;
    }

    /**
     * 获取DownloadTask
     * @param mContext
     * @param info
     * @return
     */
    private DownloadTask getDownloadTask(Context mContext, DownloadInfo info, IProcessListener callBack) {
        if (mContext == null || info == null) {
            return null;
        }

        IDownloadTask cacheTask = mDownloadTasks.get(info.getKey());
        if (cacheTask != null ) { //在下载中
            return null;
        }
        DownloadTask task = new DownloadTask(mContext, info, callBack);
        return task;
    }

    /**
     * Oss下载之前获取OssInfo，
     *
     * @param downloadUrl
     * @return
     */
    private OssInfo getOssInfo(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return null;
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setUrl(downloadUrl);

        return ossInfo;
    }

    /**
     * Oss下载之前获取OssInfo，
     *
     * @param downloadUrl
     * @param fileName
     * @param downloadUrl
     * @return
     */
    private OssInfo getOssInfo(String downloadUrl, String fileName, String savePath) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return null;
        }
        OssInfo ossInfo = new OssInfo();
        ossInfo.setUrl(downloadUrl);

        fileName = getRealFileName(fileName, downloadUrl);
        ossInfo.setFileName(fileName);

        savePath = getRealSavePath(fileName, savePath);
        ossInfo.setSavePath(savePath);

        //判断是否在下载中
        int key = getDownloadKey(downloadUrl, fileName, savePath);
        ossInfo.setKey(key);
        return ossInfo;
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
        IProcessInfo processInfo;
        String saveFile = "";
        processInfo = OssInfoDaoHelper.queryTask(key);
        if (processInfo != null) {
            saveFile = processInfo.getLocalPath();
            OssInfoDaoHelper.deleteInfo(key);
        } else {
            processInfo = DownloadInfoDaoHelper.queryTask(key);
            if (processInfo != null) {
                saveFile = processInfo.getLocalPath();
                DownloadInfoDaoHelper.deleteInfo(key);
            }
        }
        if (processInfo != null) {
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
    private void saveDownloadTask(int key, IDownloadTask task) {
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
}
