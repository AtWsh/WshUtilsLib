package it.wsh.cn.wshutilslib.mvp;

import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * author:     xumin
 * date:       2018/6/1
 * email:      xumin2@evergrande.cn
 */
public abstract class BasePresenterActivity<P extends BasePresenter<V>, V extends IView> extends BaseUiActivity {
    protected P mPresenter;

    protected abstract P initPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initData在基类中是oncreate之后，所以需要这里在oncreate之前初始化出来
        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.onAttachView((V) this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mPresenter.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPresenter.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDetachView();
            mPresenter = null;
        }
        super.onDestroy();
    }
}
