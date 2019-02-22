package it.wsh.cn.common_pay.pay;

import it.wsh.cn.hdpay.HdPayResultCode;

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

    // 恒大结果码
    public static final int HD_PAY_REQUEST_PAY_SUCCESS = HdPayResultCode.HD_PAY_REQUEST_PAY_SUCCESS;//200
    public static final int HD_PAY_REQUEST_INFO_ERR = HdPayResultCode.HD_PAY_REQUEST_PAYREQ_NULL_ERR;//100001
    public static final int HD_PAY_REQUEST_PAYRSP_ERR = HdPayResultCode.HD_PAY_REQUEST_PAYRSP_ERR;//100002
    public static final int HD_PAY_REQUEST_PAY_CONTEXT_ERR = HdPayResultCode.HD_PAY_REQUEST_PAY_CONTEXT_ERR;//100003
    public static final int PASS_WORD_ERROR_CODE = HdPayResultCode.PASS_WORD_ERROR_CODE; //密码错误，收到这个错误表示已经错误3次了，撤单，交易失败
    public static final int INVALID_REQUEST_CODE = HdPayResultCode.INVALID_REQUEST_CODE;//参数错误
    public static final int NOAUTH_CODE = HdPayResultCode.NOAUTH_CODE;//商户无此接口权限
    public static final int NOTENOUGH_CODE = HdPayResultCode.NOTENOUGH_CODE;//余额不足
    public static final int ORDERPAID_CODE = HdPayResultCode.ORDERPAID_CODE;//商户订单已支付
    public static final int ORDERCLOSED_CODE = HdPayResultCode.ORDERCLOSED_CODE;//订单已关闭
    public static final int SYSTEMERROR_CODE = HdPayResultCode.SYSTEMERROR_CODE;//系统错误
    public static final int APPID_NOT_EXIST_CODE = HdPayResultCode.APPID_NOT_EXIST_CODE;//APPID不存在
    public static final int MCHID_NOT_EXIST_CODE = HdPayResultCode.MCHID_NOT_EXIST_CODE;//MCHID不存在
    public static final int APPID_MCHID_NOT_MATCH_CODE = HdPayResultCode.APPID_MCHID_NOT_MATCH_CODE;//appid和mch_id不匹配
    public static final int LACK_PARAMS_CODE = HdPayResultCode.LACK_PARAMS_CODE;//缺少参数
    public static final int OUT_TRADE_NO_USED_CODE = HdPayResultCode.OUT_TRADE_NO_USED_CODE;//商户订单号重复
    public static final int SIGNERROR_CODE = HdPayResultCode.SIGNERROR_CODE;//签名错误
    public static final int XML_FORMAT_ERROR_CODE = HdPayResultCode.XML_FORMAT_ERROR_CODE;//XML格式错误
    public static final int REQUIRE_POST_METHOD_CODE = HdPayResultCode.REQUIRE_POST_METHOD_CODE;//请使用post方法
    public static final int POST_DATA_EMPTY_CODE = HdPayResultCode.POST_DATA_EMPTY_CODE;//post数据为空
    public static final int NOT_UTF8_CODE = HdPayResultCode.NOT_UTF8_CODE;//编码格式错误
    public static final int INVALID_TOKEN_CODE = HdPayResultCode.INVALID_TOKEN_CODE;//无效的访问令牌
    public static final int TOKEN_TIMEOUT_CODE = HdPayResultCode.TOKEN_TIMEOUT_CODE;//访问令牌已过期
    public static final int INVALID_AUTHORIZED_CODE = HdPayResultCode.INVALID_AUTHORIZED_CODE;//商户未授权当前接口
    public static final int NO_SIGN_CODE = HdPayResultCode.NO_SIGN_CODE;//缺少签名参数
    public static final int NO_SIGN_TYPE_CODE = HdPayResultCode.NO_SIGN_TYPE_CODE;//缺少签名类型参数
    public static final int NO_APPID_CODE = HdPayResultCode.NO_APPID_CODE;//缺少appId参数
    public static final int NO_TIMESTAMP_CODE = HdPayResultCode.NO_TIMESTAMP_CODE;//缺少时间戳参数
    public static final int NO_VERSION_CODE = HdPayResultCode.NO_VERSION_CODE;//缺少版本参数
    public static final int UNSUPPORT_SIGN_CODE = HdPayResultCode.UNSUPPORT_SIGN_CODE;//解密出错，不支持的加密算法
    public static final int NOT_SUPPORT_CGI_CODE = HdPayResultCode.NOT_SUPPORT_CGI_CODE;//本接口不支持第三方代理调用
}
