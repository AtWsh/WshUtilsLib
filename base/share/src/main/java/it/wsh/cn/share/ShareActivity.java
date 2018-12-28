package it.wsh.cn.share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.alibaba.android.arouter.facade.annotation.Route;

import it.wsh.cn.common_utils.utils.RouteUtils;
import it.wsh.cn.componentbase.ServiceFactory;
import it.wsh.cn.componentbase.services.IAccountService;

@Route(path = RouteUtils.SHARE)
public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        share();
    }

    private void share() {
        IAccountService accountService = ServiceFactory.getInstance().getAccountService();
        String accountId = accountService.getAccountId();
        boolean login = accountService.isLogin();
        Toast.makeText(this,"isLogin = " + login + "; accountId = " + accountId ,Toast.LENGTH_LONG).show();
    }
}
