package it.wsh.cn.wshutilslib.islandwifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.ResourceSubscriber;
import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.wshutilslib.MainApplication;
import it.wsh.cn.wshutilslib.common.NetworkUtils;
import it.wsh.cn.wshutilslib.common.ObjectUtils;
import it.wsh.cn.wshutilslib.islandwifi.model.IslandWifiRequest;
import it.wsh.cn.wshutilslib.islandwifi.model.IslandWifiRsp;
import it.wsh.cn.wshutilslib.mvp.BasePresenter;
import it.wsh.cn.wshutilslib.rx.RXManager;

import static android.content.Context.WIFI_SERVICE;


public class OnekeyInternetPresenter extends BasePresenter<IOnekeyView> {
    private static final String TAG = "OnekeyInternetPresenter";

    private List<String> CONNECTED_WIFI_NAMES = new ArrayList<>();
    public OnekeyInternetPresenter() {
        CONNECTED_WIFI_NAMES.add("XLSC");
        CONNECTED_WIFI_NAMES.add("OceanFlower-Office");
        CONNECTED_WIFI_NAMES.add("OceanFlower-Portal");
        CONNECTED_WIFI_NAMES.add("OceanFlower-Special");
    }

    /**
     * 网络验证
     *
     * @param url
     * @param request
     */
    public void requestInternet(String url, IslandWifiRequest request) {
        if (TextUtils.isEmpty(url) || request == null) {
            return;
        }

        new IslandWifiRsp.Builder(url).setTag(mView).addBodyObj(request).build(new HttpCallBack<IslandWifiRsp>() {
            @Override
            public void onSuccess(IslandWifiRsp islandWifiRsp) {
                int code = islandWifiRsp.code;
                String resultMsg = islandWifiRsp.Message;
                mView.callbackResult(code);
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                if (mView != null) {
                    mView.callbackResult(OnekeyInternetVillageActivity.RESPONSE_RESULT_ERR_1001_CODE);
                    Toast.makeText(MainApplication.getContext(), TextUtils.isEmpty(errorInfo) ? "请求异常" : errorInfo, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getConnectWifiSsid() {
        String wifiName = "";
        WifiManager wifiManager = (WifiManager) MainApplication.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfoS = wifiManager.getConnectionInfo();
            if (wifiInfoS != null) {
                wifiName = wifiInfoS.getSSID();
            }
        }

        if (TextUtils.isEmpty(wifiName) || wifiName.contains("unknown")) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            wifiName = wifiInfo.getExtraInfo();
        }
        if (wifiName != null) wifiName = wifiName.replace("\"", "");
        return wifiName;
    }

    /**
     * wsapath是否有效
     */
    public boolean isWsapathValid(String wsapath) {
        if (ObjectUtils.isNotEmpty(wsapath)) {
            wsapath = wsapath.replace("\"", "");
        }
        return ObjectUtils.isNotEmpty(wsapath) && !wsapath.equalsIgnoreCase("null");
    }

    /**
     * 恒大wifi是否已连接，不能判断是否能真正上网
     */
    public boolean isHengDaWifiConnected() {
        return CONNECTED_WIFI_NAMES.contains(getConnectWifiSsid()) && NetworkUtils.isWifiConnected();
    }

    /**
     * 请求Wifi的上网状态，判断它是否能用
     */
    public void pingNetIfHengdaWifiConnected() {
        RXManager.get().doTimeConsuming(Flowable.just("").map(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String s) {
                boolean isWifiCanUse = false;
                if (isHengDaWifiConnected()) {
                    isWifiCanUse = isNetConnected();//wifi是否能连接真正的网络
                    Log.i(TAG, "pingNetIfHengdaWifiConnected-isWifiCanUse" + isWifiCanUse);
                }
                return isWifiCanUse;
            }
        }), new ResourceSubscriber<Boolean>() {

            @Override
            public void onNext(Boolean aBoolean) {
                if (mView != null) {
                    mView.callOneKeyInternetWifiState(aBoolean);
                }
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private boolean isNetConnected() {
        boolean result = false;
        Log.i(TAG,"isNetConnected enter");
        try {
            URL url = new URL("http://www.baidu.com");
            //获取一个连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置GET请求
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            //设置超时时间
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            Log.i(TAG, "yyy:responseCode"+responseCode + "");

            String redirectedURL = connection.getHeaderField("Location");
            boolean isIPAddress = isIPAddress(redirectedURL);
            Log.i(TAG, "yyy:redirectedURL:"+redirectedURL + ".  isIpAddress: " + isIPAddress);

            if (200 == responseCode || (302 == responseCode && !isIPAddress)) {
                result = true;
            }

        } catch (Exception e) {
            Log.i(TAG,"ping exp = "+e.getMessage());
        }

        Log.i(TAG,"isNetConnected leave result = "+result);
        return result;
    }

    public boolean isIPAddress(String realUrl) {
        if (realUrl == null) {
            return false;
        }
        try {
            URL url = new URL(realUrl);
            String host = url.getHost();
            InetAddress address = InetAddress.getByName(host);
            return host.equalsIgnoreCase(address.getHostAddress());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (UnknownHostException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
}
