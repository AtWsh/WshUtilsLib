package it.wsh.cn.wshutilslib.base.ossbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;

import it.wsh.cn.wshlibrary.http.oss.OssConfigInfo;
import it.wsh.cn.wshlibrary.utils.SharedPreferenceHelper;

/**
 * author: wenshenghui
 * created on: 2018/10/12 18:02
 * description:
 */
public class OssPreferHelper {

    public static final String SP_STSTOKEN_FILE_NAME = "sp_download_systoken";
    public static final String KEY_STS_ACCESSKEY_ID = "AccessKeyId";
    public static final String KEY_STS_ACCESSKEY_SECRET = "AccessKeySecret";
    public static final String KEY_STS_TOKENKEY = "SecurityToken";
    public static final String KEY_STS_EXPIRATION = "Expiration";
    public static final String KEY_STS_ENDPOINT = "endpoint";
    public static final String KEY_STS_BUCKET = "bucket";

    public static final String ALIYUN_OSS_ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
    public static final String ALIYUN_OSS_BUCKET_NAME = "iot-upgrade";


    /**
     * 获取缓存的ststoken
     */
    public static OssConfigInfo getOssConfigInfo(Context context, String suffix) {
        if (context == null) {
            return null;
        }
        OssConfigInfo ossConfigInfo = new OssConfigInfo();
        SharedPreferences sp = SharedPreferenceHelper.getSp(context,
                SP_STSTOKEN_FILE_NAME + (TextUtils.isEmpty(suffix) ? "" : suffix));
        String accessKeyId = sp.getString(KEY_STS_ACCESSKEY_ID +
                (TextUtils.isEmpty(suffix) ? "" : suffix), "");
        String accessKeySecret = sp.getString(KEY_STS_ACCESSKEY_SECRET +
                (TextUtils.isEmpty(suffix) ? "" : suffix), "");
        String securityToken = sp.getString(KEY_STS_TOKENKEY +
                (TextUtils.isEmpty(suffix) ? "" : suffix), "");
        long expiration = sp.getLong(KEY_STS_EXPIRATION +
                (TextUtils.isEmpty(suffix) ? "" : suffix), 0);
        ossConfigInfo.mStsToken = new OSSFederationToken(accessKeyId, accessKeySecret,
                securityToken, expiration);
        ossConfigInfo.mEndpoint = sp.getString(KEY_STS_ENDPOINT +
                (TextUtils.isEmpty(suffix) ? "" : suffix), ALIYUN_OSS_ENDPOINT);
        ossConfigInfo.mBucketName = sp.getString(KEY_STS_BUCKET +
                (TextUtils.isEmpty(suffix) ? "" : suffix), ALIYUN_OSS_BUCKET_NAME);

        return ossConfigInfo;
    }

    /**
     * 将ststoken保存
     */
    public static void saveStsToken(Context context, String suffix, OssConfigInfo ossConfigInfo) {
        if (context == null || ossConfigInfo == null || ossConfigInfo.mStsToken == null) {
            return;
        }
        SharedPreferences.Editor editor = SharedPreferenceHelper.getEditor(context,
                SP_STSTOKEN_FILE_NAME + (TextUtils.isEmpty(suffix) ? "" : suffix));
        editor.putString(KEY_STS_ACCESSKEY_ID + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mStsToken.getTempAK());
        editor.putString(KEY_STS_ACCESSKEY_SECRET + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mStsToken.getTempSK());
        editor.putString(KEY_STS_TOKENKEY + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mStsToken.getSecurityToken());
        editor.putLong(KEY_STS_EXPIRATION + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mStsToken.getExpiration());
        editor.putString(KEY_STS_ENDPOINT + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mEndpoint);
        editor.putString(KEY_STS_BUCKET + (TextUtils.isEmpty(suffix) ? "" : suffix),
                ossConfigInfo.mBucketName);
        editor.commit();
    }
}
