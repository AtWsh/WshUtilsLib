package it.wsh.cn.wshutilslib.httpanotationtest.bean.req;

import com.example.wsh.common_http_processor.HttpReqBean;

import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.asyncquery.IHttpReq;
import it.wsh.cn.wshutilslib.httpanotationtest.bean.res.ZhihuStoriesResponse;

/**
 * author: wenshenghui
 * created on: 2019/1/11 9:37
 * description:
 */
//@HttpReqBean(resBean = ZhihuStoriesResponse.class)
public class ZhihuStoriesReq implements IHttpReq {

    @Override
    public String getPath() {
        return "api/4/news/latest";
    }

    @Override
    public String getBaseUrl() {
        return "https://news-at.zhihu.com/";
    }

    @Override
    public @HttpMethod.IMethed String getMethod() {
        return HttpMethod.GET;
    }
}
