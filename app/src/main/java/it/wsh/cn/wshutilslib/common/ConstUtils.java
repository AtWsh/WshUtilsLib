package it.wsh.cn.wshutilslib.common;

import android.util.Log;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/11
 *     desc  : 常量相关工具类
 * </pre>
 */
public class ConstUtils {

    private static final String TAG = "ConstUtils";

    private ConstUtils() {
        Log.w(TAG, "u can't instantiate me...");
    }

    /******************** 存储相关常量 ********************/
    /**
     * Byte与Byte的倍数
     */
    public static final int BYTE = 1;
    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;

    public enum MemoryUnit {
        BYTE,
        KB,
        MB,
        GB,
        INVALID
    }

}