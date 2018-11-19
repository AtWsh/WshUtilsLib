package it.wsh.cn.wshutilslib.rxjavademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.httpdemo.HttpDemoActivity;

/**
 * author: wenshenghui
 * created on: 2018/11/8 09:13
 * description:
 */
public class RxjavaTestActivity extends AppCompatActivity {

    private static String TAG = "RxjavaTestActivity";
    private Button mSyncSingleBtn;

    public static void luanchActivity(Activity context){
        Log.i(TAG, "RxjavaTestActivity luanchActivity");
        Intent intent = new Intent(context, RxjavaTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "RxjavaTestActivity onCreate");
        setContentView(R.layout.activity_rxjava_test);
        initView();
        initAction();
    }

    private void initAction() {
        mSyncSingleBtn.setOnClickListener(mSyncSingleListener);
    }

    private void initView() {
        mSyncSingleBtn = findViewById(R.id.sync_single);
    }

    private View.OnClickListener mSyncSingleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "mSyncSingleBtn onClick");
            doSyncSingleTest();
        }
    };

    /**
     * 发送单条数据， 上下游都在主线程的情况
     */
    private void doSyncSingleTest() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                String threadName = Thread.currentThread().getName();
                Log.i(TAG, "发射 onNext 1  (threadName = " + threadName +")");
                emitter.onNext(1);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                String threadName = Thread.currentThread().getName();
                Log.i(TAG, "接收 onSubscribe (threadName = " + threadName +")");
            }

            @Override
            public void onNext(Integer integer) {
                String threadName = Thread.currentThread().getName();
                Log.i(TAG, "接收 onNext  integer = " + integer + " (threadName = " + threadName +")");
            }

            @Override
            public void onError(Throwable e) {
                String threadName = Thread.currentThread().getName();
                Log.i(TAG, "接收 onError  (threadName = " + threadName +")");
            }

            @Override
            public void onComplete() {
                String threadName = Thread.currentThread().getName();
                Log.i(TAG, "接收 onComplete  (threadName = " + threadName +")");
            }
        });
    }
}
