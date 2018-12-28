package it.wsh.cn.wshutilslib.httpdemo.newhttp.response;

import it.wsh.cn.wshutilslib.httpdemo.bean.RouteInfoRespomseResultInfo;

/**
 * author: wenshenghui
 * created on: 2018/12/28 9:51
 * description:
 */
public class RouteResponseInfo {

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
}
