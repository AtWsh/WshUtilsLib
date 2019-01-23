package it.wsh.cn.common_pay.nativepay.model;


import it.wsh.cn.common_pay.nativepay.ICreatePayObj;

/**
 * h5返回的微信支付参数信息
 */
public class WechatPayRes implements ICreatePayObj {
    private String partnerid;//微信支付分配的商户号
    private String prepayid;//微信返回的支付交易会话ID
    private String packageValue;//暂填写固定值Sign=WXPay
    private String noncestr;//随机字符串，不长于32位。推荐随机数生成算法
    private String timestamp;//时间戳，请见接口规则-参数规定
    private String sign;//签名，详见签名生成算法

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "WechatPayRes{" +
                "partnerid='" + partnerid + '\'' +
                ", prepayid='" + prepayid + '\'' +
                ", packageValue='" + packageValue + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}