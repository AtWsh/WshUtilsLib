package it.wsh.cn.common_pay.nativepay.callback;

public interface OnPayOrderCreateRequestListenter {
    void onPayOrderCreateStart();
    void onPayOrderCreateSuccess();
    void onPayOrderCreateFailure();
}
