/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.utils.SingleToast;

public class UserInfoEditActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mCommitLl;
    private ImageView mBackIv;
    private ImageView mClearContentIv;
    private TextView mCenterNameTv;
    private TextView mCommitTv;
    private EditText mRegisterPhoneEt;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_user_info_edit);
        initView();
        initData();
    }

    private void initData() {
        String nickName = getIntent().getStringExtra(UserInfoActivity.EXTRA_KEY_USER_NICKNAME);
        String sign = getIntent().getStringExtra(UserInfoActivity.EXTRA_KEY_USER_SIGN);
        mType = getIntent().getIntExtra(UserInfoActivity.EXTRA_KEY_USER_TYPE, 1);
        if (mType == UserInfoActivity.EXTRA_KEY_USER_TYPE_CHANGE_NICK) {
            mCenterNameTv.setText(getString(com.hooview.app.R.string.nickname));
            if (!TextUtils.isEmpty(nickName)) {
                if (!getString(com.hooview.app.R.string.nickname).equals(nickName)) {
                    mRegisterPhoneEt.setText(nickName);
                    mRegisterPhoneEt.setSelection(nickName.length());
                }
            }
            mRegisterPhoneEt.setHint(getString(com.hooview.app.R.string.nickname));
        } else {
            mCenterNameTv.setText(getString(com.hooview.app.R.string.signature));
            if (!TextUtils.isEmpty(sign)) {
                if (!getString(com.hooview.app.R.string.hint_signature).equals(sign)) {
                    mRegisterPhoneEt.setText(sign);
                    mRegisterPhoneEt.setSelection(sign.length());
                }
            }
            mRegisterPhoneEt.setHint(getString(com.hooview.app.R.string.signature));
        }

    }

    private void initView() {
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        mBackIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCenterNameTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mRegisterPhoneEt = (EditText) findViewById(com.hooview.app.R.id.register_phone_et);
        mClearContentIv = (ImageView) findViewById(com.hooview.app.R.id.clear_phone_number_iv);
        mCommitLl.setOnClickListener(this);
        mClearContentIv.setOnClickListener(this);
        mCommitTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.hooview.app.R.id.add_option_view:
                String content = mRegisterPhoneEt.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    if (mType == UserInfoActivity.EXTRA_KEY_USER_TYPE_CHANGE_NICK) {
                        SingleToast.show(this, getResources().getString(com.hooview.app.R.string.msg_nickname_empty));
                    } else {
                        SingleToast.show(this, getResources().getString(com.hooview.app.R.string.msg_sign_empty));
                    }
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("content", content);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case com.hooview.app.R.id.clear_phone_number_iv:
                mRegisterPhoneEt.setText("");
                break;
        }
    }
}
