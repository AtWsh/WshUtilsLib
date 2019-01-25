package it.wsh.cn.common_pay.pay;

public class PayResultCode {

    // 通用结果码
    public static final int COMMON_PAY_OK = 0;
    public static final int COMMON_PAY_ERR = -1;
    public static final int COMMON_USER_CACELED_ERR = -2;
    public static final int COMMON_NETWORK_NOT_AVAILABLE_ERR = 1;
    public static final int COMMON_REQUEST_TIME_OUT_ERR = 2;
    public static final int COMMON_REQUEST_PAYWAY_ERR = 3;
    public static final int COMMON_REQUEST_PAYDATA_ERR = 4;

    // 微信结果码
    public static final int WECHAT_SENT_FAILED_ERR = -3;
    public static final int WECHAT_AUTH_DENIED_ERR = -4;
    public static final int WECHAT_UNSUPPORT_ERR = -5;
    public static final int WECHAT_BAN_ERR = -6;
    public static final int WECHAT_NOT_INSTALLED_ERR = -7;
    public static final int WECHAT_GSON_TO_OBJECT_ERR = -8;
    public static final int WECHAT_WXAPPID_EMPTY_ERR = -9; //wxAPPID为空

    // 支付宝结果码
    public static final int ALI_PAY_WAIT_CONFIRM_ERR = 8000;
    public static final int ALI_PAY_NET_ERR = 6002;
    public static final int ALI_PAY_UNKNOW_ERR = 6004;
    public static final int ALI_PAY_OTHER_ERR = 6005;

    public static final int ALI_PAY_GSON_TO_OBJ_ERROR = 9000;
}
