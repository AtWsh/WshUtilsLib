package it.wsh.cn.wshutilslib.httpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.wsh.cn.wshlibrary.http.HttpCallBack;
import it.wsh.cn.wshlibrary.http.HttpConfig;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.httpdemo.bean.NoticeListResponse;


/**
 * author: wenshenghui
 * created on: 2018/8/23 15:16
 * description:
 */
public class LifeCircleTestActivity extends AppCompatActivity implements  View.OnClickListener {

    private String TAG = "LifeCircleTestActivity";

    // Butter Knife
    private Unbinder bind;
    private NoticeListResponse.Builder mHttpBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("wsh", "LifeCircleTestActivity onCreate");
        setContentView(R.layout.activity_life_circle_test);
        bind = ButterKnife.bind(this);
    }

    public static void luanchActivity(Activity context){
        Intent intent = new Intent(context, LifeCircleTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    @OnClick({R.id.send_http_request, R.id.exit_life_circle_page})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_http_request:
                sendHttpRequest();
                break;
            case R.id.exit_life_circle_page:
                exit();
                break;
            default:
                break;
        }

    }

    /**
     * 清空Http的回调
     */
    private void exit() {
        if (mHttpBuilder != null) {
            if (mHttpBuilder.dispose()) {
                Toast.makeText(LifeCircleTestActivity.this.getApplication(), "成功删除回调", Toast.LENGTH_SHORT);
                Log.d("wsh", "LifeCircleTestActivity  :  成功删除回调");
            }else {
                Toast.makeText(LifeCircleTestActivity.this.getApplication(), "删除回调失败", Toast.LENGTH_SHORT);
                Log.d("wsh", "LifeCircleTestActivity  :  删除回调失败");
            }
        }
        Log.d("wsh", "LifeCircleTestActivity  :  exit");
        finish();
    }

    private void sendHttpRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("courtId", "default");
        jsonObject.addProperty("currentPage", 1);
        jsonObject.addProperty("pageSize", 5);

        mHttpBuilder = new NoticeListResponse.Builder();
        HttpConfig httpConfig = HttpConfig.create(true);
        httpConfig.readTimeout(10);
        httpConfig.connectTimeout(10);
        mHttpBuilder.setTag(LifeCircleTestActivity.this)
                .setHttpCustomConfig(httpConfig)
                .addBodyObj(jsonObject)
                .build(new HttpCallBack<NoticeListResponse>() {
            @Override
            public void onSuccess(NoticeListResponse noticeListResponse) {
                Toast.makeText(LifeCircleTestActivity.this, "请求成功！  " + noticeListResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d("wsh", noticeListResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(LifeCircleTestActivity.this, "请求失败！  errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d("wsh", "stateCode = " + stateCode + "; errorInfo = " +errorInfo);
            }
        });
    }
}
