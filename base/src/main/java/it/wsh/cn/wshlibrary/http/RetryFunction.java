package it.wsh.cn.wshlibrary.http;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * 重试函数，用于配置Rxjava
 * 如果可能，最好能放置在Rxjava专用的一些封装module上
 */
public class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

    private final int mMaxRetries;
    private final int mRetryDelayMillis;
    private int mRetryCount;

    public RetryFunction(int maxRetries, int retryDelayMillis){
        this.mMaxRetries = maxRetries;
        this.mRetryDelayMillis = retryDelayMillis;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable)throws Exception{
        return throwableObservable.concatMap(new Function<Throwable, ObservableSource<?>>(){

        @Override
        public ObservableSource<?> apply(Throwable throwable)throws Exception{
            if(++mRetryCount <= mMaxRetries){
                 return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
            }
            return Observable.error(throwable);
        }
        });
    }
}

