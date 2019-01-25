package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;

import it.wsh.cn.common_pay.pay.PaymentOrder;

public class HdPayStrategy extends BasePayStrategy {

    public HdPayStrategy(Activity mContext, String payData, PaymentOrder.PayCallBack callBack) {
        super(mContext, payData, callBack);
    }

    @Override
    public void doPay() {

    }
}
