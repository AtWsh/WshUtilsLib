package it.wsh.cn.wshutilslib.CustomVewTest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.CustomVewTest.widget.CustomFramelayout;
import it.wsh.cn.wshutilslib.CustomVewTest.widget.CustomLinearlayout;
import it.wsh.cn.wshutilslib.CustomVewTest.widget.CustomRelativelayout;
import it.wsh.cn.wshutilslib.CustomVewTest.widget.CustomTextView;
import it.wsh.cn.wshutilslib.IPCTest.activity.MessengerTestActivity;
import it.wsh.cn.wshutilslib.R;

public class OnMeasureTestActivity extends AppCompatActivity {

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, OnMeasureTestActivity.class);
        activity.startActivity(intent);
    }

    private TextView mTestTv1;
    private TextView mTestTv2;
    private TextView mTestTv3;
    private TextView mTestTv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_common);
        initView();
        initAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mTestTv1 = findViewById(R.id.tv_test1);
        mTestTv2 = findViewById(R.id.tv_test2);
        mTestTv3 = findViewById(R.id.tv_test3);
        mTestTv4 = findViewById(R.id.tv_test4);

        mTestTv1.setText("自定义Linearlayout测试");
        mTestTv2.setText("自定义RelativeLayout测试");
        mTestTv3.setText("自定义FrameLayout测试");
        mTestTv4.setText("Linearlayout weight测试");
    }

    private void initAction() {
        mTestTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomLinearlayout customlayout = new CustomLinearlayout(OnMeasureTestActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                customlayout.setOrientation(LinearLayout.VERTICAL);
                addContentView(customlayout, layoutParams);

                CustomTextView textView = new CustomTextView(OnMeasureTestActivity.this);
                textView.setText("123");
                customlayout.addView(textView);
            }
        });

        mTestTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomRelativelayout customlayout = new CustomRelativelayout(OnMeasureTestActivity.this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addContentView(customlayout, layoutParams);

                CustomTextView textView = new CustomTextView(OnMeasureTestActivity.this);
                textView.setText("123");
                customlayout.addView(textView);
            }
        });

        mTestTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomFramelayout customlayout = new CustomFramelayout(OnMeasureTestActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addContentView(customlayout, layoutParams);

                CustomTextView textView = new CustomTextView(OnMeasureTestActivity.this);
                textView.setText("123");
                customlayout.addView(textView);
            }
        });

        mTestTv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = View.inflate(OnMeasureTestActivity.this, R.layout.view_measure_test_linear_weight, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addContentView(view, layoutParams);
            }
        });

    }
}
