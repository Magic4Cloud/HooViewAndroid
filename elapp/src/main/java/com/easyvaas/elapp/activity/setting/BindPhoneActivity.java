/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easyvaas.common.widget.TimeButton;

import com.hooview.app.R;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ValidateParam;

public class BindPhoneActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = BindPhoneActivity.class.getSimpleName();
    public static final String EXTRA_IS_CHANGE_BIND_PHONE = "extra_is_change_bind_phone";

    private TextView mCountryNameTv;
    private TextView mCountryCodeTv;
    private EditText mPhoneNumberEt;
    private String mPhoneNumber;
    private EditText mVerificationEt;
    private EditText mPasswordEt;
    private Button mSubmitBtn;
    private TimeButton mTimeButton;
    private String mPassword;

    private String mSmsId;
    private String mSmsCode;

    private boolean mIsChangeBindPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone_auth);
        mIsChangeBindPhone = getIntent().getBooleanExtra(EXTRA_IS_CHANGE_BIND_PHONE, false);

        mPhoneNumberEt = (EditText) findViewById(R.id.register_phone_et);
        mVerificationEt = (EditText) findViewById(R.id.verification_code_et);
        mPasswordEt = (EditText) findViewById(R.id.set_password_et);
        mSubmitBtn = (Button) findViewById(R.id.bp_submit_btn);
        mSubmitBtn.setOnClickListener(this);
        mTimeButton = (TimeButton) findViewById(R.id.bp_time_btn);
        mTimeButton.setOnClickListener(this);
        mCountryNameTv = (TextView) findViewById(R.id.select_country_tv);
        mCountryCodeTv = (TextView) findViewById(R.id.select_code_txv);
        TextView tv = (TextView) findViewById(R.id.select_code_msg_tv);
        tv.setTextColor(getResources().getColor(R.color.text_gray));
        mCountryNameTv.setTextColor(getResources().getColor(R.color.text_gray));
        mCountryCodeTv.setTextColor(getResources().getColor(R.color.text_gray));
        findViewById(R.id.country_code_rl).setOnClickListener(this);
        mVerificationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSmsCode = s.toString();
                if (mSmsCode.length() >= 4 && !TextUtils.isEmpty(mPhoneNumber)) {
                    verifySms();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (mIsChangeBindPhone) {
            setTitle(R.string.title_change_phone_number);
            mPhoneNumberEt.setHint(R.string.prompt_new_phone_hint);
            findViewById(R.id.set_password_rl).setVisibility(View.GONE);
        } else {
            setTitle(R.string.bind_phone);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.updateCountryCode(BindPhoneActivity.this, mCountryNameTv, mCountryCodeTv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bp_time_btn:
                mPhoneNumber = mPhoneNumberEt.getText().toString().trim();
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
            case R.id.country_code_rl:
                Intent countryIntent = new Intent(BindPhoneActivity.this, CountryCodeListActivity.class);
                startActivity(countryIntent);
                break;
            case R.id.bp_submit_btn:
                if (TextUtils.isEmpty(mVerificationEt.getText())) {
                    SingleToast.show(this, R.string.msg_verify_code_empty);
                } else if (TextUtils.isEmpty(mPhoneNumber)) {
                    SingleToast.show(this, getString(R.string.msg_phone_number_empty));
                } else if (mIsChangeBindPhone) {
                    changeBindPhone();
                } else if (validatePassword()){
                    bindingPhone();
                }
                break;
            default:
                break;
        }
    }

    private boolean validatePassword() {
        mPassword = mPasswordEt.getText().toString().trim();
        int type = ValidateParam.validateUserPasswords(mPassword);
        if (TextUtils.isEmpty(mPassword)) {
            SingleToast.show(this, R.string.msg_password_empty);
        } else if (1 == type) {
            SingleToast.show(this, R.string.msg_password_space);
        } else if (2 == type) {
            SingleToast.show(this, R.string.msg_password_chinese);
        } else if (3 == type) {
            SingleToast.show(this, R.string.msg_password_asterisk);
        } else if (mPassword.length() < 6) {
            SingleToast.show(this, R.string.msg_password_length);
        } else {
            return true;
        }
        return false;
    }

    private void bindingPhone() {
        ApiHelper.getInstance().bindPhone(mPhoneNumber, mPassword, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                SingleToast.show(getApplicationContext(), R.string.msg_account_bind_success);
                User.AuthEntity authEntity = EVApplication.getUser().new AuthEntity();
                authEntity.setToken(ApiConstant.VALUE_COUNTRY_CODE + mPhoneNumber);
                authEntity.setLogin(0);
                authEntity.setType(User.AUTH_TYPE_PHONE);
                EVApplication.getUser().getAuth().add(authEntity);
                Preferences.getInstance(getApplicationContext())
                        .putString(Preferences.KEY_LOGIN_PHONE_NUMBER, authEntity.getToken());
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_KEY_USER_PHONE, mPhoneNumber);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (ApiConstant.E_AUTH_MERGE_CONFLICTS.equals(errorInfo)) {
                    SingleToast.show(getApplicationContext(), R.string.msg_phone_have_bound);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                Logger.w(TAG, "bind phone failed: " + msg);
                SingleToast.show(getApplicationContext(), R.string.msg_account_bind_failed);
            }
        });
    }

    private void changeBindPhone() {
        ApiHelper.getInstance().changeBindPhone(mPhoneNumber, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                SingleToast.show(getApplicationContext(), R.string.msg_account_bind_change_phone_success);
                for (User.AuthEntity entity : EVApplication.getUser().getAuth()) {
                    if (User.AUTH_TYPE_PHONE.equals(entity.getType())) {
                        entity.setToken(ApiConstant.VALUE_COUNTRY_CODE + mPhoneNumber);
                        break;
                    }
                }
                Preferences.getInstance(getApplicationContext())
                        .putString(Preferences.KEY_LOGIN_PHONE_NUMBER,
                                ApiConstant.VALUE_COUNTRY_CODE + mPhoneNumber);
                finish();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (ApiConstant.E_AUTH_MERGE_CONFLICTS.equals(errorInfo)) {
                    SingleToast.show(getApplicationContext(), R.string.msg_phone_have_bound);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                Logger.w(TAG, "bind phone failed: " + msg);
                SingleToast.show(getApplicationContext(), R.string.msg_account_bind_failed);
            }
        });
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
                            SingleToast.show(getApplicationContext(), R.string.msg_get_sms_duration_too_short);
                        } else if (ApiConstant.E_USER_EXISTS.equals(errorInfo)) {
                            SingleToast.show(getApplicationContext(), R.string.msg_phone_have_bound);
                            mTimeButton.clearTimer();
                            mTimeButton.setText(R.string.get_verification);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }

    private void verifySms() {
        ApiHelper.getInstance().smsVerify(mSmsId, mSmsCode, User.AUTH_TYPE_PHONE,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED)) {
                                SingleToast.show(getApplicationContext(), R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                                    .getInt(result,
                                            ApiConstant.KEY_REGISTERED)) {
                                // Do nothing
                                mSubmitBtn.setEnabled(true);
                            }
                            if (ApiConstant.VALUE_SNS_HAVE_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                                SingleToast.show(getApplicationContext(), R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_SNS_NOT_BIND == JsonParserUtil
                                    .getInt(result,
                                            ApiConstant.KEY_CONFLICTED)) {
                            }
                        } else {
                            SingleToast.show(getApplicationContext(), R.string.msg_verify_failed);
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
