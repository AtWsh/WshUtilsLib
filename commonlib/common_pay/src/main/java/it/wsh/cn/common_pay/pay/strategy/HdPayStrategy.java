package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import it.wsh.cn.common_pay.pay.PayResultCode;
import it.wsh.cn.common_pay.pay.callback.PayCallBack;
import it.wsh.cn.hdpay.model.HdPayInfo;
import it.wsh.cn.hdpay.pay.HdPayTask;
import it.wsh.cn.hdpay.utils.HdPayConstants;

public class HdPayStrategy extends BasePayStrategy {

    private HdPayInfo mHdPayReq;
    private HdPayTask mHdPayTask;

    public HdPayStrategy(Activity mContext, String payData, PayCallBack callBack) {
        super(mContext, payData, callBack);
        mHdPayReq = new Gson().fromJson(payData, HdPayInfo.class);
    }

    public HdPayStrategy(Activity mContext, HdPayInfo hdPayInfo, PayCallBack callBack) {
        super(mContext, callBack);
        mHdPayReq = hdPayInfo;
    }

    @Override
    public void doPay() {
        if (mHdPayReq == null) {
            if (mCallBack != null) {
                mCallBack.onPayCallBack(PayResultCode.HD_PAY_REQUEST_INFO_ERR);
            }
            return;
        }
        registerPayResultBroadcast();
        mHdPayTask = new HdPayTask(mHdPayReq);
        mHdPayTask.Pay();

    }

    private void registerPayResultBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter(HdPayConstants.HD_PAY_RESULT_ACTION);
        broadcastManager.registerReceiver(receiver, filter);
    }

    private void unRegistPayResultBroadcast() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            int code = intent.getIntExtra(HdPayConstants.HD_PAY_RESULT_EXTRA_CODE, -100);
            String info = intent.getStringExtra(HdPayConstants.HD_PAY_RESULT_EXTRA_INFO);
            mCallBack.onPayCallBack(code);

            unRegistPayResultBroadcast();
        }
    };

    @Override
    public void clear() {
        unRegistPayResultBroadcast();
    }
}
