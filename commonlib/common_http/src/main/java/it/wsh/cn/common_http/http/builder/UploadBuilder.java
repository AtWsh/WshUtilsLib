package it.wsh.cn.common_http.http.builder;

import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.HttpClientManager;
import it.wsh.cn.common_http.http.HttpConstants;
import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.utils.HttpLog;
import it.wsh.cn.common_http.http.utils.HttpUtils;
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
    public UploadBuilder addImgAndDescribe(File file, String des) {
        if (file == null) {
            HttpLog.e("UploadBuilder： Error! addImgAndDescribe : file == null !" );
            return null;
        }
        RequestBody imgBody = RequestBody.create(MultipartBody.FORM, file);
        RequestBody textBody = RequestBody.create(HttpConstants.TEXT_TYPE, des);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), imgBody);
        mPartMap.put(HttpConstants.FILE_DES + file.getName(), textBody);
        return this;
    }

    /**
     * 上传图文信息  自定义key
     * @param file
     * @param des
     */
    public UploadBuilder addImgAndDescribe(String fileKey, File file, String desKey, String des) {
        if (file == null || TextUtils.isEmpty(des)) {
            HttpLog.e("UploadBuilder： Error! addImgAndDescribe : file == null || TextUtils.isEmpty(des) !" );
            return null;
        }
        RequestBody imgBody = RequestBody.create(MultipartBody.FORM, file);
        RequestBody textBody = RequestBody.create(HttpConstants.TEXT_TYPE, des);
        mPartMap.put(fileKey, imgBody);
        mPartMap.put(desKey, textBody);
        return this;
    }

    public UploadBuilder addFile(File file) {
        if (file == null) {
            return null;
        }

        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        mPartMap.put(HttpConstants.FILE_NAME + file.getName(), requestBody);
        return this;
    }

    public UploadBuilder addFile(String key, File file) {
        if (file == null) {
            return null;
        }

        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        mPartMap.put(key, requestBody);
        return this;
    }

    public UploadBuilder addFile(String key, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        mPartMap.put(key, requestBody);
        return this;
    }

    public UploadBuilder addFiles(List<File> files) {
        if (files == null || files.size() == 0) {
            HttpLog.e("UploadBuilder： Error! addFiles : files == null || files.size() == 0 !" );
            return null;
        }
        for (File file : files) {
            addFile(file);
        }
        return this;
    }

    public UploadBuilder addPartMap(Map<String, RequestBody> partMap) {
        if (partMap == null) {
            HttpLog.e("UploadBuilder： Error! addPartMap : partMap == null !" );
            return null;
        }
        mPartMap = partMap;
        return this;
    }

    @Override
    protected int request(boolean onUiCallBack, final HttpCallBack<T> callback){
        HttpClientManager clientManager = HttpClientManager.getInstance();
        callback.onStart();
        int disposableCacheKey = HttpUtils.getHttpKey(getPath(), mHttpHeader, null, mPartMap, mHttpCustomConfig);

        return clientManager.upload(
                getBaseUrl(), getPath(), disposableCacheKey, mHttpHeader, mPartMap, getTagHash(),
                mRetryTimes, mRetryDelayMillis, mHttpCustomConfig, callback);
    }

    @Override
    protected @HttpMethod.IMethed
    String getMethod() {
        return HttpMethod.POST;
    };
}


