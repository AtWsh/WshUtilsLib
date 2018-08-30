package it.wsh.cn.wshutilslib.httpdemo.bean;

import java.util.ArrayList;

/**
 * author: wenshenghui
 * created on: 2018/8/23 9:57
 * description:
 */
public class NoticeDataInfo {

    public int currentPage;
    public int pageSize;
    public ArrayList<NoticeResultInfo> result;
    public int totalCount;

    @Override
    public String toString() {
        return  "currentPage = " + currentPage +
                "; pageSize = " + pageSize +
                "; result = " + (result == null ? "" : result.toString()) +
                "; totalCount = " + totalCount;
    }
}
