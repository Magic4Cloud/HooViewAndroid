/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.qq.QQLoginManager;
import com.easyvaas.common.sharelogin.wechat.WechatLoginManager;
import com.easyvaas.common.sharelogin.weibo.WeiboLoginManager;

import com.hooview.app.BuildConfig;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.activity.user.UserInfoSnsActivity;
import com.easyvaas.elapp.base.BaseFragment;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;

public class UserLoginHomeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserLoginHomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_logins, container, false);

        view.findViewById(R.id.login_qq_iv).setOnClickListener(this);
        view.findViewById(R.id.login_weibo_iv).setOnClickListener(this);
        view.findViewById(R.id.login_weixin_iv).setOnClickListener(this);
        view.findViewById(R.id.go_login_btn).setOnClickListener(this);
        view.findViewById(R.id.go_register_btn).setOnClickListener(this);
        view.findViewById(R.id.guest_login_tv).setOnClickListener(this);
        initBottomTips((TextView) view.findViewById(R.id.login_bottom_tips_tv));


        if (BuildConfig.FLAVOR.equals("dev")) {
            final String[] servers = getResources().getStringArray(R.array.server_array);
            Spinner mServerSpinner = (Spinner) view.findViewById(R.id.server_choice_sp);
            mServerSpinner.setVisibility(View.VISIBLE);
            mServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String server = servers[position];
                    Preferences.getInstance(getActivity()).putString(Preferences.KEY_SERVER_TYPE, server);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            String server = Preferences.getInstance(getActivity()).getString(Preferences.KEY_SERVER_TYPE);
            if (!TextUtils.isEmpty(server)) {
                for (int i = 0, n = servers.length; i < n; i++) {
                    if (server.equals(servers[i])) {
                        mServerSpinner.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            Preferences.getInstance(getActivity()).remove(Preferences.KEY_SERVER_TYPE);
        }

        return view;
    }

    private void initBottomTips(TextView v) {
        String content = getString(R.string.msg_login_agreement);
        SpannableString info = new SpannableString(content);
        info.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.login_btn_color)),
                content.length() - 4, content.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        info.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent serviceIntent = new Intent(getActivity(), TextActivity.class);
                        serviceIntent.putExtra(TextActivity.EXTRA_TYPE, TextActivity.TYPE_AGREEMENT);
                        serviceIntent
                                .putExtra(Constants.EXTRA_KEY_TITLE, getString(R.string.msg_login_user_agreement));
                        startActivity(serviceIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.login_btn_color));
                        ds.setUnderlineText(false);
                    }
                }, content.length()-4, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setHighlightColor(getResources().getColor(android.R.color.transparent));
        v.setMovementMethod(LinkMovementMethod.getInstance());
        v.setText(info);
    }

    @Override
        public void onResume() {
        super.onResume();
        dismissLoadingDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        ILoginManager loginManager;
        Intent intent = null;
        switch (v.getId()) {
            case R.id.go_register_btn:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_REGISTER));
                break;
            case R.id.guest_login_tv:
                intent = new Intent(getActivity(), HomeTabActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_TAB_ID, HomeTabActivity.TAB_ID_DISCOVER);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.go_login_btn:
                getActivity().sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN));
                break;
            case R.id.login_weibo_iv:
                showLoadingDialog(R.string.loading_data, false, true);
                getActivity().getIntent().putExtra("is_weibo_login", true);
                loginManager = new WeiboLoginManager(getActivity());
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by Weibo complete");
                        loginBySNSAccount(User.AUTH_TYPE_SINA, userInfo);
                    }

                    @Override
                    public void onError() {
                        if (!isAdded()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SingleToast.show(getActivity(),
                                getString(R.string.msg_sns_auth_failed, getString(R.string.weibo)));
                    }

                    @Override
                    public void onCancel() {
                        dismissLoadingDialog();
                    }
                });
                break;
            case R.id.login_weixin_iv:
                showLoadingDialog(R.string.loading_data, false, true);
                loginManager = new WechatLoginManager(getActivity());
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by Weixin complete");
                        loginBySNSAccount(User.AUTH_TYPE_WEIXIN, userInfo);
                    }

                    @Override
                    public void onError() {
                        if (!isAdded()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SingleToast.show(getActivity(),
                                getString(R.string.msg_sns_auth_failed, getString(R.string.weixin)));
                    }

                    @Override
                    public void onCancel() {
                        dismissLoadingDialog();
                    }
                });
                break;
            case R.id.login_qq_iv:
                showLoadingDialog(R.string.loading_data, false, true);
                loginManager = new QQLoginManager(getActivity());
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by QQ complete");
                        loginBySNSAccount(User.AUTH_TYPE_QQ, userInfo);
                    }

                    @Override
                    public void onError() {
                        if (!isAdded()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SingleToast.show(getActivity(),
                                getString(R.string.msg_sns_auth_failed, getString(R.string.qq)));
                    }

                    @Override
                    public void onCancel() {
                        dismissLoadingDialog();
                    }
                });
                break;
        }
    }

    private void loginBySNSAccount(final String authType, final Bundle userInfo) {
        String nickname = userInfo.getString(ShareConstants.PARAMS_NICK_NAME);
        String logoUrl = userInfo.getString(ShareConstants.PARAMS_IMAGEURL);
        String gender = userInfo.getString(ShareConstants.PARAMS_SEX);
        String openid = userInfo.getString(ShareConstants.PARAMS_OPENID);
        String unionid = userInfo.getString(ShareConstants.PARAMS_UNIONID);
        String refreshToken = userInfo.getString(ShareConstants.PARAMS_REFRESH_TOKEN);
        String accessToken = userInfo.getString(ShareConstants.PARAMS_ACCESS_TOKEN);
        long expiresIn = userInfo.getLong(ShareConstants.PARAMS_EXPIRES_IN);

        // getActivity() maybe null sometime
        ApiHelper.getInstance().loginBySNSAccount(authType, nickname, logoUrl, gender,
                openid, unionid, accessToken, refreshToken, expiresIn, new MyRequestCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        dismissLoadingDialog();
                        if (getActivity() != null) {
                            startActivity(new Intent(getActivity(), HomeTabActivity.class));
                            getActivity().finish();
                            UserUtil.handleAfterLogin(getContext(), user, "LoginByAuth");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        if(!isAdded()) {
                            return;
                        }
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getActivity(), getString(R.string.request_failed));
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(String msg) {
                        if (getActivity() == null || !isAdded()) {
                            return;
                        }
                        dismissLoadingDialog();
                        if (msg.equals(ApiConstant.E_USER_NOT_EXISTS)) {
                            // Need apply user info when first login
                            Intent intent = new Intent(getActivity(), UserInfoSnsActivity.class);
                            intent.putExtras(userInfo);
                            intent.putExtra(Constants.EXTRA_KEY_IS_REGISTER, true);
                            intent.putExtra(Constants.EXTRA_KEY_ACCOUNT_TYPE, authType);
                            startActivity(intent);
                            getActivity().finish();
                        } else if (msg.equals(ApiConstant.E_AUTH)) {
                            // SingleToast.show(getActivity(), msg);
                        } else if (msg.equals(ApiConstant.E_AUTH_MERGE_CONFLICTS)) {
                            SingleToast.show(getActivity(), R.string.msg_error_auth_conflicts);
                        }
                    }
                });
    }
}
