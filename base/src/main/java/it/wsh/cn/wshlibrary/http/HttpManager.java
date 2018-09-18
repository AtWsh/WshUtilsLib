package it.wsh.cn.wshlibrary.http;

import android.content.Context;

import it.wsh.cn.wshlibrary.http.download.XLDownloadManager;

/**
 * author: wenshenghui
 * created on: 2018/9/14 15:02
 * description:
 */
public class HttpManager {

    public static void init(Context context) {
        HttpClient.getInstance().init(context);
        XLDownloadManager.getInstance().init(context);
    }
}
