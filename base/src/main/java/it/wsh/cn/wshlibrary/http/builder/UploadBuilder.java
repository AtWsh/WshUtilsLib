package it.wsh.cn.wshlibrary.http.builder;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpClient;
import it.wsh.cn.wshlibrary.http.HttpConstants;
import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.HttpStateCode;
import it.wsh.cn.wshlibrary.http.HttpUtils;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * author: wenshenghui
 * created on: 2018/8/10 11:52
 * description:
 */
public abstract class UploadBuilder<T> extends LifeCycleBuilder<T> {

    Map<String, RequestBody> mPartMap = new HashMap<>();

    /**
     * 上传图文信息  使用默认的key
     * @param file
     * @param des
     */
    public void addImgAndDescribe(File file, String des) {
        if (file == null) {
            return;
        }
        RequestBody imgBody = RequestBody.create(MultipartBody.FORM, file);
        RequestBody textBody = RequestBody.create(HttpConstants.sTextType, des);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), imgBody);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), textBody);
    }

    /**
     * 上传图文信息  自定义key
     * @param file
     * @param des
     */
    public void addImgAndDescribe(String fileKey, File file, String desKey, String des) {
        if (file == null || TextUtils.isEmpty(des)) {
            return;
        }
        RequestBody imgBody = RequestBody.create(MultipartBody.FORM, file);
        RequestBody textBody = RequestBody.create(HttpConstants.sTextType, des);
        mPartMap.put(fileKey, imgBody);
        mPartMap.put(desKey, textBody);
    }

    public void addFile(File file) {
        if (file == null) {
            return;
        }

        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), requestBody);
    }

    public void addFiles(List<File> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        for (File file : files) {
            addFile(file);
        }
    }

    public void addPartMap(Map<String, RequestBody> partMap) {
        if (partMap == null) {
            return;
        }
        mPartMap = partMap;
    }

    @Override
    protected void request(boolean onUiCallBack, final HttpCallBack<T> callback){
        HttpClient client = getHttpClient(callback);
        if (client == null) {
            callback.onError(HttpStateCode.ERROR_HTTPCLIENT_CREATE_FAILED, null);
            return;
        }
        callback.onStart();
        mDisposableCacheKey = HttpUtils.getDisposableCacheKey(getPath(), mHttpHeader, null, mPartMap, mHttpCustomConfig);

        client.upload(getPath(), mDisposableCacheKey, mHttpHeader, mPartMap, getTagHash(), callback);
    }

    @Override
    protected @HttpMethod.IMethed
    String getMethod() {
        return HttpMethod.POST;
    };
}


