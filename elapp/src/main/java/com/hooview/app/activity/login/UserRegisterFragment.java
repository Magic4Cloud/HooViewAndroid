/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.widget.TimeButton;
import com.hooview.app.R;
import com.hooview.app.activity.setting.CountryCodeListActivity;
import com.hooview.app.activity.user.UserInfoActivity;
import com.hooview.app.base.BaseFragment;
import com.hooview.app.bean.user.User;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.JsonParserUtil;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.NetworkUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ValidateParam;

import java.security.NoSuchAlgorithmException;

public class UserRegisterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserRegisterFragment";
    private static final String EXTRA_KEY_PHONE_NUMBER = "extra_key_phone_number";

    private static final int EDIT_CLEAR_TAG = 1100;
    private static final int EDIT_VERIFY_PASS_TAG = 1200;
    private static final int STEP_INPUT_PHONE_NUMBER = 0;
    private static final int STEP_REQUEST_SMS_CODE = 1;
    private static final int STEP_VERIFIED_SMS_CODE = 2;
    private static final int STEP_COMMIT_USER_INFO = 3;

    private int mStep = STEP_INPUT_PHONE_NUMBER;
    private boolean mIsResetPasswordMode = false;

    private String mPhoneNumber;
    private String mSmsId;
    private int mSmsType;
    private String mAuthType;
    private TimeButton mTimeBtn;

    private View mRootView;
    private ImageView mPhoneVerifyIv;
    private EditText mPhoneNumberEt;
    private EditText mVerificationCodeEt;
    private EditText mSetPasswordEt;
    private Button mCommitBtn;
    private TextView mSelectCountryNameTv;
    private TextView mSelectCountryCodeTv;
    private TextView mRegisterTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(com.hooview.app.R.layout.fragment_register, container, false);
        initView();
        initBottomTips((TextView) mRootView.findViewById(com.hooview.app.R.id.login_bottom_tips_tv));
        Intent intent = getActivity().getIntent();
        mAuthType = intent.getStringExtra(Constants.EXTRA_KEY_ACCOUNT_TYPE);
        mPhoneNumber = intent.getStringExtra(EXTRA_KEY_PHONE_NUMBER);
        if (TextUtils.isEmpty(mAuthType)) {
            mAuthType = User.AUTH_TYPE_PHONE;
        }
        if (TextUtils.isEmpty(mPhoneNumber)) {
            mPhoneNumber = "";
        } else {
            mPhoneNumberEt.setText(mPhoneNumber);
        }

        mIsResetPasswordMode = Constants.ACTION_RESET_PASSWORD.equals(getActivity().getIntent().getAction());
        if (mIsResetPasswordMode) {
            setupResetPasswordView();
            mSmsType = ApiHelper.SMS_TYPE_RESET_PWD;
        } else {
            mSmsType = ApiHelper.SMS_TYPE_REGISTER;
        }
        if (!mAuthType.equals(User.AUTH_TYPE_PHONE)) {
            setupBindPhoneView();
        }
        return mRootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getIntent().putExtra(EXTRA_KEY_PHONE_NUMBER, mPhoneNumber);
    }

    private void initView() {
        mSelectCountryNameTv = (TextView) mRootView.findViewById(com.hooview.app.R.id.select_country_tv);
        mSelectCountryCodeTv = (TextView) mRootView.findViewById(com.hooview.app.R.id.select_code_txv);
        mRootView.findViewById(com.hooview.app.R.id.close_iv).setOnClickListener(this);
        mRegisterTv = (TextView) mRootView.findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mRootView.findViewById(com.hooview.app.R.id.country_code_rl).setOnClickListener(this);
        mPhoneNumberEt = (EditText) mRootView.findViewById(com.hooview.app.R.id.register_phone_et);
        mVerificationCodeEt = (EditText) mRootView.findViewById(com.hooview.app.R.id.verification_code_et);
        mSetPasswordEt = (EditText) mRootView.findViewById(com.hooview.app.R.id.set_password_et);
        mCommitBtn = (Button) mRootView.findViewById(com.hooview.app.R.id.commit_btn);
        mCommitBtn.setOnClickListener(this);
        mCommitBtn.setEnabled(false);
        mRegisterTv.setText(getString(R.string.register_account));
        mPhoneNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPhoneNumber = charSequence.toString();
                mStep = STEP_INPUT_PHONE_NUMBER;
                Logger.i(TAG, "Phone Number: " + mPhoneNumber);
                if (mPhoneNumber.length() == 0) {
                    mPhoneVerifyIv.setImageDrawable(null);
                } else if (mPhoneNumber.length() == 11
                        && ApiConstant.VALUE_COUNTRY_CODE.equals(ApiConstant.VALUE_COUNTRY_CODE_CHINA)) {
                    if (ValidateParam.validatePhone(mPhoneNumber)) {
                    } else {
                    }
                } else if (mPhoneNumber.length() > 0) {
                    mPhoneVerifyIv.setImageResource(com.hooview.app.R.drawable.login_icon_edit_clear);
                    mPhoneVerifyIv.setTag(EDIT_CLEAR_TAG);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mSetPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mVerificationCodeEt.getText().toString().length() > 0
                        && mPhoneNumberEt.getText().toString().length() > 0
                        && mSetPasswordEt.getText().toString().length() > 0) {
                    mCommitBtn.setEnabled(true);
                } else {
                    mCommitBtn.setEnabled(false);
                }
            }
        });
        mVerificationCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mVerificationCodeEt.getText().length() == 4) {
                    mVerificationCodeEt.clearFocus();
                    closeKeyboard(mVerificationCodeEt, getActivity());
                    verifySmsCode();
                    if (mPhoneNumberEt.getText().toString().length() > 0
                            && mSetPasswordEt.getText().toString().length() > 0) {
                        mCommitBtn.setEnabled(true);
                    } else {
                        mCommitBtn.setEnabled(false);
                    }
                }
            }
        });

        mPhoneNumberEt.requestFocus();
        mPhoneVerifyIv = (ImageView) mRootView.findViewById(com.hooview.app.R.id.clear_phone_number_iv);
        mPhoneVerifyIv.setOnClickListener(this);
        mTimeBtn = (TimeButton) mRootView.findViewById(com.hooview.app.R.id.register_btn);
        mTimeBtn.setOnClickListener(this);
    }

    private void initBottomTips(TextView v) {
        String content = getString(com.hooview.app.R.string.msg_login_agreement);
        SpannableString info = new SpannableString(content);
        info.setSpan(new ForegroundColorSpan(getResources().getColor(com.hooview.app.R.color.login_btn_color)),
                content.length() - 4, content.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        info.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent serviceIntent = new Intent(getActivity(), TextActivity.class);
                        serviceIntent.putExtra(TextActivity.EXTRA_TYPE, TextActivity.TYPE_AGREEMENT);
                        serviceIntent
                                .putExtra(Constants.EXTRA_KEY_TITLE, getString(com.hooview.app.R.string.msg_login_user_agreement));
                        startActivity(serviceIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(com.hooview.app.R.color.hv662d80));
                        ds.setUnderlineText(false);
                    }
                }, content.length()-4, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setHighlightColor(getResources().getColor(android.R.color.transparent));
        v.setMovementMethod(LinkMovementMethod.getInstance());
        v.setText(info);
    }
    @Override
    public void onStart() {
        Utils.updateCountryCode(getActivity(), mSelectCountryNameTv, mSelectCountryCodeTv);
        super.onStart();
    }

    private void setupResetPasswordView() {
        mRegisterTv.setText(getString(com.hooview.app.R.string.recovery_title));
        mSetPasswordEt.setHint(getString(R.string.set_pw_6to12));
    }

    private void setupBindPhoneView() {
        ((TextView) mRootView.findViewById(com.hooview.app.R.id.common_title_tv)).setText(com.hooview.app.R.string.title_bind_phone);
    }

    @Override
    public void onClick(View view) {
        int editTag = view.getTag() == null ? -1 : (Integer.parseInt(view.getTag().toString()));
        switch (view.getId()) {
            case com.hooview.app.R.id.close_iv:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN_HOME));
                break;
            case com.hooview.app.R.id.register_btn:
                checkPhoneNumberAndSendSms();
                break;
            case com.hooview.app.R.id.clear_phone_number_iv:
                if (editTag == EDIT_CLEAR_TAG) {
                    mPhoneNumberEt.setText("");
                }
                break;
            case com.hooview.app.R.id.country_code_rl:
                Intent countryIntent = new Intent(getActivity(), CountryCodeListActivity.class);
                startActivity(countryIntent);
                break;
            case com.hooview.app.R.id.commit_btn:
                verifyPassWordAndCommit();
                break;
        }
    }

    private void checkPhoneNumberAndSendSms() {
        if (TextUtils.isEmpty(mPhoneNumber)) {
            SingleToast.show(getActivity(), getString(com.hooview.app.R.string.msg_phone_number_empty));
            return;
        }
        if (!ValidateParam.validatePhone(mPhoneNumber)) {
            SingleToast.show(getActivity(), getString(com.hooview.app.R.string.msg_phone_number_invalid));
            return;
        }
        mStep = STEP_REQUEST_SMS_CODE;
        showLoadingDialog("", false, true);
        ApiHelper.getInstance().smsSend(mPhoneNumber, mSmsType, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    mSmsId = JsonParserUtil.getString(result, ApiConstant.KEY_SMS_ID);
                    if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                            .getInt(result, ApiConstant.KEY_REGISTERED)) {
                        if (mIsResetPasswordMode) {
                            mTimeBtn.startTime();
                        } else {
                            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_phone_registered);
                        }
                    } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                            .getInt(result, ApiConstant.KEY_REGISTERED)) {
                        if (mIsResetPasswordMode) {
                            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_phone_not_registered);
                        } else {
                            mTimeBtn.startTime();
                        }
                    }
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                dismissLoadingDialog();
                if (errorInfo.equals(ApiConstant.E_SMS_INTERVAL)) {
                    SingleToast.show(getActivity(), com.hooview.app.R.string.msg_get_sms_duration_too_short);
                } else if (errorInfo.equals(ApiConstant.E_SMS_SERVICE)) {
                    SingleToast.show(getActivity(), com.hooview.app.R.string.msg_server_exception_retry);
                } else if (errorInfo.equals(ApiConstant.E_USER_EXISTS)) {
                    if (!mIsResetPasswordMode) {
                        SingleToast.show(getActivity(), com.hooview.app.R.string.msg_phone_registered);
                    }
                } else if (errorInfo.equals(ApiConstant.E_USER_NOT_EXISTS)) {
                    if (mIsResetPasswordMode) {
                        SingleToast.show(getActivity(), com.hooview.app.R.string.msg_phone_not_registered);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                dismissLoadingDialog();

                mTimeBtn.clearTimer();
                mTimeBtn.setText(com.hooview.app.R.string.get_verification);
            }
        });
    }

    public static void closeKeyboard(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void verifySmsCode() {
        if (!NetworkUtil.isNetworkAvailable(getActivity(), true)) {
            return;
        }
        closeKeyboard(mVerificationCodeEt, getActivity());
        String smsCode = mVerificationCodeEt.getText().toString();
        if (smsCode.length() == 0) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_verify_code_empty);
            return;
        } else if (smsCode.length() > 0 && smsCode.length() < 4) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_verify_code_invalid);
            return;
        }
        if (TextUtils.isEmpty(mSmsId)) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_verify_failed);
            return;
        }
        ApiHelper.getInstance().smsVerify(mSmsId, smsCode, mAuthType,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!isAdded()) {
                            return;
                        }
                        if (!TextUtils.isEmpty(result)) {
                            if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED)) {
                                if (mIsResetPasswordMode) {
                                    mStep = STEP_VERIFIED_SMS_CODE;
                                    mSetPasswordEt.requestFocus();
                                } else {
                                    SingleToast.show(getActivity(), com.hooview.app.R.string.msg_phone_registered);
                                }
                            } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED)) {
                                if (mIsResetPasswordMode) {
                                    SingleToast.show(getContext(), com.hooview.app.R.string.msg_phone_not_registered);
                                } else {
                                    mStep = STEP_VERIFIED_SMS_CODE;
                                    mSetPasswordEt.requestFocus();
                                }
                            }
                            if (ApiConstant.VALUE_SNS_HAVE_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                                SingleToast.show(getActivity(), com.hooview.app.R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_SNS_NOT_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                            }
                        } else {
                            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_verify_failed);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getActivity(), com.hooview.app.R.string.msg_verify_failed);
                        mVerificationCodeEt.setText("");
                        mVerificationCodeEt.requestFocus();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    public void verifyPassWordAndCommit() {
        String password = mSetPasswordEt.getText().toString().trim();
        int type = ValidateParam.validateUserPasswords(password);
        if (TextUtils.isEmpty(mVerificationCodeEt.getText().toString())) {
            SingleToast.show(getContext(), com.hooview.app.R.string.prompt_verification_input);
            return;
        }
        if (mStep != STEP_VERIFIED_SMS_CODE) {
            SingleToast.show(getContext(), com.hooview.app.R.string.msg_phone_need_verify);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_empty);
            return;
        } else if (1 == type) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_space);
            return;
        } else if (2 == type) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_chinese);
            return;
        } else if (3 == type) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_asterisk);
            return;
        } else if (password.length() < 6) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_length);
            return;
        }

        mStep = STEP_COMMIT_USER_INFO;
        if (mIsResetPasswordMode) {
            ApiHelper.getInstance().resetPassword(mPhoneNumber, password,
                    new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_reset_password_success);
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN));
                                mPhoneNumberEt.getText().clear();
                                mVerificationCodeEt.getText().clear();
                                mTimeBtn.clearTimer();
                                mSetPasswordEt.getText().clear();
                            }
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                        }

                        @Override
                        public void onFailure(String msg) {
                            RequestUtil.handleRequestFailed(msg);
                            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_reset_password_failed);
                        }
                    });
        } else {
            if (User.AUTH_TYPE_PHONE.equals(mAuthType)) {
                Bundle bundle = new Bundle();
                bundle.putString("authtype", User.AUTH_TYPE_PHONE);
                bundle.putString("token", ApiConstant.VALUE_COUNTRY_CODE + mPhoneNumber);
                if (!password.isEmpty()) {
                    try {
                        bundle.putString("password", Utils.getMD5(password));
                    } catch (NoSuchAlgorithmException e) {
                        Logger.e(TAG, "getMD5 string failed !", e);
                    }
                }
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_IS_REGISTER, true);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

}
