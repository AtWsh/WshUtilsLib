package it.wsh.cn.wshutilslib;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.squareup.leakcanary.LeakCanary;

import it.wsh.cn.common_http.http.HttpManager;
import it.wsh.cn.common_imageloader.GlideConfig;
import it.wsh.cn.common_oss.OssManager;
import it.wsh.cn.common_oss.glidemodel.AliyunOSSModelLoaderFactory;
import it.wsh.cn.common_pay.pay.PayConfig;
import it.wsh.cn.componentbase.application.ApplicationConfig;
import it.wsh.cn.componentbase.application.BaseApplication;
import it.wsh.cn.wshutilslib.config.OssBusinessConfig;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:39
 * description:
 */
public class MianApplication extends BaseApplication {

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

        //OSS环境初始化
        OssBusinessConfig.init(BuildConfig.ENV);
        OssManager.init(this, OssBusinessConfig.getStsTokenBaseUrl(), OssBusinessConfig.getPath());

        //图片库初始化,(让Glide支持Oss图片下载)
        GlideConfig.addModelLoaderFactory(new AliyunOSSModelLoaderFactory());

        HttpManager.init(this);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //支付初始化
        PayConfig.init(this, "");
    }

    @Override
    public void initModule(Application application) {
        for (String s : ApplicationConfig.sApplicationPath) {
            try {
                Class<?> aClass = Class.forName(s);
                BaseApplication app = (BaseApplication) aClass.newInstance();
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
                BaseApplication app = (BaseApplication) aClass.newInstance();
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
