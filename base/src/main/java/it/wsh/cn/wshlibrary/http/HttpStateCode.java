package it.wsh.cn.wshlibrary.http;

/**
 * author: wenshenghui
 * created on: 2018/8/2 12:06
 * description:
 */
public class HttpStateCode {

    /**
     * 正常返回
     */
    public static final int RESULT_OK = 200;

    /**
     * 请求失败1000~2000
     */
    public static final int ERROR_HTTPCLIENT_CREATE_FAILED = 1001;

    public static final int ERROR_GSON_PARSE = 1002;

    /**
     * onNext返回T为null
     */
    public static final int ERROR_ONNEXT_NULL = 1003;

    /**
     * Observable中返回onError
     */
    public static final int ERROR_SUBSCRIBE_ERROR = 1004;

    /**
     * path为空
     */
    public static final int ERROR_PATH_EMPTY = 1005;

    /**
     * onNext返回T为null
     */
    public static final int ERROR_ONNEXT_EXCEPTION = 1006;


    /**
     * DOWNLOAD相关  url为空
     */
    public static final int ERROR_DOWNLOAD_URL_IS_NULL = 1007;

    /**
     * DOWNLOAD相关  开启下载时，检测到正在下载
     */
    public static final int ERROR_IS_DOWNLOADING = 1009;

    /**
     * DOWNLOAD相关  retrofit的逻辑执行出错
     */
    public static final int ERROR_DOWNLOAD_RETROFIT = 1010;

    /**
     * DOWNLOAD相关  注册下载监听时，通知该任务不在下载中
     */
    public static final int ERROR_IS_NOT_DOWNLOADING = 1011;

    /**
     * DOWNLOAD相关  注册下载监听时，通知已经注册过
     */
    public static final int ERROR_HAS_REGIST = 1012;

    /**
     * DOWNLOAD相关  检查ContextLength时出错
     */
    public static final int ERROR_CHECK_LENGTH = 1013;

}
