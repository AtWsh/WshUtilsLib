package it.wsh.cn.hdpay.model;

import it.wsh.cn.common_http.http.HttpMethod;

public class HdCancelPayRsp extends HdPayBaseRsp {

    public String trade_state;
    public String prepay_id;

    public static class Builder extends HdPayBaseRsp.BasePayBuilder<HdCancelPayRsp> {
        public Builder() {
            super();
        }

        @Override
        protected String getPath() {
            return "hdpay/cancelpay";
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
