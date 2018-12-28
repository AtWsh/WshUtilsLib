package it.wsh.cn.common_http.http.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {
    public static void closeAll(Closeable...closeables){
        if(closeables == null){
            return;
        }
        for (Closeable closeable : closeables) {
            if(closeable!=null){
                try {
                    closeable.close();
                } catch (IOException e) {
                    HttpLog.w(e);
                }
            }
        }
    }
}
