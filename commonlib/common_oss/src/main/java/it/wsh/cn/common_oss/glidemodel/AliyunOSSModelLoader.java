package it.wsh.cn.common_oss.glidemodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import java.io.InputStream;

import it.wsh.cn.common_oss.OssConfig;


/**
 * author: wenshenghui
 * created on: 2018/11/26 15:27
 * description:
 */
public class AliyunOSSModelLoader implements ModelLoader<String, InputStream> {
    private static final String TAG = "AliyunOSSModelLoader";

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        return new LoadData<InputStream>(new GlideUrl(model), new AliyunOSSDataFetcher(model));
    }

    @Override
    public boolean handles(@NonNull String model) {
        boolean isOssUrl = OssConfig.isOssUrl(model);
        //BHLog.i(TAG, "handles model   isOssUrl = " + isOssUrl);
        return isOssUrl;
    }
}
