/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

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
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.JsonParserUtil;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ValidateParam;

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
        setContentView(com.hooview.app.R.layout.activity_bind_phone_auth);
        mIsChangeBindPhone = getIntent().getBooleanExtra(EXTRA_IS_CHANGE_BIND_PHONE, false);

        mPhoneNumberEt = (EditText) findViewById(com.hooview.app.R.id.register_phone_et);
        mVerificationEt = (EditText) findViewById(com.hooview.app.R.id.verification_code_et);
        mPasswordEt = (EditText) findViewById(com.hooview.app.R.id.set_password_et);
        mSubmitBtn = (Button) findViewById(com.hooview.app.R.id.bp_submit_btn);
        mSubmitBtn.setOnClickListener(this);
        mTimeButton = (TimeButton) findViewById(com.hooview.app.R.id.bp_time_btn);
        mTimeButton.setOnClickListener(this);
        mCountryNameTv = (TextView) findViewById(com.hooview.app.R.id.select_country_tv);
        mCountryCodeTv = (TextView) findViewById(com.hooview.app.R.id.select_code_txv);
        TextView tv = (TextView) findViewById(com.hooview.app.R.id.select_code_msg_tv);
        tv.setTextColor(getResources().getColor(com.hooview.app.R.color.text_gray));
        mCountryNameTv.setTextColor(getResources().getColor(com.hooview.app.R.color.text_gray));
        mCountryCodeTv.setTextColor(getResources().getColor(com.hooview.app.R.color.text_gray));
        findViewById(com.hooview.app.R.id.country_code_rl).setOnClickListener(this);
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

        TextView tv1 = (TextView) findViewById(R.id.common_custom_title_tv);


        if (mIsChangeBindPhone) {
            tv1.setText(R.string.title_change_phone_number);
            mPhoneNumberEt.setHint(com.hooview.app.R.string.prompt_new_phone_hint);
            findViewById(com.hooview.app.R.id.set_password_rl).setVisibility(View.GONE);
        } else {
            tv1.setText(R.string.bind_phone);
        }
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.updateCountryCode(BindPhoneActivity.this, mCountryNameTv, mCountryCodeTv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.hooview.app.R.id.bp_time_btn:
                mPhoneNumber = mPhoneNumberEt.getText().toString().trim();
                if (!TextUtils.isEmpty(mPhoneNumber)) {
                    if (ValidateParam.validatePhone(mPhoneNumber)) {
                        requestSms();
                        mTimeButton.startTime();
                    } else {
                        SingleToast.show(this, com.hooview.app.R.string.msg_phone_number_invalid);
                    }
                } else {
                    SingleToast.show(this, com.hooview.app.R.string.msg_phone_number_empty);
                }
                break;
            case com.hooview.app.R.id.country_code_rl:
                Intent countryIntent = new Intent(BindPhoneActivity.this, CountryCodeListActivity.class);
                startActivity(countryIntent);
                break;
            case com.hooview.app.R.id.bp_submit_btn:
                if (TextUtils.isEmpty(mVerificationEt.getText())) {
                    SingleToast.show(this, com.hooview.app.R.string.msg_verify_code_empty);
                } else if (TextUtils.isEmpty(mPhoneNumber)) {
                    SingleToast.show(this, getString(com.hooview.app.R.string.msg_phone_number_empty));
                } else if (mIsChangeBindPhone) {
                    changeBindPhone();
                } else if (validatePassword()) {
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
            SingleToast.show(this, com.hooview.app.R.string.msg_password_empty);
        } else if (1 == type) {
            SingleToast.show(this, com.hooview.app.R.string.msg_password_space);
        } else if (2 == type) {
            SingleToast.show(this, com.hooview.app.R.string.msg_password_chinese);
        } else if (3 == type) {
            SingleToast.show(this, com.hooview.app.R.string.msg_password_asterisk);
        } else if (mPassword.length() < 6) {
            SingleToast.show(this, com.hooview.app.R.string.msg_password_length);
        } else {
            return true;
        }
        return false;
    }

    private void bindingPhone() {
        ApiHelper.getInstance().bindPhone(mPhoneNumber, mPassword, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_account_bind_success);
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
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_phone_have_bound);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                Logger.w(TAG, "bind phone failed: " + msg);
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_account_bind_failed);
            }
        });
    }

    private void changeBindPhone() {
        ApiHelper.getInstance().changeBindPhone(mPhoneNumber, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_account_bind_change_phone_success);
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
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_phone_have_bound);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                Logger.w(TAG, "bind phone failed: " + msg);
                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_account_bind_failed);
            }
        });
    }

    private void requestSms() {
        ApiHelper.getInstance().smsSend(mPhoneNumber, ApiHelper.SMS_TYPE_REGISTER,
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
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_get_sms_duration_too_short);
                        } else if (ApiConstant.E_USER_EXISTS.equals(errorInfo)) {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_phone_have_bound);
                            mTimeButton.clearTimer();
                            mTimeButton.setText(com.hooview.app.R.string.get_verification);
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
                                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                                    .getInt(result,
                                            ApiConstant.KEY_REGISTERED)) {
                                // Do nothing
                                mSubmitBtn.setEnabled(true);
                            }
                            if (ApiConstant.VALUE_SNS_HAVE_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_SNS_NOT_BIND == JsonParserUtil
                                    .getInt(result,
                                            ApiConstant.KEY_CONFLICTED)) {
                            }
                        } else {
                            SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_verify_failed);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_verify_failed);
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }
}
