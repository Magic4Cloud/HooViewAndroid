/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.activity;

import java.util.Stack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;

public class SetPasswordActivity extends BaseActivity {
    public static final String EXTRA_KEY_PASSWORD = "extra_key_password";

    private Stack<String> mStack;
    private String mVid;

    private TextView mPasswordTv1;
    private TextView mPasswordTv2;
    private TextView mPasswordTv3;
    private TextView mPasswordTv4;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_one:
                case R.id.btn_two:
                case R.id.btn_three:
                case R.id.btn_four:
                case R.id.btn_five:
                case R.id.btn_six:
                case R.id.btn_seven:
                case R.id.btn_eight:
                case R.id.btn_nine:
                case R.id.btn_zero:
                    setPassword(((Button) v).getText().toString());
                    break;
                case R.id.btn_delete:
                    if (mStack.size() > 0) {
                        mStack.pop();
                    }
                    updatePasswordTextView();
                    break;
                case R.id.btn_cancel:
                    setResult(RESULT_CANCELED);
                    mStack.clear();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCancelRequestAfterDestroy = false;
        setContentView(R.layout.activity_set_password);
        setTitle(R.string.permission_password);
        mVid = getIntent().getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);

        findViewById(R.id.btn_one).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_two).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_three).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_four).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_five).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_six).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_seven).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_eight).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_nine).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_zero).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_delete).setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_cancel).setOnClickListener(mOnClickListener);

        mPasswordTv1 = (TextView) findViewById(R.id.password_1_tv);
        mPasswordTv2 = (TextView) findViewById(R.id.password_2_tv);
        mPasswordTv3 = (TextView) findViewById(R.id.password_3_tv);
        mPasswordTv4 = (TextView) findViewById(R.id.password_4_tv);

        mStack = new Stack<String>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                returnResult();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPassword(String pwd) {
        if (mStack.size() < 4) {
            mStack.push(pwd);
        }
        updatePasswordTextView();
    }

    private void updatePasswordTextView() {
        switch (mStack.size()) {
            case 0:
                // Do nothing
                mPasswordTv1.setText("");
                mPasswordTv2.setText("");
                mPasswordTv3.setText("");
                mPasswordTv4.setText("");
                break;
            case 1:
                mPasswordTv1.setText(mStack.peek());
                mPasswordTv2.setText("");
                mPasswordTv3.setText("");
                mPasswordTv4.setText("");
                break;
            case 2:
                mPasswordTv2.setText(mStack.peek());
                mPasswordTv3.setText("");
                mPasswordTv4.setText("");
                break;
            case 3:
                mPasswordTv3.setText(mStack.peek());
                mPasswordTv4.setText("");
                break;
            case 4:
                mPasswordTv4.setText(mStack.peek());
                returnResult();
                break;
        }
    }

    private void returnResult() {
        String password = "";
        for (String pwd : mStack) {
            password += pwd;
        }
        if (TextUtils.isEmpty(password)) {
            SingleToast.show(this, R.string.msg_password_empty);
            return;
        } else if (password.length() != 4) {
            SingleToast.show(this, R.string.msg_password_invalid);
            return;
        }

        if (!TextUtils.isEmpty(mVid)) {
            Intent intent = getIntent();
            intent.putExtra(EXTRA_KEY_PASSWORD, password);
            intent.setClass(this, PlayerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_KEY_PASSWORD, password);
            setResult(RESULT_OK, intent);
        }
        finish();
    }
}
