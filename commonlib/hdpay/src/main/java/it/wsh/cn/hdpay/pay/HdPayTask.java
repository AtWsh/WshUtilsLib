package it.wsh.cn.hdpay.pay;

import android.content.Context;
import android.text.TextUtils;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.hdpay.HdPayManager;
import it.wsh.cn.hdpay.HdPayResultCode;
import it.wsh.cn.hdpay.model.HdAuthenticationReq;
import it.wsh.cn.hdpay.model.HdAuthenticationRsp;
import it.wsh.cn.hdpay.model.HdPayInfo;
import it.wsh.cn.hdpay.model.HdQrcodePayReq;
import it.wsh.cn.hdpay.model.HdQrcodePayRsp;
import it.wsh.cn.hdpay.ui.PasswordActivity;
import it.wsh.cn.hdpay.utils.HdPayLog;

public class HdPayTask {

    private HdPayInfo mHdPayReq;

    public HdPayTask(HdPayInfo req) {
        mHdPayReq = req;
    }

    //发起鉴权
    public void Pay() {
        //todo 一： authorizaton需要确认如何产生； 二：正式逻辑  需要传递mHdPayReq
        if (mHdPayReq == null) {
            HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAYREQ_NULL_ERR, "");
            return;
        }

        boolean qrcodePay = !TextUtils.isEmpty(mHdPayReq.qr_code);
        if (qrcodePay) {
            //todo 这里开启轮询
            doQrcodePayPolling();
        } else {
            doAuthentication();
        }


    }

    private void doQrcodePayPolling() {
        HdQrcodePayReq hdQrcodePayReq = new HdQrcodePayReq();
        hdQrcodePayReq.qr_code = mHdPayReq.qr_code;
        hdQrcodePayReq.wallet_id = mHdPayReq.wallet_id;

        new HdQrcodePayRsp.Builder()
                //todo 正式接口，需要放开此处
                /* .addBodyObj(hdQrcodePayReq)*/
                .build(new HttpCallBack<HdQrcodePayRsp>() {
                    @Override
                    public void onSuccess(HdQrcodePayRsp hdQrcodePayRsp) {
                        HdPayLog.d("doQrcodePayPolling onSuccess");
                        if (hdQrcodePayRsp == null) {
                            HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAYRSP_ERR, "");
                        } else {
                            //获取成功，调起密码界面。 此处已是主线程
                            startPasswordActivity();
                        }
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        HdPayLog.d("doQrcodePayPolling ERROR stateCode = " + stateCode + "; errorInfo :" + errorInfo);
                        HdPayManager.getInstance().sendPayResultBroadcast(stateCode, errorInfo);
                    }
                });
    }

    private void doAuthentication() {
        HdAuthenticationReq hdAuthenticationReq = new HdAuthenticationReq(mHdPayReq);
        new HdAuthenticationRsp.Builder()/*.addBodyObj(hdAuthenticationReq)*/.build(new HttpCallBack<HdAuthenticationRsp>() {
            @Override
            public void onSuccess(HdAuthenticationRsp rsp) {
                HdPayLog.d("authorizaton onSuccess");
                if (rsp == null) {
                    HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAYRSP_ERR, "");
                } else {
                    //鉴权成功，调起密码界面。 此处已是主线程
                    startPasswordActivity();
                }
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                HdPayManager.getInstance().sendPayResultBroadcast(stateCode, errorInfo);
                HdPayLog.d("authorizaton ERROR stateCode = " + stateCode + "; errorInfo :" + errorInfo);
            }
        });
    }

    private void startPasswordActivity() {
        Context context = HdPayManager.getInstance().getContext();
        if (context == null) {
            HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAY_CONTEXT_ERR, "");
            return;
        }
        PasswordActivity.luanchActivity(context);
    }
}
