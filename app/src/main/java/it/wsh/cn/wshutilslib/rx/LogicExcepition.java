package it.wsh.cn.wshutilslib.rx;

/**
 * author:     xumin
 * date:       2018/5/4
 * email:      xumin2@evergrande.cn
 * 业务逻辑异常封装
 */
public class LogicExcepition extends Exception{
    private int code;
    private String errorMsg;

    public LogicExcepition(Throwable throwable, int code){
        this.code=code;
        this.errorMsg=throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
