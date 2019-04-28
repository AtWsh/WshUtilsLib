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

import com.example.administrator.studyapp.aidl.BookInfo;
import com.example.administrator.studyapp.aidl.BookManager;

import it.wsh.cn.wshutilslib.R;

public class AidlTestActivity extends AppCompatActivity {

    private final static String TAG = "AidlTestActivity";

    private TextView mAddTv;
    private TextView mGetTv;

    private BookManager mBookManager;

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, AidlTestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_aidl);

        initView();
        initAction();
        startServiceForMessenger();
    }

    private void initView() {
        mAddTv = findViewById(R.id.tv_aidl_add);
        mGetTv = findViewById(R.id.tv_aidl_get);
    }

    private void initAction() {
        mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addBook();

            }
        });

        mGetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBook();
            }
        });
    }

    private void getBook() {
        if (mBookManager != null) {
            try {
                BookInfo info = mBookManager.getBook(0);
                Log.d(TAG, info.mBookName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void addBook() {
        if (mBookManager == null) {
            return;
        }

        BookInfo info = new BookInfo();
        info.mBookName = "月亮与六便士";
        try {
            mBookManager.addBook(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startServiceForMessenger() {
        Intent intent = new Intent();
        //android5.0开始隐式意图启动Service必须加上提供Service进程的包命。
        intent.setPackage("com.example.administrator.studyapp");
        intent.setAction("com.example.wsh.aidl.remote");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = BookManager.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
