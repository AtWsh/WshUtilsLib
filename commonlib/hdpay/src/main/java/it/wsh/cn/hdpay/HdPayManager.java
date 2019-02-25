package it.wsh.cn.hdpay;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import it.wsh.cn.hdpay.utils.HdPayConstants;
import it.wsh.cn.hdpay.utils.HdPayLog;

public class HdPayManager {

    private final static String TAG = "HdPayManager";

    private Context mContext;
    private static volatile HdPayManager sInstance;

    private String mAuthorization;
    private String mFrontType;

    private HdPayManager() {
    }

    public static HdPayManager getInstance() {
        if (sInstance == null) {
            synchronized (HdPayManager.class) {
                if (sInstance == null) {
                    sInstance = new HdPayManager();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context, String frontType) {
        if (context == null) {
            HdPayLog.e("HdPayManager: init, context == null");
            return;
        }
        mContext = context.getApplicationContext();
        mFrontType = frontType;
    }

    public void setOrUpdateAuthorization(String authorization) {
        if (TextUtils.isEmpty(authorization)) {
            return;
        }
        mAuthorization = authorization;
    }

    public String getAuthorization() {
        return mAuthorization;
    }

    public String getFrontType() {
        return mFrontType;
    }

    public Context getContext() {
        return mContext;
    }

    public void sendPayResultBroadcast(int resultCode, String info) {
        if (mContext == null) {
            return;
        }
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
        Intent payResult = new Intent();
        payResult.setAction(HdPayConstants.HD_PAY_RESULT_ACTION);
        payResult.putExtra(HdPayConstants.HD_PAY_RESULT_EXTRA_CODE, resultCode);
        payResult.putExtra(HdPayConstants.HD_PAY_RESULT_EXTRA_INFO, info);
        broadcastManager.sendBroadcast(payResult);
    }
}
