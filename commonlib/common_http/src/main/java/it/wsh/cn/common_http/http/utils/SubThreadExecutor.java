package it.wsh.cn.common_http.http.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import it.wsh.cn.common_http.http.HttpCallBack;

public class SubThreadExecutor<T> implements Executor {

    private final static String TAG = "SubThreadExecutor";

    private Handler mSubThreadHandler;

    public SubThreadExecutor() {
        HandlerThread workerThread = new HandlerThread(TAG);
        workerThread.start();
        mSubThreadHandler = new Handler(workerThread.getLooper());
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mSubThreadHandler.post(command);
    }

    public void onStart(final HttpCallBack<T> callBack) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onStart();
            }
        });
    }

    public void onError(final HttpCallBack<T> callBack, final int stateCode, final String msg) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onError(stateCode, msg);
            }
        });
    }


    public void onSuccess(final HttpCallBack<T> callBack, final T t) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(t);
            }
        });
    }
}
