package it.wsh.cn.wshutilslib.IPCTest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;

public class MainActivityForIpc extends AppCompatActivity {

    private TextView mMessengerTestTv;
    private TextView mAidlTestTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_ipc);

        initView();
        initAction();
    }

    private void initView() {
        mMessengerTestTv = findViewById(R.id.tv_messenger_test);
        mAidlTestTv = findViewById(R.id.tv_aidl_test);
    }

    private void initAction() {
        mMessengerTestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessengerTestActivity.startSelf(MainActivityForIpc.this);
            }
        });

        mAidlTestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AidlTestActivity.startSelf(MainActivityForIpc.this);
            }
        });
    }
}
