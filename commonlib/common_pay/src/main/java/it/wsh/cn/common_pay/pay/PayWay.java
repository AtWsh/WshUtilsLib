package it.wsh.cn.common_pay.pay;

import android.support.annotation.StringDef;

/**
 * author: wenshenghui
 * created on: 2018/8/2 9:58
 * description:
 */
public class PayWay {

    public static final String ALI_PAY = "alipay_zsj";
    public static final String WX_PAY = "wechat_zsj";
    public static final String HD_PAY = "hdpay_zsj";

    @StringDef({ALI_PAY, WX_PAY, HD_PAY})
    public @interface Type{}
}
