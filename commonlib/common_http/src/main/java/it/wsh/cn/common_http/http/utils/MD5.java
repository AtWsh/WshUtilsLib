package it.wsh.cn.common_http.http.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author: wenshenghui
 * created on: 2019/1/8 17:56
 * description:
 */
public class MD5 {

    /**
     * 得到strSrc的文件MD5信息
     */
    public static String strMD5(String strSrc) {
        MessageDigest md = null;
        byte[] bt = strSrc.getBytes();
        String strDes = null;

        try {
            md = MessageDigest.getInstance("MD5");
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            HttpLog.e(e.getMessage());
        }
        return strDes;
    }

    /**
     * 二行制转hex字符串
     */
    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
