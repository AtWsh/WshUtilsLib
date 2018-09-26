package it.wsh.cn.wshlibrary.http.download;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;
import it.wsh.cn.wshlibrary.http.HttpStateCode;

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
     * @param listener
     */
    public void addListener(IDownloadListener listener) {
        if (listener == null) {
            return;
        }
        if (mListeners.contains(listener)) {
            listener.onError(HttpStateCode.ERROR_HAS_REGIST, "");
            return;
        }

        mListeners.add(listener);
    }

    /**
     * 删除下载监听
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
     * @param progress
     */
    private void notifyProcessUpdate(int progress) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IDownloadListener listener : mListeners) {
            listener.onProgress(progress);
        }
    }

    /**
     * 通知所有的DownloadProcessListener出错
     * @param e
     */
    private void notifyError(Throwable e) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IDownloadListener listener : mListeners) {
            listener.onError(HttpStateCode.ERROR_DOWNLOAD_RETROFIT, e.getMessage());
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
            listener.onPause();
        }
    }
}
