package it.wsh.cn.common_oss.glidemodel;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.InputStream;

import it.wsh.cn.common_oss.InputStreamCallBack;
import it.wsh.cn.common_oss.OssManager;

/**
 * author: wenshenghui
 * created on: 2018/11/26 15:27
 * description:
 */
public class AliyunOSSDataFetcher implements DataFetcher<InputStream> {

    private static final String TAG = "AliyunOSSDataFetcher";
    private String mModel;

    public AliyunOSSDataFetcher(String model) {
        mModel = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback callback) {
        OssManager.startDownloadPic(mModel, new InputStreamCallBack() {
            @Override
            public void onSuccess(InputStream inputStream) {
                //BHLog.i(TAG, "AliyunOSSDataFetcher loadData onSuccess");
                callback.onDataReady(inputStream);
            }

            @Override
            public void onError() {
                //BHLog.i(TAG, "AliyunOSSDataFetcher loadData onError");
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
