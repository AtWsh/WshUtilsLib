package it.wsh.cn.wshutilslib.mvp;

public interface IBaseView extends IView {
    void hideLoadingDialog();

    void showLoadingDialog();

    void showToast(String message);

}
