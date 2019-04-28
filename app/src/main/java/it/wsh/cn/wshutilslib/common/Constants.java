package it.wsh.cn.wshutilslib.common;

/**
 * Created by wangyuhang@evergrande.cn on 2017-11-14.
 */

public class Constants {
    /**
     * 恒大路由器是否设置过
     */
    public static final int ROUTER_SETUP_DONE = 1;
    public static final int ROUTER_NOT_SETUP = 0;
    /******产品属性关键字 start********/
    public static String  PROP_IS_CONFIG_DEVICE = "is_config_device";


    /******产品属性关键字 end********/

    public static final String KEY_BELONG_ROOM = "key_belong_room";
    //所属房间
    public static final int BELONG_ROOM = 1;

    public static boolean DEBUG = true;

    public static final String  SP_FILE_PRODUCT_ATTRS="product_attrs";
    public static final String  ATTR_COLOR="rgb";
    public static final String ATTR_LEVEL = "level";
    public static final int STATE_NO_LOCAL_VALUE = 0;//本地没有存属性，需要联网获取
    public static final int STATE_NO_COLOR_ATTR = 1;//没有颜色
    public static final int STATE_HAS_COLOR_ATTR = 2;//有颜色
    public static final String EVERGRANDE_SERVER = "https://qrcode.xl.cn/download.php";
    public static final String BLANK_DEVICE_FLAG = "aa:bb:cc:xx:yy:zz";
    /**
     * 头像上传服务器地址
     * */
    public static final String AVATAR_UPLOAD_SERVER = "http://120.76.199.208/upload";
    public static boolean DEMO = false;
    // 对接开放平台开关
    public static boolean IS_OPEN_PLATFORM = true;
    // 开放平台添加设备功能。0：关闭；1：开启；
    public static int DEVICE_ADD_TYPE = 1;
    // 开放平台是否添加设备时选择型号。0：不选择；1：选择；
    public static int DEVICE_SELECT_PRODUCT = 1;
    // 开放平台是否添加设备时选择渠道商。0：不选择；1：选择；
    public static int DEVICE_SELECT_BRAND = 1;
    // 开放平台后缀
    public static String SUFFIX_OPEN_PLATFORM = "?type=open";
    // 星络渠道产品
    public static String BRAND_EVERGRANDE = "EVERGRANDE";
    // 苏宁渠道产品
    public static String BRAND_SUNING = "SUNING";

    public static boolean ENABLE_WATERMARK = true;

    public static String OUTPUT_TYPE = "";
    public static String ENV = "";
    public static String APPLICATION_ID = "";


    public static final int MEMBER_STATE_AGREE = 1;
    public static final int MEMBER_STATE_REFUSE = 2;
    public static final int MEMBER_STATE_APPLY = 0;
    public static final String KB = "KB";
    public static final String MB = "MB";
    public static final String GB = "GB";

    // 总开关
    public static final int TOTAL_CONTROL_ID = -1;
    // 全屋灯
    public static final int HOUSE_LIGHT_ID = -2;

    // 设备开启/关闭模式
    public static final String MODE_ON = "on";
    public static final String MODE_OFF = "off";
    public static final String MODE_TOGGLE = "toggle";

    // 自动化测试标志位。为true时，部分控件的需设置状态到控件描述，用于自动化测试
    public static final boolean AUTO_TEST = true;

    // 头像名称
    public static final String IMAGE_FILE = "_head.p";
    public static final String QRCODE_FILE = "qrcode.jpg";
    // 头像临时目录
    public static final String imgCacheTempDir = StorageUtils.getAPPCachePath()+"temp/";
    public static final String imgBaseDir = StorageUtils.getAPPStoragePath("img/");


    public static final int USER_NAME_LENGTH = 12;
    public static final int ROOM_NAME_LENGTH = 12;
    public static final int FAMILY_NAME_LENGTH = 12;

    // 为保证notify只有一个时，使用同一个notify id
    // 下载APP的notify id
    public static final int NOTIFY_ID_DOWNLOAD_APP = 1001;

