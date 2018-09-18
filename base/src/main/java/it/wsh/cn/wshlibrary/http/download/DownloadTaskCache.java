package it.wsh.cn.wshlibrary.http.download;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author: wenshenghui
 * created on: 2018/9/17 16:35
 * description:
 */
public class DownloadTaskCache {

    private HashMap<Integer, DownloadTask> mDownloadTasks = new HashMap<>();

    private volatile static DownloadTaskCache sInstance;
    public static DownloadTaskCache getInstance() {
        if (sInstance == null) {
            synchronized (DownloadTaskCache.class) {
                if (sInstance == null) {
                    sInstance = new DownloadTaskCache();
                }
            }
        }
        return sInstance;
    }

    /**
     * 保存正在下载的task
     * @param key
     * @param task
     */
    public void save(int key, DownloadTask task) {
        if (key == -1 || task == null) {
            return;
        }
        mDownloadTasks.put(key, task);
    }

    /**
     * 删除task
     * @param key
     */
    public void remove(int key) {
        if (mDownloadTasks.containsKey(key)) {
            mDownloadTasks.remove(key);
        }
    }

    /**
     * 根据key值获取正在下载的task
     * @param key
     */
    public DownloadTask get(int key) {
        return mDownloadTasks.get(key);
    }

    /**
     * 获取所有正在下载的task
     */
    public List<DownloadTask> getAllTask() {
        List<DownloadTask> tasks = new ArrayList<>();
        if (mDownloadTasks == null || mDownloadTasks.size() <= 0) {
            return tasks;
        }

        Collection<DownloadTask> values = mDownloadTasks.values();
        for (DownloadTask task : values) {
            tasks.add(task);
        }
        return tasks;
    }
}
