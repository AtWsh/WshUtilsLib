package it.wsh.cn.wshlibrary.http;

import okhttp3.MediaType;

/**
 * author: wenshenghui
 * created on: 2018/8/3 18:03
 * description:
 */
public class HttpConstants {

    public static final MediaType sJsonType = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType sTextType = MediaType.parse("text/plain");

    public static final String FILE_NAME = "file\"; filename=\"";
    public static final String FILE_DES = "file\"; filedes=\"";

    public static long SIZE_OF_CACHE = 50 * 1024 * 1024; // 50 MiB

    public static final String CACHE_CONTROL = "Cache-Control";

    public static final String HTTPS = "https";

    //下载相关
    public final static String DOWNLOAD_DIR = "/downlaod/";
}
