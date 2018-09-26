package it.wsh.cn.wshlibrary.http.download;

/**
 * author: wenshenghui
 * created on: 2018/9/14 12:16
 * description:
 */
public interface IDownloadListener {

    void onProgress(int progress);
    void onError(int stateCode, String errorInfo);
    void onPause();
}