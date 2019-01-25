package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;

import it.wsh.cn.common_pay.pay.PaymentOrder;


public abstract class BasePayStrategy{

    protected String payData;
    protected PaymentOrder.PayCallBack callBack;
    protected Activity mContext;

    public BasePayStrategy(Activity mContext,String payData, PaymentOrder.PayCallBack callBack){
        this.mContext = mContext;
        this.payData = payData;
        this.callBack = callBack;
    }

    public void hdAuthentication() {

    }


    public abstract void doPay();
}
