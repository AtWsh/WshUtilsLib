package it.wsh.cn.wshutilslib.httpdemo.bean;


import java.util.ArrayList;

import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.builder.LifeCycleBuilder;


public class StoriesResponse {
    public ArrayList<ZhiHuStories> stories;
    public ArrayList<Banner> top_stories;

    @Override
    public String toString() {
        return  "stories = " + (stories == null ? "" : stories.toString()) +
                "; top_stories = " + (top_stories == null ? "" : top_stories.toString());
    }

    public static class Builder extends LifeCycleBuilder<StoriesResponse> {
        @Override
        protected String getPath() {
            return "api/4/news/latest";
        }

        @Override
        protected String getBaseUrl() {
            return "https://news-at.zhihu.com/";
        }

        @Override
        protected @HttpMethod.IMethed String getMethod() {
            return HttpMethod.GET;
        }
    }

}
