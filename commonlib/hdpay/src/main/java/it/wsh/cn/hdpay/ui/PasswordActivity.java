package it.wsh.cn.hdpay.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import it.wsh.cn.hdpay.HdPayManager;
import it.wsh.cn.hdpay.HdPayResultCode;
import it.wsh.cn.hdpay.R;
import it.wsh.cn.hdpay.callback.PasswordInputListener;
import it.wsh.cn.hdpay.utils.HdPayConstants;
import it.wsh.cn.hdpay.utils.HdPayLog;

public class PasswordActivity extends AppCompatActivity {

    private PasswordPresenter mPresenter;
    private PasswordView mPasswordView;
    private String mPrepayId;
    private String mToken;
    private String mWalletId;
    private String mQrcode;

    public static void luanchActivity(Context context, String prepay_id, String token, String wallet_id, String qr_code) {
        HdPayLog.d("Start PasswordActivity ");
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(HdPayConstants.KEY_PREPAY_ID, prepay_id);
        intent.putExtra(HdPayConstants.KEY_TOKEN, token);
        intent.putExtra(HdPayConstants.KEY_WALLET_ID, wallet_id);
        intent.putExtra(HdPayConstants.KEY_QRCODE, qr_code);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mPresenter = new PasswordPresenter();
        if (mPresenter != null) {
            mPresenter.onAttachView(this);
            mPresenter.initData(getIntent());
        }

        initView();
    }

    private void initView() {
        mPasswordView = findViewById(R.id.pwd_view);
        mPasswordView.setInputListener(mPasswordInputListener);
    }

    private PasswordInputListener mPasswordInputListener = new PasswordInputListener() {
        @Override
        public void inputFinish() {
            if (mPresenter != null) {
                mPresenter.payWithPassword(mPasswordView.getStrPassword());
            }
        }

        //取消支付
        @Override
        public void cancel() {
            //关闭支付页面
            if (mPresenter != null) {
                mPresenter.cancelPay();
            }
            HdPayManager.getInstance().sendPayResultBroadcast(HdPayResultCode.USER_CANCEL_PAY_CODE, "");
            finish();
        }

        //忘记密码回调事件
        @Override
        public void forgetPwd() {
            //todo 忘记密码
            Toast.makeText(PasswordActivity.this, "忘记密码", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDetachView();
            mPresenter = null;
        }
        super.onDestroy();
    }
}