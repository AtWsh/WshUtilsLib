package it.wsh.cn.hdpay;

public class HdPayResultCode {

    //支付成功
    public static final int HD_PAY_REQUEST_PAY_SUCCESS = 200;
    //支付信息为空错误
    public static final int HD_PAY_REQUEST_PAYREQ_NULL_ERR = 10001;
    //支付服务器信息错误，返回为空
    public static final int HD_PAY_REQUEST_PAYRSP_ERR = 10002;
    //Context错误，为空
    public static final int HD_PAY_REQUEST_PAY_CONTEXT_ERR = 10003;

    public static final int PASS_WORD_ERROR_CODE = 10004; //密码错误，收到这个错误表示已经错误3次了，撤单，交易失败
    public static final int INVALID_REQUEST_CODE = 10005;//参数错误
    public static final int NOAUTH_CODE = 10006;//商户无此接口权限
    public static final int NOTENOUGH_CODE = 10007;//余额不足
    public static final int ORDERPAID_CODE = 10008;//商户订单已支付
    public static final int ORDERCLOSED_CODE = 10009;//订单已关闭
    public static final int SYSTEMERROR_CODE = 10010;//系统错误
    public static final int APPID_NOT_EXIST_CODE = 10011;//APPID不存在
    public static final int MCHID_NOT_EXIST_CODE = 10012;//MCHID不存在
    public static final int APPID_MCHID_NOT_MATCH_CODE = 10013;//appid和mch_id不匹配
    public static final int LACK_PARAMS_CODE = 10014;//缺少参数
    public static final int OUT_TRADE_NO_USED_CODE = 10015;//商户订单号重复
    public static final int SIGNERROR_CODE = 10016;//签名错误
    public static final int XML_FORMAT_ERROR_CODE = 10017;//XML格式错误
    public static final int REQUIRE_POST_METHOD_CODE = 10018;//请使用post方法
    public static final int POST_DATA_EMPTY_CODE = 10019;//post数据为空
    public static final int NOT_UTF8_CODE = 10020;//编码格式错误
    public static final int INVALID_TOKEN_CODE = 10021;//无效的访问令牌
    public static final int TOKEN_TIMEOUT_CODE = 10022;//访问令牌已过期
    public static final int INVALID_AUTHORIZED_CODE = 10023;//商户未授权当前接口
    public static final int NO_SIGN_CODE = 10024;//缺少签名参数
    public static final int NO_SIGN_TYPE_CODE = 10025;//缺少签名类型参数
    public static final int NO_APPID_CODE = 10026;//缺少appId参数
    public static final int NO_TIMESTAMP_CODE = 10027;//缺少时间戳参数
    public static final int NO_VERSION_CODE = 10028;//缺少版本参数
    public static final int UNSUPPORT_SIGN_CODE = 10029;//解密出错，不支持的加密算法
    public static final int NOT_SUPPORT_CGI_CODE = 10030;//本接口不支持第三方代理调用

    public static final int USER_CANCEL_PAY_CODE = 10031;//用户主动发起撤单

    public static final int UNKNOW = 10100;//未知错误
}
