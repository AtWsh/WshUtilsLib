package it.wsh.cn.common_pay.pay.model;


/**
 * h5返回的支付宝支付参数信息
 */
public class AliPayRes {
    private String orderStr;

    public String getOrderStr() {
        return orderStr;
    }

    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }

    @Override
    public String toString() {
        return "AliPayRes{" +
                "orderStr='" + orderStr + '\'' +
                '}';
    }
}