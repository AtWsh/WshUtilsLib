package it.wsh.cn.common_http.http.asyncquery;

import it.wsh.cn.common_http.http.HttpMethod;

/**
 * author: wenshenghui
 * created on: 2019/1/9 15:58
 * description:
 */
public interface IHttpReq {
    String getPath();
    String getBaseUrl();
    @HttpMethod.IMethed String getMethod();
}
