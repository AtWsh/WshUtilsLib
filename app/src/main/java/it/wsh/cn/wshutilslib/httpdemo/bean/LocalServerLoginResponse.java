package it.wsh.cn.wshutilslib.httpdemo.bean;

import java.util.List;

import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.builder.LifeCycleBuilder;

public class LocalServerLoginResponse {
    public String name;
    public String unit;

    @Override
    public String toString() {
        return "name = " + name +
                "; unit = " + unit;
    }

    public static class ListBuilder extends LifeCycleBuilder<List<LocalServerLoginResponse>> {

        public ListBuilder() {
            HttpConfig httpConfig = HttpConfig.create(false);
            httpConfig.addHeader("content-type", "application/json;charset=utf-8");
            httpConfig.addHeader("sessionid", "h3yh82hdbd8nf");
            setHttpCustomConfig(httpConfig);
        }

        @Override
        protected String getPath() {
            return "assetApp/assetList";
        }

        @Override
        protected String getBaseUrl() {
            return "http://localhost:8887/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }
}
