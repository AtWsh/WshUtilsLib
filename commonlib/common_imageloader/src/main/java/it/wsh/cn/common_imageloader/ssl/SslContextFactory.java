package it.wsh.cn.common_imageloader.ssl;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * author: wenshenghui
 * created on: 2018/12/12 9:50
 * description:
 */
public class SslContextFactory {
    private static final String TAG = "SslContextFactory";
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            //BHLog.e(TAG,e);
        }

        return ssfFactory;
    }
}
