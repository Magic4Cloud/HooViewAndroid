/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.activity.HooViewHomeActivity;
import com.hooview.app.activity.setting.CountryCodeListActivity;
import com.hooview.app.base.BaseFragment;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.StringUtil;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ValidateParam;

public class UserLoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserLoginFragment";

    private EditText mUserNameEt;
    private EditText mPasswordEt;
    private TextView mCountryNameTv;
    private TextView mCountryCodeTv;

    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.hooview.app.R.layout.fragment_login, container, false);
        sp = getActivity().getSharedPreferences("config", Context.MODE_APPEND);

        final ImageView clearPhoneNumberIv = (ImageView) view.findViewById(com.hooview.app.R.id.clear_phone_number_iv);
        final ImageView clearPasswordIv = (ImageView) view.findViewById(com.hooview.app.R.id.clear_password_iv);
        TextView contentTv = (TextView) view.findViewById(com.hooview.app.R.id.common_custom_title_tv);
        view.findViewById(com.hooview.app.R.id.close_iv).setOnClickListener(this);
        view.findViewById(com.hooview.app.R.id.login_btn).setOnClickListener(this);
        view.findViewById(com.hooview.app.R.id.forget_password_tv).setOnClickListener(this);
        clearPhoneNumberIv.setOnClickListener(this);
        clearPasswordIv.setOnClickListener(this);
        mCountryNameTv = (TextView) view.findViewById(com.hooview.app.R.id.select_country_tv);
        mCountryCodeTv = (TextView) view.findViewById(com.hooview.app.R.id.select_code_txv);
        view.findViewById(com.hooview.app.R.id.country_code_rl).setOnClickListener(this);
        mUserNameEt = (EditText) view.findViewById(com.hooview.app.R.id.register_phone_et);
        mPasswordEt = (EditText) view.findViewById(com.hooview.app.R.id.password_et);
        contentTv.setText(getString(com.hooview.app.R.string.login));
        if (!mUserNameEt.getText().toString().isEmpty()) {
            clearPhoneNumberIv.setImageResource(com.hooview.app.R.drawable.login_icon_edit_clear);
            clearPhoneNumberIv.setVisibility(View.VISIBLE);
        }
        String fullPhone = Preferences.getInstance(getActivity())
                .getString(Preferences.KEY_LOGIN_PHONE_NUMBER, "");
        String[] numbers = StringUtil.parseFullPhoneNumber(fullPhone);
        if (numbers.length == 2) {
            mCountryCodeTv.setText("+" + numbers[0]);
            mUserNameEt.setText(numbers[1]);
        }

        mUserNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_NEXT == actionId) {
                    mPasswordEt.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_GO == actionId) {
                    login();
                    return true;
                }
                return false;
            }
        });
        mUserNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    clearPhoneNumberIv.setVisibility(View.VISIBLE);
                } else if (charSequence.length() == 0) {
                    clearPhoneNumberIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    clearPasswordIv.setVisibility(View.VISIBLE);
                } else {
                    clearPasswordIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mUserNameEt.requestFocus();
        if (mUserNameEt.getText().length() > 0) {
            clearPhoneNumberIv.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onStart() {
        Utils.updateCountryCode(getActivity(), mCountryNameTv, mCountryCodeTv);
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.hooview.app.R.id.close_iv:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN_HOME));
                break;
            case com.hooview.app.R.id.login_btn:
                login();
                break;
            case com.hooview.app.R.id.forget_password_tv:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_RESET_PWD));
                break;
            case com.hooview.app.R.id.clear_phone_number_iv:
                mUserNameEt.setText("");
                mUserNameEt.requestFocus();
                break;
            case com.hooview.app.R.id.clear_password_iv:
                mPasswordEt.setText("");
                mPasswordEt.requestFocus();
                break;
            case com.hooview.app.R.id.country_code_rl:
                Intent countryIntent = new Intent(getActivity(), CountryCodeListActivity.class);
                startActivity(countryIntent);
                break;
        }
    }

    private void login() {
        String phone = mUserNameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            SingleToast.show(getActivity(), getString(com.hooview.app.R.string.msg_phone_number_empty));
            return;
        } else if (!ValidateParam.validatePhone(phone)) {
            SingleToast.show(getActivity(), getString(com.hooview.app.R.string.msg_phone_number_invalid));
            return;
        } else if (TextUtils.isEmpty(password)) {
            SingleToast.show(getActivity(), com.hooview.app.R.string.msg_password_empty);
            return;
        }
        showLoadingDialog(com.hooview.app.R.string.loading_data, false, true);
        loginByPhone(phone, password);
    }

    /**
     * 请求数据
     *
     * @param phone    电话号码
     * @param password 密码
     */
    private void loginByPhone(String phone, String password) {
        ApiHelper.getInstance().loginByPhone(phone, password, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                dismissLoadingDialog();

                //TODO 替换之前的HomeTabActivity的入口
                if (getActivity() != null) {
                    sp.edit().putBoolean("live", user.getJurisdiction() == 0 ? false : true).commit();
                    startActivity(new Intent(getActivity(), HooViewHomeActivity.class));
                    getActivity().finish();
                    UserUtil.handleAfterLogin(getActivity(), user, "LoginByPhone");
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (getActivity() == null) {
                    return;
                }
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
                SingleToast.show(getActivity(), com.hooview.app.R.string.msg_login_failed);
                dismissLoadingDialog();
            }
        });
    }
}
