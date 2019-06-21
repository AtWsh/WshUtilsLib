package it.wsh.cn.common_http.http;

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

    void onStart(); //任务开启则会回调，
    void onProgress(IProcessInfo info); //只回调进度，不关心状态
    void onResult(int stateCode, String info); //只关心状态：暂停或结束


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
