package it.wsh.cn.wshutilslib.httpdemo.bean;


import java.util.List;

import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.builder.CommonBuilder;
import it.wsh.cn.wshlibrary.http.builder.LifeCycleBuilder;

/**
 * author: wenshenghui
 * created on: 2018/8/22 19:27
 * description:
 */
public class RouteInfoResponse {

    public String target_uuid;
    public String app_uuid;
    public String method;
    public String req_id;
    public String timestamp;
    public String msg;
    public String code;
    public RouteInfoRespomseResultInfo result;

    @Override
    public String toString() {
        return  "target_uuid = " + target_uuid +
                "; app_uuid = " + app_uuid +
                "; method = " + method +
                "; req_id = " + req_id +
                "; timestamp = " + timestamp +
                "; msg = " + msg +
                "; result = " + (result == null ? "" : result.toString()) +
                "; code = " + code;
    }

    public static class Builder extends CommonBuilder<RouteInfoResponse> {

        @Override
        protected String getPath() {
            return "test1";
        }

        @Override
        protected String getBaseUrl() {
            //return "https://61.141.236.30:443/api/notice/";
            return "http://192.168.10.1/cgi-bin/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }

    public static class ListBuilder extends LifeCycleBuilder<List<RouteInfoResponse>> {

        @Override
        protected String getPath() {
            return "test1";
        }

        @Override
        protected String getBaseUrl() {
            return "http://192.168.10.1/cgi-bin/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }
}
