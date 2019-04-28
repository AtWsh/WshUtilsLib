package it.wsh.cn.wshutilslib.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.haha.perflib.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import it.wsh.cn.wshutilslib.MainApplication;


/**
 *****************************************************************************
 * Copyright (C) 2018-2023 HD Corporation. All rights reserved
 * File        : NetworkUtils.java
 *
 * Description : 网络公共相关接口
 *
 * Creation    : 2018-11-23
 * Author      : zhoulong1@evergrande.cn
 * Notes：
 * 1，提供wifi和移动网络查询
 *****************************************************************************
 */
public final class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * 获取活动网络信息
     *
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager manager =
                (ConnectivityManager) MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return null;
        return manager.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 判断网络是否可用
     *
     * <p>需要异步 ping，如果 ping 不通就说明网络不可用</p>
     * <p>ping 的 ip 为阿里巴巴公共 ip：223.5.5.5</p>
     *
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailableByPing() {
        return isAvailableByPing(null);
    }

    /**
     * 判断网络是否可用
     *
     * <p>需要异步 ping，如果 ping 不通就说明网络不可用</p>
     *
     * @param ip ip 地址（自己服务器 ip），如果为空，ip 为阿里巴巴公共 ip
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailableByPing(String ip) {
        if (ip == null || ip.length() <= 0) {
            ip = "223.5.5.5";// 阿里巴巴公共 ip
        }
        ShellUtils.CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false);
        boolean ret = result.result == 0;
        if (result.errorMsg != null) {
            Log.i(TAG, "isAvailableByPing() called" + result.errorMsg);
        }
        if (result.successMsg != null) {
            Log.i(TAG, "isAvailableByPing() called" + result.successMsg);
        }
        return ret;
    }

    /**
     * 判断移动数据是否打开
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getMobileDataEnabled() {
        try {
            TelephonyManager tm =
                    (TelephonyManager) MainApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return false;
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 打开或关闭移动数据
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setMobileDataEnabled(final boolean enabled) {
        try {
            TelephonyManager tm =
                    (TelephonyManager) MainApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return;
            Method setMobileDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(tm, enabled);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 判断网络是否是移动数据
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    @SuppressLint("MissingPermission")
    public static boolean isMobileData() {
        NetworkInfo info = getActiveNetworkInfo();
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 判断网络是否是 4G
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null
                && info.isAvailable()
                && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断 wifi 是否打开
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getWifiEnabled() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) MainApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        return manager != null && manager.isWifiEnabled();
    }

    /**
     * 打开或关闭 wifi
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    @SuppressLint("MissingPermission")
    public static void setWifiEnabled(final boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) MainApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) return;
        if (enabled) {
            if (!manager.isWifiEnabled()) {
                manager.setWifiEnabled(true);
            }
        } else {
            if (manager.isWifiEnabled()) {
                manager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断 wifi 是否连接状态
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm !=null){
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * 判断 wifi 数据是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailableByPing();
    }

    /**
     * 获取网络运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 运营商名称
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm =
                (TelephonyManager) MainApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     * 获取当前网络类型
     * @return 网络类型
     * <ul>
     * <li>{@link NetworkType#NETWORK_WIFI   } </li>
     * <li>{@link NetworkType#NETWORK_4G     } </li>
     * <li>{@link NetworkType#NETWORK_3G     } </li>
     * <li>{@link NetworkType#NETWORK_2G     } </li>
     * <li>{@link NetworkType#NETWORK_UNKNOWN} </li>
     * <li>{@link NetworkType#NETWORK_NO     } </li>
     * </ul>
     */
    public static NetworkType getNetworkType() {
        NetworkType netType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NetworkType.NETWORK_4G;
                        break;
                    default:

                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     * 获取 IP 地址,可能是wifi或移动网络的
     *
     * @param useIPv4 是否用 IPv4
     * @return IP 地址
     */
    public static String getIPAddress(final boolean useIPv4) {
        try {
            for (Enumeration<NetworkInterface> nis =
                 NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                // 防止小米手机返回 10.0.2.15
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return hostAddress;
                        } else {
                            if (!isIPv4) {
                                int index = hostAddress.indexOf('%');
                                return index < 0
                                        ? hostAddress.toUpperCase()
                                        : hostAddress.substring(0, index).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
        }
        return null;
    }

    /**
     * 获取域名 ip 地址
     *
     * @param domain 域名
     * @return ip 地址
     */
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * ping baidu.com网络返回时间，供业务层确认网络速度
     *
     * @return ping通时间，单位毫秒
     */
    public static int ping() {
        String  result = null;
        int delayTime = 10000;
        try {
            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);//ping3次
            // 读取ping的内容，可不加。
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                //stringBuffer.append(content);
                if(content.contains("avg")){
                    int i = content.indexOf("/",20);
                    int j = content.indexOf(".", i);
                    String delay = content.substring(i+1, j);
                    Log.i(TAG,"delay: "+ delay);
                    if(!TextUtils.isEmpty(delay)){
                        delayTime = Integer.parseInt(delay);
                    }
                }
                Log.i(TAG,"speed log: "+content );
            }
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
            } else {
                delayTime = -1;
            }
        } catch (IOException e) {
            delayTime = -1;
        } catch (InterruptedException e) {
            delayTime = -1;
        }
        return delayTime;
    }

    //获取设备wifi ip
    public static String getWifiIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) MainApplication.getContext()
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }
}
