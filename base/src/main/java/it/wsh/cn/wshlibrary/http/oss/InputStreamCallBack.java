package it.wsh.cn.wshlibrary.http.oss;

import java.io.InputStream;

public interface InputStreamCallBack {

    void onSuccess(InputStream inputStream);
    void onError();
}
