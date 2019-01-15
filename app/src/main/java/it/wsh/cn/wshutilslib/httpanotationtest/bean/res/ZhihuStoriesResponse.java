package it.wsh.cn.wshutilslib.httpanotationtest.bean.res;


import java.util.ArrayList;

import it.wsh.cn.wshutilslib.httpdemo.bean.Banner;
import it.wsh.cn.wshutilslib.httpdemo.bean.ZhiHuStories;


public class ZhihuStoriesResponse {
    public ArrayList<ZhiHuStories> stories;
    public ArrayList<Banner> top_stories;

    @Override
    public String toString() {
        return  "stories = " + (stories == null ? "" : stories.toString()) +
                "; top_stories = " + (top_stories == null ? "" : top_stories.toString());
    }
}
