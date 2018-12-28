package it.wsh.cn.wshutilslib.httpdemo.newhttp.requestinfo;

import java.util.HashMap;

import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.request.IBaseReqInfo;

/**
 * author: wenshenghui
 * created on: 2018/12/28 9:49
 * description:
 */
public class RouteRequestInfo extends IBaseReqInfo {

    public String req_id = "";
    public String method = "";
    public String timestamp = "";
    public String params = "";

    public RouteRequestInfo(String reqId, String method, String params) {
        initHttpConfig();
        this.req_id = reqId;
        this.timestamp = System.currentTimeMillis() + "";
        this.method = method;
        this.params = params;

        /*HashMap<String, String> map = new HashMap<>();
        map.put("req_id", reqId);
        map.put("method", method);
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", params);
        addBodyMap(map);*/
    }

    public RouteRequestInfo() {
        initHttpConfig();
    }

    private void initHttpConfig() {
        HttpConfig httpConfig = HttpConfig.create(false);
        httpConfig.connectTimeout(5);
        httpConfig.readTimeout(5);
        httpConfig.writeTimeout(5);
        setHttpCustomConfig(httpConfig);
    }

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
