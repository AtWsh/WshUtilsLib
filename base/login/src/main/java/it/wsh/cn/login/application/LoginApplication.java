package it.wsh.cn.login.application;

import android.app.Application;

import it.wsh.cn.componentbase.ServiceFactory;
import it.wsh.cn.componentbase.application.BaseApplication;
import it.wsh.cn.login.services.AccountService;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:36
 * description:
 */
public class LoginApplication extends BaseApplication {
    @Override
    public void initModule(Application application) {
        ServiceFactory.getInstance().setAccountService(new AccountService());
    }

    @Override
    public void initModuleData(Application application) {

    }
}
