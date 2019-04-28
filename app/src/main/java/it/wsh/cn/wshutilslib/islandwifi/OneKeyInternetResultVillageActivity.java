package it.wsh.cn.wshutilslib.islandwifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.common.StatusBarUtil;
import it.wsh.cn.wshutilslib.mvp.BasePresenterActivity;


public class OneKeyInternetResultVillageActivity extends BasePresenterActivity<OnekeyInternetPresenter, IOnekeyView> {
    private static final String TAG = "OneKeyResultActivity";
    private TextView tv_owner_verify_state;

    @Override
    protected OnekeyInternetPresenter initPresenter() {
        return new OnekeyInternetPresenter();
    }

    public static void startOneKeyInternetResultActivity(Activity activity, int connectWifiResult) {
        Intent intent = new Intent(activity, OneKeyInternetResultVillageActivity.class);
        intent.putExtra("connectState", connectWifiResult);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
            return;
        }

        StatusBarUtil.immersive(this);
        StatusBarUtil.setTopMarginSmart(this, headerLayout);
        rootView.setBackgroundColor(Color.WHITE);
        setHeaderCenter(getString(R.string.one_key_to_internet_title));
        setHeadRightTv(R.string.wifi_connect_finish);


        tv_owner_verify_state = findViewById(R.id.tv_owner_verify_state);
        tv_owner_verify_state.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.dimen_20dp));

        setConnectedResult(getIntent());

    }

    @Override
    protected void onHeadRightClicked() {
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_onekey_internet_result;
    }

    private void setConnectedResult(Intent intent) {
        int connectWifiResult = intent.getIntExtra("connectState", OnekeyInternetVillageActivity.RESPONSE_RESULT_OK_CODE);
        switch (connectWifiResult){
            case OnekeyInternetVillageActivity.RESPONSE_RESULT_OK_CODE:
                tv_owner_verify_state.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_one_key_internet_success), null, null);
                tv_owner_verify_state.setText(getString(R.string.wifi_connected_success_tips));
                break;
            case OnekeyInternetVillageActivity.RESPONSE_RESULT_ERR_207_CODE:
                tv_owner_verify_state.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_one_key_internet_fail), null, null);
                tv_owner_verify_state.setText(getString(R.string.wifi_connected_fail_error_207_tips));
                break;
            default:
                tv_owner_verify_state.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_one_key_internet_fail), null, null);
                tv_owner_verify_state.setText(getString(R.string.wifi_connected_fail_tips));

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            Log.i(TAG, "yyy-onNewIntent onNewIntent--called");
            setConnectedResult(intent);
        }
    }
}
