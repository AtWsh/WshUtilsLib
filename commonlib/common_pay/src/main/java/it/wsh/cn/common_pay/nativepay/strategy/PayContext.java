package it.wsh.cn.common_pay.nativepay.strategy;

/**
 * PayContext 支付策略的上下文对象
 */
public class PayContext {

    private IPayStrategy payStrategy;

    public PayContext(IPayStrategy payStrategy){
        this.payStrategy = payStrategy;
    }

    public void pay(){
        if(payStrategy != null){
            payStrategy.doPay();
        }
    }
}
