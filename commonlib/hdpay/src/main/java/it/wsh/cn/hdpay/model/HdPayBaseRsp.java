package it.wsh.cn.hdpay.model;

import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.builder.LifeCycleBuilder;

public class HdPayBaseRsp {

    public String code;
    public String data;
    public String message;

    public abstract static class BasePayBuilder<T> extends LifeCycleBuilder<T> {

        public BasePayBuilder(String authorization) {
            HttpConfig config = HttpConfig.create(false);
            config.addHeader("content-type", "application/json")
                    .addHeader("FrontType", "egc-mobile-ui")
                    .addHeader("Authorization", authorization);
            setHttpCustomConfig(config);
        }
    }

}
