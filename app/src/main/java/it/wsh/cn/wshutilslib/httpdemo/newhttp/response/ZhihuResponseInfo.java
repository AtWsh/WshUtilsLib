package it.wsh.cn.wshutilslib.httpdemo.newhttp.response;

import java.util.ArrayList;

import it.wsh.cn.wshutilslib.httpdemo.bean.Banner;
import it.wsh.cn.wshutilslib.httpdemo.bean.ZhiHuStories;

/**
 * author: wenshenghui
 * created on: 2018/12/28 8:46
 * description:
 */
public class ZhihuResponseInfo {

    public ArrayList<ZhiHuStories> stories;
    public ArrayList<Banner> top_stories;

    @Override
    public String toString() {
        return  "stories = " + (stories == null ? "" : stories.toString()) +
                "; top_stories = " + (top_stories == null ? "" : top_stories.toString());
    }
}
