package it.wsh.cn.wshutilslib.base.ossbase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.InputStream;

import it.wsh.cn.common_http.http.oss.InputStreamCallBack;

public class AliyunOSSDataFetcher implements DataFetcher<InputStream>  {

    private static final String TAG = "AliyunOSSDataFetcher";
    private String mModel;
    public AliyunOSSDataFetcher(String model) {
        mModel = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback callback) {
        OssHelper.startDownloadPic(mModel, new InputStreamCallBack() {
            @Override
            public void onSuccess(InputStream inputStream) {
                Log.i(TAG, "AliyunOSSDataFetcher loadData onSuccess");
                callback.onDataReady(inputStream);
            }

            @Override
            public void onError() {
                Log.i(TAG, "AliyunOSSDataFetcher loadData onError");
                callback.onLoadFailed(new Exception("loadpic inputStream failed"));
            }
        });

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
