package it.wsh.cn.wshlibrary.http.download;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * author: wenshenghui
 * created on: 2018/9/17 14:58
 * description:
 */
public class DownloadDataKernel {

    private static final String TAG = "DownloadDataKernel";
    private final int UPDATE_DELAYED = 1000;
    private volatile static DownloadDataKernel sInstance;

    private HandlerThread mWorkerThread;
    private Handler mWorkerHandler;
    private boolean mUpdating;

    private DownloadDataKernel() {
        mWorkerThread = new HandlerThread(TAG);
        mWorkerThread.start();
        mWorkerHandler = new Handler(mWorkerThread.getLooper());
        startUpdateTimer();
    }

    public static DownloadDataKernel getInstance() {
        if (sInstance == null) {
            synchronized (DownloadDataKernel.class) {
                if (sInstance == null) {
                    sInstance = new DownloadDataKernel();
                }
            }
        }
        return sInstance;
    }

    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mWorkerHandler != null) {
                    mWorkerHandler.removeCallbacks(this);
                    mWorkerHandler.postDelayed(this, UPDATE_DELAYED);
                }
                updateDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 更新数据库
     */
    private void updateDataBase() {
        List<DownloadTask> allTasks = DownloadTaskCache.getInstance().getAllTask();
        if (allTasks == null || allTasks.size() <= 0) {
            stopUpdateTimer();
            return;
        }
        List<DownloadInfo> allTaskInfos = new ArrayList<>();
        for (DownloadTask task : allTasks) {
            DownloadInfo downloadInfo = task.getDownloadInfo();
            allTaskInfos.add(downloadInfo);
        }
        DownloadInfoDaoHelper.insertTasks(allTaskInfos);
    }

    /**
     * 界面更新定时器
     */
    public void startUpdateTimer() {
        if (mUpdating) {
            return;
        }
        mUpdating = true;
        if (mWorkerHandler != null) {
            mWorkerHandler.removeCallbacks(mUpdateRunnable);
            mWorkerHandler.postDelayed(mUpdateRunnable, UPDATE_DELAYED);
        }
    }

    private void stopUpdateTimer() {
        mUpdating = false;
        if (mWorkerHandler != null) {
            mWorkerHandler.removeCallbacks(mUpdateRunnable);
        }
    }
}
