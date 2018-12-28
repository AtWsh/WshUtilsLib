package it.wsh.cn.componentbase.services;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


/**
 * author: wenshenghui
 * created on: 2018/8/10 16:39
 * description:
 */
public interface IAccountService {

    boolean isLogin();

    String getAccountId();

    /**
     * 创建 UserFragment
     * @param activity
     * @param containerId
     * @param manager
     * @param bundle
     * @param tag
     * @return
     */
    Fragment newUserFragment(Activity activity, int containerId, FragmentManager manager, Bundle bundle, String tag);
}
