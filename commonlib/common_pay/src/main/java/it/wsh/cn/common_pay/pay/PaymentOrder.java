package it.wsh.cn.common_pay.pay;

import android.app.Activity;
import android.text.TextUtils;

import it.wsh.cn.common_pay.pay.callback.PayCallBack;
import it.wsh.cn.common_pay.pay.callback.PayResultCallback;
import it.wsh.cn.common_pay.pay.strategy.AliPayStrategy;
import it.wsh.cn.common_pay.pay.strategy.BasePayStrategy;
import it.wsh.cn.common_pay.pay.strategy.HdPayStrategy;
import it.wsh.cn.common_pay.pay.strategy.WxPayStrategy;
import it.wsh.cn.common_pay.pay.util.NetWorkUtils;
import it.wsh.cn.hdpay.model.HdPayInfo;


public class PaymentOrder {

    private static String TAG = "PaymentOrder";

    private Activity mContext;
    private PayResultCallback mPayResultListenter;
    private @PayWay.Type
    String mPayWay;
    private BasePayStrategy mCurrentPayStrategy;


    public PaymentOrder(Activity context, @PayWay.Type String payWay) {
        mContext = context;
        mPayWay = payWay;
    }

    /**
     * 统一的支付入口
     */
    public void pay(String payData, PayResultCallback onPayResultCallback) {
        mPayResultListenter = onPayResultCallback;
        if (!NetWorkUtils.isConnected(mContext)) {
            sendPayResult(PayResultCode.COMMON_NETWORK_NOT_AVAILABLE_ERR);
            return;
        }

        if (PayWay.WX_PAY.equals(mPayWay) && !hasWxAppid()) {
            sendPayResult(PayResultCode.WECHAT_WXAPPID_EMPTY_ERR);
            return;
        }

        PayCallBack callBack = new PayCallBack() {
            @Override
            public void onPayCallBack(int code) {
                //BHLog.i(TAG,"payCallback -->"+mPayWay + " code -->"+code);
                sendPayResult(code);
            }
        };

        if (PayWay.WX_PAY.equals(mPayWay)) {
            mCurrentPayStrategy = new WxPayStrategy(mContext, payData, callBack);
        } else if (PayWay.ALI_PAY.equals(mPayWay)) {
            mCurrentPayStrategy = new AliPayStrategy(mContext, payData, callBack);
        } else if (PayWay.HD_PAY.equals(mPayWay)) {
            mCurrentPayStrategy = new HdPayStrategy(mContext, payData, callBack);
        }

        if (mCurrentPayStrategy != null) {
            mCurrentPayStrategy.doPay();
        } else {
            sendPayResult(PayResultCode.COMMON_REQUEST_PAYWAY_ERR);
        }
    }

    public void hdQrcodePay(String qrcode, String walletId, PayResultCallback callback) {

        mPayResultListenter = callback;
        if (!NetWorkUtils.isConnected(mContext)) {
            sendPayResult(PayResultCode.COMMON_NETWORK_NOT_AVAILABLE_ERR);
            return;
        }

        if (TextUtils.isEmpty(qrcode) || TextUtils.isEmpty(walletId)) {
            sendPayResult(PayResultCode.INVALID_REQUEST_CODE);
            return;
        }

        PayCallBack callBack = new PayCallBack() {
            @Override
            public void onPayCallBack(int code) {
                //BHLog.i(TAG,"payCallback -->"+mPayWay + " code -->"+code);
                sendPayResult(code);
            }
        };

        HdPayInfo hdPayInfo = new HdPayInfo();
        hdPayInfo.wallet_id = walletId;
        hdPayInfo.qr_code = qrcode;
        mCurrentPayStrategy = new HdPayStrategy(mContext, hdPayInfo, callBack);
        mCurrentPayStrategy.doPay();
    }

    /**
     * 判断微信appId是否初始化
     *
     * @return
     */
    private boolean hasWxAppid() {
        if (TextUtils.isEmpty(PayManager.getWXAppid())) {
            return false;
        }
        return true;
    }


    /**
     * 发送支付结果
     *
     * @param code
     */
    private void sendPayResult(int code) {
        if (TextUtils.isEmpty(mPayWay)) {
            return;
        }
        if (mPayResultListenter == null) {
            return;
        }
        switch (code) {
            case PayResultCode.COMMON_PAY_OK:
                mPayResultListenter.onPaySuccess(mPayWay);
                break;
            case PayResultCode.COMMON_USER_CACELED_ERR:
                mPayResultListenter.onPayCancel(mPayWay);
                break;

            default:
                mPayResultListenter.onPayFailure(mPayWay, code);
                break;
        }
    }

    public void clear() {
        if (mCurrentPayStrategy != null) {
            mCurrentPayStrategy.clear();
        }
    }
}
