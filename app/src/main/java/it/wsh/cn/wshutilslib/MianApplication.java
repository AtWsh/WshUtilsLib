package it.wsh.cn.wshutilslib;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.leakcanary.LeakCanary;

import it.wsh.cn.common_http.http.HttpManager;
import it.wsh.cn.common_http.http.database.utils.GreenDaoDatabase;
import it.wsh.cn.componentbase.application.ApplicationConfig;
import it.wsh.cn.componentbase.application.BaseApplication;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:39
 * description:
 */
public class MianApplication extends BaseApplication{

    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initModule(this);
        initModuleData(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        // 打印日志
        ARouter.openLog();
        // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.openDebug();
        ARouter.init(this);

        HttpManager.init(this);

        GreenDaoDatabase.getInstance().initDatabase(this);
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
