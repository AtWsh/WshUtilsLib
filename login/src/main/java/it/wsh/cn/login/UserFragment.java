package it.wsh.cn.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.wsh.cn.login.utils.AccountUtils;

/**
 * author: wenshenghui
 * created on: 2018/8/30 15:00
 * description:
 */
public class UserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_user, container, false);
        TextView tvName = view.findViewById(R.id.tv_user_name);
        tvName.setText(AccountUtils.userInfo == null ? "用户未登录" : "登录用户：" + AccountUtils.userInfo.getUserName());
        return view;
    }
}
