package it.wsh.cn.hdpay.model;

public class HdAuthenticationReq extends HdPayBaseReq {

    //鉴权参数
    public String prepay_id;
    public String wallet_id;
    public String out_trade_no;
    public String time_expire;

    public HdAuthenticationReq(HdPayInfo hdPayReq) {
        if (hdPayReq != null) {
            prepay_id = hdPayReq.prepay_id;
            wallet_id = hdPayReq.wallet_id;
            out_trade_no = hdPayReq.out_trade_no;
            time_expire = hdPayReq.time_expire;
            timestamp = System.currentTimeMillis();
            head = hdPayReq.head;
            body = hdPayReq.body;
            sign = hdPayReq.sign;
            version = hdPayReq.version;
        }
    }
}
