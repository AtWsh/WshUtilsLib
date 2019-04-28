package it.wsh.cn.wshutilslib.CustomVewTest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;

public class CustomViewTestActivity extends AppCompatActivity {

    private TextView mTestTv1;
    private TextView mTestTv2;

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, CustomViewTestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_common);

        initView();
        initAction();
    }

    private void initView() {
        mTestTv1 = findViewById(R.id.tv_test1);
        mTestTv2 = findViewById(R.id.tv_test2);

        mTestTv1.setText("显示圆环");
    }

    private void initAction() {
        mTestTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomCircleViewActivity.startSelf(CustomViewTestActivity.this);
            }
        });

    }
}
