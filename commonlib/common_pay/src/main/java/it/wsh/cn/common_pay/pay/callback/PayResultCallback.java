package it.wsh.cn.common_pay.pay.callback;

public interface PayResultCallback {
    void onPaySuccess(String payWay);
    void onPayCancel(String payWay);
    void onPayFailure(String payWay, int errCode);
}