    // 每次请求的设备数量
    public static final int REQUEST_DEVICE_SIZE = 20;
    // 每次请求的设备品类数量
    public static final int REQUEST_DEVICE_TYPE_SIZE = 10;

    // jsbridge配置
    public static final String PROTOCOL = "HdIot";
    public static final String READY_FUNCNAME = "onDeviceJsReady";

    public static final String ACTIVITY_TITLE = "activity_title";
    public static final String ACTIVITY_TEXT = "activity_text";
    public static final String ACTIVITY_TIP = "activity_tip";
    public static final String DEVICE_TV = "device_tv";
    public static final String DEVICE_TV_IP = "device_tv_ip";
    public static final String DEVICE_TV_NAME = "device_tv_name";
    public static final String DEVICE_TV_SSID = "device_tv_ssid";
    public static final String DEVICE_HAS_ADD_TV_ROOMS = "device_has_add_tv_rooms"; //已经添加过电视的房间
    public static final String DEVICE_LIGHT = "device_light";
    public static final String TAG = "mTag";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_PWD = "password";
    public static final String USER_INFO = "user_info";
    public static final String ROOM_INFO = "room_info";
    public static final String ROOM_ACTION = "room_action";
    public static final String ROOM_DEVICE = "room_device";
    public static final String ROOM_DEVICE_LIST = "room_device_list";
    public static final String SWITCH_CHANNEL_NAME = "switch_key";
    public static final String SWITCH_CHANNEL = "switch_channel";
    public static final String SWITCH_CHANNEL_INDEX = "switch_channel_index";
    public static final String SWITCH_ATTR_BEAN = "switch_attr_bean";
    public static final String ROOM_ID = "room_id";
    public static final String ROOM_NAME = "room_name";
    public static final String DEVICE_COUNT = "device_count";
    public static final String FAILED_TITLE = "failed_title";
    public static final String FAILED_TIPS = "failed_tips";
    public static final String ROUTER_CONNECTER = "router_connecter";
    public static final String KEY_ROOM_DEVICE = "key_room_device";
    public static final String NOTICE_TITLE = "notice_title";
    public static final String NOTICE_DES = "notice_des";
    public static final String DEVICE_VERSION_INFO = "device_version_info";
    public static final String KEY_EXTEND = "key_extend";
    public static final String KEY_IS_DELETED = "key_is_deleted";
    public static final String CAN_DELETE = "can_delete";
    public static final String KEY_TEXT_LENGTH = "key_text_length";
    public static final String KEY_TRANSFER_ROOM = "key_transfer_room";
    public static final String KEY_MODIFY_MONITOR_MODE = "key_modify_monitor_mode";
    public static final String TRANSFER_ROOM = "transfer_room";
    public static final String KEY_DEVICE_CATEGORY = "device_category";
    public static final String KEY_ADD_DEVICE = "key_add_device";
    public static final String DEVICE_DEVICE = "delete_device";
    public static final String DEVICE_TYPE = "device_type";
    public static final String FAMILY_MEMBER = "family_member";
    public static final String FAMILY_ID = "family_id";
    public static final String USER_APPLY = "user_apply";
    public static final String INTERNET_MODE = "internet_mode";
    public static final String DEFAULT_INTERNET_MODE = "default_internet_mode";
    public static final String INTERNET_STATUS = "internet_status";
    public static final String KEY_ROUTER_INPUT_PWD_ACTION_TYPE = "router_input_pwd_type_manage";
    public static final String KEY_ROUTER_PASSWORD_ACTION = "router_password_action";
    public static final String ROUTER_PASSWORD_ACTION_INPUT = "router_password_action_input";
    public static final String ROUTER_PASSWORD_ACTION_SETUP= "router_password_action_setup";
    public static final String ROUTER_PASSWORD_ACTION_VERIFY = "router_password_action_verify";
    public static final String KEY_WIFI_SETUPED= "key_wifi_setuped";
    public static final String KEY_CONFIRM = "key_confirm";
    public static final String KEY_REFRESH_DATA = "key_refresh_DATA";
    public static final String KEY_ERROR_CODE = "key_error_code";
    public static final String KEY_JOIN_FAMILY_TIME = "key_join_family_time";
    public static final String KEY_DEVICE_FIRMWARE_UPDATE_ACTION = "device_firmware_update_action";
    public static final String DEVICE_FIRMWARE_UPDATE = "device_firmware_update";
    public static final String INTERNET_MODE_BROADBAND = "pppoe";
    public static final String INTERNET_MODE_DYNAMIC_IP = "dhcp";
    public static final String INTERNET_MODE_STATIC_IP = "static";
    public static final String INTERNET_MODE_WITHOUT = "without";//未知模式

