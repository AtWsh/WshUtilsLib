package it.wsh.cn.wshutilslib.islandwifi;


import it.wsh.cn.wshutilslib.mvp.IBaseView;

public interface IOnekeyView extends IBaseView {
    void callbackResult(int result);

    void callOneKeyInternetWifiState(boolean wifiCanUse);
}
