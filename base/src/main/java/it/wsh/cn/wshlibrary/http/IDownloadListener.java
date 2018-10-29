package it.wsh.cn.wshlibrary.http;

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
    int ERROR_DOWNLOAD_OSSTASK_CREATE = 1002;

    void onProgress(IProcessInfo info);
    void onComplete(int stateCode, String info);
}
