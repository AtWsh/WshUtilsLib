package it.wsh.cn.wshutilslib.common;

import android.os.Build;
import android.text.TextUtils;

public class RomUtils {
    public static final String BRAND_MIUI = "xiaomi";
    //EMUI标识
    public static final String BRAND_EMUI1 = "huawei";
    public static final String BRAND_EMUI2 = "honor";

    public static final String BRAND_360 = "360";
    public static final String BRAND_QIKU = "qiku";

    public static final String BRAND_SMARTISAN = "smartisan";

    public static boolean is360Rom() {
        String brand = Build.BRAND;
        if (TextUtils.isEmpty(brand)) {
            return false;
        }
        return brand.toLowerCase().contains(BRAND_360) || brand.toLowerCase().contains(BRAND_QIKU);
    }

    public static boolean isSmartisanRom() {
        if (TextUtils.isEmpty(Build.BRAND)) {
            return false;
        }
        return Build.BRAND.toLowerCase().contains(BRAND_SMARTISAN);
    }
}
