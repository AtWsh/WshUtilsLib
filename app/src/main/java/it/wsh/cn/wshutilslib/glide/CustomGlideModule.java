package it.wsh.cn.wshutilslib.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import it.wsh.cn.wshutilslib.base.ossbase.AliyunOSSModelLoaderFactory;
import okhttp3.OkHttpClient;

@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
//        return super.isManifestParsingEnabled();
        return false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // 设置长时间读取和断线重连
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(90 * 1000, TimeUnit.MILLISECONDS)
                .build();
        registry.prepend(GlideUrl.class, InputStream.class, new WshOkHttpUrlLoader.Factory(okhttpClient));
        registry.prepend(String.class, InputStream.class, new AliyunOSSModelLoaderFactory());
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        long defaultMemoryCacheSize = 8 * 1024 * 1024L; //8M
        long runtimeMemoryCacheSize = Runtime.getRuntime().maxMemory() / 8; //  1/8
        long defaultDiskCacheSize = 250 * 1024 * 1024L; //250M
        if (runtimeMemoryCacheSize > defaultDiskCacheSize) {
            defaultMemoryCacheSize = runtimeMemoryCacheSize;
        }
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize))
                .setDiskCache(new InternalCacheDiskCacheFactory(context, defaultDiskCacheSize));
    }
}
