package it.wsh.cn.common_pay.nativepay;

import android.app.Activity;
import android.text.TextUtils;

import it.wsh.cn.common_pay.nativepay.callback.OnPayResultListenter;
import it.wsh.cn.common_pay.nativepay.strategy.AliPayStrategy;
import it.wsh.cn.common_pay.nativepay.strategy.PayContext;
import it.wsh.cn.common_pay.nativepay.strategy.WxPayStrategy;
import it.wsh.cn.common_pay.nativepay.util.NetWorkUtils;


public class PaymentOrder {

    private Activity mContext;
    private OnPayResultListenter onPayResultListenter;
    private String payWay;

    private static String TAG = "PaymentOrder";


    public PaymentOrder(Activity context){
        this.mContext = context;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public PaymentOrder toPay(OnPayResultListenter onPayResultListener) {
        this.onPayResultListenter = onPayResultListener;
        if(!NetWorkUtils.isConnected(mContext)){
            sendPayResult(PayResultCode.COMMON_NETWORK_NOT_AVAILABLE_ERR);
        }
        return this;
    }

    //处理支付那边传过来的一些值

    private void sendPayResult(int code) {
        if(TextUtils.isEmpty(payWay)){
            return;
        }
        switch (code){
            case PayResultCode.COMMON_PAY_OK:
                onPayResultListenter.onPaySuccess(payWay);
                break;
            case PayResultCode.COMMON_USER_CACELED_ERR:
                onPayResultListenter.onPayCancel(payWay);
                break;

                default:
                    onPayResultListenter.onPayFailure(payWay,code);
                    break;
        }
        //完了，可以在这里释放资源
        release();
    }
    /**
     * 支付入口
     */
    public void pay(String payData,String weChatAppId){
        PayContext payContext = null;

        PayCallBack callBack= new PayCallBack(){
            @Override
            public void onPayCallBack(int code) {
                //BHLog.i(TAG,"payCallback -->"+payWay + " code -->"+code);
                sendPayResult(code);
            }
        };

        if(payWay.contains("wechat")){
            WxPayStrategy wxPayStrategy = new WxPayStrategy(mContext,payData,callBack);
            wxPayStrategy.setWeChatAppId(weChatAppId);
            payContext = new PayContext(wxPayStrategy);
        }else if(payWay.contains("alipay")){
            payContext = new PayContext(new AliPayStrategy(mContext,payData,callBack));
        }
        if(payContext != null){
            payContext.pay();
        }else {
            //BHLog.i(TAG,"沒有可用的支付方式 "+payWay);
        }
    }

    private void release() {
    }

    public interface PayCallBack{
        void  onPayCallBack(int code);
    }
}
