package it.wsh.cn.hdpay.utils;

public class HdPayConstants {

    public static final String HD_PAY_RESULT_ACTION = "it.cn.hd.pay.HD_PAY_RESULT_ACTION";
    public static final String HD_PAY_RESULT_EXTRA_CODE = "it.cn.hd.pay.HD_PAY_RESULT_EXTRA_CODE";
    public static final String HD_PAY_RESULT_EXTRA_INFO = "it.cn.hd.pay.HD_PAY_RESULT_EXTRA_INFO";

    public static class HdPayResultStringCode {
        public static final String PASS_WORD_ERROR = "PASS_WORD_ERROR"; //密码错误
        public static final String INVALID_REQUEST = "INVALID_REQUEST";//参数错误
        public static final String NOAUTH = "NOAUTH";//商户无此接口权限
        public static final String NOTENOUGH = "NOTENOUGH";//余额不足
        public static final String ORDERPAID = "ORDERPAID";//商户订单已支付
        public static final String ORDERCLOSED = "ORDERCLOSED";//订单已关闭
        public static final String SYSTEMERROR = "SYSTEMERROR";//系统错误
        public static final String APPID_NOT_EXIST = "APPID_NOT_EXIST";//APPID不存在
        public static final String MCHID_NOT_EXIST = "MCHID_NOT_EXIST";//MCHID不存在
        public static final String APPID_MCHID_NOT_MATCH = "APPID_MCHID_NOT_MATCH";//appid和mch_id不匹配
        public static final String LACK_PARAMS = "LACK_PARAMS";//缺少参数
        public static final String OUT_TRADE_NO_USED = "OUT_TRADE_NO_USED";//商户订单号重复
        public static final String SIGNERROR = "SIGNERROR";//签名错误
        public static final String XML_FORMAT_ERROR = "XML_FORMAT_ERROR";//XML格式错误
        public static final String REQUIRE_POST_METHOD = "REQUIRE_POST_METHOD";//请使用post方法
        public static final String POST_DATA_EMPTY = "POST_DATA_EMPTY";//post数据为空
        public static final String NOT_UTF8 = "NOT_UTF8";//编码格式错误
        public static final String INVALID_TOKEN = "INVALID_TOKEN";//无效的访问令牌
        public static final String TOKEN_TIMEOUT = "TOKEN_TIMEOUT";//访问令牌已过期
        public static final String INVALID_AUTHORIZED = "INVALID_AUTHORIZED";//商户未授权当前接口
        public static final String NO_SIGN = "NO_SIGN";//缺少签名参数
        public static final String NO_SIGN_TYPE = "NO_SIGN_TYPE";//缺少签名类型参数
        public static final String NO_APPID = "NO_APPID";//缺少appId参数
        public static final String NO_TIMESTAMP = "NO_TIMESTAMP";//缺少时间戳参数
        public static final String NO_VERSION = "NO_VERSION";//缺少版本参数
        public static final String UNSUPPORT_SIGN = "UNSUPPORT_SIGN";//解密出错，不支持的加密算法
        public static final String NOT_SUPPORT_CGI = "NOT_SUPPORT_CGI";//本接口不支持第三方代理调用
    }

    public static class HdPayTradeState {
        public static final String SUCCESS = "SUCCESS"; //支付成功
        public static final String REFUND = "REFUND"; //转入退款
        public static final String NOTPAY = "NOTPAY"; //未支付
        public static final String CLOSED = "CLOSED"; //已关闭
        public static final String REVOKED = "REVOKED"; //已撤销（刷卡支付）
        public static final String USERPAYING = "USERPAYING"; //用户支付中
        public static final String PAYERROR = "PAYERROR"; //支付失败
    }
}
