package it.wsh.cn.hdpay.model;

import it.wsh.cn.common_http.http.HttpMethod;

public class HdQrcodePayRsp extends HdPayBaseRsp {

    public String token;
    public boolean need_token;
    public String prepay_id;
    public String trade_state;
    public String wallet_id;
    public String currency;
    public String subject;
    public String goods_detail;
    public String out_trade_no;
    public String total_amount;
    public String amount;
    public String discount_amount;
    public long time_expire;
    public String pay_id;

    public static class Builder extends HdPayBaseRsp.BasePayBuilder<HdQrcodePayRsp> {
        public Builder() {
            super();
        }

        @Override
        protected String getPath() {
            return "hdpay/qrcodepay";
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
