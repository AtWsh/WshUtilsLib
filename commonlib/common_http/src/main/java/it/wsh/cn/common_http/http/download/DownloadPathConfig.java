package it.wsh.cn.common_http.http.download;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import it.wsh.cn.common_http.http.HttpConstants;


/**
 * author: wenshenghui
 * created on: 2018/8/27 12:14
 * description:
 */
public class DownloadPathConfig {

    private static String sSavePath = "";

    public static String getDownloadPath(Context context) {
        if (context == null) {
            return "";
        }

        String savePath;
        if (!TextUtils.isEmpty(sSavePath)){
            savePath =  sSavePath;
        }else {
            savePath = context.getObbDir() + HttpConstants.DOWNLOAD_DIR;
        }

        if (!TextUtils.isEmpty(savePath) && !savePath.endsWith("/")) {
            savePath = savePath + "/";
        }
        return savePath;
    }

    public static void setDownloadPath(String path) {

        if (TextUtils.isEmpty(path)){
            return;
        }

        File file = new File(path);
        if (file != null && file.exists()) {
            sSavePath = path;
            return;
        }
        if (file != null && file.mkdirs()) {
            sSavePath = path;
        }

    }
}
