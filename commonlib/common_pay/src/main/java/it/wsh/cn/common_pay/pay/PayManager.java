package it.wsh.cn.common_pay.pay;

import android.content.Context;

import it.wsh.cn.hdpay.HdPayManager;

public class PayManager {

    private static String sWXAppid = "";

    public static void init(Context context, String wxAppid, String frontType) {
        sWXAppid = wxAppid;

        HdPayManager.getInstance().init(context, frontType);
    }

    public static void setOrUpdateAuthorization(String authorization) {
        HdPayManager.getInstance().setOrUpdateAuthorization(authorization);
    }

    public static String getWXAppid() {
        return sWXAppid;
    }
}
