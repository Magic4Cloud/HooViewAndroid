package com.easyvaas.elapp.ui.user.newuser.activity;

import android.support.v4.app.FragmentManager;

import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.newuser.fragment.UserCollectionFragment;
import com.hooview.app.R;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户收藏
 */

public class UserCollectionNewActivity extends MyBaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_collection_layout;
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.user_colloction);
    }

    @Override
    protected void initViewAndData() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.user_collection_layout, UserCollectionFragment.newInstance()).commit();
    }

}
