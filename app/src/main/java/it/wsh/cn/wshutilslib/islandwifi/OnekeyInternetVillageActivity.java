package it.wsh.cn.wshutilslib.islandwifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pools;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;

import it.wsh.cn.wshutilslib.MainApplication;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.common.DeviceUtils;
import it.wsh.cn.wshutilslib.common.EncryptUtils;
import it.wsh.cn.wshutilslib.common.ObjectUtils;
import it.wsh.cn.wshutilslib.common.PhoneUtils;
import it.wsh.cn.wshutilslib.common.StatusBarUtil;
import it.wsh.cn.wshutilslib.common.StringUtils;
import it.wsh.cn.wshutilslib.islandwifi.model.IslandWifiRequest;
import it.wsh.cn.wshutilslib.mvp.BasePresenterActivity;
import it.wsh.cn.wshutilslib.receiver.NetWorkStateReceiver;

@Route(path = "/internet/onekey_internet")
public class OnekeyInternetVillageActivity extends BasePresenterActivity<OnekeyInternetPresenter, IOnekeyView>
        implements IOnekeyView, NetWorkStateReceiver.NetIsConnectLisenter,
        ViewPager.OnPageChangeListener, View.OnTouchListener ,View.OnClickListener{
    private static final String TAG = OnekeyInternetVillageActivity.class.getSimpleName();
    private static final int REQUEST_LOGIN_CODE = 123;

    public static final int RESPONSE_RESULT_OK_CODE = 200;    //
    public static final int RESPONSE_RESULT_ERR_201_CODE = 201; //非平台用户
    public static final int RESPONSE_RESULT_ERR_202_CODE = 202; //禁止通过
    public static final int RESPONSE_RESULT_ERR_203_CODE = 203; //未认证
    public static final int RESPONSE_RESULT_ERR_204_CODE = 204; //未登录
    public static final int RESPONSE_RESULT_ERR_205_CODE = 205; //未登录
    public static final int RESPONSE_RESULT_ERR_206_CODE = 206; //异常
    public static final int RESPONSE_RESULT_ERR_207_CODE = 207; //超过上网次数
    public static final int RESPONSE_RESULT_ERR_1001_CODE = 1001; //请求发送错误
    public static final int RESPONSE_PARSE_ERR_1002_CODE = 1002; //请求发送错误

    private static final String SERVER_KEY = "3d3f7b0e92a3b9c3d007424bea5c5fef";
    private static final String WSAPATH = "wsap=";
    private static final String TERMINALMAC = "terminalMac=";
    private static final String APMACPATH = "apMac=";

    @Autowired
    String mWsapath;

    String mAPMac;

    private Button btSetting;
    private ViewPager one_key_internet_banner;
    private TextView indicator;
    private WebView webView;

    private Handler mHandler = new Handler();

    private Integer[] images = {
            R.mipmap.icon_one_key_internet_setting_wifi_step1,
            R.mipmap.icon_one_key_internet_setting_wifi_step2,
            R.mipmap.icon_one_key_internet_setting_wifi_step3};
    private String[] titles = {
            MainApplication.getContext().getString(R.string.one_key_to_internet_step1_tips),
            MainApplication.getContext().getString(R.string.one_key_to_internet_step2_tips),
            MainApplication.getContext().getString(R.string.one_key_to_internet_step3_tips)};

    private int currentPos;

    @Override
    protected OnekeyInternetPresenter initPresenter() {
        return new OnekeyInternetPresenter();
    }

    public static void startSelf(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, OnekeyInternetVillageActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate enter");

        StatusBarUtil.immersive(this);
        //immerse();
        StatusBarUtil.setTopMarginSmart(this, headerLayout);
        setHeaderCenter(getString(R.string.one_key_to_internet_title));
        //rootView.setBackgroundResource(R.mipmap.gradient_theme_bg);
        rootView.setBackgroundColor(Color.WHITE);
        one_key_internet_banner = findViewById(R.id.one_key_internet_banner);
        indicator = findViewById(R.id.one_key_internet_indicator);
        btSetting = findViewById(R.id.bt_setting_network);
        webView = new WebView(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null && intent.getDataString() != null) {
            Log.i(TAG, "data from H5 link, data = "+intent.getDataString());
            mWsapath = getDataByPath(intent.getDataString(),WSAPATH);
            mAPMac = getDataByPath(intent.getDataString(),APMACPATH);

            Log.i(TAG, "mWsapath = "+ mWsapath+",mAPMac = "+mAPMac);
        } // mWsapath = "http://192.168.0.251/wsa/agent/allowEndpoint.do";
        //http%3A%2F%2F172.16.1.38%2FApi%2FApp%2Flogin

        //todo mWsapath = "http://172.16.1.38/Api/App/login";
        //todo arouter://hd.smartvillage.com/main/onekey_internet?wsap=http://172.16.1.38/Api/App/login&terminalMac=5c:1d:d9:55:0c:78&apMac=00:0b:86:fb:6a:20

        initWebView();
        setBanner();
        NetWorkStateReceiver.setNetIsConnectLisenter(this);
        btSetting.setOnClickListener(this);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_PHONE_STATE},
                    1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, PhoneUtils.getUUID());
                } else {
                    finish();
                }
                break;
                default:
                    break;
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.one_key_to_internet_layout;
    }

    private String getDataByPath(String totalData,String path){
        String splitData = null;
        String[] split = totalData.split("&");
        for (int i = 0;i < split.length;i++){
            if(split[i].contains(path)){
                int index = split[i].indexOf(path) + path.length();
                splitData = split[i].substring(index);
            }
        }

        return splitData;
    }
    private void setBanner() {
        Drawable bannerImage = ContextCompat.getDrawable(this, R.mipmap.icon_one_key_internet_setting_wifi_step1);
        float ratio = 1F * bannerImage.getIntrinsicHeight() / bannerImage.getIntrinsicWidth();
        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = (int) (width * ratio);

        one_key_internet_banner.getLayoutParams().height = height;
        one_key_internet_banner.addOnPageChangeListener(this);
        one_key_internet_banner.setOnTouchListener(this);
        one_key_internet_banner.setAdapter(new PagerAdapter() {
            Pools.SimplePool<ImageView> pool = new Pools.SimplePool<>(2);

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView imageView = pool.acquire();
                if (imageView == null) {
                    imageView = new ImageView(container.getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                imageView.setImageResource(images[position % images.length]);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                pool.release((ImageView) object);
                container.removeView((View) object);
            }
        });

        one_key_internet_banner.setCurrentItem(currentPos = images.length * 1000, false);
        indicator.setText(titles[0]);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(autoScrollAction);
        } else if (action == MotionEvent.ACTION_UP) {
            mHandler.postDelayed(autoScrollAction, 2000);
        }
        return false;
    }

    @Override
    public void onPageSelected(int position) {
        currentPos = position;
        indicator.setText(titles[position % titles.length]);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

    private Runnable autoScrollAction = new Runnable() {
        @Override
        public void run() {
            one_key_internet_banner.setCurrentItem(++currentPos);
            mHandler.postDelayed(this, 2000);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_setting_network) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private boolean hasAllow;

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        Log.i(TAG,"initWebView enter");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "WebView-onPageFinished-url: "+ mWsapath);
                Log.i(TAG, "onPageFinished url = "+url);
                if (mPresenter.isWsapathValid(mWsapath)) { // wsapath有效的情况下不再需要用js去取
                    return;
                }

                view.evaluateJavascript("document.getElementById('hasAllow').value;", new ValueCallback<String>() {
                    //为了兼容三星S8第一次发请求取不到Wifi IP地址的问题。
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i(TAG, "hasAllow: "+value);
                        hasAllow = !TextUtils.isEmpty(value) && !"null".equals(value);
                    }
                });

                view.evaluateJavascript("document.getElementById('apMac').value;", new ValueCallback<String>() {
                    //为了兼容三星S8第一次发请求取不到Wifi IP地址的问题。
                    @Override
                    public void onReceiveValue(String value) {
                        if (ObjectUtils.isNotEmpty(value)) {
                            mAPMac = value.replace("\"", "");
                            Log.i(TAG, "yyy:isWsapathValid:"+mAPMac);
                        }
                    }
                });

                view.evaluateJavascript("document.getElementById('wsapath').value;", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i(TAG, "WebView-onPageFinished-mWsapath: "+ value);
                        if (TextUtils.isEmpty(value)) return;
                        Log.i(TAG, "mWsapath has allow:" + hasAllow);
                        if (!hasAllow) return;

                        mWsapath = value;
                        if (mPresenter.isWsapathValid(mWsapath)) {
                            requestInternet();
                        } else {
                            showWifiSetting();
                        }

                    }
                });

                Log.i(TAG,"onPageFinished mWsapath = "+ mWsapath);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume isHengdawifi = "+mPresenter.isHengDaWifiConnected());
        if (mPresenter.isHengDaWifiConnected()) {
            showWifiConnecting();
        }
        //todo 是否需要登录校验? 融合版此处有做登录校验

        pingNetIfHengdaWifiConnected();
        requestInternet();
    }

    private void pingNetIfHengdaWifiConnected() {
        mPresenter.pingNetIfHengdaWifiConnected();
    }

    private void requestInternet() {

        Log.i(TAG,"requestInternet enter");

        if (!mPresenter.isWsapathValid(mWsapath)) {
            Log.i(TAG, "requestInternet mWsapath is invalid,mWsapath = "+ mWsapath);
            return;
        }

        Log.i(TAG, "requestInternet-mWsapath: "+ mWsapath);
        mWsapath = mWsapath.replace("\"", "");
        String deviceId = PhoneUtils.getUUID();
        //String deviceId = "866146037426909";
        String terminalMac = DeviceUtils.getMacAddress(MainApplication.getContext());
        String apMac = mAPMac;
        //todo String account = loginData.getUuid();
        String account = "48e3f2cedefb4e97afe3cf5ee8633f60";
        String secondTime = System.currentTimeMillis() / 1000 + "";
        //todo String courtUuid = loginData.getCourtUuid();
        String courtUuid = "0c1ba49c74774414a21ce91b66b79a2d";
        String authType = "app";//认证类型:message、weixin、app 为以后扩展


        List<String> encryptSource = new ArrayList<>();
        if(!StringUtils.isEmpty(deviceId)){
            encryptSource.add("deviceId="+deviceId);
        }

        if(!StringUtils.isEmpty(terminalMac)){
            encryptSource.add("terminalMac="+terminalMac);
        }

        if(!StringUtils.isEmpty(apMac)){
            encryptSource.add("apMac="+apMac);
        }

        if(!StringUtils.isEmpty(account)){
            encryptSource.add("account="+account);
        }

        if(!StringUtils.isEmpty(courtUuid)){
            encryptSource.add("courtUuid="+courtUuid);
        }

        encryptSource.add("authType="+authType);

        if(!StringUtils.isEmpty(secondTime)){
            encryptSource.add("time="+secondTime);
        }

        if(!StringUtils.isEmpty(secondTime)){
            encryptSource.add("serverKey="+SERVER_KEY);
        }

        String sign = getEncrptyData(encryptSource);

        IslandWifiRequest request = new IslandWifiRequest();
        request.deviceId = deviceId;
        request.terminalMac = terminalMac;
        request.apMac = apMac;
        request.account = account;
        request.time = secondTime;
        request.courtUuid = courtUuid;
        request.authType = authType;
        request.sign = sign;

        mPresenter.requestInternet(mWsapath, request);

        Log.i(TAG,"requestInternet leave");
    }

    private String getEncrptyData(List<String> data){
        StringBuilder encrptyData = new StringBuilder();
        String encrptySource;
        if(data == null || data.size() == 0){
            return null;
        }

        java.util.Collections.sort(data);

        encrptyData.append(data.get(0));

        for(int i = 1; i < data.size();i++){
            encrptyData.append("&").append(data.get(i));
        }

        Log.i(TAG,"encrptyData = "+encrptyData.toString());

        encrptySource = EncryptUtils.encryptMD5ToString(String.valueOf(encrptyData));

        return encrptySource.toLowerCase();

    }

    @Override
    public void callbackResult(int result) {
        Log.i(TAG,"callbackSuccess enter wifiConnectionResult = "+result);
        OneKeyInternetResultVillageActivity.startOneKeyInternetResultActivity(OnekeyInternetVillageActivity.this, result);
        finish();
    }

    @Override
    public void callOneKeyInternetWifiState(boolean wifiCanUse) {
        Log.i(TAG,"callOneKeyInternetWifiState wifiCanUse = "+wifiCanUse);
        if (mPresenter.isHengDaWifiConnected()) {
            showWifiConnecting();
            if (wifiCanUse) {
                Log.i(TAG, "callOneKeyInternetWifiState-WifiConnected-mWsapath: "+ mWsapath);
                //一键上网成功联上wifi
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callbackResult(RESPONSE_RESULT_OK_CODE);
                    }
                }, 2000);

                Log.i(TAG, "callOneKeyInternetWifiState postDelayed callbackResult");

            } else {
                if (!mPresenter.isWsapathValid(mWsapath)) {
                    webView.loadUrl("http://www.baidu.com/"); //通过加载一个网页，重定向到Portal页，再通过JS去获取wsapath地址
                    Log.i(TAG, "webView.loadUrl--start.");
                }else{
                    showWifiSetting();
                }
            }
        } else {
            showWifiSetting();
        }
    }

    private void showWifiConnecting() {
        btSetting.setText(getString(R.string.wifi_connecting));
        btSetting.setEnabled(false);
    }

    private void showWifiSetting() {
        btSetting.setText(getString(R.string.setting_network));
        btSetting.setEnabled(true);
    }

    


    @Override
    public void isNetConnect(boolean isNetConnect) {
        Log.i(TAG, "isNetConnect = "+isNetConnect);
        if (mPresenter.isHengDaWifiConnected() && !mPresenter.isWsapathValid(mWsapath)) {
            pingNetIfHengdaWifiConnected();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(autoScrollAction, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(autoScrollAction);
    }

    @Override
    protected void onDestroy() {
        NetWorkStateReceiver.removeNetIsConnectListener();
        if (webView != null) {
            synchronized (this) {
                webView.setVisibility(View.GONE);
                try {
                    webView.stopLoading();
                    // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
                    webView.getSettings().setJavaScriptEnabled(false);
                    webView.clearHistory();
                    webView.clearView();
                    webView.removeAllViews();
                    ((ViewGroup) webView.getParent()).removeView(webView);
                } catch (Exception e) {
                }
                webView.destroy();
                webView = null;
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult-requestCode: "+resultCode);
        if (requestCode == REQUEST_LOGIN_CODE) {
            if (resultCode == RESULT_OK) { // 用户登录成功
                // do nothing
            } else { //用户未登录直接后退回来
                finish();
            }
        }
    }
}
