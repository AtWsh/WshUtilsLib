package it.wsh.cn.wshutilslib.httpdemo.newhttp.requestinfo;

import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.request.IBaseReqInfo;

/**
 * author: wenshenghui
 * created on: 2018/12/28 8:45
 * description:
 */
public class ZhihuRequestInfo extends IBaseReqInfo {



    @Override
    protected String getPath() {
        return "api/4/news/latest";
    }

    @Override
    protected String getBaseUrl() {
        return "https://news-at.zhihu.com/";
    }

    @Override
    protected String getMethod() {
        return HttpMethod.GET;
    }
}
