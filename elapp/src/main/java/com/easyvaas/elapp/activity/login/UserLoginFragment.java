/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.login;

import android.content.Intent;
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

import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.activity.setting.CountryCodeListActivity;
import com.easyvaas.elapp.base.BaseFragment;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ValidateParam;

public class UserLoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserLoginFragment";

    private EditText mUserNameEt;
    private EditText mPasswordEt;
    private TextView mCountryNameTv;
    private TextView mCountryCodeTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final ImageView clearPhoneNumberIv = (ImageView) view.findViewById(R.id.clear_phone_number_iv);
        final ImageView clearPasswordIv = (ImageView) view.findViewById(R.id.clear_password_iv);
        TextView contentTv = (TextView) view.findViewById(R.id.common_custom_title_tv);
        view.findViewById(R.id.close_iv).setOnClickListener(this);
        view.findViewById(R.id.login_btn).setOnClickListener(this);
        view.findViewById(R.id.forget_password_tv).setOnClickListener(this);
        clearPhoneNumberIv.setOnClickListener(this);
        clearPasswordIv.setOnClickListener(this);
        mCountryNameTv = (TextView) view.findViewById(R.id.select_country_tv);
        mCountryCodeTv = (TextView) view.findViewById(R.id.select_code_txv);
        view.findViewById(R.id.country_code_rl).setOnClickListener(this);
        mUserNameEt = (EditText) view.findViewById(R.id.register_phone_et);
        mPasswordEt = (EditText) view.findViewById(R.id.password_et);
        contentTv.setText(getString(R.string.login));
        if (!mUserNameEt.getText().toString().isEmpty()) {
            clearPhoneNumberIv.setImageResource(R.drawable.login_icon_edit_clear);
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
            case R.id.close_iv:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN_HOME));
                break;
            case R.id.login_btn:
                login();
                break;
            case R.id.forget_password_tv:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_RESET_PWD));
                break;
            case R.id.clear_phone_number_iv:
                mUserNameEt.setText("");
                mUserNameEt.requestFocus();
                break;
            case R.id.clear_password_iv:
                mPasswordEt.setText("");
                mPasswordEt.requestFocus();
                break;
            case R.id.country_code_rl:
                Intent countryIntent = new Intent(getActivity(), CountryCodeListActivity.class);
                startActivity(countryIntent);
                break;
        }
    }

    private void login() {
        String phone = mUserNameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            SingleToast.show(getActivity(), getString(R.string.msg_phone_number_empty));
            return;
        } else if (!ValidateParam.validatePhone(phone)) {
            SingleToast.show(getActivity(), getString(R.string.msg_phone_number_invalid));
            return;
        } else if (TextUtils.isEmpty(password)) {
            SingleToast.show(getActivity(), R.string.msg_password_empty);
            return;
        }
        showLoadingDialog(R.string.loading_data, false, true);
        loginByPhone(phone, password);
    }

    private void loginByPhone(String phone, String password) {
        ApiHelper.getInstance().loginByPhone(phone, password, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                dismissLoadingDialog();
                if (getActivity() != null) {
                    startActivity(new Intent(getActivity(), HomeTabActivity.class));
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
                SingleToast.show(getActivity(), R.string.msg_login_failed);
                dismissLoadingDialog();
            }
        });
    }
}
