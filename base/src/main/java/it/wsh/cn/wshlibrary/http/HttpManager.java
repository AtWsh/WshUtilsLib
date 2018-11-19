package it.wsh.cn.wshlibrary.http;

import android.content.Context;

import it.wsh.cn.wshlibrary.http.download.DownloadManager;
import it.wsh.cn.wshlibrary.http.upload.UploadManager;

/**
 * author: wenshenghui
 * created on: 2018/9/14 15:02
 * description:
 */
public class HttpManager {

    public static void init(Context context) {
        DownloadManager.getInstance().init(context);
        UploadManager.getInstance().init(context);
        HttpClientManager.getInstance().init(context);
    }
}
