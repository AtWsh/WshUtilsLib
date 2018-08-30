package it.wsh.cn.wshlibrary.http.download;


import it.wsh.cn.wshlibrary.http.HttpCallBack;

/**
 * author: wenshenghui
 * created on: 2018/8/25 13:17
 * description:
 */
public abstract class DownloadCallBack<String> extends HttpCallBack<String> {

    abstract void onPause();
}
