/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.easyvaas.common.emoji.utils.EmoticonsUtils;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.SoftReference;

public abstract class BaseActivity extends AppCompatActivity {
    private SystemBarTintManager mTintManager;
    private Dialog mLoadingDialog;
    private long mStartShowTime;
    protected boolean mIsFragmentActivity;
    protected boolean mIsCancelRequestAfterDestroy = true;
    protected boolean mIsActionBarColorStatusBar = false;

    protected static class MyHandler<T> extends Handler {
        private SoftReference<T> softReference;

        public MyHandler(T activity) {
            softReference = new SoftReference<T>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            T activity = softReference.get();
            if (activity == null) {
                return;
            }
            ((BaseActivity) activity).handleMessage(msg);
        }
    }

    protected void handleMessage(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleToast.show(this,getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1));
        mStartShowTime = System.currentTimeMillis();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // setTranslucentStatus(true);

            if (!Utils.isFullScreen(this) && ((actionBar != null && actionBar.isShowing())
                    || mIsActionBarColorStatusBar)) {
                mTintManager = new SystemBarTintManager(this);
                mTintManager.setStatusBarTintEnabled(true);
                // mTintManager.setStatusBarTintResource(R.color.action_bar_bg);
            }
        }
    }

    protected void showActionBar(boolean isShow, View rootView) {
        if (getSupportActionBar() != null) {
            if (isShow) {
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setFitsSystemWindows(View rootView, boolean fit) {
        if (rootView != null) {
            rootView.setFitsSystemWindows(fit);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void showLoadingDialog(int resId, boolean dismissTouchOutside, boolean cancelable) {
        showLoadingDialog(getString(resId), dismissTouchOutside, cancelable);
    }

    protected void showLoadingDialog(String message, boolean dismissTouchOutside, boolean cancelable) {
        if (isFinishing()) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogUtil.getProcessDialog(this, message, dismissTouchOutside, cancelable);
        }
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(dismissTouchOutside);
        mLoadingDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getSupportParentActivityIntent();
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        return intent;
    }

    @Override
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng analytics
        if (!mIsFragmentActivity) {
            MobclickAgent.onPageStart(this.getClass().getSimpleName());
        }
        MobclickAgent.onResume(this);
        mStartShowTime = Long.MAX_VALUE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng analytics
        if (!mIsFragmentActivity) {
            MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        }
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        if (mIsCancelRequestAfterDestroy) {
            ApiHelper.getInstance().cancelRequest();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // ViewUtils.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    protected void startActivity(Class classz, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), classz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(Class classz) {
        Intent intent = new Intent(getApplicationContext(), classz);
        startActivity(intent);
    }

    protected void startActivityForResults(Class classz, int mode) {
        Intent intent = new Intent(getApplicationContext(), classz);
        startActivityForResult(intent, mode);
    }

    protected boolean isInputMethodShow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    protected void toggleInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    protected void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
        //        getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (EmoticonsUtils.emojiMap.size() == 0) {
            EmoticonsUtils.initEmoticonsDB(this.getApplication(), false);
        }
    }
}
