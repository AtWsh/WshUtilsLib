package it.wsh.cn.common_oss;

import android.content.Context;
import android.content.SharedPreferences;

import it.wsh.cn.common_oss.bean.OssConfigInfo;

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
    public static OssConfigInfo getOssConfigInfo(Context context) {
        if (context == null) {
            return null;
        }
        OssConfigInfo ossConfigInfo = new OssConfigInfo();
        SharedPreferences sp = context.getSharedPreferences(SP_STSTOKEN_FILE_NAME , Context.MODE_PRIVATE);
        String accessKeyId = sp.getString(KEY_STS_ACCESSKEY_ID, "");
        String accessKeySecret = sp.getString(KEY_STS_ACCESSKEY_SECRET , "");
        String securityToken = sp.getString(KEY_STS_TOKENKEY , "");
        long expiration = sp.getLong(KEY_STS_EXPIRATION, 0);
        ossConfigInfo.AccessKeyId = accessKeyId;
        ossConfigInfo.AccessKeySecret = accessKeySecret;
        ossConfigInfo.SecurityToken = securityToken;
        ossConfigInfo.Expiration = expiration;
        ossConfigInfo.mEndpoint = sp.getString(KEY_STS_ENDPOINT , ALIYUN_OSS_ENDPOINT);
        ossConfigInfo.mBucketName = sp.getString(KEY_STS_BUCKET , ALIYUN_OSS_BUCKET_NAME);
        return ossConfigInfo;
    }

    /**
     * 将ststoken保存
     */
    public static void saveStsToken(Context context, OssConfigInfo ossConfigInfo) {
        if (context == null || ossConfigInfo == null) {
            return;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SP_STSTOKEN_FILE_NAME , Context.MODE_PRIVATE).edit();
        editor.putString(KEY_STS_ACCESSKEY_ID , ossConfigInfo.AccessKeyId);
        editor.putString(KEY_STS_ACCESSKEY_SECRET, ossConfigInfo.AccessKeySecret);
        editor.putString(KEY_STS_TOKENKEY, ossConfigInfo.SecurityToken);
        editor.putLong(KEY_STS_EXPIRATION, ossConfigInfo.Expiration);
        editor.putString(KEY_STS_ENDPOINT, ossConfigInfo.mEndpoint);
        editor.putString(KEY_STS_BUCKET, ossConfigInfo.mBucketName);
        editor.commit();
    }
}
