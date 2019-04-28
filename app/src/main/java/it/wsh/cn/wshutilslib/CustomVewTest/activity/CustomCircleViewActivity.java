package it.wsh.cn.wshutilslib.CustomVewTest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.CustomVewTest.widget.CustomCircleView;
import it.wsh.cn.wshutilslib.R;

public class CustomCircleViewActivity extends AppCompatActivity {

    private TextView m10Tv;
    private TextView m50Tv;
    private TextView m100Tv;
    private TextView mStartTv;
    private TextView mStopTv;
    private CustomCircleView mCustomCircleView;

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, CustomCircleViewActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_circle_view);

        initView();
        initAction();
    }

    private void initView() {
        m10Tv = findViewById(R.id.tv_custom_circle_view_10);
        m50Tv = findViewById(R.id.tv_custom_circle_view_50);
        m100Tv = findViewById(R.id.tv_custom_circle_view_100);

        mStartTv = findViewById(R.id.tv_custom_circle_view_start);
        mStopTv = findViewById(R.id.tv_custom_circle_view_stop);

        mCustomCircleView = findViewById(R.id.custom_circle_view);
    }

    private void initAction() {
        m10Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCircleView.setPercent(10);
            }
        });

        m50Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCircleView.setPercent(50);
            }
        });

        m100Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCircleView.setPercent(100);
            }
        });

        mStartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCircleView.start();
            }
        });

        mStopTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCircleView.stop();
            }
        });
    }


}
