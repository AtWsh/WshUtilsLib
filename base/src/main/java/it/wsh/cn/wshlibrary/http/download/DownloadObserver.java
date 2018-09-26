package it.wsh.cn.wshlibrary.http.download;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;
import it.wsh.cn.wshlibrary.http.HttpStateCode;

public class DownloadObserver implements Observer<DownloadInfo> {

    private int mKey;
    private List<IDownloadListener> mProcessListeners = new ArrayList<>();

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
        if (downloadInfo.isExit()) {
            notifyPause();
        }else {
            long downloadedLength = downloadInfo.getDownloadPosition();
            long totalSize = downloadInfo.getTotalSize();
            int progress = (int) (downloadedLength * 100 / totalSize);
            notifyProcessUpdate(progress);
        }
    }

    @Override
    public void onError(Throwable e) {
        notifyError(e);
        DownloadManager.getInstance().removeDownloadTask(mKey);
    }

    @Override
    public void onComplete() {
        DownloadManager.getInstance().removeDownloadTask(mKey);
    }

    /**
     * 添加下载监听
     * @param processListener
     */
    public void addProcessListener(IDownloadListener processListener) {
        if (processListener == null) {
            return;
        }
        if (mProcessListeners.contains(processListener)) {
            processListener.onError(HttpStateCode.ERROR_HAS_REGIST, "");
            return;
        }

        mProcessListeners.add(processListener);
    }

    /**
     * 删除下载监听
     * @param processListener
     */
    public boolean removeProcessListener(IDownloadListener processListener) {
        if (processListener == null || mProcessListeners.size() == 0) {
            return false;
        }
        return mProcessListeners.remove(processListener);
    }

    /**
     * 通知所有的DownloadProcessListener更新
     * @param progress
     */
    private void notifyProcessUpdate(int progress) {
        if (mProcessListeners == null || mProcessListeners.size() == 0) {
            return;
        }
        for (IDownloadListener processListener : mProcessListeners) {
            processListener.onProgress(progress);
        }
    }

    /**
     * 通知所有的DownloadProcessListener出错
     * @param e
     */
    private void notifyError(Throwable e) {
        if (mProcessListeners == null || mProcessListeners.size() == 0) {
            return;
        }
        for (IDownloadListener processListener : mProcessListeners) {
            processListener.onError(HttpStateCode.ERROR_DOWNLOAD_RETROFIT, e.getMessage());
        }
    }

    /**
     * 通知所有的DownloadProcessListener下载结束
     */
    private void notifyPause() {
        if (mProcessListeners == null || mProcessListeners.size() == 0) {
            return;
        }
        for (IDownloadListener processListener : mProcessListeners) {
            processListener.onPause();
        }
    }
}
