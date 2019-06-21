package it.wsh.cn.wshutilslib.httpdemo;

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
import it.wsh.cn.common_http.http.request.HttpRequest;
import it.wsh.cn.wshutilslib.R;
import it.wsh.cn.wshutilslib.httpanotationtest.HttpAnnotationDemoActivity;
import it.wsh.cn.wshutilslib.httpdemo.bean.AccountExistResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.LocalServerLoginResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.NoticeListResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.RouteInfoResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.StoriesResponse;
import it.wsh.cn.wshutilslib.httpdemo.bean.WeatherResponse;
import it.wsh.cn.wshutilslib.httpdemo.builder.RemoteLogUploadBuilder;
import it.wsh.cn.wshutilslib.httpdemo.newhttp.requestinfo.RouteRequestInfo;
import it.wsh.cn.wshutilslib.httpdemo.newhttp.response.RouteResponseInfo;


/**
 * author: wenshenghui
 * created on: 2018/8/22 10:02
 * description:
 */
public class HttpDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "HttpTestActivity";

    // Butter Knife
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HttpTestActivity onCreate");
        setContentView(R.layout.activity_http_test);
        bind = ButterKnife.bind(this);
    }


    public static void luanchActivity(Activity context) {
        Log.d(TAG, "openHttp click");
        Intent intent = new Intent(context, HttpDemoActivity.class);
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
            , R.id.upload, R.id.download, R.id.life_circle_test, R.id.list_response_test
            , R.id.new_http_test, R.id.http_local_test})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.new_http_test:

                newHttpTest();

                break;

            case R.id.http_local_test:

                httpLocalTest();

                break;

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
                //DownloadTestActivity.luanchActivity(HttpDemoActivity.this);
                DownloadTestActivity2.luanchActivity(this);
                break;

            case R.id.life_circle_test:
                LifeCircleTestActivity.luanchActivity(HttpDemoActivity.this);

                break;

            case R.id.list_response_test:
                doListResponseTest();

                break;

            default:
                break;
        }
    }

    private void httpLocalTest() {
        new LocalServerLoginResponse.ListBuilder().setTag(this).build(new HttpCallBack<List<LocalServerLoginResponse>>() {

            @Override
            public void onSuccess(List<LocalServerLoginResponse> list) {
                Toast.makeText(HttpDemoActivity.this, "请求成功！  " + list.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, list.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });
    }

    private void newHttpTest() {

        //Get 请求
       /* new HttpRequest<ZhihuRequestInfo, ZhihuResponseInfo>().setTag(this).start(
                new ZhihuRequestInfo(), new HttpCallBack<ZhihuResponseInfo>() {
            @Override
            public void onSuccess(ZhihuResponseInfo zhihuInfoResponse) {
                Toast.makeText(HttpDemoActivity.this, "请求成功！  " + zhihuInfoResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, zhihuInfoResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });*/

        //Post请求 在外部构建数据
        /*RouteRequestInfo routeRequestInfo = new RouteRequestInfo();
        HashMap<String, String> map = new HashMap<>();
        map.put("req_id", "6");
        map.put("method", "isValidRouter");
        map.put("timestamp", System.currentTimeMillis() + "");
        map.put("params", "");
        routeRequestInfo.addBodyMap(map);
        new HttpRequest<RouteRequestInfo, RouteResponseInfo>()
                .setTag(HttpDemoActivity.this)
                .setRetryTimes(3,2000)
                .start(routeRequestInfo, new HttpCallBack<RouteResponseInfo>() {
                    @Override
                    public void onSuccess(RouteResponseInfo responseInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + responseInfo.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, responseInfo.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });*/

        //Post请求 在内部构建Obj数据
        RouteRequestInfo routeRequestInfo = new RouteRequestInfo("6", "isValidRouter", "params");
        new HttpRequest<RouteRequestInfo, RouteResponseInfo>()
                .setTag(HttpDemoActivity.this)
                .setRetryTimes(3, 2000)
                .start(routeRequestInfo, new HttpCallBack<RouteResponseInfo>() {
                    @Override
                    public void onSuccess(RouteResponseInfo responseInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + responseInfo.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, responseInfo.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });

        //Post请求 在内部构建BodyMap数据
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
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + list.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, list.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, RouteInfoResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, RouteInfoResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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
                Toast.makeText(HttpDemoActivity.this, "请求成功！  " + RouteInfoResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, RouteInfoResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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
                Toast.makeText(HttpDemoActivity.this, "请求成功！  " + noticeListResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, noticeListResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpDemoActivity.this, "请求失败！  errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + accountExistResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, accountExistResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
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

        new WeatherResponse.Builder().setTag(HttpDemoActivity.this)
                .addParamsMap(map)
                .build(new HttpCallBack<WeatherResponse>() {
                    @Override
                    public void onSuccess(WeatherResponse weatherResponse) {
                        Toast.makeText(HttpDemoActivity.this, "请求成功！  " + weatherResponse.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, weatherResponse.toString());
                    }

                    @Override
                    public void onError(int stateCode, String errorInfo) {
                        Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
                    }
                });
    }

    /**
     * GET 无参数请求测试  获取知乎新闻
     */
    private void requestZhihuInfo() {
        HttpConfig httpConfig = HttpConfig.create(true);
        new StoriesResponse.Builder().setTag(this).setHttpCustomConfig(httpConfig).build(true, new HttpCallBack<StoriesResponse>() {
            @Override
            public void onSuccess(StoriesResponse storiesResponse) {
                Toast.makeText(HttpDemoActivity.this, "请求成功！  " + storiesResponse.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, storiesResponse.toString());
            }

            @Override
            public void onError(int stateCode, String errorInfo) {
                Toast.makeText(HttpDemoActivity.this, "请求失败！  stateCode = " + stateCode + "; errorInfo = " + errorInfo, Toast.LENGTH_LONG).show();
                Log.d(TAG, "stateCode = " + stateCode + "; errorInfo = " + errorInfo);
            }
        });
    }
}

