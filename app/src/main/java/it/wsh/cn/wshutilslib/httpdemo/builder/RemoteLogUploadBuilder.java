package it.wsh.cn.wshutilslib.httpdemo.builder;

import android.text.TextUtils;

import it.wsh.cn.common_http.http.builder.UploadBuilder;

/**
 * author: wenshenghui
 * created on: 2018/12/13 9:08
 * description:
 */
public class RemoteLogUploadBuilder extends UploadBuilder<String> {

    private String mUrl = "";
    public RemoteLogUploadBuilder(String url) {
        if (!TextUtils.isEmpty(url)) {
            mUrl = url;
        }
    }
    @Override
    protected String getPath() {
        return getPath(mUrl);
    }

    @Override
    protected String getBaseUrl() {
        return getBaseUrl(mUrl);
    }

    private String getBaseUrl(String url) {
        int index = url.lastIndexOf("/");
        if (index < 0) {
            return "";
        }
        String baseUrl = url.substring(0, index + 1);
        return baseUrl;
    }

    private String getPath(String url) {
        int index = url.lastIndexOf("/");
        if (index < 0) {
            return "";
        }
        String path = url.substring(index + 1);
        return path;
    }
}
