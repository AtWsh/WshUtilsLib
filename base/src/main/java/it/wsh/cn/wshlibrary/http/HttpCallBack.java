package it.wsh.cn.wshlibrary.http;

/**
 * author: wenshenghui
 * created on: 2018/8/2 16:27
 * description:
 */
public abstract class HttpCallBack<T> {

    public void onStart(){}
    public abstract void onSuccess(T t);
    public abstract void onError(int stateCode, String errorInfo);
    public void onProgress(int progress){}
}
