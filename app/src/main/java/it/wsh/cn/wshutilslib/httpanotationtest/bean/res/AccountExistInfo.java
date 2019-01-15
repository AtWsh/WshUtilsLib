package it.wsh.cn.wshutilslib.httpanotationtest.bean.res;

import com.example.wsh.common_http_processor.HttpBody;

/**
 * author: wenshenghui
 * created on: 2019/1/9 11:01
 * description:
 */

public class AccountExistInfo {

    @HttpBody
    public String code;
    @HttpBody
    public String data;
    @HttpBody
    public String message;

}
