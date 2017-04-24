package com.easyvaas.elapp.ui.user.newuser.activity;

import android.support.v4.app.FragmentManager;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserFansFragment;
import com.hooview.app.R;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 用户粉丝
 */

public class UserFansActivity extends MyBaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_collection_layout;
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.user_my_fans);
    }

    @Override
    protected void initViewAndData() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.user_collection_layout, UserFansFragment.newInstance(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid())).commit();
    }
}
