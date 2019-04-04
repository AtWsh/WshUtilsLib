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

public class SingleTaskTestSecondActivity extends AppCompatActivity {

    public static void startSelf(Context context) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, SingleTaskTestSecondActivity.class);
        context.startActivity(intent);
    }

    private TextView mBtn;
    private TextView mBtnForResult;

    public static final String TAG = "wsh_log_SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_single_task_test_second);

        initView();
        initAction();
    }
    private void initAction() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleTaskTestFirstActivity.startSelf(SingleTaskTestSecondActivity.this);
            }
        });

        mBtnForResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleTaskTestSecondActivity.this, SingleTaskTestFirstActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initView() {
        mBtn = findViewById(R.id.tv_test_start_activity);
        mBtnForResult = findViewById(R.id.tv_test_start_activity_for_result);
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
