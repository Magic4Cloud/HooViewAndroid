package com.easyvaas.elapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.event.RegisterSuccessEvent;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.NetworkUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ValidateParam;

import org.greenrobot.eventbus.EventBus;

import java.security.NoSuchAlgorithmException;

public class RegisterDialog extends BaseUserDialog implements View.OnClickListener {
    private static final String TAG = "RegisterDialog";
    private ViewFlipper mViewFlipper;
    private View mPhoneView;
    private View mVerCodeView;
    private View mSetPwdView;
    private LinearLayout mLlProtocol;
    private EditText mEtPhone, mEtPwd, mEtCode_1, mEtCode_2, mEtCode_3, mEtCode_4;
    private TextView mTvLogin, mTvSetPwdPrompt, mTvSendSmsAgain;
    private String mSmsCode;
    private String mSmsId;
    private String mPhoneNumber;
    private static final int STEP_PHONE = 0;
    private static final int STEP_VERIFY_CODE = 1;
    private static final int STEP_SET_PWD = 2;
    private int mStep = STEP_PHONE;
    private String mAuthType = User.AUTH_TYPE_PHONE;
    private LinearLayout mLLLoginPrompt;
    private boolean isFindPwd;
    private CheckBox cb_agree;
    protected static final int MSG_COUNTDOWN = 105;
    private int count = 50;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_COUNTDOWN) {
                if (count > 0) {
                    mTvSendSmsAgain.setText(getContext().getApplicationContext().getResources().getString(R.string.send_sms_agin_countdown, count));
                    count--;
                    mTvSendSmsAgain.setEnabled(false);
                    mHandler.sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
                } else {
                    count = 50;
                    mTvSendSmsAgain.setEnabled(true);
                    mTvSendSmsAgain.setText(getContext().getApplicationContext().getResources().getString(R.string.send_sms_agin));
                }

            }
        }
    };

    public static void showRegisterDialog(Context context, boolean isFindPwd) {
        RegisterDialog registerDialog = new RegisterDialog(context, isFindPwd);
        if (!(context instanceof Activity)) {
            registerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        registerDialog.show();
    }

    public RegisterDialog(Context context, boolean isFindPwd) {
        super(context);
        this.isFindPwd = isFindPwd;
    }

    private RegisterDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.dialog_register);
        initView();
    }

    private void initView() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mPhoneView = View.inflate(getContext(), R.layout.view_register_phone, null);
        mPhoneView.findViewById(R.id.tv_user_protocol).setOnClickListener(this);
        mEtPhone = (EditText) mPhoneView.findViewById(R.id.et_phone);
        cb_agree = (CheckBox) mPhoneView.findViewById(R.id.cb_agree);
        mPhoneView.findViewById(R.id.btn_next_step_phone).setOnClickListener(this);
        mTvLogin = (TextView) mPhoneView.findViewById(R.id.tv_login);
        mLlProtocol = (LinearLayout) mPhoneView.findViewById(R.id.ll_protocol);
        mTvLogin.setOnClickListener(this);
        mPhoneView.findViewById(R.id.iv_close).setOnClickListener(this);
        mLLLoginPrompt = (LinearLayout) mPhoneView.findViewById(R.id.ll_login_prompt);

        mVerCodeView = View.inflate(getContext(), R.layout.view_register_verify_code, null);
        mTvSendSmsAgain = (TextView) mVerCodeView.findViewById(R.id.tv_send_code_again);
        mTvSendSmsAgain.setOnClickListener(this);
        mEtCode_1 = (EditText) mVerCodeView.findViewById(R.id.code_1_tv);
        mEtCode_2 = (EditText) mVerCodeView.findViewById(R.id.code_2_tv);
        mEtCode_3 = (EditText) mVerCodeView.findViewById(R.id.code_3_tv);
        mEtCode_4 = (EditText) mVerCodeView.findViewById(R.id.code_4_tv);
        mEtCode_1.setOnKeyListener(mKeyListener);
        mEtCode_2.setOnKeyListener(mKeyListener);
        mEtCode_3.setOnKeyListener(mKeyListener);
        mEtCode_4.setOnKeyListener(mKeyListener);
        mEtCode_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCode_1.getText().length() == 1) {
                    mEtCode_2.requestFocus();
                    openKeyboard(mEtCode_2, getContext());
                }
            }
        });
        mEtCode_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCode_2.getText().length() == 1) {
                    mEtCode_3.requestFocus();
                    openKeyboard(mEtCode_3, getContext());
                }
            }
        });
        mEtCode_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCode_3.getText().length() == 1) {
                    mEtCode_4.requestFocus();
                    openKeyboard(mEtCode_4, getContext());
                }
            }
        });
        mEtCode_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEtCode_4.getText().length() == 1) {
                    mEtCode_4.clearFocus();
                    closeKeyboard(mEtCode_4, getContext());
                }
            }
        });
        mVerCodeView.findViewById(R.id.btn_next_step_ver_code).setOnClickListener(this);
        mVerCodeView.findViewById(R.id.iv_code_back).setOnClickListener(this);


        mSetPwdView = View.inflate(getContext(), R.layout.view_register_set_pwd, null);
        mTvSetPwdPrompt = (TextView) mSetPwdView.findViewById(R.id.tv_set_password_prompt);
        mEtPwd = (EditText) mSetPwdView.findViewById(R.id.et_password);
        mSetPwdView.findViewById(R.id.btn_finish).setOnClickListener(this);
        mSetPwdView.findViewById(R.id.iv_pwd_back).setOnClickListener(this);
        mSetPwdView.findViewById(R.id.iv_pwd_visible).setOnClickListener(this);
        CheckBox ivPwdVisible = (CheckBox) mSetPwdView.findViewById(R.id.iv_pwd_visible);
        ivPwdVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEtPwd.setSelection(mEtPwd.getText().length());
                } else {
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEtPwd.setSelection(mEtPwd.getText().length());
                }
            }
        });
        mViewFlipper.addView(mPhoneView);
        mViewFlipper.addView(mVerCodeView);
        mViewFlipper.addView(mSetPwdView);
        if (isFindPwd) {
            mTvSetPwdPrompt.setText(R.string.reset_pwd_prompt);
        } else {
            mTvSetPwdPrompt.setText(R.string.hint_password);
        }
        if(isFindPwd){
            mLlProtocol.setVisibility(View.GONE);
        }else{
            mLlProtocol.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                verifyPassWordAndCommit();
                break;
            case R.id.tv_login:
                dismiss();
                LoginActivity.start(getContext());
                break;
            case R.id.btn_next_step_ver_code:
                verifySmsCode();
                break;
            case R.id.btn_next_step_phone:
                if (cb_agree.isChecked()) {
                    checkPhoneNumberAndSendSms(false);
                } else {
                    SingleToast.show(getContext().getApplicationContext(), getContext().getString(R.string.protocol_prompt));
                }
                break;
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_pwd_back:
                showPrePage();
                break;
            case R.id.iv_code_back:
                showPrePage();
                break;
            case R.id.tv_user_protocol:
                ProtocolDialog.showProtocol(getContext());
                break;
            case R.id.tv_send_code_again:
                if (cb_agree.isChecked()) {
                    checkPhoneNumberAndSendSms(true);
                } else {
                    SingleToast.show(getContext().getApplicationContext(), getContext().getString(R.string.protocol_prompt));
                }
                break;
        }
    }

    View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && KeyEvent.ACTION_DOWN == event.getAction()) {
                delCode();
            }
            return false;
        }
    };


    private void delCode() {
        mSmsCode = mEtCode_1.getText().toString() + mEtCode_2.getText().toString()
                + mEtCode_3.getText().toString() + mEtCode_4.getText().toString();
        switch (mSmsCode.length()) {
            case 0:
                clearVerificationCodes();
                mEtCode_1.requestFocus();
                break;
            case 1:
                clearVerificationCodes();
                mEtCode_1.requestFocus();
                break;
            case 2:
                mEtCode_1.setText(mSmsCode);
                mEtCode_2.setText("");
                mEtCode_3.setText("");
                mEtCode_4.setText("");
                mEtCode_2.requestFocus();
                break;
            case 3:
                mEtCode_1.setText(mSmsCode.substring(0, 1));
                mEtCode_2.setText(mSmsCode.substring(1, 2));
                mEtCode_3.setText("");
                mEtCode_4.setText("");
                mEtCode_3.requestFocus();
                break;
            case 4:
                mEtCode_1.setText(mSmsCode.substring(0, 1));
                mEtCode_2.setText(mSmsCode.substring(1, 2));
                mEtCode_3.setText(mSmsCode.substring(2, 3));
                mEtCode_4.setText("");
                mEtCode_4.requestFocus();
                break;
            default:
                break;
        }
    }

    private void clearVerificationCodes() {
        mEtCode_1.setText("");
        mEtCode_2.setText("");
        mEtCode_3.setText("");
        mEtCode_4.setText("");
    }

    private void showNextPage() {
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pannel_right_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pannel_left_out));
        mViewFlipper.showNext();
    }

    private void showPrePage() {
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pannel_left_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.pannel_right_out));
        mViewFlipper.showPrevious();
    }

    public static void openKeyboard(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void closeKeyboard(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void checkPhoneNumberAndSendSms(final boolean isSendSmsAgain) {
        mPhoneNumber = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(mPhoneNumber)) {
            SingleToast.show(getContext(), R.string.msg_phone_number_empty);
            return;
        }
        if (!ValidateParam.validatePhone(mPhoneNumber)) {
            SingleToast.show(getContext(), R.string.msg_phone_number_invalid);
            return;
        }
        mHandler.sendEmptyMessageDelayed(MSG_COUNTDOWN, 1000);
        ApiHelper.getInstance().smsSend(mPhoneNumber, isFindPwd ? ApiHelper.SMS_TYPE_RESET_PWD : ApiHelper.SMS_TYPE_REGISTER, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    mSmsId = JsonParserUtil.getString(result, ApiConstant.KEY_SMS_ID);
                    if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                            .getInt(result, ApiConstant.KEY_REGISTERED) && !isFindPwd) {
                        SingleToast.show(getContext(), R.string.msg_phone_registered);
                        mTvLogin.setVisibility(View.VISIBLE);
                        mLLLoginPrompt.setVisibility(View.VISIBLE);
                        hideInputMethod();
                    } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                            .getInt(result, ApiConstant.KEY_REGISTERED) || isFindPwd) {
                        mTvLogin.setVisibility(View.INVISIBLE);
                        mStep = STEP_VERIFY_CODE;
                        mLLLoginPrompt.setVisibility(View.GONE);
                        if (!isSendSmsAgain) {
                            showNextPage();
                        }
                    }
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (errorInfo.equals(ApiConstant.E_SMS_INTERVAL)) {
                    SingleToast.show(getContext(), R.string.msg_get_sms_duration_too_short);
                } else if (errorInfo.equals(ApiConstant.E_SMS_SERVICE)) {
                    SingleToast.show(getContext(), R.string.msg_server_exception_retry);
                } else if (errorInfo.equals(ApiConstant.E_USER_EXISTS)) {
                    SingleToast.show(getContext(), R.string.msg_phone_registered);
                    mTvLogin.setVisibility(View.VISIBLE);
                } else if (errorInfo.equals(ApiConstant.E_USER_NOT_EXISTS)) {
                    SingleToast.show(getContext(), R.string.msg_phone_unregistered);
                }
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    private void verifySmsCode() {
        if (!NetworkUtil.isNetworkAvailable(getContext(), true)) {
            return;
        }
        mSmsCode = mEtCode_1.getText().toString() + mEtCode_2.getText().toString()
                + mEtCode_3.getText().toString() + mEtCode_4.getText().toString();
        if (mSmsCode.length() == 0) {
            SingleToast.show(getContext(), R.string.msg_verify_code_empty);
            return;
        } else if (mSmsCode.length() > 0 && mSmsCode.length() < 4) {
            SingleToast.show(getContext(), R.string.msg_verify_code_invalid);
            return;
        }
        if (TextUtils.isEmpty(mSmsId)) {
            SingleToast.show(getContext(), R.string.msg_verify_failed);
            return;
        }

        ApiHelper.getInstance().smsVerify(mSmsId, mSmsCode, mAuthType,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (mViewFlipper.getCurrentView() != mVerCodeView) {
                            return;
                        }
                        if (!TextUtils.isEmpty(result)) {
                            if (ApiConstant.VALUE_PHONE_HAVE_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED) && !isFindPwd) {
                                SingleToast.show(getContext(), R.string.msg_phone_registered);
                                hideInputMethod();
                                mTvLogin.setVisibility(View.VISIBLE);
                                mLLLoginPrompt.setVisibility(View.VISIBLE);
                            } else if (ApiConstant.VALUE_PHONE_NOT_REGISTERED == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_REGISTERED) || isFindPwd) {
                                mStep = STEP_SET_PWD;
                                showNextPage();
                            }
                            if (ApiConstant.VALUE_SNS_HAVE_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                                SingleToast.show(getContext(), R.string.msg_sns_account_have_bind);
                            } else if (ApiConstant.VALUE_SNS_NOT_BIND == JsonParserUtil
                                    .getInt(result, ApiConstant.KEY_CONFLICTED)) {
                            }
                        } else {
                            SingleToast.show(getContext(), R.string.msg_verify_failed);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getContext(), R.string.msg_verify_failed);
                        clearVerificationCodes();
                        mEtCode_1.requestFocus();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                    }
                });
    }

    public void verifyPassWordAndCommit() {
        String password = mEtPwd.getText().toString().trim();
        int type = ValidateParam.validateUserPasswords(password);
        if (TextUtils.isEmpty(password)) {
            SingleToast.show(getContext(), R.string.msg_password_empty);
            return;
        } else if (1 == type) {
            SingleToast.show(getContext(), R.string.msg_password_space);
            return;
        } else if (2 == type) {
            SingleToast.show(getContext(), R.string.msg_password_chinese);
            return;
        } else if (3 == type) {
            SingleToast.show(getContext(), R.string.msg_password_asterisk);
            return;
        } else if (password.length() < 6) {
            SingleToast.show(getContext(), R.string.msg_password_length);
            return;
        }
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
            if (isFindPwd) {
                resetPwd(mPhoneNumber, password);
            } else {
                registerByPhone(mPhoneNumber, password);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewFlipper.getCurrentView() == mPhoneView) {
            dismiss();
        } else if (mViewFlipper.getCurrentView() == mVerCodeView) {
            mViewFlipper.showPrevious();
        } else if (mViewFlipper.getCurrentView() == mSetPwdView) {
            mViewFlipper.showPrevious();
        }
    }


    private void registerByPhone(String phone, String password) {
        String nickname = getContext().getResources().getString(R.string.default_nickname, phone.substring(phone.length() - 4));
        String gender = "male";
        if (phone.length() == 11) {
            phone = ApiConstant.VALUE_COUNTRY_CODE_CHINA + phone;
        }
        ApiHelper.getInstance().registerByPhone(nickname, phone, gender, password, "1990-01-01",
                getContext().getString(R.string.default_location), getContext().getString(R.string.default_singture), mAuthType, new MyRequestCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        SingleToast.show(getContext(), R.string.msg_registered_success);
                        UserUtil.handleAfterLogin(getContext(), user, "RegisterByPhone");
                        EventBus.getDefault().post(new RegisterSuccessEvent());
                        dismiss();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getContext(), R.string.msg_registered_error);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getContext(), R.string.msg_registered_error);
                    }
                });
    }

    public void resetPwd(String phone, String password) {
        ApiHelper.getInstance().resetPassword(mPhoneNumber, password,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        SingleToast.show(getContext(), R.string.msg_reset_password_success);
                        dismiss();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        SingleToast.show(getContext(), R.string.msg_reset_password_failed);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getContext(), R.string.msg_reset_password_failed);
                    }
                });
    }
}
