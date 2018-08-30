package it.wsh.cn.wshutilslib.httpdemo.bean;


import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.builder.LifeCycleBuilder;

/**
 * author: wenshenghui
 * created on: 2018/8/21 11:56
 * description:  Get 请求Demo
 */

public class WeatherResponse {
    public String message;
    public String status;

    @Override
    public String toString() {
        return "message = " + message +
                ";   status = " + status;
    }

    public static class Builder extends LifeCycleBuilder<WeatherResponse> {
        @Override
        protected String getPath() {
            return "telematics/v3/weather";
        }

        @Override
        protected String getBaseUrl() {
            return "http://api.map.baidu.com/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.GET;
        }
    }

}
