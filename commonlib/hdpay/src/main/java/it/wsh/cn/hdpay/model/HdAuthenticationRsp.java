package it.wsh.cn.hdpay.model;

import it.wsh.cn.common_http.http.HttpMethod;

public class HdAuthenticationRsp {

       /* "token" : //String，交易秘钥
        "need_token" : //boolean，是否需要鉴权
        "prepay_id" : //String，商户订单号（预支付交易会话ID）
        "trade_state" : //String，交易状态
        "wallet_id" : //String，钱包id
        "currency" : //String，币种CNY/USD
        "subject" : //String，商品描述
        "goods_detail" : //String，商品详情
        "out_trade_no" : //String，商户订单号
        "total_amount" : //String，订单总金额
        "amount" : //String，订单金额
        "discount_amount" : //String，优惠金额
        "time_expire" : //long，交易结束时间
        "pay_id" : //String，支付订单号*/

    public String token;
    public boolean need_token;
    public String prepay_id;
    public String trade_state;
    public String wallet_id;
    public String currency;
    public String subject;
    public String goods_detail;
    public String out_trade_no;
    public String amount;
    public String discount_amount;
    public long time_expire;
    public String pay_id;

    public static class Builder extends HdPayBaseRsp.BasePayBuilder<HdAuthenticationRsp> {
        public Builder() {
            super();
        }

        @Override
        protected String getPath() {
            return "hdpay/authentication";
        }

        @Override
        protected String getBaseUrl() {
            return "http://172.26.87.67:8887/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }


}
