package it.wsh.cn.common_http.http.download;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.common_http.http.IProcessInfo;
import it.wsh.cn.common_http.http.IProcessListener;
import it.wsh.cn.common_http.http.database.bean.DownloadInfo;

/**
 * author: wenshenghui
 * created on: 2018/9/27 18:16
 * description: 下载任务观察者，控制数据接收和分发
 */
public class DownloadObserver implements Observer<DownloadInfo> {

    private int mKey;
    private List<IProcessListener> mListeners = new ArrayList<>();

    public DownloadObserver(int key) {
        mKey = key;
    }

    @Override
    public void onSubscribe(Disposable d) {
        notifyStart();
    }

    @Override
    public void onNext(DownloadInfo downloadInfo) {
        long downloadedLength = downloadInfo.getCurrentPosition();
        long totalSize = downloadInfo.getTotalSize();
        int progress = (int) (downloadedLength * 100 / totalSize);
        downloadInfo.setProcess(progress);
        notifyProcessUpdate(downloadInfo);
    }

    @Override
    public void onError(Throwable e) {
        if (e != null && IProcessListener.PAUSE_STATE.equals(e.getMessage())) {
            notifyPause();
        } else {
            notifyError(e);
        }
        DownloadManager.getInstance().removeDownloadTask(mKey);
    }

    @Override
    public void onComplete() {
        notifyComplete();
        DownloadManager.getInstance().removeDownloadTask(mKey);
    }

    /**
     * 添加下载监听
     *
     * @param listener
     * @return true： 添加成功
     */
    public boolean addListener(IProcessListener listener) {
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
    public boolean removeListener(IProcessListener listener) {
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
    private void notifyProcessUpdate(IProcessInfo downloadInfo) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            listener.onProgress(downloadInfo);
        }
    }

    /**
     * 通知所有的DownloadProcessListener开始
     */
    private void notifyStart() {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            listener.onStart();
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
        for (IProcessListener listener : mListeners) {
            listener.onResult(IProcessListener.ERROR_DOWNLOAD_RETROFIT, e.getMessage());
        }
    }

    /**
     * 通知所有的DownloadProcessListener下载结束
     */
    private void notifyPause() {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            listener.onResult(IProcessListener.PAUSE, "Download pause");
        }
    }

    private void notifyComplete() {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IProcessListener listener : mListeners) {
            listener.onResult(IProcessListener.SUCCESS, "Download finish");
        }
    }
}
