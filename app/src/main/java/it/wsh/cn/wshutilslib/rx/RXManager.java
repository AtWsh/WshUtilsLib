package it.wsh.cn.wshutilslib.rx;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.ResourceSubscriber;
import io.reactivex.subscribers.SafeSubscriber;

/**
 *这个类主要目的就是
 * 利用RX的一些优点，
 * 主线程与子线程优化处理
 * 图片加载，非网络请求操作耗时工作等
 * add by hede   2018.11.19
 * **/
public class RXManager {
    //初始化引用
    static final AtomicReference<RXManager> INSTANCE = new AtomicReference<>(new RXManager());
    public static RXManager get() {
        RXManager rxManager = INSTANCE.get();
        if (null == rxManager) {
            rxManager = new RXManager();
            INSTANCE.set(rxManager);
        }
        return rxManager;
    }

    //耗时加载
    private ResourceSubscriber subscriber;
    /**
     * 取消RX订阅
     * 所有 present 退出的时候，都要取消订阅，
     * 放置内存溢出
     */
    public void onUnsubscribe() {
        if (subscriber != null && !subscriber.isDisposed()) {
            subscriber.dispose();
        }
    }
    /**
     * 这里用得比较多的是 一些非网络请求的耗时工作；
     * 或者一些比较灵活的http请求
     */
    public void doTimeConsuming(Flowable flowable, ResourceSubscriber subscriber) {
        flowable.compose(RxJavaUtils.flowableSchedulers()).onErrorResumeNext(new Function<Throwable, Flowable>() {
            @Override
            public Flowable apply(Throwable throwable) throws Exception {
                return Flowable.error(ExceptionHandle.handleException(throwable));
            }
        }).subscribe(new SafeSubscriber(subscriber));
        this.subscriber = subscriber;
    }

}
