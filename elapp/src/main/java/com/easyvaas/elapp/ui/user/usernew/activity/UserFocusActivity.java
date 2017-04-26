package com.easyvaas.elapp.ui.user.usernew.activity;

import android.support.v4.app.FragmentManager;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserFocusFragment;
import com.hooview.app.R;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 用户关注
 */

public class UserFocusActivity extends MyBaseActivity {

    String userId;
    String sessionId;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_collection_layout;
    }

    @Override
    protected String getTitleText() {
        if (EVApplication.isLogin()) {
            if (userId.equals(EVApplication.getUser().getName()))
                return getString(R.string.user_myfoucs);
        }
            return getString(R.string.user_tafoucs);
    }

    @Override
    protected void initViewAndData() {
        userId = getIntent().getStringExtra(AppConstants.USER_ID);
        sessionId = getIntent().getStringExtra(AppConstants.SESSION_ID);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.user_collection_layout, UserFocusFragment.newInstance(userId,sessionId)).commit();
    }
}
