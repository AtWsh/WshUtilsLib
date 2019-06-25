package it.wsh.cn.wshutilslib.httpanotationtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.wsh.cn.common_http.http.HttpCallBack;
import it.wsh.cn.common_http.http.HttpConfig;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.httpdemo.DownloadTestActivity;
import it.wsh.cn.wshutilslib.httpdemo.DownloadTestActivity2;
import it.wsh.cn.wshutilslib.httpdemo.LifeCircleTestActivity;
import it.wsh.cn.wshutilslib.httpdemo.bean.AccountExistResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.NoticeListResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.RouteInfoResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.WeatherResponse;
import it.wsh.cn.wshutilslib.httpdemo.builder.RemoteLogUploadBuilder;


/**
 * author: wenshenghui
 * created on: 2018/9/29 09:29
 * description:
 */
public class HttpAnnotationDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "HttpAnnotationDemoActivity";

    // Butter Knife
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HttpAnnotationDemoActivity onCreate");
        setContentView(R.layout.activity_http_test);
        bind = ButterKnife.bind(this);
    }


    public static void luanchActivity(Activity context) {
        Log.d(TAG, "openHttp click");
        Intent intent = new Intent(context, HttpAnnotationDemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    @OnClick({R.id.get_zhihu_info_btn, R.id.weather_btn, R.id.account_exist, R.id.notice_list, R.id.route_info_btn
            , R.id.route_info_add_header, R.id.route_info_set_http_config
            , R.id.upload, R.id.download, R.id.life_circle_test, R.id.list_response_test})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.get_zhihu_info_btn:

                requestZhihuInfo();

                break;

            case R.id.weather_btn:

                requestWeatherInfo();

                break;

            case R.id.account_exist:

                requestAccountExist();

                break;

            case R.id.notice_list:

                requestNoticeList();

                break;

            case R.id.route_info_btn:

                requestRouteInfo();

                break;

            case R.id.route_info_add_header:

                requestRouteInfoAddHeader();

                break;

            case R.id.route_info_set_http_config:
                requestRouteInfoSetHttpConfig();
                break;

            case R.id.upload:
                testUpload();
                break;

            case R.id.download:
                DownloadTestActivity.luanchActivity(HttpAnnotationDemoActivity.this);

                break;

            case R.id.life_circle_test:
                LifeCircleTestActivity.luanchActivity(HttpAnnotationDemoActivity.this);

                break;

            case R.id.list_response_test:
                doListResponseTest();

                break;

            default:
                break;
        }
    }

    private void testUpload() {
        String path = Environment.getExternalStorageDirectory().getPath();
        String uploadPath = path + "/111/vip0.txt";
        new RemoteLogUploadBuilder("http://www.oss_web.com/upload").addFile("file", uploadPath).build(new HttpCallBack() {

            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
                Log.d("wsh_log", "onProgress ：" + progress);
            }

            @Override
            public void onSuccess(Object o) {
                Log.d("wsh_log", "onSuccess ：");
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Log.d("wsh_log", "onError ：");
            }
        });
    }

    /**
     * 对返回数据是list的情况进行测试
     * 测试环境，随便发起一个请求，callBack泛型为一个list<RouteInfoResponse>
     * 修改HttpClient代码让其返回的json为多个AccountExistResponse 的jsonArray形式
     * 正常请求这里会请求失败，需要修改HttpClient的代码 让其返回一个list
     */
    private void doListResponseTest() {
        HttpConfig httpConfig = HttpConfig.create(false);
        httpConfig.connectTimeout(5);
        httpConfig.readTimeout(5);
        httpConfig.writeTimeout(5);
        HashMap<String, String> map = new HashMap<>();
        map.put("req_id", "6");
        map.put("method", "isValidRouter");
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", "");

        new RouteInfoResponse.ListBuilder().addBodyMap(map)
                .addHeader("name", "wsh")
                .setHttpCustomConfig(httpConfig)
                .build(new HttpCallBack<List<RouteInfoResponse>>() {
                    @Override
                    public void onSuccess(List<RouteInfoResponse> list) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + list.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, list.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });
    }

    /**
     * POST setHeaderCustomcofig 测试 账号是否存在
     */
    private void requestRouteInfoSetHttpConfig() {


        HttpConfig httpConfig = HttpConfig.create(false);
        httpConfig.connectTimeout(5);
        httpConfig.readTimeout(5);
        httpConfig.writeTimeout(5);
        HashMap<String, String> map = new HashMap<>();
        map.put("req_id", "6");
        map.put("method", "isValidRouter");
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", "");

        new RouteInfoResponse.Builder().addBodyMap(map)
                .addHeader("name", "wsh")
                .setHttpCustomConfig(httpConfig)
                .build(new HttpCallBack<RouteInfoResponse>() {
                    @Override
                    public void onSuccess(RouteInfoResponse RouteInfoResponse) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, RouteInfoResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });
    }

    private void requestRouteInfoAddHeader() {
        HashMap<String, String> map = new HashMap<>();
        map.put("req_id", "6");
        map.put("method", "isValidRouter");
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", "");

        new RouteInfoResponse.Builder().addBodyMap(map)
                .addHeader("name", "wsh")
                .build(true, new HttpCallBack<RouteInfoResponse>() {
                    @Override
                    public void onSuccess(RouteInfoResponse RouteInfoResponse) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, RouteInfoResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });
    }

    /**
     * POST addBodyMap 测试 当前是否为恒大路由器
     */
    private void requestRouteInfo() {

        HashMap<String, String> map = new HashMap<>();
        map.put("req_id", "6");
        map.put("method", "isValidRouter");
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", "");

        new RouteInfoResponse.Builder().addBodyMap(map).build(new HttpCallBack<RouteInfoResponse>() {
            @Override
            public void onSuccess(RouteInfoResponse RouteInfoResponse) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, RouteInfoResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });
    }

    /**
     * POST addBodyJsonObject 测试   获取小区公告列表
     */
    private void requestNoticeList() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("courtId", "default");
        jsonObject.addProperty("currentPage", 1);
        jsonObject.addProperty("pageSize", 5);

        new NoticeListResponse.Builder().addBodyObj(jsonObject).build(new HttpCallBack<NoticeListResponse>() {
            @Override
            public void onSuccess(NoticeListResponse noticeListResponse) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + noticeListResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, noticeListResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });
    }

    /**
     * POST 测试 账号是否存在
     */
    private void requestAccountExist() {

        HashMap<String, String> map = new HashMap();
        map.put("phone", "15820463550");

        new AccountExistResponse.Builder().addBodyMap(map)
                .build(new HttpCallBack<AccountExistResponse>() {
                    @Override
                    public void onSuccess(AccountExistResponse accountExistResponse) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + accountExistResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, accountExistResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });

    }

    /**
     * get addmap 带参测试，获取天气信息
     */
    private void requestWeatherInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("location", "北京");
        map.put("output", "json");
        map.put("ak", "94Tmshjhp03oul7xy95Gu3wwHkjGZvkk");
        map.put("mcode", "EE:0C:C8:50:54:53:96:5A:55:8C:23:2F:93:7E:EB:AE:D8:C8:1B:F1;com.example.tangdekun.androidannotationsdemo");

        new WeatherResponse.Builder().setTag(HttpAnnotationDemoActivity.this)
                .addParamsMap(map)
                .build(new HttpCallBack<WeatherResponse>() {
                    @Override
                    public void onSuccess(WeatherResponse weatherResponse) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + weatherResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, weatherResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });
    }

    /**
     * GET 无参数请求测试  获取知乎新闻
     */
    private void requestZhihuInfo() {

        /*new ZhihuStoriesReq().ZhihuStoriesReqAsyncQuery().setTag(HttpAnnotationDemoActivity.this).build(new ZhihuStoriesReq(), new HttpCallBack<ZhihuStoriesResponse>() {
            @Override
            public void onSuccess(ZhihuStoriesResponse zhihuStoriesResponse) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求成功！  " + zhihuStoriesResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, zhihuStoriesResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpAnnotationDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });*/
    }
}
