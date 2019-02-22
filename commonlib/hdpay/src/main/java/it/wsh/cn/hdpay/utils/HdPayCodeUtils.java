package it.wsh.cn.hdpay.utils;

import it.wsh.cn.hdpay.HdPayResultCode;

public class HdPayCodeUtils {

    public static int getResultCodeInt(String codeStr) {

        int resultCode = HdPayResultCode.UNKNOW;

        switch (codeStr) {
            case HdPayConstants.HdPayResultStringCode.PASS_WORD_ERROR:
                resultCode = HdPayResultCode.PASS_WORD_ERROR_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.INVALID_REQUEST:
                resultCode = HdPayResultCode.INVALID_REQUEST_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NOAUTH:
                resultCode = HdPayResultCode.NOAUTH_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NOTENOUGH:
                resultCode = HdPayResultCode.NOTENOUGH_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.ORDERPAID:
                resultCode = HdPayResultCode.ORDERPAID_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.ORDERCLOSED:
                resultCode = HdPayResultCode.ORDERCLOSED_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.SYSTEMERROR:
                resultCode = HdPayResultCode.SYSTEMERROR_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.APPID_NOT_EXIST:
                resultCode = HdPayResultCode.APPID_NOT_EXIST_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.MCHID_NOT_EXIST:
                resultCode = HdPayResultCode.MCHID_NOT_EXIST_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.APPID_MCHID_NOT_MATCH:
                resultCode = HdPayResultCode.APPID_MCHID_NOT_MATCH_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.LACK_PARAMS:
                resultCode = HdPayResultCode.LACK_PARAMS_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.OUT_TRADE_NO_USED:
                resultCode = HdPayResultCode.OUT_TRADE_NO_USED_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.SIGNERROR:
                resultCode = HdPayResultCode.SIGNERROR_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.XML_FORMAT_ERROR:
                resultCode = HdPayResultCode.XML_FORMAT_ERROR_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.REQUIRE_POST_METHOD:
                resultCode = HdPayResultCode.REQUIRE_POST_METHOD_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.POST_DATA_EMPTY:
                resultCode = HdPayResultCode.POST_DATA_EMPTY_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NOT_UTF8:
                resultCode = HdPayResultCode.NOT_UTF8_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.INVALID_TOKEN:
                resultCode = HdPayResultCode.INVALID_TOKEN_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.TOKEN_TIMEOUT:
                resultCode = HdPayResultCode.TOKEN_TIMEOUT_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.INVALID_AUTHORIZED:
                resultCode = HdPayResultCode.INVALID_AUTHORIZED_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NO_SIGN:
                resultCode = HdPayResultCode.NO_SIGN_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NO_SIGN_TYPE:
                resultCode = HdPayResultCode.NO_SIGN_TYPE_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NO_APPID:
                resultCode = HdPayResultCode.NO_APPID_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NO_TIMESTAMP:
                resultCode = HdPayResultCode.NO_TIMESTAMP_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NO_VERSION:
                resultCode = HdPayResultCode.NO_VERSION_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.UNSUPPORT_SIGN:
                resultCode = HdPayResultCode.UNSUPPORT_SIGN_CODE;
                break;

            case HdPayConstants.HdPayResultStringCode.NOT_SUPPORT_CGI:
                resultCode = HdPayResultCode.NOT_SUPPORT_CGI_CODE;
                break;

        }

        return resultCode;

    }
}
