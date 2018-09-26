package it.wsh.cn.wshlibrary.http;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * author: wenshenghui
 * created on: 2018/8/2 11:58
 * description:
 */
public interface HttpServices {
    /* common interface */

    @POST
    Observable<Response<String>> post(@Url String path);

    @POST
    Observable<Response<String>> postWithParamsMap(@Url String path, @QueryMap Map<String, String> params);

    @POST
    Observable<Response<String>> post(@Url String path, @Body Object requestBody);

    @POST
    Observable<Response<String>> postWithHeaderMap(@Url String path, @HeaderMap Map<String, String> headers);

    @POST
    Observable<Response<String>> post(@Url String path, @QueryMap Map<String, String> params, @Body Object requestBody);

    @POST
    Observable<Response<String>> post(@Url String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> headers);

    @POST
    Observable<Response<String>> post(@Url String path, @Body Object requestBody, @HeaderMap Map<String, String> headers);

    @POST
    Observable<Response<String>> post(@Url String path, @QueryMap Map<String, String> params, @Body Object requestBody, @HeaderMap Map<String, String> headers);


    //get
    @GET
    Observable<Response<String>> get(@Url String path);

    @GET
    Observable<Response<String>> getWithParamsMap(@Url String path, @QueryMap Map<String, String> params);

    @GET
    Observable<Response<String>> getWithHeaderMap(@Url String path, @HeaderMap Map<String, String> header);

    @GET
    Observable<Response<String>> get(@Url String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> header);

    //上传
    @POST
    Observable<Response<String>> upload(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @PartMap Map<String, RequestBody> partMap
    );
}
