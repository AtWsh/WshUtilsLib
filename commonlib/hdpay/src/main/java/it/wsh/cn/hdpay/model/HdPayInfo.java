package it.wsh.cn.hdpay.model;

public class HdPayInfo extends HdPayBaseReq {

    public String wallet_id;

    //鉴权参数
    public String prepay_id;
    public String out_trade_no;
    public String time_expire;

    //二维码轮询参数
    public String qr_code;

}
