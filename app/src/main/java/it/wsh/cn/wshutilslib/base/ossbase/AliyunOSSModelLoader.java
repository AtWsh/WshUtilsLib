package it.wsh.cn.wshutilslib.base.ossbase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import java.io.InputStream;

public class AliyunOSSModelLoader implements ModelLoader<String, InputStream> {
    private static final String TAG = "AliyunOSSModelLoader";
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        return new LoadData<InputStream>(new GlideUrl(model), new AliyunOSSDataFetcher(model));
    }

    @Override
    public boolean handles(@NonNull String model) {
        Log.i(TAG, "handles model = " + model);
        if (TextUtils.isEmpty(model)) {
            return false;
        }
        return model.startsWith("product_type") ||
                model.startsWith("avatar");
    }
}
