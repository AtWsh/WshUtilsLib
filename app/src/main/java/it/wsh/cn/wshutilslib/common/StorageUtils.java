package it.wsh.cn.wshutilslib.common;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

import it.wsh.cn.wshutilslib.MainApplication;

/**
 *****************************************************************************
 * Copyright (C) 2018-2023 HD Corporation. All rights reserved
 * File        : StorageUtils.java
 *
 * Description : 操作外部存储接口公共接口
 *
 * Creation    : 2018-11-7
 * Author      : zhoulong1@evergrande.cn
 * Notes：
 * 1,SD Card 全称Secure Digital Memory Card
 * 2,APP存储缺省的路径为sdcard/应用名称
 * 3,批量删除存储文件
 *****************************************************************************
 */

public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static final String  APP_NAME = "smartVillage";
    private static final String  APP_CACHE = "cache";

    private StorageUtils() {

        Log.w(TAG, "u can't instantiate me...");
    }

    /**
     * 获取SD卡路径
     *
     * @return SD卡绝对路径,格式/storage/emulated/0/
     */
    public static String getSDCardPath() {
        if (!isSDCardEnable()) return null;
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取以应用名称为后缀的外部存储路径
     *
     * @return 外部存储绝对路径,格式/storage/emulated/0/app_name/
     */
    public static String getAPPStoragePath() {
        String path = null;

        if(isSDCardEnable()){
            path = getSDCardPath() + APP_NAME + File.separator;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
        }

        return path;
    }

    /**
     * 获取以应用名称为后缀的外部存储路径
     * @param subPath 子路径
     * @return 外部存储绝对路径,格式/storage/emulated/0/app_name/{subPath}
     */
    public static String getAPPStoragePath(String subPath) {
        String path = null;

        if(isSDCardEnable()){
            path = getAPPStoragePath() + subPath;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
        }

        return path;
    }

    /**
     * 获取应用名称下的cache路径
     *
     * @return 外部cache存储绝对路径,格式/storage/emulated/0/app_name/cache/
     */
    public static String getAPPCachePath() {
        String path = null;

        if(isSDCardEnable()){
            path = getAPPStoragePath() + APP_CACHE + File.separator;
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
        }

        return path;
    }

    /**
     * 获取应用名称下的image路径
     *
     * @return 外部cache存储绝对路径,格式/storage/emulated/0/app_name/image/
     */
    public static String getAPPImagePath() {
        String path = null;
        File directory;
        if(isSDCardEnable()){

            directory = MainApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!directory.exists()) directory.mkdirs();
            path = FileUtils.getDirName(directory);
        }

        return path;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用；false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 清除内部文件
     * <p>/data/data/com.xxx.xxx/files</p>
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanInternalFiles() {
        return FileUtils.deleteFilesInDir(MainApplication.getContext().getFilesDir());
    }
    /**
     * 清除内部缓存
     * <p>/data/data/com.xxx.xxx/cache</p>
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanInternalCache() {
        return FileUtils.deleteFilesInDir(MainApplication.getContext().getCacheDir());
    }

    /**
     * 清除内部数据库
     * <p>/data/data/com.xxx.xxx/databases</p>
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanInternalDbs() {
        return FileUtils.deleteFilesInDir(new File(MainApplication.getContext().getFilesDir().getParent(), "databases"));
    }

    /**
     * 根据名称清除数据库
     * <p>/data/data/com.xxx.xxx/databases/dbName</p>
     *
     * @param dbName 数据库名称
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanInternalDbByName(final String dbName) {
        return MainApplication.getContext().deleteDatabase(dbName);
    }

    /**
     * 清除内部 SP
     * <p>/data/data/com.xxx.xxx/shared_prefs</p>
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanInternalSP() {
        return FileUtils.deleteFilesInDir(new File(MainApplication.getContext().getFilesDir().getParent(), "shared_prefs"));
    }

    /**
     * 清除外部缓存
     * <p>/storage/emulated/0/android/data/com.xxx.xxx/cache</p>
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static boolean cleanExternalCache() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && FileUtils.deleteFilesInDir(MainApplication.getContext().getExternalCacheDir());
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间，返回带单位的字符串
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getFreeSpace() {
        if (!isSDCardEnable()) return "sdcard unable!";
        StatFs stat = new StatFs(getSDCardPath());
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize);
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间，单位为MB
     */
    public static  long getSDFreeSize(){
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static SDCardInfo getSDCardInfo() {
        SDCardInfo sd = new SDCardInfo();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sd.isExist = true;
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            sd.totalBlocks = sf.getBlockCountLong();
            sd.blockByteSize = sf.getBlockSizeLong();
            sd.availableBlocks = sf.getAvailableBlocksLong();
            sd.availableBytes = sf.getAvailableBytes();
            sd.freeBlocks = sf.getFreeBlocksLong();
            sd.freeBytes = sf.getFreeBytes();
            sd.totalBytes = sf.getTotalBytes();
        }
        return sd;
    }

    private static class SDCardInfo {
        boolean isExist;
        long totalBlocks;
        long freeBlocks;
        long availableBlocks;

        long blockByteSize;

        long totalBytes;
        long freeBytes;
        long availableBytes;

        @Override
        public String toString() {
            return "SDCardInfo{" +
                    "isExist=" + isExist +
                    ", totalBlocks=" + totalBlocks +
                    ", freeBlocks=" + freeBlocks +
                    ", availableBlocks=" + availableBlocks +
                    ", blockByteSize=" + blockByteSize +
                    ", totalBytes=" + totalBytes +
                    ", freeBytes=" + freeBytes +
                    ", availableBytes=" + availableBytes +
                    '}';
        }
    }
}