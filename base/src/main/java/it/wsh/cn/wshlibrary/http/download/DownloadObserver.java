package it.wsh.cn.wshlibrary.http.download;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;

/**
 * author: wenshenghui
 * created on: 2018/9/27 18:16
 * description:
 */
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
        DownloadManager.getInstance().removeDownloadTask(mKey);
    }

    @Override
    public void onComplete() {
        DownloadManager.getInstance().removeDownloadTask(mKey);
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
