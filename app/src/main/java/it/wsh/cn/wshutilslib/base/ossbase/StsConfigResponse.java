package it.wsh.cn.wshutilslib.base.ossbase;


import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.builder.LifeCycleBuilder;

/**
 * author: wenshenghui
 * created on: 2018/10/12 17:52
 * description:
 */
public class StsConfigResponse {

    /*{
        "message": "success",
            "timestamp": 1542335716,
            "code": 0,
            "result": {
                "AccessKeySecret": "Fef5rdWrDqepW1hB3ocoDnKHF4aVJLwH8GTr7u6KDfSa",
                "AccessKeyId": "STS.NJdw2mEGBTGjaW1Md8rArgApo",
                "Expiration": 1542336616,
                "SecurityToken": "CAIS9QF1q6Ft5B2yfSjIr4nRPIjZqJhj44WBY3GAqWRtfs5eiITbjTz2IH9EfXZqBesYt\/U0lWpY6PoalqFhS5hYWU3Na5OaNUjsaEfzDbDasumZsJYm6vT8a0XxZjf\/2MjNGZabKPrWZvaqbX3diyZ32sGUXD6+XlujQ\/br4NwdGbZxZASjaidcD9p7PxZrrNRgVUHcLvGwKBXn8AGyZQhKwlMn2TwntPrvkp3AskKE1wfAp7VL99irEP+NdNJxOZpzadCx0dFte7DJuCwqsEcarfgs0PcaoGac74vNXQUJ+XOMPufS\/8EqJgJlb+0gBL7EVmdcKCWr0BqAAR3D0kLbiA5jBLduzETDEsLEYRPM3oMPc+RbFwpKmD6tWqfR2M3vCxeFbK5\/Uqhs7So55GPZUr8SmAmdXYrShk0qhzRN57fjvN7TKbiGpi\/aYLlwyEr1r9lIrfQW\/x7e5axXcXg7w1bJW\/nWx6R9qsVxvTygxvsPlMZ9f7AIXMnp",
                "endpoint": "oss-cn-shenzhen.aliyuncs.com",
                "bucket": "zsj-smarthome"
            }
       }*/

    public long timestamp;
    public int code;
    public StsConfigResult result;


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
