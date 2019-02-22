package it.wsh.cn.hdpay.callback;

public interface PasswordInputListener {
    //添加密码输入完成的接口
    void inputFinish();

    //取消支付接口
    void cancel();

    //忘记密码接口
    void forgetPwd();
}
