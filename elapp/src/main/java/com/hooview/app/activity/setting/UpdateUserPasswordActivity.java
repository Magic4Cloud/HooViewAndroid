/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.base.BaseActivity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.SingleToast;

public class UpdateUserPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mNewPasswordEt;
    private EditText mOldPasswordEt;
    private EditText mNewPasswordConfirmEt;
    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_update_password);
        setTitle(com.hooview.app.R.string.update_password);
        initView();
    }

    private void initView() {
        mNewPasswordEt = (EditText) findViewById(com.hooview.app.R.id.new_password_et);
        mOldPasswordEt = (EditText) findViewById(com.hooview.app.R.id.old_password_et);
        mNewPasswordConfirmEt = (EditText) findViewById(com.hooview.app.R.id.new_password_confirm_et);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText(getString(com.hooview.app.R.string.complete));
        mCenterContentTv.setText(getString(com.hooview.app.R.string.update_password));
        mCommitLl.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.hooview.app.R.menu.complete, menu);
        menu.findItem(com.hooview.app.R.id.menu_complete).setTitle(com.hooview.app.R.string.complete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.hooview.app.R.id.menu_complete:
                VerifyPassword();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void VerifyPassword() {
        String oldPass = mOldPasswordEt.getText().toString().trim();
        String newPass = mNewPasswordEt.getText().toString().trim();
        String newPassConfirm = mNewPasswordConfirmEt.getText().toString().trim();
        if (TextUtils.isEmpty(oldPass)) {
            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_old_password_empty);
            return;
        }
        if (TextUtils.isEmpty(newPass)) {
            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_new_password_empty);
            return;
        }
        if (TextUtils.isEmpty(newPassConfirm)) {
            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_new_password_again_empty);
            return;
        }
        if (newPass.equals(oldPass)){
            SingleToast.show(getApplicationContext(), getString(com.hooview.app.R.string.password_identical_tip));
            return;
        }
        if (!newPass.equals(newPassConfirm)) {
            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_inconsistent_password);
            return;
        }
        if (!verifyPasswordFormat(oldPass) || !verifyPasswordFormat(newPass)) {
            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_password_invalid);
            return;
        }
        updatePassWord(oldPass, newPass);
    }

    public boolean verifyPasswordFormat(String str) {
        String regex = "[a-zA-Z\\d]{6,20}";
        return Pattern.matches(regex, str);
    }

    private void updatePassWord(String oldPassword, String newPassword) {
        ApiHelper.getInstance().userUpdatePassword(oldPassword, newPassword
                , new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_update_password_success);
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(String msg) {
                        if (ApiConstant.E_AUTH.equals(msg)) {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_password_old_wrong);
                        } else if (ApiConstant.E_AUTH_MERGE_CONFLICTS.equals(msg)) {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_error_auth_conflicts);
                        } else {
                            SingleToast.show(getApplicationContext(), msg);
                        }
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.hooview.app.R.id.add_option_view:
                VerifyPassword();
                break;
        }
    }
}

