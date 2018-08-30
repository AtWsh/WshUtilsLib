package it.wsh.cn.wshlibrary.http.download;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author: wenshenghui
 * created on: 2018/8/24 10:14
 * description:
 */
public class SPDownloadUtil {

    private static final String SP = ".downloadSp";
    private static final String KEY_SAVE_FILE = "_saveFile";
    private static final String KEY_DOWNLOAD_POSITION = ".downloadPosition";
    private static SharedPreferences mSharedPreferences;
    private static SPDownloadUtil instance;
    private Thread spTask;

    private SPDownloadUtil() {
    }

    public static SPDownloadUtil getInstance(Context context) {
        if (mSharedPreferences == null || instance == null) {
            synchronized (SPDownloadUtil.class) {
                if (mSharedPreferences == null || instance == null) {
                    instance = new SPDownloadUtil();
                    mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName() + SP, Context.MODE_PRIVATE);
                }
            }
        }
        return instance;
    }

    /**
     * 清空数据
     *
     * @return true 成功
     */
    public boolean clear() {
        return mSharedPreferences.edit().clear().commit();
    }

    /**
     * 保存数据
     *
     * @param downloadUrl   键
     * @param value 保存的value
     */
    public void saveDownloadPosition(String downloadUrl, long value) {
        mSharedPreferences.edit().putLong(downloadUrl + KEY_DOWNLOAD_POSITION, value).commit();
    }

    /**
     * 获取保存的数据
     *
     * @param downloadUrl      键
     * @param defValue 默认返回的value
     * @return value
     */
    public long getDownloadPosition(String downloadUrl, long defValue) {
        return mSharedPreferences.getLong(downloadUrl + KEY_DOWNLOAD_POSITION, defValue);
    }

    /**
     * 保存数据
     *
     * @param downloadUrl   键
     * @param saveFile 保存的value
     */
    public void saveDownloadSaveFile(String downloadUrl, String saveFile) {
        mSharedPreferences.edit().putString(downloadUrl + KEY_SAVE_FILE, saveFile).commit();
    }

    /**
     * 获取保存的数据
     *
     * @param downloadUrl      键
     * @return value
     */
    public String getDownloadSaveFile(String downloadUrl) {
        return mSharedPreferences.getString(downloadUrl + KEY_SAVE_FILE, "");
    }

}
