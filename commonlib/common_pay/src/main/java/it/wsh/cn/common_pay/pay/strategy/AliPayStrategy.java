package it.wsh.cn.common_pay.pay.strategy;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;

import java.util.Map;

import it.wsh.cn.common_pay.pay.PayResultCode;
import it.wsh.cn.common_pay.pay.PaymentOrder;
import it.wsh.cn.common_pay.pay.model.AliPayRes;
import it.wsh.cn.common_pay.pay.model.AliPayResult;
import it.wsh.cn.common_pay.pay.util.ThreadManager;

public class AliPayStrategy extends BasePayStrategy {

    private AliPayRes aliPayRes;

    private static final int PAY_RESULT_MSG = 0x12;

    private static final String TAG = AliPayStrategy.class.getSimpleName();

    public AliPayStrategy(Activity context,String payData, PaymentOrder.PayCallBack callBack){
        super(context,payData,callBack);
        aliPayRes = new Gson().fromJson(payData,AliPayRes.class);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what != PAY_RESULT_MSG){
                return;
            }
            ThreadManager.shutdown();
            AliPayResult result = new AliPayResult((Map<String, String>)msg.obj);
            switch (result.getResultStatus()) {
                case AliPayResult.PAY_OK_STATUS:
                    callBack.onPayCallBack(PayResultCode.COMMON_PAY_OK);
                    break;

                case AliPayResult.PAY_CANCLE_STATUS:
                    callBack.onPayCallBack(PayResultCode.COMMON_USER_CACELED_ERR);
                    break;

                case AliPayResult.PAY_FAILED_STATUS:
                    callBack.onPayCallBack(PayResultCode.COMMON_PAY_ERR);
                    break;

                case AliPayResult.PAY_WAIT_CONFIRM_STATUS:
                    callBack.onPayCallBack(PayResultCode.ALI_PAY_WAIT_CONFIRM_ERR);
                    break;

                case AliPayResult.PAY_NET_ERR_STATUS:
                    callBack.onPayCallBack(PayResultCode.ALI_PAY_NET_ERR);
                    break;

                case AliPayResult.PAY_UNKNOWN_ERR_STATUS:
                    callBack.onPayCallBack(PayResultCode.ALI_PAY_UNKNOW_ERR);
                    break;

                default:
                    callBack.onPayCallBack(PayResultCode.ALI_PAY_OTHER_ERR);
                    break;
            }
            mHandler.removeCallbacksAndMessages(null);
        }
    };

    @Override
    public void doPay() {
        Runnable payRun = new Runnable() {
            @Override
            public void run() {
                if(mContext == null || aliPayRes == null){
                    return;
                }
                PayTask task = new PayTask(mContext);
                if(aliPayRes != null && !TextUtils.isEmpty(aliPayRes.getOrderStr())){
                    Map<String,String> result = task.payV2(aliPayRes.getOrderStr(),true);
                    Message message = mHandler.obtainMessage();
                    message.what = PAY_RESULT_MSG;
                    message.obj = result;
                    mHandler.sendMessage(message);
                }else {
                    //BHLog.i(TAG,"alipayStrategy is null");
                    callBack.onPayCallBack(PayResultCode.ALI_PAY_GSON_TO_OBJ_ERROR);
                }
            }
        };
//        ThreadManager.getInstance().postLogicTask(payRun);
        ThreadManager.execute(payRun);
    }

}
