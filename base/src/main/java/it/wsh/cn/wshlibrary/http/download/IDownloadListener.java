package it.wsh.cn.wshlibrary.http.download;

import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;

/**
 * author: wenshenghui
 * created on: 2018/9/14 12:16
 * description:
 */
public interface IDownloadListener {

    String PAUSE_STATE = "pause";
    int PAUSE = 1;
    int ERROR_DOWNLOAD_RETROFIT = 1001;

    void onProgress(DownloadInfo info);
    void onComplete(int stateCode, String info);
}
