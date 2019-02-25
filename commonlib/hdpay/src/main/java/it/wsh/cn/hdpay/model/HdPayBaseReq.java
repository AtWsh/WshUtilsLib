package it.wsh.cn.hdpay.model;

public class HdPayBaseReq {

    public long timestamp;
    public String head;
    public String body;
    public String sign;
    public String version;

    public HdPayBaseReq() {
        timestamp = System.currentTimeMillis();
        head = "";
        body = "";
        version = "1.0";
    }

    public void createSign() {
        //todo 生成sign
    }
}
