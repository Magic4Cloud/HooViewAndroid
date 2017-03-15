/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.qq.QQLoginManager;
import com.easyvaas.common.sharelogin.wechat.WechatLoginManager;
import com.easyvaas.common.sharelogin.weibo.WeiboLoginManager;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.login.BindOrChangePhoneActivity;
import com.easyvaas.elapp.activity.setting.BindPhoneActivity;
import com.easyvaas.elapp.activity.setting.UpdateUserPasswordActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.StringUtil;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import java.util.List;

public class BindUserAuthActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = BindUserAuthActivity.class.getSimpleName();
    public static final String EXTRA_KEY_IS_BIND_WEIXIN_CASH = "extra_key_is_bind_weixin_cash";

    private static int REQUEST_CODE_PHONE = 0x110;

    private TextView mQQText;
    private TextView mWeiXinText;
    private TextView mWeiBoText;
    private TextView mBindPhoneTv;
    private TextView mPhoneNumberTv;
    private SsoHandler mSsoHandler;
    private User mUser;

    private boolean mIsQQBind;
    private boolean mIsWeiboBind;
    private boolean mIsWeixinBind;
    private boolean mIsPhoneBind;
    private String mPhoneNumber;


    private boolean mIsBindWXCauseCash;

    public static void start(Context context) {
        Intent starter = new Intent(context, BindUserAuthActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_user_auth);

        mIsBindWXCauseCash = getIntent().getBooleanExtra(EXTRA_KEY_IS_BIND_WEIXIN_CASH, false);
        mUser = EVApplication.getUser();
        List<User.AuthEntity> authEntities = EVApplication.getUser().getAuth();
        if (authEntities != null) {
            for (User.AuthEntity authEntity : EVApplication.getUser().getAuth()) {
                if (User.AUTH_TYPE_PHONE.equals(authEntity.getType())) {
                    mIsPhoneBind = true;
                    break;
                }
            }
        }
        TextView textView = (TextView) findViewById(R.id.tv_title);
        textView.setText(R.string.account_bind);
        findViewById(R.id.iv_back).setOnClickListener(this);
        initView();
    }

    private void initView() {
//        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
//        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
//        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
//        mCenterContentTv.setText(getString(R.string.account_bind));
        mQQText = (TextView) findViewById(R.id.bind_qq_tv);
        mQQText.setOnClickListener(this);
        mWeiXinText = (TextView) findViewById(R.id.bind_weixin_tv);
        mWeiXinText.setOnClickListener(this);
        mWeiBoText = (TextView) findViewById(R.id.bind_weibo_tv);
        mWeiBoText.setOnClickListener(this);
        mBindPhoneTv = (TextView) findViewById(R.id.bind_phone_tv);
        mBindPhoneTv.setOnClickListener(this);
        mPhoneNumberTv = (TextView) findViewById(R.id.tv_phone);
        mPhoneNumberTv.setOnClickListener(this);
        View changePassRl = findViewById(R.id.update_password_rl);
        changePassRl.setOnClickListener(this);
        if (mIsPhoneBind) {
            mBindPhoneTv.setText(R.string.change_phone_number);
            mBindPhoneTv.setVisibility(View.INVISIBLE);
        } else {
            mPhoneNumberTv.setVisibility(View.GONE);
            changePassRl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBindState(mUser);
    }

    @Override
    public void onClick(View v) {
        ILoginManager loginManager;
        switch (v.getId()) {
            case R.id.bind_weixin_tv:
                if (mIsWeixinBind) {
                    unbindAuth(User.AUTH_TYPE_WEIXIN);
                } else {
                    showLoadingDialog(R.string.loading_data, false, true);
                    loginManager = new WechatLoginManager(this);
                    loginManager.login(new PlatformActionListener() {
                        @Override
                        public void onComplete(Bundle userInfo) {
                            Logger.d(TAG, "Login by weibo complete");
                            checkUserIsBinding(User.AUTH_TYPE_WEIXIN, userInfo, User.AUTH_TYPE_WEIXIN);
                        }

                        @Override
                        public void onError() {
                            dismissLoadingDialog();
                            SingleToast.show(getApplicationContext(),
                                    getString(R.string.msg_sns_auth_failed, getString(R.string.weixin)));
                        }

                        @Override
                        public void onCancel() {
                            dismissLoadingDialog();
                        }
                    });
                }
                break;
            case R.id.bind_weibo_tv:
                if (mIsWeiboBind) {
                    unbindAuth(User.AUTH_TYPE_SINA);
                } else {
                    showLoadingDialog(R.string.loading_data, false, true);
                    WeiboLoginManager weiboLoginManager = new WeiboLoginManager(this);
                    weiboLoginManager.login(new PlatformActionListener() {
                        @Override
                        public void onComplete(Bundle userInfo) {
                            Logger.d(TAG, "Login by Weixin complete");
                            checkUserIsBinding(User.AUTH_TYPE_SINA, userInfo, User.AUTH_TYPE_SINA);
                        }

                        @Override
                        public void onError() {
                            dismissLoadingDialog();
                            SingleToast.show(getApplicationContext(),
                                    getString(R.string.msg_sns_auth_failed, getString(R.string.weibo)));
                        }

                        @Override
                        public void onCancel() {
                            dismissLoadingDialog();
                        }
                    });
                    mSsoHandler = WeiboLoginManager.getSsoHandler();
                }
                break;
            case R.id.bind_qq_tv:
                if (mIsQQBind) {
                    unbindAuth(User.AUTH_TYPE_QQ);
                } else {
                    showLoadingDialog(R.string.loading_data, false, true);
                    loginManager = new QQLoginManager(this);
                    loginManager.login(new PlatformActionListener() {
                        @Override
                        public void onComplete(Bundle userInfo) {
                            Logger.d(TAG, "Login by QQ complete");
                            checkUserIsBinding(User.AUTH_TYPE_QQ, userInfo, User.AUTH_TYPE_QQ);
                        }

                        @Override
                        public void onError() {
                            dismissLoadingDialog();
                            SingleToast.show(getApplicationContext(),
                                    getString(R.string.msg_sns_auth_failed, getString(R.string.qq)));
                        }

                        @Override
                        public void onCancel() {
                            dismissLoadingDialog();
                        }
                    });
                }
                break;
            case R.id.bind_phone_tv:
            case R.id.tv_phone:
                if (mIsPhoneBind) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), BindOrChangePhoneActivity.class);
                    intent.putExtra(BindOrChangePhoneActivity.EXTRA_PHONE_NUMBER, mPhoneNumber);
                    startActivity(intent);
                } else {
                    startActivityForResult(new Intent(getApplicationContext(), BindPhoneActivity.class),
                            REQUEST_CODE_PHONE);
                }
                break;
            case R.id.update_password_rl:
                startActivity(new Intent(this, UpdateUserPasswordActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHONE) {
            if (resultCode == RESULT_OK) {
                String phone = data.getStringExtra(Constants.EXTRA_KEY_USER_PHONE);
                Preferences.getInstance(getApplicationContext())
                        .putString(Preferences.KEY_LOGIN_PHONE_NUMBER, phone);
                setBindState(User.AUTH_TYPE_PHONE, true, phone);
            }
        } else {
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    private void initBindState(User user) {
        if (user != null && user.getAuth() != null) {
            for (User.AuthEntity auth : user.getAuth()) {
                setBindState(auth.getType(), true, auth.getToken());
            }
        }
    }

    private void setBindState(String type, boolean isBind, String token) {
        int bindStateResId = isBind ? R.string.unbind : R.string.binding;
        if (User.AUTH_TYPE_QQ.equals(type)) {
            mIsQQBind = true;
            mQQText.setTextColor(getResources().getColor(R.color.text_common));
            if (isBind && (mIsPhoneBind || (mIsWeixinBind || mIsWeiboBind))) {
                mQQText.setText(R.string.unbind);
                mQQText.setEnabled(true);
                mQQText.setSelected(true);
            } else {
//                mQQText.setBackgroundResource(R.drawable.common_fill_round_three_btn_blue_selector);
//                mQQText.setTextColor(getResources().getColor(R.color.text_white));
                mQQText.setText(R.string.bound);
                mQQText.setEnabled(false);
                mQQText.setSelected(false);
            }
        } else if (User.AUTH_TYPE_WEIXIN.equals(type)) {
            mIsWeixinBind = true;
            mWeiXinText.setTextColor(getResources().getColor(R.color.text_common));
            if (isBind && (mIsPhoneBind || (mIsWeiboBind || mIsQQBind))) {
//                mWeiXinText.setBackgroundResource(R.drawable.common_stroke_round_three_btn_blue_selector);
//                mWeiXinText.setTextColor(getResources().getColor(R.color.btn_color_main));
                mWeiXinText.setText(R.string.unbind);
                mWeiXinText.setEnabled(true);
                mWeiXinText.setSelected(true);
            } else {
//                mWeiXinText.setBackgroundResource(R.drawable.common_fill_round_three_btn_blue_selector);
//                mWeiXinText.setTextColor(getResources().getColor(R.color.text_white));
                mWeiXinText.setText(R.string.bound);
                mWeiXinText.setEnabled(false);
                mWeiXinText.setSelected(false);
            }
        } else if (User.AUTH_TYPE_SINA.equals(type)) {
            mIsWeiboBind = true;
            mWeiBoText.setTextColor(getResources().getColor(R.color.text_common));
            if (isBind && (mIsPhoneBind || (mIsWeixinBind || mIsQQBind))) {
//                mWeiBoText.setBackgroundResource(R.drawable.common_stroke_round_three_btn_blue_selector);
//                mWeiBoText.setTextColor(getResources().getColor(R.color.btn_color_main));
                mWeiBoText.setText(R.string.unbind);
                mWeiBoText.setEnabled(true);
                mWeiBoText.setSelected(true);
            } else {
//                mWeiBoText.setBackgroundResource(R.drawable.common_fill_round_three_btn_blue_selector);
//                mWeiBoText.setTextColor(getResources().getColor(R.color.text_white));
                mWeiBoText.setText(R.string.bound);
                mWeiBoText.setEnabled(false);
                mWeiBoText.setSelected(false);

            }
        } else if (User.AUTH_TYPE_PHONE.equals(type)) {
            mIsPhoneBind = true;
            if (!TextUtils.isEmpty(token)) {
                String[] numbers = StringUtil.parseFullPhoneNumber(token);
                mPhoneNumber = numbers.length > 1 ? numbers[1] : token;
                mPhoneNumberTv.setText(getString(R.string.bind_phone_number_ok, mPhoneNumber.substring(0, 3)
                        , mPhoneNumber.substring(7, mPhoneNumber.length())));
                mBindPhoneTv.setText(R.string.change_phone_number);
            }
        }
    }

    private void checkUserIsBinding(final String authType, final Bundle userInfo, String bindType) {
        final String openid = userInfo.getString(ShareConstants.PARAMS_OPENID);
        final String unionid = bindType.equals(User.AUTH_TYPE_WEIXIN)
                ? userInfo.getString(ShareConstants.PARAMS_UNIONID) : "";
        final String refreshToken = userInfo.getString(ShareConstants.PARAMS_REFRESH_TOKEN);
        final String accessToken = userInfo.getString(ShareConstants.PARAMS_ACCESS_TOKEN);
        final long expiresIn = userInfo.getLong(ShareConstants.PARAMS_EXPIRES_IN);

        bindingAuth(authType, openid, unionid, refreshToken, accessToken, expiresIn);
    }

    private void bindingAuth(final String type, final String token, final String unionId, String refreshToken,
                             String accessToken, final long expiresIn) {
        ApiHelper.getInstance().bindByAuth(type, token, accessToken, refreshToken, expiresIn, unionId,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        User.AuthEntity authEntity = EVApplication.getUser().new AuthEntity();
                        authEntity.setExpire_time(DateTimeUtil.
                                formatToServerDate(expiresIn + System.currentTimeMillis()));
                        authEntity.setToken(token);
                        authEntity.setLogin(0);
                        authEntity.setType(type);
                        if (EVApplication.getUser().getAuth() != null) {
                            EVApplication.getUser().getAuth().add(authEntity);
                        }
                        if (User.AUTH_TYPE_PHONE.equals(type)) {
                            Preferences.getInstance(getApplicationContext())
                                    .putString(Preferences.KEY_LOGIN_PHONE_NUMBER, token);
                        }

                        if (isFinishing()) {
                            return;
                        }
                        dismissLoadingDialog();
                        SingleToast.show(getApplicationContext(), R.string.msg_account_bind_success);
                        setBindState(type, true, "");
                        if (User.AUTH_TYPE_WEIXIN.equals(type) && mIsBindWXCauseCash) {
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(String msg) {
                        if (ApiConstant.E_AUTH_MERGE_CONFLICTS.equals(msg)) {
                            SingleToast.show(getApplicationContext(), R.string.msg_error_auth_conflicts);
                        } else {
                            SingleToast.show(getApplicationContext(), msg);
                        }
                        dismissLoadingDialog();
                    }
                });
    }

    private void unbindAuth(final String type) {
        ApiHelper.getInstance().userAuthUnbind(type, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                SingleToast.show(getApplicationContext(), R.string.msg_unbind_success);
                if (type.equals(User.AUTH_TYPE_SINA)) {
//                    mWeiBoText.setBackgroundResource(R.drawable.round_btn_selector);
                    mWeiBoText.setText(R.string.binding);
                    mWeiBoText.setSelected(false);
                    mWeiBoText.setTextColor(getResources().getColor(R.color.text_common));
                    mIsWeiboBind = false;
                } else if (type.equals(User.AUTH_TYPE_WEIXIN)) {
//                    mWeiXinText.setBackgroundResource(R.drawable.round_btn_selector);
                    mWeiXinText.setText(R.string.binding);
                    mWeiXinText.setSelected(false);
                    mWeiXinText.setTextColor(getResources().getColor(R.color.text_common));
                    mIsWeixinBind = false;
                } else if (type.equals(User.AUTH_TYPE_QQ)) {
//                    mQQText.setBackgroundResource(R.drawable.round_btn_selector);
                    mQQText.setText(R.string.binding);
                    mQQText.setSelected(false);
                    mQQText.setTextColor(getResources().getColor(R.color.text_common));
                    mIsQQBind = false;
                }
                List<User.AuthEntity> authEntities = EVApplication.getUser().getAuth();
                for (int i = 0, n = authEntities.size(); i < n; i++) {
                    if (type.equals(authEntities.get(i).getType())) {
                        authEntities.remove(authEntities.get(i));
                        break;
                    }
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

