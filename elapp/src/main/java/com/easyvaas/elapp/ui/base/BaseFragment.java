/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.elapp.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment {

    private Dialog mLoadingDialog;
    private long mStartShowTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mStartShowTime = System.currentTimeMillis();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Umeng analytics
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        mStartShowTime = Long.MAX_VALUE;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Umeng analytics
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    protected void showLoadingDialog(int resId, boolean dismissTouchOutside, boolean cancelable) {
        showLoadingDialog(getString(resId), dismissTouchOutside, cancelable);
    }

    protected void showLoadingDialog(String message, boolean dismissTouchOutside, boolean cancelable) {
        if (!isAdded()) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogUtil.getProcessDialog(getActivity(), message, dismissTouchOutside,
                    cancelable);
        }
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(dismissTouchOutside);
        mLoadingDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (isAdded() && mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
