package it.wsh.cn.wshutilslib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.wsh.baidumap.DituDemoActivity;

import it.wsh.cn.common_utils.utils.RouteUtils;
import it.wsh.cn.wshutilslib.httpanotationtest.HttpAnnotationDemoActivity;
import it.wsh.cn.wshutilslib.httpdemo.HttpDemoActivity;
import it.wsh.cn.wshutilslib.ossdemo.OssDemoActivity;
import it.wsh.cn.wshutilslib.rxjavademo.RxjavaTestActivity;

public class MainActivity extends AppCompatActivity {

    private Button mShareBtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAction();
    }

    private void initAction() {
        mLoginBtn.setOnClickListener(mLoginListener);
        mShareBtn.setOnClickListener(mShareListener);
    }

    private void initView() {
        mShareBtn = (Button) findViewById(R.id.btn_share);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ARouter.getInstance().build(RouteUtils.LOGIN).navigation();
        }
    };

    private View.OnClickListener mShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ARouter.getInstance().build(RouteUtils.SHARE).navigation();
        }
    };

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void fragment(View view) {
        startActivity(new Intent(this, FragmentActivity.class));
    }

    /**
     * 跳 HttpDemoActivity
     *
     * @param view
     */
    public void httpTest(View view) {
        HttpDemoActivity.luanchActivity(this);
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void rxjavaTest(View view) {
        RxjavaTestActivity.luanchActivity(this);
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void ossTest(View view) {
        OssDemoActivity.luanchActivity(this);
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void dituTest(View view) {
        DituDemoActivity.luanchActivity(this);
    }

    /**
     * 跳 FragmentActivity
     *
     * @param view
     */
    public void httpAnnotationTest(View view) {
        HttpAnnotationDemoActivity.luanchActivity(this);
    }
}
