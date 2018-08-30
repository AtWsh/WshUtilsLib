package it.wsh.cn.wshutilslib.httpdemo.bean;


import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.builder.LifeCycleBuilder;

/**
 * author: wenshenghui
 * created on: 2018/8/21 11:56
 * description:  POST 请求Demo
 */
public class NoticeListResponse {

    public String code;
    public NoticeDataInfo data;
    public String message;

    @Override
    public String toString() {
        return  "code = " + code +
                "; data = " + (data == null ? "" : data.toString()) +
                "; message = " + message;
    }

    public static class Builder extends LifeCycleBuilder<NoticeListResponse> {

        @Override
        protected String getPath() {
            return "noticeList";
        }

        @Override
        protected String getBaseUrl() {
            //return "https://61.141.236.30:443/api/notice/";
            return "https://61.141.236.30:449/api/notice/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }
}
