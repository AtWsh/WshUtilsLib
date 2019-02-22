package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import it.wsh.cn.common_pay.pay.PayConfig;
import it.wsh.cn.common_pay.pay.PayConstants;
import it.wsh.cn.common_pay.pay.PayResultCode;
import it.wsh.cn.common_pay.pay.callback.PayCallBack;
import it.wsh.cn.common_pay.pay.model.WechatPayRes;


public class WxPayStrategy extends BasePayStrategy {

    private WechatPayRes mPayRes;

    public WxPayStrategy(Activity mContext, String payData, PayCallBack callBack) {
        super(mContext, payData, callBack);
        mPayRes = new Gson().fromJson(payData, WechatPayRes.class);
    }

    @Override
    public void doPay() {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, null);
        if (!api.isWXAppInstalled()) {
            mCallBack.onPayCallBack(PayResultCode.WECHAT_NOT_INSTALLED_ERR);
            return;
        }

        if (!api.isWXAppSupportAPI()) {
            mCallBack.onPayCallBack(PayResultCode.WECHAT_UNSUPPORT_ERR);
            return;
        }

        String wxAppid = PayConfig.getWXAppid();
        api.registerApp(wxAppid);
        registerPayResultBroadcast();
        if (mPayRes != null) {
            PayReq request = new PayReq();
            request.appId = wxAppid;//微信开放平台审核通过的应用APPID
            request.partnerId = mPayRes.getPartnerid();//微信支付分配的商户号
            request.prepayId = mPayRes.getPrepayid();//微信返回的支付交易会话ID
            request.packageValue = mPayRes.getPackageValue();//暂填写固定值Sign=WXPay
            request.nonceStr = mPayRes.getNoncestr();//随机字符串，不长于32位。推荐随机数生成算法
            request.timeStamp = mPayRes.getTimestamp();//时间戳，请见接口规则-参数规定
            request.sign = mPayRes.getSign();//wxPayRes详见签名生成算法
            api.sendReq(request);//api.openWXApp();
        } else {
            //BHLog.i(TAG,"wxPayRes is null ");
            mCallBack.onPayCallBack(PayResultCode.WECHAT_GSON_TO_OBJECT_ERR);
        }
    }

    private void registerPayResultBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter(PayConstants.WECHAT_PAY_RESULT_ACTION);
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
            int code = intent.getIntExtra(PayConstants.WECHAT_PAY_RESULT_EXTRA, -100);
            mCallBack.onPayCallBack(code);

            unRegistPayResultBroadcast();
        }
    };

    @Override
    public void clear() {
        unRegistPayResultBroadcast();
    }
}