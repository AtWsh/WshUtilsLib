package it.wsh.cn.common_http.http.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

import it.wsh.cn.common_http.http.IProcessInfo;
import it.wsh.cn.common_http.http.IProcessListener;


public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
        handler.post(command);
    }

    public void onStart(final IProcessListener listener) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                listener.onStart();
            }
        });
    }

    public void onError(final IProcessListener listener, final int stateCode, final String msg) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                listener.onResult(stateCode, msg);
            }
        });
    }


    public void onSuccess(final IProcessListener listener, final String msg) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                listener.onResult(IProcessListener.SUCCESS, msg);
            }
        });
    }

    public void onProgress(final IProcessListener listener, final IProcessInfo processInfo) {
        this.execute(new Runnable() {
            @Override
            public void run() {
                listener.onProgress(processInfo);
            }
        });
    }
}
