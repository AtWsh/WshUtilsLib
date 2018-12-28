package it.wsh.cn.common_imageloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import it.wsh.cn.common_imageloader.ssl.SslContextFactory;
import it.wsh.cn.common_imageloader.ssl.TrustAllCerts;
import okhttp3.OkHttpClient;

/**
 * 自定义glideModule
 * Created by wenshenghui on 2018/11/21.
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {
    private static final String TAG = "CustomGlideModule";

    public CustomGlideModule() {
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

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

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //BHLog.i(TAG, "registerComponents");
        //优先装载动态设置的ModelLoaderFactory， 下面固定的OkHttpUrlLoader会处理所有的url
        List<ModelLoaderFactory> factories = GlideConfig.getModelLoaderFactories();
        if (factories != null && factories.size() > 0) {
            for (int i = 0; i < factories.size(); i++) {
                registry.prepend(String.class, InputStream.class, factories.get(i));
                //BHLog.i(TAG, "registerComponents  registry.prepend");
            }
        }

        // 设置长时间读取和断线重连
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(SslContextFactory.createSSLSocketFactory(), new TrustAllCerts())
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .retryOnConnectionFailure(true)
                .readTimeout(90 * 1000, TimeUnit.MILLISECONDS)
                .build();
        registry.prepend(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okhttpClient));
    }
}
