/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import com.easyvaas.elapp.utils.DialogUtil;

public class BasePlayerActivity extends Activity {
    //TODO User this base activity to fix PlayerActivity input comment issue

    private Dialog mLoadingDialog;
    private long mStartShowTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartShowTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng analytics
        MobclickAgent.onResume(this);
        mStartShowTime = Long.MAX_VALUE;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Umeng analytics
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }

    protected void showLoadingDialog(int resId, boolean dismissTouchOutside, boolean cancelable) {
        showLoadingDialog(getString(resId), dismissTouchOutside, cancelable);
    }

    protected void showLoadingDialog(String message, boolean dismissTouchOutside, boolean cancelable) {
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
}
