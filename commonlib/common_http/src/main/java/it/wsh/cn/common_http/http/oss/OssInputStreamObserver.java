package it.wsh.cn.common_http.http.oss;

import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author: wenshenghui
 * created on: 2018/10/12 09:16
 * description:
 */
public class OssInputStreamObserver implements Observer<InputStream> {

    private InputStreamCallBack mCallBack;

    public OssInputStreamObserver() {

    }

    protected Disposable d;//可以用于取消注册的监听者

    @Override
    public void onSubscribe(Disposable d) {}

    @Override
    public void onNext(InputStream inputStream) {
        if (mCallBack != null) {
            mCallBack.onSuccess(inputStream);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mCallBack != null) {
            mCallBack.onError();
        }
    }

    @Override
    public void onComplete() {

    }

    /**
     * 添加下载监听
     *
     * @return true： 添加成功
     */
    public void setCallBack(InputStreamCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * 删除下载监听
     *
     */
    public void removeCallBack() {
        mCallBack = null;
    }
}
