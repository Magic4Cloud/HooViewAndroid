package com.easyvaas.common.chat.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.easyvaas.common.chat.utils.DialogUtil;
import com.easyvaas.common.chat.BuildConfig;

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
        mStartShowTime = Long.MAX_VALUE;
    }

    @Override
    protected void onPause() {
        super.onPause();
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
