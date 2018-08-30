package it.wsh.cn.wshutilslib.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import it.wsh.cn.componentbase.application.ApplicationConfig;
import it.wsh.cn.componentbase.application.BaseApplication;
import it.wsh.cn.wshlibrary.http.HttpClient;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:39
 * description:
 */
public class MianApplication extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        initModule(this);
        initModuleData(this);

        // 打印日志
        ARouter.openLog();
        // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.openDebug();
        ARouter.init(this);
        HttpClient.getInstance().init(this);
    }

    @Override
    public void initModule(Application application) {
        for (String s : ApplicationConfig.sApplicationPath) {
            try {
                Class<?> aClass = Class.forName(s);
                BaseApplication app = (BaseApplication)aClass.newInstance();
                app.initModule(this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initModuleData(Application application) {
        for (String s : ApplicationConfig.sApplicationPath) {
            try {
                Class<?> aClass = Class.forName(s);
                BaseApplication app = (BaseApplication)aClass.newInstance();
                app.initModuleData(this);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
