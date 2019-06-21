package it.wsh.cn.common_oss;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;

import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.database.bean.OssInfo;
import it.wsh.cn.common_http.http.database.daohelper.OssInfoDaoHelper;
import it.wsh.cn.common_http.http.download.DownloadPathConfig;
import it.wsh.cn.common_http.http.download.IDownloadTask;
import it.wsh.cn.common_oss.bean.OssConfigInfo;
import it.wsh.cn.common_oss.task.OssDownloadTask;


/**
 * author: wenshenghui
 * created on: 2018/12/10 14:12
 * description:
 */
public class OssDownloadManager {
    private OssDownloadManager(){}
    private Context mContext;

    private static volatile OssDownloadManager sInstance;
    private HashMap<Integer, IDownloadTask> mDownloadTasks = new HashMap<>();

    public static OssDownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (OssDownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new OssDownloadManager();
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
     * 开始Oss下载
     * @param downloadUrl
     * @param callBack   此方法所有结果在callBack返回，因为configInfo的获取经过了一次异步过程
     */
    public void startOss(String downloadUrl, String fileName, String savePath, OssConfigInfo configInfo, IProcessListener callBack) {

        if (TextUtils.isEmpty(downloadUrl) || configInfo == null) {
            if (callBack != null) {
                callBack.onResult(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR,
                        "downloadUrl is empty or OssConfigInfo == null");
            }
            return ;
        }
        //1.构建IDownloadInfo
        OssInfo info = getOssInfo(downloadUrl, fileName, savePath);
        if (info == null) {
            callBack.onResult(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR,
                    "OssEntity == null");
            return ;
        }
        info.setBucketName(configInfo.mBucketName);
        info.setEndpoint(configInfo.mEndpoint);
        info.setAccessKeyId(configInfo.AccessKeyId);
        info.setAccessKeySecret(configInfo.AccessKeySecret);
        info.setExpiration(configInfo.Expiration);
        info.setSecurityToken(configInfo.SecurityToken);
        IDownloadTask cacheTask = mDownloadTasks.get(info.getKey());
        if (cacheTask != null ) { //在下载中
            callBack.onResult(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR,
                    "The task is downloading");
            return ;
        }
        //2.创建OssTask 下载用Task
        OssDownloadTask task = getOssTask(mContext, info, callBack);
        if (task == null) {
            callBack.onResult(IProcessListener.ERRCODE_UPLOAD_UNKNOWN_ERR,
                    "OssDownloadTask == null");
            return ;
        }
        //3.保存OssTask并开始下载
        int key = info.getKey();
        saveDownloadTask(key, task);
        task.start();
    }

    /**
     * 开始Oss下载 图片
     * @param downloadUrl
     * @param callBack   此方法所有结果在callBack返回，因为configInfo的获取经过了一次异步过程
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
        info.setAccessKeyId(configInfo.AccessKeyId);
        info.setAccessKeySecret(configInfo.AccessKeySecret);
        info.setExpiration(configInfo.Expiration);
        info.setSecurityToken(configInfo.SecurityToken);
        //2.创建OssTask 下载用Task
        OssDownloadTask task = getOssTask(mContext, info, callBack);
        if (task == null) {
            return false;
        }
        task.startDownloadPic();
        return true;
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
        OssDownloadTask task = new OssDownloadTask(mContext, info, callBack);
        return task;
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

    /**
     * 清除下载记录和下载文件
     * @param key
     * @return
     */
    private boolean clearDownloadData(int key) {
        String saveFile = "";
        OssInfo entity = OssInfoDaoHelper.queryTask(key);
        if (entity != null) {
            saveFile = entity.getLocalPath();
            OssInfoDaoHelper.deleteInfo(entity);
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
