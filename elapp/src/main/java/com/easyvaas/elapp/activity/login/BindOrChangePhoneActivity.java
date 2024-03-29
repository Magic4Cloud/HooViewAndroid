/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.TimeButton;

import com.hooview.app.R;
import com.easyvaas.elapp.activity.setting.BindPhoneActivity;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.ValidateParam;

public class BindOrChangePhoneActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "BindOrChangePhoneActivity";
    public static final String EXTRA_PHONE_NUMBER = "extra_phone_number";
    private String mPhoneNumber;
    private TimeButton mTimeButton;
    private String mSmsId;
    private String mSmsCode;
    private TextView mPhoneNumberTv;
    private EditText mVerifyCodeEt;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_before_change_phone);
        setTitle(R.string.verify_phone);
        mPhoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        if (TextUtils.isEmpty(mPhoneNumber)) {
            finish();
            return;
        }
        mPhoneNumberTv = (TextView) findViewById(R.id.vp_phone_number_tv);
        mPhoneNumberTv.setText(mPhoneNumber);
        mVerifyCodeEt = (EditText) findViewById(R.id.vp_verification_et);
        mTimeButton = (TimeButton) findViewById(R.id.vp_time_btn);
        mTimeButton.setOnClickListener(this);
        findViewById(R.id.vp_phone_error_tv).setOnClickListener(this);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mCommitTv.setVisibility(View.VISIBLE);
        mCommitTv.setText(getString(R.string.next_step));
        mCenterContentTv.setText(getString(R.string.verify_phone));
        mCenterContentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCommitLl.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        menu.findItem(R.id.menu_complete).setTitle(R.string.next_step);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                mSmsCode = mVerifyCodeEt.getText().toString();
                if (mSmsCode.length() < 4) {
                    SingleToast.show(this, R.string.prompt_verification_input);
                    return false;
                }
                if (mSmsCode.length() >= 4 && !TextUtils.isEmpty(mPhoneNumber)) {
                    verifySms();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vp_time_btn:
                mPhoneNumber = mPhoneNumberTv.getText().toString().trim();
                if (!TextUtils.isEmpty(mPhoneNumber)) {
                    if (ValidateParam.validatePhone(mPhoneNumber)) {
                        requestSms();
                        mTimeButton.startTime();
                    } else {
                        SingleToast.show(this, R.string.msg_phone_number_invalid);
                    }
                } else {
                    SingleToast.show(this, R.string.msg_phone_number_empty);
                }
                break;
            case R.id.vp_phone_error_tv:
                Intent serviceIntent = new Intent(this, TextActivity.class);
                serviceIntent.putExtra(TextActivity.EXTRA_TYPE, TextActivity.TYPE_PHONE_BIND_ISSUE);
                serviceIntent
                        .putExtra(Constants.EXTRA_KEY_TITLE, getString(R.string.verify_appeals_phone_error));
                startActivity(serviceIntent);
                break;
            case R.id.add_option_view:
                mSmsCode = mVerifyCodeEt.getText().toString();
                if (mSmsCode.length() < 4) {
                    SingleToast.show(this, R.string.prompt_verification_input);
                    return;
                }
                if (mSmsCode.length() >= 4 && !TextUtils.isEmpty(mPhoneNumber)) {
                    verifySms();
                }
                break;
            default:
                break;
        }
    }

    private void requestSms() {
        ApiHelper.getInstance().smsSend(mPhoneNumber, ApiHelper.SMS_TYPE_RESET_PWD,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            mSmsId = JsonParserUtil.getString(result, ApiConstant.KEY_SMS_ID);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        if (ApiConstant.E_SMS_INTERVAL.equals(errorInfo)) {
                            SingleToast
                                    .show(getApplicationContext(), R.string.msg_get_sms_duration_too_short);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mTimeButton.clearTimer();
                        mTimeButton.setText(R.string.get_verification);
                    }
                });
    }

    private void verifySms() {
        ApiHelper.getInstance().smsVerify(mSmsId, mSmsCode, User.AUTH_TYPE_PHONE,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            /*if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                                     .getInt(result, ApiConstant.KEY_REGISTERED)) {
                                SingleToast.show(getApplicationContext(), R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED)) {*/
                            Intent intent = new Intent(getApplicationContext(), BindPhoneActivity.class);
                            intent.putExtra(BindPhoneActivity.EXTRA_IS_CHANGE_BIND_PHONE, true);
                            startActivity(intent);
                            finish();
                            //}
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getApplicationContext(), R.string.msg_verify_failed);
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }
}
