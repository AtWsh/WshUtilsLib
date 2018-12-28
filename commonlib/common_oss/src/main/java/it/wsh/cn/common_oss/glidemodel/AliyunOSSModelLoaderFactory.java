package it.wsh.cn.common_oss.glidemodel;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * author: wenshenghui
 * created on: 2018/11/26 15:27
 * description:
 */
public class AliyunOSSModelLoaderFactory implements ModelLoaderFactory<String, InputStream> {
    private static final String TAG = "AliyunOSSModelLoaderFactory";

    @NonNull
    @Override
    public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new AliyunOSSModelLoader();
    }

    @Override
    public void teardown() {

    }
}
