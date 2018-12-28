package it.wsh.cn.componentbase.emptyservices;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


import it.wsh.cn.componentbase.services.IAccountService;

/**
 * author: wenshenghui
 * created on: 2018/8/10 16:40
 * description:
 */
public class EmptyAccountService implements IAccountService {
    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String getAccountId() {
        return null;
    }

    @Override
    public Fragment newUserFragment(Activity activity, int containerId, FragmentManager manager, Bundle bundle, String tag) {
        return null;
    }
}
