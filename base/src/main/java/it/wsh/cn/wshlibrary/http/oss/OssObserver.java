package it.wsh.cn.wshlibrary.http.oss;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshlibrary.database.bean.OssInfo;
import it.wsh.cn.wshlibrary.http.download.DownloadManager;
import it.wsh.cn.wshlibrary.http.IDownloadListener;

/**
 * author: wenshenghui
 * created on: 2018/10/12 09:16
 * description:
 */
public class OssObserver implements Observer<OssInfo> {

    private int mKey;
    private List<IDownloadListener> mListeners = new ArrayList<>();

    public OssObserver(int key) {
        mKey = key;
    }

    protected Disposable d;//可以用于取消注册的监听者

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(OssInfo ossInfo) {
        long downloadedLength = ossInfo.getDownloadPosition();
        long totalSize = ossInfo.getTotalSize();
        int progress = (int) (downloadedLength * 100 / totalSize);
        ossInfo.setProcess(progress);
        notifyProcessUpdate(ossInfo);
    }

    @Override
    public void onError(Throwable e) {
        if (e != null && IDownloadListener.PAUSE_STATE.equals(e.getMessage())) {
            notifyPause();
        } else {
            notifyError(e);
        }
        DownloadManager.getInstance().removeDownloadTask(mKey, true);
    }

    @Override
    public void onComplete() {
        DownloadManager.getInstance().removeDownloadTask(mKey, true);
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
     * @param ossInfo
     */
    private void notifyProcessUpdate(OssInfo ossInfo) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (IDownloadListener listener : mListeners) {
            listener.onProgress(ossInfo);
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
