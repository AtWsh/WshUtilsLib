package it.wsh.cn.wshutilslib.httpdemo.bean;


import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.builder.CommonBuilder;

/**
 * author: wenshenghui
 * created on: 2018/8/21 15:30
 * description:
 */
public class AccountExistResponse {

    public String code;
    public String data;
    public String message;

    @Override
    public String toString() {
        return  "code = " + code +
                "; data = " + data +
                "; message = " + message;
    }

    public static class Builder extends CommonBuilder<AccountExistResponse> {

        @Override
        protected String getPath() {
            return "checkAccountIfExist";
        }

        @Override
        protected String getBaseUrl() {
            return "https://61.141.236.30:443/api/register/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }
}
