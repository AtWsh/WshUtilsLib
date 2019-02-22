package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;

import it.wsh.cn.common_pay.pay.callback.PayCallBack;


public abstract class BasePayStrategy {

    protected String mPayData;
    protected PayCallBack mCallBack;
    protected Activity mContext;

    public BasePayStrategy(Activity mContext, String payData, PayCallBack callBack) {
        this.mContext = mContext;
        this.mPayData = payData;
        this.mCallBack = callBack;
    }

    public BasePayStrategy(Activity mContext, PayCallBack callBack) {
        this.mContext = mContext;
        this.mCallBack = callBack;
    }

    public abstract void doPay();

    public abstract void clear();
}
