package it.wsh.cn.wshutilslib.mvp;



/**
 * Created by guotingzhu@evergrande.cn on 2017/7/26.
 * modified by xumin on 2018/06/01
 */

public interface IBasePresenter<V extends IView> {

    void onAttachView(V view);

    void onDetachView();
}
