package it.wsh.cn.wshutilslib.glide;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import okhttp3.Call;

public class WshOkHttpUrlLoader extends OkHttpUrlLoader {
    private static final String TAG = "WshOkHttpUrlLoader";
    public WshOkHttpUrlLoader(@NonNull Call.Factory client) {
        super(client);
    }

    @Override
    public boolean handles(@NonNull GlideUrl url) {
        String realUrl = url.toStringUrl();
        Log.i(TAG, "handles model = " + realUrl);
        if (TextUtils.isEmpty(realUrl)) {
            return false;
        }
        boolean isOssUrl = realUrl.startsWith("product_type") ||
                realUrl.startsWith("avatar");
        Log.i(TAG, "handles model = " + !isOssUrl);
        return !isOssUrl;
    }
}
