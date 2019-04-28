package it.wsh.cn.wshutilslib.rx;


import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhoulong  on 2018/1/16.
 * <p>
 * RxJava的一些工具方法
 */
public class RxJavaUtils {

    /*----- 线程调度器。被观察者在io线程，观察者在主线程。 compose操作符调用。 -----*/

    /**
     * {@link SingleTransformer}线程调度器
     */
    public static <T> SingleTransformer<T, T> singleSchedulers() {
        return new SingleTransformer<T, T>() {
            @Override
            public SingleSource<T> apply(Single<T> upstream) {
                return upstream.<T>subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * {@link ObservableTransformer}线程调度器
     */
    public static <T> ObservableTransformer<T, T> observableSchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * {@link FlowableTransformer}线程调度器
     */
    public static <T> FlowableTransformer<T, T> flowableSchedulers() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * {@link Observable}计时器，时间跨度[0,count)。
     *
     * @param count 计时器结束时间
     */
    public static Observable<Long> observableTimer(int count) {
        return Observable.interval(1, TimeUnit.SECONDS)
                .take(count)
                .compose(RxJavaUtils.<Long>observableSchedulers());
    }


    /**
     * {@link Observable}倒计时器，时间跨度(count,0]。
     *
     * @param count 倒计时器开始时间
     */
    public static Observable<Long> observableCountdown(final int count) {
        return observableTimer(count).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) throws Exception {
                return count - aLong - 1;
            }
        });
    }


}
