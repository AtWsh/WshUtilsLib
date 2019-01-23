package it.wsh.cn.common_pay.nativepay.model;


import it.wsh.cn.common_pay.nativepay.ICreatePayObj;

/**
 * h5返回的支付宝支付参数信息
 */
public class AliPayRes implements ICreatePayObj {
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