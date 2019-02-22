package it.wsh.cn.common_pay.pay;

import android.content.Context;

import it.wsh.cn.hdpay.HdPayManager;

public class PayConfig {

    private static String sWXAppid = "";

    public static void init(Context context, String wxAppid) {
        HdPayManager.getInstance().init(context);
        sWXAppid = wxAppid;
    }

    public static String getWXAppid() {
        return sWXAppid;
    }
}
