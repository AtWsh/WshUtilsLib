package it.wsh.cn.common_pay.nativepay.callback;

public interface OnPayResultListenter {
    void onPaySuccess(String payWay);
    void onPayCancel(String payWay);
    void onPayFailure(String payWay, int errCode);
    void onPayWayNonSupport(String payWay);
}
