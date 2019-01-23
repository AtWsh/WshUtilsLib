package it.wsh.cn.common_pay.nativepay.strategy;

import android.app.Activity;

import it.wsh.cn.common_pay.nativepay.PaymentOrder;


public abstract class BasePayStrategy implements IPayStrategy {

    protected String payData;
    protected PaymentOrder.PayCallBack callBack;
    protected Activity mContext;

    public BasePayStrategy(Activity mContext,String payData, PaymentOrder.PayCallBack callBack){
        this.mContext = mContext;
        this.payData = payData;
        this.callBack = callBack;
    }

    public abstract void doPay();
}
