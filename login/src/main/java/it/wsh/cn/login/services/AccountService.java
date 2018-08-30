package it.wsh.cn.login.services;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.FragmentManager;
import android.app.Fragment;


import it.wsh.cn.componentbase.services.IAccountService;
import it.wsh.cn.login.UserFragment;
import it.wsh.cn.login.utils.AccountUtils;

/**
 * author: wenshenghui
 * created on: 2018/8/10 17:14
 * description:
 */
public class AccountService implements IAccountService {
    @Override
    public boolean isLogin() {
        return true;
    }

    @Override
    public String getAccountId() {
        return AccountUtils.userInfo == null ? null : AccountUtils.userInfo.getAccountId();
    }

    @Override
    public Fragment newUserFragment(Activity activity, int containerId, FragmentManager manager, Bundle bundle, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment userFragment = new UserFragment();
        transaction.add(containerId, userFragment, tag);
        transaction.commit();
        return userFragment;
    }
}
