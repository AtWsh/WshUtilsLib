package it.wsh.cn.common_utils.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by guotingzhu@evergrande.cn on 2017/10/11.
 */

public class SharedPreferenceHelper {


    public static SharedPreferences.Editor getEditor(Context context, String file) {
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sp.edit();
    }


    public static SharedPreferences getSp(Context context, String file) {
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sp;
    }

    public static void saveString(Context context, String file, String name, String value) {
        SharedPreferences.Editor editor = getEditor(context, file);
        editor.putString(name, value);
        editor.commit();
    }

    public static void saveLong(Context context, String file, String name, long value) {
        SharedPreferences.Editor editor = getEditor(context, file);
        editor.putLong(name, value);
        editor.commit();
    }

    public static void saveBoolean(Context context, String file, String name, boolean value) {
        SharedPreferences.Editor editor = getEditor(context, file);
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static String getString(Context context, String file, String name) {
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sp.getString(name, "");
    }

    public static long getLong(Context context, String file, String name) {
        SharedPreferences sp = getSp(context, file);
        return sp.getLong(name, 0);
    }

    public static boolean getBoolean(Context context, String file, String name) {
        SharedPreferences sp = getSp(context, file);
        return sp.getBoolean(name, false);
    }
}
