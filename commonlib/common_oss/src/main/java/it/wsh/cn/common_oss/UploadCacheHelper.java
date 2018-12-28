package it.wsh.cn.common_oss;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import it.wsh.cn.common_oss.task.OssUploadTask;


public class UploadCacheHelper {

    private Map<String, OssUploadTask> mUploadTasks = new HashMap<>();
    private UploadCacheHelper() {
    }

    private static volatile UploadCacheHelper sInstance;

    public static UploadCacheHelper getInstance() {
        if (sInstance == null) {
            synchronized (UploadCacheHelper.class) {
                if (sInstance == null) {
                    sInstance = new UploadCacheHelper();
                }
            }
        }
        return sInstance;
    }

    public void clear() {
        mUploadTasks.clear();
    }

    public boolean save(String key, OssUploadTask task) {
        if (TextUtils.isEmpty(key) || task == null) {
            return false;
        }
        if (mUploadTasks.containsKey(key)) {
            return false;
        }
        mUploadTasks.put(key, task);
        return true;
    }

    public boolean remove(String key) {
        if (TextUtils.isEmpty(key) ) {
            return false;
        }
        mUploadTasks.remove(key);
        return true;
    }

    public OssUploadTask get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return mUploadTasks.get(key);
    }

    public boolean exist(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return mUploadTasks.containsKey(key);
    }
}
