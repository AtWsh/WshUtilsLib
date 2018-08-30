package it.wsh.cn.wshutilslib.httpdemo.bean;

/**
 * author: wenshenghui
 * created on: 2018/8/23 9:32
 * description:
 */
public class RouteInfoRespomseResultInfo {

    public int bind_status;
    public int status;
    public String sw_version;
    public String uuid;

    @Override
    public String toString() {
        return "bind_status = " + bind_status +
                "; status = " + status +
                "; status = " + status +
                "; uuid = " + uuid;
    }
}
