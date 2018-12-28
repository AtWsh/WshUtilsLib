package it.wsh.cn.common_http.http.upload;

import android.content.Context;
import android.text.TextUtils;

import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.oss.OssConfigInfo;
import it.wsh.cn.common_http.http.oss.OssUploadTask;

public class UploadManager {

    private Context mContext;

    private UploadManager() {
    }

    private static volatile UploadManager sInstance;

    public static UploadManager getInstance() {
        if (sInstance == null) {
            synchronized (UploadManager.class) {
                if (sInstance == null) {
                    sInstance = new UploadManager();
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
     * @param objectKey
     * @param localPath
     * @param listener
     * @return
     */
    public boolean start(String objectKey, String localPath, OssConfigInfo configInfo, IProcessListener listener) {

        if (TextUtils.isEmpty(objectKey) || TextUtils.isEmpty(localPath)) {
            return false;
        }

        final UploadCacheHelper cacheHelper = UploadCacheHelper.getInstance();
        if (cacheHelper.exist(objectKey)) {
            return false;
        }

        //3.创建OssTask 下载用Task
        OssUploadTask task = new OssUploadTask(mContext, objectKey, localPath, configInfo);
        if (task == null) {
            return false;
        }
        cacheHelper.save(objectKey, task);  //缓存
        task.start(listener);
        return true;
    }

    /**
     * 注册下载监听
     *
     * @param objectKey
     * @param processListener
     */
    public boolean addUploadListener(String objectKey, IProcessListener processListener) {
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
    public boolean removeUploadListener(String objectKey,
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
    public void clear(String objectKey) {
        OssUploadTask task = UploadCacheHelper.getInstance().get(objectKey);
        if (task == null) {
            return;
        }
        task.clear();
    }
}
