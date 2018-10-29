package it.wsh.cn.wshlibrary.http.oss;

import it.wsh.cn.wshlibrary.http.HttpMethod;
import it.wsh.cn.wshlibrary.http.builder.LifeCycleBuilder;

/**
 * author: wenshenghui
 * created on: 2018/10/12 17:52
 * description:
 */
public class StsConfigResponse {

    public String AccessKeyId;
    public String AccessKeySecret;
    public String SecurityToken;
    public long Expiration;
    public String endpoint;
    public String bucket;


    public static class AsyncQuery extends LifeCycleBuilder<StsConfigResponse> {

        @Override
        protected String getPath() {
            return OssConfig.getSuffix();
        }

        @Override
        protected String getBaseUrl() {
            return OssConfig.getStsTokenUrl();
        }

        @Override
        protected String getMethod() {
            return HttpMethod.GET;
        }
    }
}
