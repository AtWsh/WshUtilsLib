package it.wsh.cn.common_pay.pay.callback;

public interface HdAuthenticationCallback {
    void onSuccess();
    void onError(int errCode);
}
