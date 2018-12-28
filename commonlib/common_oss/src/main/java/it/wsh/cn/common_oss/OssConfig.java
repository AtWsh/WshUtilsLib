package it.wsh.cn.common_oss;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: wenshenghui
 * created on: 2018/12/10 16:13
 * description:
 */
public class OssConfig {
    private static List<String> sOssUrlPrefix = new ArrayList<>();

    public static void addOssUrlPrefix(String prefix) {
        if (TextUtils.isEmpty(prefix)) {
            return;
        }
        sOssUrlPrefix.add(prefix);
    }

    public static boolean isOssUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (sOssUrlPrefix == null || sOssUrlPrefix.size() <= 0) {
            return false;
        }
        for (int i = 0; i < sOssUrlPrefix.size(); i++) {
           if (url.startsWith(sOssUrlPrefix.get(i))) {
               return true;
           }
        }
        return false;
    }
}
