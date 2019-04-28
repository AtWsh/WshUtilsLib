package it.wsh.cn.wshutilslib.IPCTest.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;

public class MessengerTestActivity extends AppCompatActivity {

    private TextView mMessengerTestTv;

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MessengerTestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_ipc);

        initView();
        initAction();
    }

    private void initView() {
        mMessengerTestTv = findViewById(R.id.tv_messenger_test);
        mMessengerTestTv.setText("开启服务");
    }

    private void initAction() {
        mMessengerTestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServiceForMessenger();
            }
        });
    }

    private void startServiceForMessenger() {
        Intent intent = new Intent();
        //android5.0开始隐式意图启动Service必须加上提供Service进程的包命。
        intent.setPackage("com.example.administrator.studyapp");
        intent.setAction("com.example.wsh.messenger.remote");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Messenger messenger = new Messenger(service);
            Message msg = Message.obtain();
            msg.what = 1;
            try {
                messenger.send(msg);
                Log.i("客户端", "执行添加用户操作");
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                msg.recycle();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
