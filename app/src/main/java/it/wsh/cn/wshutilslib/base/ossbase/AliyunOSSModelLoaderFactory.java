package it.wsh.cn.wshutilslib.base.ossbase;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

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
