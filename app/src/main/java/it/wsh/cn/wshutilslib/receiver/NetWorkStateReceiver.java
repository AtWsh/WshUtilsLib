package it.wsh.cn.wshutilslib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import it.wsh.cn.wshutilslib.common.NetworkUtils;


public class NetWorkStateReceiver extends BroadcastReceiver {
    private static String TAG = "NetWorkState";
    private boolean isForeground = true;

    private static NetIsConnectLisenter netIsConnectLisenter;

    public interface NetIsConnectLisenter {
        void isNetConnect(boolean isNetConnect);
    }

    public static void setNetIsConnectLisenter(NetIsConnectLisenter netIsConnectLisen) {
        if (netIsConnectLisen != null) {
            netIsConnectLisenter = netIsConnectLisen;
        }
    }

    private void setNetIsConnect(boolean isNetConnect) {
        if (netIsConnectLisenter != null) {
            netIsConnectLisenter.isNetConnect(isNetConnect);
        }
    }

    public static void removeNetIsConnectListener() {
        if (netIsConnectLisenter != null) {
            netIsConnectLisenter = null;
        }
    }

    /**
     * 判断当前是否有网络连接,但是如果该连接的网络无法上网，也会返回true
     *
     * @param mContext
     * @return
     */
    public static boolean isNetConnection(Context mContext) {
        if (mContext != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                setNetIsConnect(true);
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                setNetIsConnect(true);
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                setNetIsConnect(true);
            } else {
                setNetIsConnect(false);
            }
//API大于23时使用下面的方式进行网络监听
        } else {

            System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();


            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.delete(0, sb.length());
                if (networkInfo != null) {
                    if ("MOBILE".equalsIgnoreCase(networkInfo.getTypeName())) {
//                        ToastUtil.showShort(context.getString(R.string.mobile_network), isForeground);
                        setNetIsConnect(true);
                    } else if ("WIFI".equalsIgnoreCase(networkInfo.getTypeName())) {
//                        ToastUtil.showShort(context.getString(R.string.wifi_network), isForeground);
                        setNetIsConnect(true);
                    }
                }
                //   sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                //  Toast.makeText(context,sb.toString(),Toast.LENGTH_SHORT).show();
            }

            if (networks.length == 0 || !NetworkUtils.isWifiConnected()) {
                setNetIsConnect(false);
//                ToastUtil.showShort(context.getString(R.string.no_network),isForeground);
            }
        }
    }
}
