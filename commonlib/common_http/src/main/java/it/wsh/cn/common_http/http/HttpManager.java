package it.wsh.cn.common_http.http;

import android.content.Context;

import it.wsh.cn.common_http.http.download.DownloadManager;
import it.wsh.cn.common_http.http.upload.UploadManager;


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
