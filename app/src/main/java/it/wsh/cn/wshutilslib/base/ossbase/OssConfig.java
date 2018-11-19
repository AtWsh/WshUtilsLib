package it.wsh.cn.wshutilslib.base.ossbase;

/**
 * author: wenshenghui
 * created on: 2018/10/12 17:32
 * description:
 */
public class OssConfig {

    // 开放平台后缀
    public static String SUFFIX_OPEN_PLATFORM = "?type=open";

    //   开发联调环境
    public static final String STSTOKEN_SERVER_URl_DEV = "http://iot-sts-dev.evergrande.me:8880/";
    //  软件提测环境
    public static final String STSTOKEN_SERVER_URl_TEST = "http://beenet-oss-sts-tice.egtest.cn:9000/";
    //老板环境
    public static final String STSTOKEN_SERVER_URl_BOSS = "http://poc-sts.evergrande.me/";
    //硬件提测环境
    public static final String STSTOKEN_SERVER_URl_HARDWARE_TEST = "http://beenet-oss-sts-hardware.egtest.cn:9000/";
    //预发布环境
    public static final String STSTOKEN_SERVER_URl_YUFABU = "http://poc-sts.evergrande.me/";
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
    private static String sStsTokenUrl = STSTOKEN_SERVER_URl_DEV;
    private static String sSuffix = "?type=open";

    /**
     * 应用初始化时赋值
     * @param env
     */
    public static void init(String env) {
        if (ENV_SOFT_TEST.equals(env)) {// 没有设置，默认也是开发环境
            sStsTokenUrl = STSTOKEN_SERVER_URl_TEST;
        } else if (ENV_DEV.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_DEV;
        } else if (ENV_HARD_TEST.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_HARDWARE_TEST;
        } else if (ENV_BOSS.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_BOSS;
        } else if (ENV_YUFABU.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_YUFABU;
        } else if (ENV_SANDBOX.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_SANDBOX;
        } else if (ENV_JIAJU.equals(env)) {
            sStsTokenUrl = STSTOKEN_SERVER_URl_JIAJU;
        } else {
            sStsTokenUrl = STSTOKEN_SERVER_URl_DEV;
        }
    }

    public static void setSuffix(String sSuffix) {
        OssConfig.sSuffix = sSuffix;
    }

    public static String getSuffix() {
        return sSuffix;
    }

    public static String getStsTokenUrl() {
        return sStsTokenUrl;
    }

}
