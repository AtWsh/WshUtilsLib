package it.wsh.cn.wshutilslib.TaskAndLaunchModeTest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;

public class SingleInstanceTestAActivity extends AppCompatActivity {

    public static final String TAG = "wsh_log_SingleInsTestA";

    private TextView mBtn;
    public static void startSelf(Context context) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, SingleInstanceTestAActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_simple_task_test);
        initView();
        initAction();
    }

    private void initView() {
        mBtn = findViewById(R.id.tv_task_test1);
        mBtn.setText("跳到SingleInstanceB");
    }

    private void initAction() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleInstanceTestBActivity.startSelf(SingleInstanceTestAActivity.this);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        Log.d(TAG,"onResume info.numActivities = " + info.numActivities);
        Log.d(TAG,"onResume info.baseActivity = " + info.baseActivity);
        Log.d(TAG,"onResume info.topActivity = " + info.topActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
