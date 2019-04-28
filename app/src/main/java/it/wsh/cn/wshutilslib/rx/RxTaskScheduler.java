package it.wsh.cn.wshutilslib.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author:     xumin
 * date:       2018/5/4
 * email:      xumin2@evergrande.cn
 * 封装了异步任务线程调度的类
 */
public class RxTaskScheduler {
    /**
     * 逻辑在子线线程，结果回调在主线程执行
     * onStart回调：在调用线程总执行
     * onSuccess回调：在主线程中执行,也就是UI线程
     * onFailed回调：在主线程中执行，也就是UI线程
     * doWork在子线程
     * @param rxTaskCallBack
     * @param <T>            计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，
     *                       例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。
     *                       不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
     *
     */
    public static <T> void postLogicMainTask(final RxTaskCallBack<T> rxTaskCallBack) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                T t = rxTaskCallBack.doWork();
                if (t != null) {
                    e.onNext(t);
                } else {
                    postUiTask(new RxTaskCallBack<Boolean>() {
                        @Override
                        public Boolean doWork() {
                            rxTaskCallBack.onSuccess(null);
                            return true;
                        }
                    });
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxTaskCallBack.onStart(d);
                    }

                    @Override
                    public void onNext(T t) {
                        rxTaskCallBack.onSuccess(t);
                    }

                    @Override
                    public void onError(Throwable e) {
                        rxTaskCallBack.onFailed(e);
                    }

                    @Override
                    public void onComplete() {
                        rxTaskCallBack.onComplete();
                    }
                });
    }

    /**
     * I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
     *
     * 逻辑在子线线程，结果回调在主线程执行
     * onStart回调：在调用线程总执行
     * onSuccess回调：在主线程中执行,也就是UI线程
     * doWork在子线程
     * onFailed回调：在主线程中执行，也就是UI线程
     * @param rxTaskCallBack
     * @param <T>
     */
    public static <T> void postIoMainTask(final RxTaskCallBack<T> rxTaskCallBack) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                T t = rxTaskCallBack.doWork();
                if (t != null) {
                    e.onNext(t);
                } else {
                    postUiTask(new RxTaskCallBack<Boolean>() {
                        @Override
                        public Boolean doWork() {
                            rxTaskCallBack.onSuccess(null);
                            return true;
                        }
                    });
                }
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxTaskCallBack.onStart(d);
                    }

                    @Override
                    public void onNext(T t) {
                        rxTaskCallBack.onSuccess(t);
                    }

                    @Override
                    public void onError(Throwable e) {
                        rxTaskCallBack.onFailed(e);
                    }

                    @Override
                    public void onComplete() {
                        rxTaskCallBack.onComplete();
                    }
                });
    }


    /**
     * UI线程 操作 所使用的 Scheduler。
     * doWork在 ui线程
     * onSuccess ui线程
     * @param rxTaskCallBack
     * @param <T>
     */
    public static <T> void postUiTask(final RxTaskCallBack<T> rxTaskCallBack) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                T t = rxTaskCallBack.doWork();
                if (t != null) {
                    e.onNext(t);
                } else {
                    //因为onnext不能发射null值
                    rxTaskCallBack.onSuccess(null);
                }
                e.onComplete();
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        rxTaskCallBack.onStart(d);
                    }

                    @Override
                    public void onNext(T t) {
                        rxTaskCallBack.onSuccess(t);
                    }

                    @Override
                    public void onError(Throwable e) {
                        rxTaskCallBack.onFailed(e);
                    }

                    @Override
                    public void onComplete() {
                        rxTaskCallBack.onComplete();
                    }
                });
    }


}