    public static final String NOT_LINK = "not link";
    public static final String KEY_WIFI_NAME = "wifi_name";
    public static final String KEY_LOGIN= "key_logined";
    public static final String KEY_SIZE = "key_size";
    public static final String KEY_DATA_LIST = "key_data_list";
    public static final String KEY_DEVICE_SHORTCUT = "key_device_shortcut";
    public static final String KEY_RULER_INFO = "key_ruler_info";
    public static final String KEY_DEVICE_UUID = "device_uuid";
    public static final String KEY_NEW_JOIN_FAMILY = "key_new_join_family";

    public static final String KEY_PARCELABLE_ = "key_parceable_";
    public static final String KEY_DRAWABLE_TIPS1 = "key_drawable_tips1";
    public static final String KEY_TEXT_TIPS1 = "key_text_tips1";
    public static final String KEY_DRAWABLE_TIPS2 = "key_drawable_tips2";
    public static final String KEY_TEXT_TIPS2 = "key_text_tips2";
    public static final String KEY_DRAWABLE_FAILED = "key_drawable_failed";
    public static final String KEY_TEXT_FAILED = "key_text_failed";
    public static final String KEY_ADD_DEVICE_TYPE = "key_add_device_type";
    public static final String KEY_PROTO = "key_proto";
    public static final String KEY_QRCODE_BRAND = "key_qrcode_brand";

    public static final String KEY_VOICE_PLATFORM = "key_voice_platform";
    public static final String KEY_ROUTER_DEVICE_UUID = "key_device_uuid";

    public static final String KEY_DEVICE_NAME = "key_device_name";
    public static final String KEY_SHORTCUT_PROTOCOL = "key_shortcut_protocol";
    public static final String KEY_CATEGORY_ID = "key_category_Id";
    public static final String SWITCH_CHAN_INFO = "key_switch_chan_info";
    public static final String KEY_IS_WIRELESS = "key_is_wireless";
    public static final String KEY_TERMINAL_ITEM = "key_terminal_item";
    public static final String KEY_KICKEOFF_EVENT = "key_kickoff_event";
    public static final String KEY_KICKEOFF_TYPE = "key_kickoff_type";
    public static final String KEY_FIND_PWD_TYPE = "key_find_pwd_type";


    public static final String KEY_RV_LOCK_POS = "key_rv_lock_position";

    // 亮度范围1-254，分为4档，增量为64，对应的百分比值为25、50、75、100
    public static final int LIGHT_CONTROL_LIGHT_LEVEL_1 = 63;
    public static final int LIGHT_CONTROL_LIGHT_LEVEL_2 = 127;
    public static final int LIGHT_CONTROL_LIGHT_LEVEL_3 = 191;
    public static final int LIGHT_CONTROL_LIGHT_LEVEL_4 = 254;
    public static final int LIGHT_CONTROL_LIGHT_LEVEL_INCREMENTAL = 64;

    public static final int LIGHT_CONTROL_LIGHT_PERCENTAGE_25 = 25;
    public static final int LIGHT_CONTROL_LIGHT_PERCENTAGE_50 = 50;
    public static final int LIGHT_CONTROL_LIGHT_PERCENTAGE_75 = 75;
    public static final int LIGHT_CONTROL_LIGHT_PERCENTAGE_100 = 100;

    // 空调温度范围16 ~ 30，增量为10
    public static final int AIRCONDITIONER_TEMPERATURE_INCREMENTAL = 10;
    public static final int AIRCONDITIONER_TEMPERATURE_MIN = 16 * AIRCONDITIONER_TEMPERATURE_INCREMENTAL;
    public static final int AIRCONDITIONER_TEMPERATURE_MAX = 30 * AIRCONDITIONER_TEMPERATURE_INCREMENTAL;

