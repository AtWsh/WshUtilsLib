package it.wsh.cn.wshlibrary.http;

/**
 * author: wenshenghui
 * created on: 2018/10/12 9:30
 * description:
 */
public interface IProcessInfo {
    String getUrl();
    long getTotalSize();
    long getDownloadPosition();
    int getProcess();
    String getSavePath();
}
