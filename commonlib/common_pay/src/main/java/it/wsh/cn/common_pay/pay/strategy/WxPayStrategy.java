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
import it.wsh.cn.common_pay.pay.PayResultCode;
import it.wsh.cn.common_pay.pay.PaymentOrder;
import it.wsh.cn.common_pay.pay.model.WechatPayRes;


public class WxPayStrategy extends BasePayStrategy {

    private WechatPayRes wxPayRes;

    private LocalBroadcastManager broadcastManager;

    private static final String TAG = "WxPayStrategy";

    public static final String WECHAT_PAY_RESULT_ACTION = "com.tencent.mm.opensdk.WECHAT_PAY_RESULT_ACTION";
    public static final String WECHAT_PAY_RESULT_EXTRA = "com.tencent.mm.opensdk.WECHAT_PAY_RESULT_EXTRA";

    public WxPayStrategy(Activity mContext,String payData, PaymentOrder.PayCallBack callBack) {
        super(mContext,payData, callBack);
        wxPayRes = new Gson().fromJson(payData,WechatPayRes.class);
    }

    @Override
    public void doPay() {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, null);
        if(!api.isWXAppInstalled()){
            callBack.onPayCallBack(PayResultCode.WECHAT_NOT_INSTALLED_ERR);
            return;
        }
        
        if(!api.isWXAppSupportAPI()){
            callBack.onPayCallBack(PayResultCode.WECHAT_UNSUPPORT_ERR);
            return;
        }

        String wxAppid = PayConfig.getWXAppid();
        api.registerApp(wxAppid);
        registerPayResultBroadcast();
        if(wxPayRes != null){
            PayReq request = new PayReq();
            request.appId = wxAppid;//微信开放平台审核通过的应用APPID
            request.partnerId = wxPayRes.getPartnerid();//微信支付分配的商户号
            request.prepayId = wxPayRes.getPrepayid();//微信返回的支付交易会话ID
            request.packageValue = wxPayRes.getPackageValue();//暂填写固定值Sign=WXPay
            request.nonceStr = wxPayRes.getNoncestr();//随机字符串，不长于32位。推荐随机数生成算法
            request.timeStamp = wxPayRes.getTimestamp();//时间戳，请见接口规则-参数规定
            request.sign = wxPayRes.getSign();//wxPayRes详见签名生成算法
            api.sendReq(request);//api.openWXApp();
        }else{
            //BHLog.i(TAG,"wxPayRes is null ");
            callBack.onPayCallBack(PayResultCode.WECHAT_GSON_TO_OBJECT_ERR);
        }
    }

    private void registerPayResultBroadcast() {
        broadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter filter = new IntentFilter(WECHAT_PAY_RESULT_ACTION);
        broadcastManager.registerReceiver(receiver,filter);
    }

    private void unRegistPayResultBroadcast() {
        if (broadcastManager != null && receiver != null) {
            broadcastManager.unregisterReceiver(receiver);
            broadcastManager = null;
            receiver = null;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null){
                return;
            }
            int code = intent.getIntExtra(WECHAT_PAY_RESULT_EXTRA, -100);
            callBack.onPayCallBack(code);

            unRegistPayResultBroadcast();
        }
    };
}