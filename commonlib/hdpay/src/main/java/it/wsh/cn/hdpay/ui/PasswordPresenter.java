package it.wsh.cn.hdpay.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.hdpay.HdPayManager;
import it.wsh.cn.hdpay.HdPayResultCode;
import it.wsh.cn.hdpay.model.HdCancelPayReq;
import it.wsh.cn.hdpay.model.HdCancelPayRsp;
import it.wsh.cn.hdpay.model.HdPasswordPayReq;
import it.wsh.cn.hdpay.model.HdPasswordPayRsp;
import it.wsh.cn.hdpay.utils.HdPayCodeUtils;
import it.wsh.cn.hdpay.utils.HdPayConstants;

public class PasswordPresenter {

    private Activity mView;
    private int mErrorTimes = 0;
    private final int MAX_ERROR_TIMES = 3;

    private String mPrepayId;
    private String mToken;
    private String mWalletId;
    private String mQrcode;


    public void onAttachView(Activity view) {

        this.mView = view;
    }

    public void onDetachView() {
        this.mView = null;

    }

    public void payWithPassword(final String password) {
        //todo 获得支付结果，如果是密码错误则判断是否超过三次输入错误密码，没超过三次则弹窗重新输入和忘记密码。超过三次则撤单，关闭界面。
        HdPasswordPayReq hdPasswordPayReq = new HdPasswordPayReq();
        hdPasswordPayReq.pay_pwd = password;
        hdPasswordPayReq.prepay_id = mPrepayId;
        hdPasswordPayReq.token = mToken;
        hdPasswordPayReq.wallet_id = mWalletId;

        new HdPasswordPayRsp.Builder()
                .setTag(mView)
                //todo 正式开发需要加上
                /*.addBodyObj(hdPasswordPayReq)*/
                .build(new HttpCallBack<HdPasswordPayRsp>() {
                    @Override
                    public void onSuccess(HdPasswordPayRsp passwordPayRsp) {
                        if (passwordPayRsp == null) {
                            HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAYRSP_ERR, "");
                            return;
                        }
                        String code = passwordPayRsp.code;
                        if (HdPayConstants.HdPayResultStringCode.PASS_WORD_ERROR.equals(code)) { //密码错误
                            mErrorTimes++;

                            if (mErrorTimes >= MAX_ERROR_TIMES) {
                                Toast.makeText(mView, "密码错误超过3次，支付失败!", Toast.LENGTH_LONG).show();
                                cancelPay();
                                HdPayManager.getInstance().sendPayResultBroadcast(HdPayCodeUtils.getResultCodeInt(passwordPayRsp.code), "");
                                mView.finish();
                            } else { //todo 弹窗提示重新输入密码或者忘记密码
                                Toast.makeText(mView, "密码错误第" + mErrorTimes + "次,3次错误则交易失败", Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        if (!HdPayConstants.HdPayTradeState.SUCCESS.equals(passwordPayRsp.trade_state)) {
                            HdPayManager.getInstance().sendPayResultBroadcast(HdPayCodeUtils.getResultCodeInt(passwordPayRsp.code), "");
                            return;
                        }

                        //todo 支付成功
                        HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.HD_PAY_REQUEST_PAY_SUCCESS, "");
                        Toast.makeText(mView, "支付成功!", Toast.LENGTH_LONG).show();
                        mView.finish();

                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        HdPayManager.getInstance().sendPayResultBroadcast(stateCode, errorInfo);
                    }
                });
    }

    //撤单
    public void cancelPay() {
        HdCancelPayReq hdCancelPayReq = new HdCancelPayReq();
        hdCancelPayReq.prepay_id = mPrepayId;
        new HdCancelPayRsp.Builder()
                .setTag(mView)
                //todo 正式开发需要加上
                /*.addBodyObj(hdCancelPayReq)*/
                .build(new HttpCallBack<HdCancelPayRsp>() {
                    @Override
                    public void onSuccess(HdCancelPayRsp hdCancelPayRsp) {
                        Toast.makeText(mView, "撤单成功!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(mView, "撤单失败 stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void initData(Intent intent) {
        if (intent == null) {
            return;
        }
        mPrepayId = intent.getStringExtra(HdPayConstants.KEY_PREPAY_ID);
        mToken = intent.getStringExtra(HdPayConstants.KEY_TOKEN);
        mWalletId = intent.getStringExtra(HdPayConstants.KEY_WALLET_ID);
        mQrcode = intent.getStringExtra(HdPayConstants.KEY_QRCODE);
    }
}