    // 空调风速 低、中、高、自动，对应的数值是1、2、3、4
    public static final String AIRCONDITIONER_SPEED_LOW = "low";
    public static final String AIRCONDITIONER_SPEED_NORMAL = "normal";
    public static final String AIRCONDITIONER_SPEED_HIGH = "high";
    public static final String AIRCONDITIONER_SPEED_AUTO = "auto";

    public static final String AIRCONDITIONER_SPEED_LOW_DES = "低";
    public static final String AIRCONDITIONER_SPEED_NORMAL_DES = "中";
    public static final String AIRCONDITIONER_SPEED_HIGH_DES = "高";
    public static final String AIRCONDITIONER_SPEED_AUTO_DES = "自动";
    // 空调模式 制冷
    public static final String AIRCONDITIONER_MODE_COLD = "cold";

    // 设备的在线、离线状态
    public static final String DEVICE_ONLINE = "online";
    public static final String DEVICE_OFFLINE = "offline";
    public static final String ROUTER_OFFLINE = "router_offline";
    public static final String NETWORK_OFFLINE = "network_offline";
    public static final String NONE_FAMILY_WIFI = "none_family_wifi";
    public static final String DEFAULT = "default";



    public static final String STATUS_LOGIN = "login";
    public static final String STATUS_LOGOUT = "logout";
    public static final String STATUS_KICKOFF = "kickoff";
    public static final String STATUS_OFFLINE = "offline";

    public static final String ROUTER_IS_UNCONFIGED = "router_is_unconfiged";
    public static final String ROUTER_NETWORK_ERROR = "router_network_error";
    public static final String ROUTER_CABLE_DISCONNECTED = "router_cable_disconnected";
    //客户端类型
    public static final String CLIENT_TYPE_ROUTER = "Router";
    public static final String CLIENT_TYPE_APP = "APP";

    //二维码类型
    public static final String QRCODE_TYPE_FAMILY = "qrcode_family";//家庭二维码
    public static final String QRCODE_TYPE_H5_PLUGIN = "qrcode_h5_plugin";//h5包二维码
    public static final String QRCODE_TYPE_ROUTER = "qrcode_router";//路由器二维码
    public static final String QRCODE_TYPE_UNKNOWN = "unknown";//无效二维码
    //家庭成员角色类型
    public static final int ADMINISTRATOR = 0;//管理员，户主的身份


    public static final String FEEDBACK_SCENE="feedback_scene";
    public static final String FEEDBACK_URL="feedback_url";
    public static final String MULTI_PIC_NUM="multi_pic_num";
    public static final String MULTI_PIC_RESULT="multi_pic_result";

    //路由器在线离线状态
    public static final int BANNER_TYPE_NO_NETWORK = 1;
    public static final int BANNER_TYPE_DEFAULT = 2;
    public static final int BANNER_TYPE_ROUTER_DIRECT_ONLINE = 3;//路由器直连在线
    public static final int BANNER_TYPE_ROUTER_REMOTE_ONLINE = 4;//路由器远程在线
    public static final int BANNER_TYPE_ROUTER_OFFLINE = 5;//路由器离线
    public static final int BANNER_TYPE_DEVICE_OFFLINE = 6;//设备离线
    public static final int BANNER_TYPE_LOGOUT = 7;//App未登录
    public static final int BANNER_TYPE_CLOUD_LOGIN = 8;//App云端登录

    //第一次登录或者切换家庭时，标识要跳转的界面
    public static final int PAGE_TYPE_FAMILY_NO_ROUTER_HOLDER = 1;
    public static final int PAGE_TYPE_FAMILY_NO_ROUTER_MEMBER = 2;
    public static final int PAGE_TYPE_FAMILY_BOUND_ROUTER= 3;
    public static final int PAGE_TYPE_FAMILY_WELCOME_CREATE_FAMILY = 4;

    // **毫秒间隔内再次收到点击事件，则认为是连击，以最后的值为结果
    public static final int MESSAGE_DELAY_TIME = 400;
    public static final int MSG_ATTR_MINUS = 0;
    public static final int MSG_ATTR_PLUS = 1;

