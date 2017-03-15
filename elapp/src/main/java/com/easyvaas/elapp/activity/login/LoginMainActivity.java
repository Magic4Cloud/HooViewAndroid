/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;

import com.easyvaas.common.sharelogin.weibo.WeiboLoginManager;

import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseFragmentActivity;
import com.easyvaas.elapp.base.TabManager;
import com.easyvaas.elapp.utils.Constants;

public class LoginMainActivity extends BaseFragmentActivity {
    private static final String TAG = "LoginMainActivity";

    private static final String TAB_LOGIN_HOME = Constants.ACTION_GO_LOGIN_HOME;
    private static final String TAB_LOGIN = Constants.ACTION_GO_LOGIN;
    private static final String TAB_REGISTER = Constants.ACTION_REGISTER;
    private static final String TAB_RESET_PWD = Constants.ACTION_GO_RESET_PWD;

    private TabHost mTabHost;
    private InputMethodManager mInputMethodManager;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_GO_LOGIN_HOME.equals(intent.getAction())) {
                mTabHost.setCurrentTabByTag(TAB_LOGIN_HOME);
            } else if (Constants.ACTION_GO_LOGIN.equals(intent.getAction())) {
                mTabHost.setCurrentTabByTag(TAB_LOGIN);
            } else if (Constants.ACTION_GO_REGISTER.equals(intent.getAction())) {
                getIntent().setAction(Constants.ACTION_REGISTER);
                mTabHost.setCurrentTabByTag(TAB_REGISTER);
            } else if (Constants.ACTION_GO_RESET_PWD.equals(intent.getAction())) {
                getIntent().setAction(Constants.ACTION_RESET_PASSWORD);
                mTabHost.setCurrentTabByTag(TAB_RESET_PWD);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsCancelRequestAfterDestroy = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        TabManager mTabManager = new TabManager(this, mTabHost, R.id.real_tab_content);
        mTabManager.addTab(mTabHost.newTabSpec(TAB_LOGIN_HOME).setIndicator(""),
                UserLoginHomeFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(TAB_LOGIN).setIndicator(""),
                UserLoginFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(TAB_REGISTER).setIndicator(""),
                UserRegisterFragment.class, null);
        mTabManager.addTab(mTabHost.newTabSpec(TAB_RESET_PWD).setIndicator(""),
                UserRegisterFragment.class, null);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_GO_LOGIN_HOME);
        filter.addAction(Constants.ACTION_GO_LOGIN);
        filter.addAction(Constants.ACTION_GO_REGISTER);
        filter.addAction(Constants.ACTION_GO_RESET_PWD);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String action = getIntent().getAction();
        if (Constants.ACTION_GO_REGISTER.equals(action)) {
            mTabHost.setCurrentTabByTag(TAB_REGISTER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getIntent().getBooleanExtra("is_weibo_login", false)) {
            WeiboLoginManager.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mTabHost.getCurrentTabTag().equals(TAB_LOGIN)
                || mTabHost.getCurrentTabTag().equals(TAB_REGISTER)) {
            mTabHost.setCurrentTabByTag(TAB_LOGIN_HOME);
        } else if (mTabHost.getCurrentTabTag().equals(TAB_RESET_PWD)) {
            mTabHost.setCurrentTabByTag(TAB_LOGIN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null) {
                if (getCurrentFocus().getWindowToken() != null) {
                    mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
