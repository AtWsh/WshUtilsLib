package it.wsh.cn.common_http.http.download;

import android.support.annotation.IntDef;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * author: wenshenghui
 * created on: 2019/6/17 14:36
 * description: 当有下载任务时，数据更新每秒通知一次
 */
public class DownloadDataEngine {

    private static volatile DownloadDataEngine sInstance;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static int TASK_COUNT_LIMIT_DEFAULT = CPU_COUNT + 1; //默认支持n(cpu) + 1个任务
    private static final BlockingQueue<IDownloadTask> sPoolWorkQueue =
            new LinkedBlockingQueue<>(128); //缓存队列

    private int mTaskCountLimit = TASK_COUNT_LIMIT_DEFAULT;
    private HashMap<Integer, IDownloadTask> mDownloadingTasks = new HashMap<>(); //正在下载的任务
    private HashMap<Integer, IDownloadTask> mAllTasks = new HashMap<>(); //全部任务，包括缓存队列和下载中的任务
    public static final int RESULT_ERROR = 0; //表示失败
    public static final int RESULT_OK = 1; //表示成功
    public static final int RESULT_CACHE = 2; //表示进入了缓存队列

    @IntDef({RESULT_ERROR, RESULT_OK, RESULT_CACHE})
    public @interface Result {
    }

    public static DownloadDataEngine getInstance() {
        if (sInstance == null) {
            synchronized (DownloadDataEngine.class) {
                if (sInstance == null) {
                    sInstance = new DownloadDataEngine();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置同时下载的任务数
     *
     * @param taskCountLimit
     */
    public void setTaskCountLimit(int taskCountLimit) {
        this.mTaskCountLimit = taskCountLimit;
    }

    /**
     * 保存正在下载的task
     *
     * @param key  表征下载任务的唯一值
     * @param task
     * @return 返回执行结果 {@link Result}
     */
    public @Result
    int addDownloadTask(int key, IDownloadTask task) {
        if (key == -1 || task == null) {
            return RESULT_ERROR;
        }

        int downloadingTaskSize = mDownloadingTasks.size();
        if (downloadingTaskSize >= mTaskCountLimit) { //任务满了,丢进缓存池
            if (sPoolWorkQueue.offer(task)) { //成功放进缓存池
                return RESULT_CACHE;
            }
            return RESULT_ERROR; //缓存池满了，一般不会发生，目前策略：发生了就直接将任务丢弃
        }

        if (mDownloadingTasks.put(key, task) != null) {
            mAllTasks.put(key, task);
            return RESULT_OK;
        } else {
            return RESULT_ERROR;
        }

    }

    /**
     * 移除任务（任务下载完成）
     *
     * @param key
     * @return 返回执行结果 {@link Result}
     */
    public @Result int removeDownloadTask(int key) {
        IDownloadTask task = mAllTasks.get(key);
        if (task == null) {
            return RESULT_ERROR;
        }
        if (mDownloadingTasks.remove(key) != null) {
            mAllTasks.remove(key);
            return RESULT_OK;
        } else if (sPoolWorkQueue.remove(task)){
            mAllTasks.remove(key);
            return RESULT_ERROR;
        }else {
            return RESULT_ERROR;
        }
    }

    /**
     * 获取下载任务
     * @param key
     * @return IDownloadTask
     */
    public IDownloadTask getDownloadTask(int key) {
        return mAllTasks.get(key);
    }

    /**
     * 判断任务是否在下载中
     * @param key
     * @return
     */
    public boolean isTaskDownloading(int key) {
        if (mDownloadingTasks.containsKey(key)) {
            return true;
        }
        return false;
    }


}
