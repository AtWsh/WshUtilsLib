package it.wsh.cn.wshutilslib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;

import it.wsh.cn.wshlibrary.utils.RouteUtils;

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
            ;
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
}
