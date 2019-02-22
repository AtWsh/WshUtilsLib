package it.wsh.cn.wshutilslib.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import it.wsh.cn.common_pay.pay.PayWay;
import it.wsh.cn.common_pay.pay.PaymentOrder;
import it.wsh.cn.common_pay.pay.callback.PayResultCallback;
import it.wsh.cn.wshutilslib.R;

public class PayDemoActivity extends AppCompatActivity {

    private static String TAG = "PayDemoActivity";
    private Button mPasswordPayTestBtn;
    private Button mQrcodePayTestBtn;
    private PaymentOrder mPaymentOrder;

    public static void luanchActivity(Activity context) {
        Log.i(TAG, "Start PayDemoActivity");
        Intent intent = new Intent(context, PayDemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "OssDemoActivity onCreate");
        setContentView(R.layout.activity_pay_test);
        initView();
        initAction();
    }

    private void initAction() {
        mPasswordPayTestBtn.setOnClickListener(mPasswordPayTestBtnClickListener);
        mQrcodePayTestBtn.setOnClickListener(mQrcodePayTestBtnClickListener);
    }

    private void initView() {
        mPasswordPayTestBtn = findViewById(R.id.passwordpay);
        mQrcodePayTestBtn = findViewById(R.id.qrcodepay);
    }

    private View.OnClickListener mPasswordPayTestBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPaymentOrder != null) {
                mPaymentOrder.clear();
                mPaymentOrder = null;
            }
            mPaymentOrder = new PaymentOrder(PayDemoActivity.this, PayWay.HD_PAY);
            getPrepayData();
            mPaymentOrder.pay(getPrepayData(), mPasswordPayCallback);
        }
    };

    private View.OnClickListener mQrcodePayTestBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPaymentOrder != null) {
                mPaymentOrder.clear();
                mPaymentOrder = null;
            }
            mPaymentOrder = new PaymentOrder(PayDemoActivity.this, PayWay.HD_PAY);
            mPaymentOrder.hdQrcodePay("二维码信息", "钱包id", mPasswordPayCallback);
        }
    };

    private String getPrepayData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prepay_id", "123456");
            jsonObject.put("wallet_id", "123456");
            jsonObject.put("out_trade_no", "123456");
            long currentTimeMillis = System.currentTimeMillis();
            long time_expire = currentTimeMillis + 1000 * 60 * 5;
            //jsonObject.put("time_expire", time_expire);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private PayResultCallback mPasswordPayCallback = new PayResultCallback() {
        @Override
        public void onPaySuccess(String payWay) {
            Log.d(TAG, "onPasswordPaySuccess");
        }

        @Override
        public void onPayCancel(String payWay) {
            Log.d(TAG, "onPasswordPayCancel");
        }

        @Override
        public void onPayFailure(String payWay, int errCode) {
            Log.d(TAG, "onPasswordPayFailure errCode = " + errCode);
        }
    };

    @Override
    protected void onDestroy() {
        if (mPaymentOrder != null) {
            mPaymentOrder.clear();
        }
        super.onDestroy();
    }
}
