package it.wsh.cn.common_http.http;

import android.content.Context;

import it.wsh.cn.common_http.http.database.utils.GreenDaoDatabase;
import it.wsh.cn.common_http.http.download.DownloadManager;


/**
 * author: wenshenghui
 * created on: 2018/9/14 15:02
 * description:
 */
public class HttpManager {

    public static void init(Context context) {
        //初始化普通Http请求模块
        HttpClientManager.getInstance().init(context);
        //初始化下载模块
        DownloadManager.getInstance().init(context);
        //初始化数据库(下载，断点续传用到)
        GreenDaoDatabase.getInstance().initDatabase(context);
    }
}
