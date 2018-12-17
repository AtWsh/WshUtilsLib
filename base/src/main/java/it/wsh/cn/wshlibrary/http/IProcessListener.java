package it.wsh.cn.wshlibrary.http;

import it.wsh.cn.wshlibrary.database.bean.DownloadInfo;

/**
 * author: wenshenghui
 * created on: 2018/9/14 12:16
 * description:
 */
public interface IProcessListener {

    String PAUSE_STATE = "pause";
    int PAUSE = 1;
    int SUCCESS = 200;
    int ERROR_DOWNLOAD_RETROFIT = 1001;
    int ERROR_DOWNLOAD_OSSTASK_CREATE = 1002;

    void onStart();
    void onProgress(IProcessInfo info);
    void onComplete(int stateCode, String info);


    /**
     * 上传时本地异常
     */
    int ERRCODE_UPLOAD_LOCAL_ERR = 2001;

    /**
     * 上传时服务器异常
     */
   int ERRCODE_UPLOAD_SERVER_ERR = 2002;

    /**
     * 上传时未知异常
     */
    int ERRCODE_UPLOAD_UNKNOWN_ERR = 2003;
}
