package it.wsh.cn.wshlibrary.http;

import android.content.Context;

import it.wsh.cn.wshlibrary.http.download.DownloadManager;

/**
 * author: wenshenghui
 * created on: 2018/9/14 15:02
 * description:
 */
public class HttpManager {

    public static void init(Context context) {
        HttpClient.getInstance().init(context);
        DownloadManager.getInstance().init(context);
    }
}
