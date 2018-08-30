package it.wsh.cn.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import it.wsh.cn.wshlibrary.utils.RouteUtils;

@Route(path = RouteUtils.LOGIN)
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_login);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }
}
