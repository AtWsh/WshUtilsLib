package it.wsh.cn.wshutilslib;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import it.wsh.cn.wshutilslib.base.ossbase.AliyunOSSModelLoaderFactory;

@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
//        return super.isManifestParsingEnabled();
        return false;
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
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
