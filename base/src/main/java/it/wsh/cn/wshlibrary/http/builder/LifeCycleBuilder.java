package it.wsh.cn.wshlibrary.http.builder;


import android.arch.lifecycle.LifecycleOwner;

import it.wsh.cn.wshlibrary.http.HttpClient;

/**
 * author: wenshenghui
 * created on: 2018/8/7 10:15
 * description:  需要绑定生命周期的Builder
 */
public abstract class LifeCycleBuilder<T> extends CommonBuilder<T> {

    private String TAG = "LifeCycleBuilder";

    protected int mTagHash;

    /**
     *
     * 如果要绑定生命周期，界面销毁时取消请求，
     * 则tag需要传Activity或者Fragmeng对象
     * @param tag
     * @return
     */
    public CommonBuilder<T> setTag(Object tag) {
        registerLifecycle(tag);
        mTagHash = tag == null ? TAG.hashCode() : tag.hashCode();
        return this;
    }

    /**
     * @param tag
     */
    public void registerLifecycle(Object tag) {
        LifecycleOwner owner;
        if (tag instanceof LifecycleOwner) {
            owner = (LifecycleOwner) tag;
            owner.getLifecycle().addObserver(HttpClient.getInstance());
        }
    }

    @Override
    protected int getTagHash() {
        if (mTagHash == 0) {
            return TAG.hashCode();
        }else {
            return mTagHash;
        }
    }
}
