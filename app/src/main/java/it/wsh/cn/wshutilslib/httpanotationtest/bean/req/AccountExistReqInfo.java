package it.wsh.cn.wshutilslib.httpanotationtest.bean.req;

import com.example.wsh.common_http_processor.HttpBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.wsh.cn.common_http.http.HttpMethod;
import it.wsh.cn.common_http.http.asyncquery.HttpAsyncQuery;
import it.wsh.cn.common_http.http.asyncquery.IHttpReq;
import it.wsh.cn.wshutilslib.httpanotationtest.bean.res.AccountExistInfo;

/**
 * author: wenshenghui
 * created on: 2019/1/9 11:06
 * description:
 */
//@HttpReqBean(resBean = AccountExistInfo.class)
public class AccountExistReqInfo implements IHttpReq {



    @HttpBody
    public String phone;

    @Override
    public String getPath() {
        return "checkAccountIfExist";
    }

    @Override
    public String getBaseUrl() {
        return "https://61.141.236.30:443/api/register/";
    }

    @Override
    public @HttpMethod.IMethed String getMethod() {
        return HttpMethod.POST;
    }

    public static class AccountExistInfoAsyncQuery extends HttpAsyncQuery<AccountExistReqInfo, AccountExistInfo> {
        List<String> mBodyKeys = new ArrayList<>();
        List<String> mHeaderKeys = new ArrayList<>();
        List<String> mParamKeys = new ArrayList<>();

        public AccountExistInfoAsyncQuery() {
            mBodyKeys.add("phone");
            //这一段需要拼接String
        }

        @Override
        protected void prepareData(AccountExistReqInfo req) {
            if (req == null) {
                return;
            }

            mBaseUrl = req.getBaseUrl();
            mPath = req.getPath();
            mHttpMethod = req.getMethod();

            Map<String, Object> bodyMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramsMap = new HashMap<>();

            /*if (mBodyKeys.size() > 0) {
                for (String key : mBodyKeys) {
                    "bodyMap.put(key, req." + key + ");/n"
                }
            }*/  //这一段需要拼接String

            addBodyObj(bodyMap);
            addParamsMap(headerMap);
            addParamsMap(paramsMap);
        }
    }
}