    // 添加灯设备状态
    public static final int READY_TO_ADD_DEVICE_STATUS = 0;
    // 正在添加灯设备状态
    public static final int ADDING_DEVICE_STATUS = 1;

    // 升级
    public static final int NEED_UPGRADE = 1;

    //版本更新状态
    public static final int VERSION_STATE_UNKNOWN = 0;//初始值
    public static final int VERSION_STATE_UPDATING = 1;//更新中
    public static final int VERSION_STATE_UPDATE_DONE = 2;//更新完成
    public static final int VERSION_STATE_UPDATE_FAIL = 3;//更新失败
    public static final int VERSION_STATE_TOBE_UPDATE = 4;//等待更新
    public static final int VERSION_STATE_DEVICE_OFFLINE = 5;//设备离线
    public static final int VERSION_STATE_DEVICE_DELETED = 6;//设备被移除
    public static final int VERSION_STATE_TOBE_INSTALL = 10;//待安装

    //天气相关，天气数据保存到sp中；
    public static final  String  WEATHER_FILE_NAME="weather_data";
    public static final  String  WEATHER_SP_KEY="weather";

    // 重命名设备
    public static final int RENAME_DEVICE = 4;
    // 转移设备
    public static final int TRANSFER_DEVICE = 1;
    // 修改摄像头监控模式
    public static final int MODIFY_MONITOR_MODE = 2;
    //判断从设备页面进入设置页是否刷新界面
    public static final int MODIFY_DEVICE = 3;
    //踢出的类型
    public static final int KICKEROFF_TYPE_WHEN_ONLINE = 1; //在线的时候被踢出
    public static final int KICKEROFF_TYPE_WHEN_LOGIN = 2; //登录的时候发现被踢出
    //找回密码类型
    public static final int FIND_PWD_TYPE_FORGET = 1; //找回密码-因为忘记密码
    public static final int FIND_PWD_TYPE_LEAKAGE= 2; //找回密码-因为账号泄露


    public static final String SCENE1_CRASH="闪退或卡顿";
    public static final  String SCENE2_VIEW="界面错位";
    public static final String SCENE3_DEVICE="设备";
    public static final String SCENE4_OTHERS="其他";
    public static final String SCENE5_ROUTER="路由器";

    //h5帮助反馈url dev
    public final static String HELP_FEEDBACK_PAD_H5_URL =
            "http://zsj-smarthome.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/help_feedback/Index.html?nav=0";
    public final static String FEEDBACK_H5_URL =
            "http://zsj-smarthome.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/user_feedback/index.html?title=%E8%AE%BE%E5%A4%87%E9%97%AE%E9%A2%98";
    public final static String FEEDBACK_ROUTER_H5_URL =
            "http://zsj-smarthome.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/user_feedback/index.html?title=%E8%B7%AF%E7%94%B1%E5%99%A8%E9%97%AE%E9%A2%98";
    public final static String  HELP_FEEDBACK_PHONE_H5_URL =
            "http://zsj-smarthome.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/help_feedback/android/Index.html?nav=0";

    //h5帮助反馈url softtest
    public final static String HELP_FEEDBACK_PAD_H5_URL_SOFT_TEST =
            "http://zsj-smarthome-test.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/help_feedback/Index.html?nav=0";
    public final static String FEEDBACK_H5_URL_SOFT_TEST =
            "http://zsj-smarthome-test.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/user_feedback/index.html?title=%E8%AE%BE%E5%A4%87%E9%97%AE%E9%A2%98";
    public final static String FEEDBACK_ROUTER_H5_URL_SOFT_TEST =
            "http://zsj-smarthome-test.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/user_feedback/index.html?title=%E8%B7%AF%E7%94%B1%E5%99%A8%E9%97%AE%E9%A2%98";
    public final static String HELP_FEEDBACK_PHONE_H5_URL_SOFT_TEST =
            "http://zsj-smarthome-test.oss-cn-shenzhen.aliyuncs.com/scattered-pages-in-app/dist/help/help_feedback/android/Index.html?nav=0";
}
