package com.easyvaas.elapp.ui.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ILoginManager;
import com.easyvaas.common.sharelogin.model.PlatformActionListener;
import com.easyvaas.common.sharelogin.qq.QQLoginManager;
import com.easyvaas.common.sharelogin.wechat.WechatLoginManager;
import com.easyvaas.common.sharelogin.weibo.WeiboLoginManager;
import com.easyvaas.elapp.bean.pay.MyAssetEntity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.dialog.RegisterDialog;
import com.easyvaas.elapp.event.RegisterSuccessEvent;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.ValidateParam;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.tencent.open.utils.Global.getContext;

public class LoginActivity extends Activity implements View.OnClickListener {
    public final String TAG = "LoginActivity";
    private EditText mEtPhone;
    private EditText mEtPassword;
    private CheckBox mIvPwdVisible;
    private CheckBox mCbAgree;


    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mIvPwdVisible = (CheckBox) findViewById(R.id.iv_pwd_visible);
        mIvPwdVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEtPassword.setSelection(mEtPassword.getText().length());
                } else {
                    mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEtPassword.setSelection(mEtPassword.getText().length());
                }
            }
        });
        mCbAgree = (CheckBox) findViewById(R.id.cb_agree);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.iv_wechat).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.iv_weibo).setOnClickListener(this);
        findViewById(R.id.tv_user_protocol).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ILoginManager loginManager;
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                RegisterDialog.showRegisterDialog(this, false);
                break;
            case R.id.iv_wechat:
                showLoadingDialog(R.string.loading_data, false, true);
                loginManager = new WechatLoginManager(this);
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by Weixin complete");
                        loginBySNSAccount(User.AUTH_TYPE_WEIXIN, userInfo);
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
                break;
            case R.id.iv_qq:
                showLoadingDialog(R.string.loading_data, false, true);
                loginManager = new QQLoginManager(this);
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by QQ complete");
                        loginBySNSAccount(User.AUTH_TYPE_QQ, userInfo);
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
                break;
            case R.id.iv_weibo:
                showLoadingDialog(R.string.loading_data, false, true);
                this.getIntent().putExtra("is_weibo_login", true);
                loginManager = new WeiboLoginManager(this);
                loginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(Bundle userInfo) {
                        Logger.d(TAG, "Login by Weibo complete");
                        loginBySNSAccount(User.AUTH_TYPE_SINA, userInfo);
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
                break;
            case R.id.tv_user_protocol:
                break;
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_forget_pwd:
                RegisterDialog.showRegisterDialog(this, true);
                break;
        }
    }

    private void login() {
        String phone = mEtPhone.getText().toString();
        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            SingleToast.show(getApplicationContext(), R.string.msg_phone_number_empty);
            return;
        } else if (!ValidateParam.validatePhone(phone)) {
            SingleToast.show(getApplicationContext(), R.string.msg_phone_number_invalid);
            return;
        } else if (TextUtils.isEmpty(password)) {
            SingleToast.show(getApplicationContext(), R.string.msg_password_empty);
            return;
        } else if (password.length() < 6) {
            SingleToast.show(getApplicationContext(), R.string.msg_password_invalid);
            return;
        }
        loginByPhone(phone, password);
    }

    private void loginByPhone(String phone, String password) {
        showLoadingDialog(R.string.logining, false, true);
        ApiHelper.getInstance().loginByPhone(phone, password, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                getAssetInfo(user, "LoginByPhone");
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                dismissLoadingDialog();
                SingleToast.show(getApplicationContext(), R.string.msg_login_failed);

            }

            @Override
            public void onFailure(String msg) {
                dismissLoadingDialog();
                RequestUtil.handleRequestFailed(msg);
                SingleToast.show(getApplicationContext(), R.string.msg_login_failed);
            }
        });
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
                        getAssetInfo(user, "LoginByAuth");
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        SingleToast.show(getApplicationContext(), getString(R.string.request_failed));
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(String msg) {
                        dismissLoadingDialog();
                        if (msg.equals(ApiConstant.E_USER_NOT_EXISTS)) {
                            // Need apply user info when first login
                            uploadUserInfo(userInfo);
                            dismissLoadingDialog();
                            finish();
                        } else if (msg.equals(ApiConstant.E_AUTH)) {
                            // SingleToast.show(getActivity(), msg);
                        } else if (msg.equals(ApiConstant.E_AUTH_MERGE_CONFLICTS)) {
                            SingleToast.show(getApplicationContext(), R.string.msg_error_auth_conflicts);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RegisterSuccessEvent event) {
        finish();
    }


    private Dialog mLoadingDialog;

    protected void showLoadingDialog(int resId, boolean dismissTouchOutside, boolean cancelable) {
        showLoadingDialog(getString(resId), dismissTouchOutside, cancelable);
    }

    protected void showLoadingDialog(String message, boolean dismissTouchOutside, boolean cancelable) {
        if (isFinishing()) {
            return;
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogUtil.getProcessDialog(this, message, dismissTouchOutside, cancelable);
        }
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(dismissTouchOutside);
        mLoadingDialog.show();
    }

    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void uploadUserInfo(Bundle userInfo){
        ApiHelper.getInstance()
                .registerByAuth(userInfo.getString(ShareConstants.PARAMS_NICK_NAME), userInfo.getString(ShareConstants.PARAMS_OPENID), userInfo.getString(ShareConstants.PARAMS_SEX), userInfo.getString(ShareConstants.PARAMS_ACCESS_TOKEN), "19890704",
                        "beijing", getString(R.string.default_sign), userInfo.getString(ShareConstants.AUTHTYPE), userInfo.getLong(ShareConstants.PARAMS_EXPIRES_IN)+"", userInfo.getString(ShareConstants.PARAMS_UNIONID),
                        userInfo.getString(ShareConstants.PARAMS_REFRESH_TOKEN), userInfo.getString(ShareConstants.PARAMS_IMAGEURL), new MyRequestCallBack<User>() {
                            @Override
                            public void onSuccess(User user) {
                                getAssetInfo(user, "RegisterByAuth");
                            }

                            @Override
                            public void onError(String errorInfo) {
                                super.onError(errorInfo);
                                dismissLoadingDialog();
                            }

                            @Override
                            public void onFailure(String msg) {
                                dismissLoadingDialog();
                            }
                        });
    }

    // 得到火眼豆
    private void getAssetInfo(final User user, final String loginType){
        if (user == null){
            SingleToast.show(getApplicationContext(), R.string.msg_login_failed);
            return;
        }
        UserUtil.handleAfterLogin(getContext(), user, loginType);
        ApiHelper.getInstance().getAssetInfo(new MyRequestCallBack<MyAssetEntity>() {
            @Override
            public void onSuccess(MyAssetEntity result) {
                if (result != null) {
                    Log.e("test", "result not null");
                    Preferences.getInstance(LoginActivity.this).putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                    GiftManager.setECoinCount(LoginActivity.this, result.getEcoin());
                }
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onFailure(String msg) {
                SingleToast.show(getApplicationContext(), R.string.msg_login_failed);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getIntent().getBooleanExtra("is_weibo_login", false)) {
            WeiboLoginManager.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
