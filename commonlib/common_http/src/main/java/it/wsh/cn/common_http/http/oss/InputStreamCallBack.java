package it.wsh.cn.common_http.http.oss;

import java.io.InputStream;

public interface InputStreamCallBack {

    void onSuccess(InputStream inputStream);
    void onError();
}
