package it.wsh.cn.wshutilslib.mvp;


import android.os.Bundle;
import android.os.PersistableBundle;

import it.wsh.cn.wshutilslib.rx.RXManager;

/**
 * author:     xumin
 * date:       2018/6/1
 * email:      xumin2@evergrande.cn
 */
public class BasePresenter<V extends IView> implements IBasePresenter<V> {

    protected V mView;

    @Override
    public void onAttachView(V view) {

        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = null;
        RXManager.get().onUnsubscribe();

    }

    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {


    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }
}
