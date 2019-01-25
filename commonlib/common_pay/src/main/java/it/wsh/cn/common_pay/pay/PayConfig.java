package it.wsh.cn.common_pay.pay;

public class PayConfig {

    private static String sWXAppid = "";

    public static void init(String wxAppid) {
        sWXAppid = wxAppid;
    }

    public static String getWXAppid() {
        return sWXAppid;
    }
}
