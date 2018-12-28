package it.wsh.cn.wshutilslib.config;

import it.wsh.cn.common_oss.OssConfig;

public class OssBusinessConfig {

    public static final String OSS_PREFIX_PRODUCT = "product_type";
    public static final String OSS_PREFIX_AVATAR = "avatar";

    // 开放平台后缀
    public static String PATH_OPEN_PLATFORM = "?type=open";

    //   开发联调环境
    public static final String STSTOKEN_SERVER_URl_DEV = "http://iot-sts-dev.evergrande.me:8880/";
    //  软件提测环境
    public static final String STSTOKEN_SERVER_URl_TEST = "http://beenet-oss-sts-tice.egtest.cn:9000/";
    //老板环境
    public static final String STSTOKEN_SERVER_URl_BOSS = "http://poc-sts.evergrande.me/";
    //硬件提测环境
    public static final String STSTOKEN_SERVER_URl_HARDWARE_TEST = "http://beenet-oss-sts-hardware.egtest.cn:9000/";
    //生产环境
    public static final String STSTOKEN_SERVER_URl_RELEASE = "http://poc-sts.evergrande.me/";
    //预发布环境
    public static final String STSTOKEN_SERVER_URl_YUFABU = "http://yfb-sts.xl.cn:28082/";
    //沙箱环境
    public static final String STSTOKEN_SERVER_URl_SANDBOX = "http://sanbox-sts.egtest.cn:7777/";
    //家居体验环境
    public static final String STSTOKEN_SERVER_URl_JIAJU = "http://jiaju-show-sts.xl.cn/";

    /**
     * 发布环境
     */
    public final static String ENV_RELEASE = "Release";
    /**
     * 老板环境
     */
    public final static String ENV_BOSS = "Boss";
    /**
     * 开发环境
     */
    public final static String ENV_DEV = "Dev";

    /**
     * 软件测试环境
     */
    public final static String ENV_SOFT_TEST = "SoftTest";
    /**
     * 硬件测试环境
     */
    public final static String ENV_HARD_TEST = "HardTest";
    /**
     * 预发布环境
     */
    public final static String ENV_YUFABU = "Yufabu";
    /**
     * 沙箱环境
     */
    public final static String ENV_SANDBOX = "Sandbox";
    /**
     * 家居体验环境
     */
    public static final String ENV_JIAJU = "Jiaju";

    private static String sStsTokenBaseUrl = STSTOKEN_SERVER_URl_DEV;
    private static String sPath = PATH_OPEN_PLATFORM;

    /**
     * 应用初始化时赋值
     *
     */
    public static void init(String env) {
        if (ENV_SOFT_TEST.equals(env)) {// 没有设置，默认也是开发环境
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_TEST;
        } else if (ENV_DEV.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_DEV;
        } else if (ENV_HARD_TEST.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_HARDWARE_TEST;
        } else if (ENV_BOSS.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_BOSS;
        } else if (ENV_RELEASE.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_RELEASE;
        } else if (ENV_YUFABU.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_YUFABU;
        } else if (ENV_SANDBOX.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_SANDBOX;
        } else if (ENV_JIAJU.equals(env)) {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_JIAJU;
        } else {
            sStsTokenBaseUrl = STSTOKEN_SERVER_URl_DEV;
        }

        OssConfig.addOssUrlPrefix(OSS_PREFIX_PRODUCT);
        OssConfig.addOssUrlPrefix(OSS_PREFIX_AVATAR);
    }

    public static void setPath(String suffix) {
        sPath = suffix;
    }

    public static String getPath() {
        return sPath;
    }

    public static String getStsTokenBaseUrl() {
        return sStsTokenBaseUrl;
    }
}
