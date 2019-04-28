package it.wsh.cn.wshutilslib.rx;

import io.reactivex.disposables.Disposable;

/**
 * author:     xumin
 * date:       2018/5/4
 * email:      xumin2@evergrande.cn
 */
public interface BaseRxTask<T> {

    void onStart(Disposable disposable);

    void onSuccess(T t);

    void onFailed(Throwable e);

    void onComplete();
    T doWork() throws Exception;

}
