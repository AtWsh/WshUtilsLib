package it.wsh.cn.componentbase.application;

import android.app.Application;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:34
 * description:
 */
public abstract class BaseApplication extends Application {


    public abstract void initModule(Application application);

    public abstract void initModuleData(Application application);
}
