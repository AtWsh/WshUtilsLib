package it.wsh.cn.wshutilslib.islandwifi.model;

import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.builder.LifeCycleBuilder;

/**
 * author:wenshenghui
 * created on: 2019-3-21 15:41
 * description:
 */
public class IslandWifiRsp {

   /* {
        “code”:”200”,
        “Message”:”成功”,
        “data”:{
	        “bandwidth”:,
	        “time”:,
        }
    }*/

    public static final String SUCCESS = "200";


    public int code;
    public String Message;
    public IslandWifiRspData data;

    class IslandWifiRspData{
        public String bandwidth;
        public String time;
    }

    public static class Builder extends LifeCycleBuilder<IslandWifiRsp> {

        private String mUrl;
        public Builder(String url){
            mUrl = url;
        }

        @Override
        protected String getPath() {
            return mUrl;
        }

        @Override
        protected String getBaseUrl() {
            return "http://www.baidu.com/";
        }

        @Override
        protected String getMethod() {
            return HttpMethod.POST;
        }
    }

}
