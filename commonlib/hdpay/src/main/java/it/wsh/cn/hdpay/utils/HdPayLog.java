package it.wsh.cn.hdpay.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import it.wsh.cn.common_http.BuildConfig;


/**
 * author: wenshenghui
 * created on: 2019/2/21 11:10
 * description:
 */
public class HdPayLog {

    private static final String LOG_TAG = "HdPayLog";


    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(LOG_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(LOG_TAG, msg);
        }
    }

    public static void v(String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, msg);
        }
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(LOG_TAG, msg);
        }
    }

    public static void e(Throwable e) {
        if (BuildConfig.DEBUG) {
            e(getThrowableString(e));
        }
    }

    public static void w(Throwable e) {
        if (BuildConfig.DEBUG) {
            w(getThrowableString(e));
        }
    }

    private static String getThrowableString(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        while (e != null) {
            e.printStackTrace(printWriter);
            e = e.getCause();
        }

        String text = writer.toString();

        printWriter.close();

        return text;
    }
}