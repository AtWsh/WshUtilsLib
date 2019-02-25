package it.wsh.cn.hdpay.model;

import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.builder.LifeCycleBuilder;
import it.wsh.cn.hdpay.HdPayManager;

public class HdPayBaseRsp {

    public String code;
    public String data;
    public String message;

    public abstract static class BasePayBuilder<T> extends LifeCycleBuilder<T> {

        public BasePayBuilder() {
            HttpConfig config = HttpConfig.create(false);
            config.addHeader("content-type", "application/json")
                    .addHeader("FrontType", HdPayManager.getInstance().getFrontType())
                    .addHeader("Authorization", HdPayManager.getInstance().getAuthorization());
            setHttpCustomConfig(config);
        }
    }

}
