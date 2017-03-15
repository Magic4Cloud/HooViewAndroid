/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.feedback.FeedbackHelper;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.activity.login.LoginMainActivity;
import com.easyvaas.elapp.activity.setting.AboutActivity;
import com.easyvaas.elapp.activity.setting.BindUserAuthActivity;
import com.easyvaas.elapp.activity.setting.LiveMessageSetListActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserSettingEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import com.easyvaas.common.chat.ChatManager;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private final int REQUEST_CODE_LIVE_PUSH_STATE = 1;
    private final int MSG_CLEAN_CACHE_COMPLETE = 2;
    private CheckBox mNoticeFollowedCb;
    private CheckBox mNoticeNewChatCb;
    private CheckBox mNoticeAllCb;

    private ImageView mCloseIv;
    private TextView mCommitTv;
    private TextView mCenterContentTv;
    private LinearLayout mCommitLl;

    private boolean mIsLiveNoticed;
    private Dialog confirmDialog;
    MyHandler myHandler = new MyHandler(this);

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_CLEAN_CACHE_COMPLETE:
                String cacheSize = (String) msg.obj;
                if (!TextUtils.isEmpty(cacheSize)) {
                    ((TextView) findViewById(R.id.cached_size_tv))
                            .setText(cacheSize);
                }
                break;
        }

    }

    public SettingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mNoticeFollowedCb = (CheckBox) findViewById(R.id.notice_follow_event_cb);
        mNoticeFollowedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
            }
        });
        mNoticeNewChatCb = (CheckBox) findViewById(R.id.notice_chat_event_cb);
        mNoticeNewChatCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
//                ChatManager.getInstance().setPushEnable(isChecked);
            }
        });

//        mNoticeNewChatCb.setChecked(ChatManager.getInstance().isPushEnable());
        mNoticeAllCb = (CheckBox) findViewById(R.id.notice_all_cb);
        mNoticeAllCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
            }
        });
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCommitTv = (TextView) findViewById(R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(R.id.add_option_view);
        mCenterContentTv.setText(getString(R.string.my_settings));
        mCloseIv.setOnClickListener(this);

        findViewById(R.id.notice_push_message_setting_rl).setOnClickListener(this);
        findViewById(R.id.item_account_bind_rl).setOnClickListener(this);
        findViewById(R.id.item_clean_cached).setOnClickListener(this);
        findViewById(R.id.item_about_us_rl).setOnClickListener(this);
        findViewById(R.id.contact_us_rl).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        findViewById(R.id.feedback_rl).setOnClickListener(this);

        if (UpdateManager.getInstance(this).isHaveUpdate()) {
            findViewById(R.id.about_us_remind_tv).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.about_us_remind_tv).setVisibility(View.GONE);
        }
        new Thread() {
            @Override
            public void run() {
                String cacheSize = FileUtil.getSimpleFolderSize(FileUtil.CACHE_DIR);
                Message msg = Message.obtain();
                msg.what = MSG_CLEAN_CACHE_COMPLETE;
                msg.obj = cacheSize;
                myHandler.sendMessage(msg);
            }
        }.start();
        getUserSetting();

        initFeedback();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAuth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_LIVE_PUSH_STATE) {
            mIsLiveNoticed = data.getBooleanExtra(LiveMessageSetListActivity.EXTRA_KEY_IS_LIVE_PUSH, true);
        }

    }

    @Override
    protected void onDestroy() {
        mIsCancelRequestAfterDestroy = false;
        super.onDestroy();
        if (confirmDialog != null) {
            confirmDialog.dismiss();
        }
        if (Preferences.getInstance(this).isLogin()) {
            putUserSetting();
        }
    }

    private void putUserSetting() {
        ApiHelper.getInstance().userEditSetting(mIsLiveNoticed, mNoticeFollowedCb.isChecked(),
                mNoticeAllCb.isChecked(), new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {

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

    private void getUserSetting() {
        ApiHelper.getInstance().userSettingInfo(new MyRequestCallBack<UserSettingEntity>() {
            @Override
            public void onSuccess(UserSettingEntity result) {
                if (result != null) {
                    mNoticeAllCb.setChecked(result.getDisturb() == UserSettingEntity.IS_NOTICE_ALL);
                    mNoticeFollowedCb.setChecked(result.getFollow() == UserSettingEntity.IS_NOTICE_FOLLOWED);
                    mIsLiveNoticed = result.getLive() == UserSettingEntity.IS_NOTICE_LIVING;
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notice_push_message_setting_rl:
                Intent intent = new Intent(getApplicationContext(), LiveMessageSetListActivity.class);
                intent.putExtra(LiveMessageSetListActivity.EXTRA_KEY_IS_LIVE_PUSH, mIsLiveNoticed);
                startActivityForResult(intent, REQUEST_CODE_LIVE_PUSH_STATE);
                break;
            case R.id.item_account_bind_rl:
                Intent userInfoIntent = new Intent(this, BindUserAuthActivity.class);
                startActivity(userInfoIntent);
                break;
            case R.id.item_clean_cached:
                ((TextView) findViewById(R.id.cached_size_tv))
                        .setText("0 KB");
                new Thread() {
                    @Override
                    public void run() {
                        FileUtil.deleteDir(new File(FileUtil.CACHE_DIR));
                        String cacheSize = FileUtil.getSimpleFolderSize(FileUtil.CACHE_DIR);
                        Message msg = Message.obtain();
                        msg.what = MSG_CLEAN_CACHE_COMPLETE;
                        msg.obj = cacheSize;
                        myHandler.sendMessage(msg);
                    }
                }.start();
                break;
            case R.id.item_about_us_rl:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.feedback_rl:
                //startActivity(FeedbackActivity.class);
                FeedbackHelper.getInstance(this).showFeedbackUI();
                break;
            case R.id.contact_us_rl:
                Intent contactIntent = new Intent(this, WebViewActivity.class);
                contactIntent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(R.string.contact_us));
                contactIntent.putExtra(WebViewActivity.EXTRA_KEY_URL,
                        Preferences.getInstance(this).getString(Preferences.KEY_PARAM_CONTACT_US_URL));
                startActivity(contactIntent);
                break;
            case R.id.logout_btn:
                showConfirmDialog();
                break;
            case R.id.close_iv:
                finish();
                break;
        }
    }

    private void showConfirmDialog() {
        if (confirmDialog == null) {
            confirmDialog = DialogUtil.getButtonsDialog(this,
                    getString(R.string.content_confirm_exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logout();
                        }
                    });
        }
        confirmDialog.show();
    }

    public void logout() {
        putUserSetting();
        ApiHelper.getInstance().userLogout(new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
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
        Preferences.getInstance(EVApplication.getApp()).logout(true);
        startActivity(new Intent(this, LoginMainActivity.class));
        sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN_OUT));
        finish();
    }

    private void setAuth() {
        findViewById(R.id.account_bind_qq_iv).setEnabled(false);
        findViewById(R.id.account_bind_wexin_iv).setEnabled(false);
        findViewById(R.id.account_bind_weibo_iv).setEnabled(false);
        User user = EVApplication.getUser();
        if (user.getAuth() != null && user.getAuth() != null) {
            for (User.AuthEntity auth : user.getAuth()) {
                if (User.AUTH_TYPE_PHONE.equals(auth.getType())) {
                    findViewById(R.id.account_bind_phone_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_QQ.equals(auth.getType())) {
                    findViewById(R.id.account_bind_qq_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_WEIXIN.equals(auth.getType())) {
                    findViewById(R.id.account_bind_wexin_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_SINA.equals(auth.getType())) {
                    findViewById(R.id.account_bind_weibo_iv).setEnabled(true);
                }
            }
        }
    }

    private void initFeedback() {
        JSONObject jsonObject = new JSONObject();
        String phoneNumber = Preferences.getInstance(this).getString(Preferences.KEY_LOGIN_PHONE_NUMBER, "");
        if (phoneNumber.startsWith(ApiConstant.VALUE_COUNTRY_CODE_CHINA)) {
            String[] numbers = StringUtil.parseFullPhoneNumber(Preferences.getInstance(this)
                    .getString(Preferences.KEY_LOGIN_PHONE_NUMBER, ""));
            if (numbers.length == 2) {
                phoneNumber = numbers[1];
            }
        }
        /// TODO: 8/9/16 This need to replace with dynamic url
        String toAvatar = "http://aliimg.yizhibo.tv/test/message/07/fd/Secretary.png";
        FeedbackHelper.getInstance(this).customUI(getString(R.string.feedback), phoneNumber,
                EVApplication.getUser().getLogourl(), toAvatar);

        String qqNumber = Preferences.getInstance(this).getString(Preferences.KEY_LOGIN_QQ_NUMBER, "");
        // Calculate user age .
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(Calendar.YEAR);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        String birthDay = EVApplication.getUser().getBirthday();
        int ageInt = 0;
        if (!TextUtils.isEmpty(birthDay)) {
            Date birthdayDate = formatter.parse(birthDay, pos);
            calendar.setTimeInMillis(birthdayDate.getTime());
            ageInt = currentYear - calendar.get(Calendar.YEAR);
        }
        try {
            if (!TextUtils.isEmpty(phoneNumber)) {
                jsonObject.put("phone", phoneNumber);
            }
            if (!TextUtils.isEmpty(qqNumber)) {
                jsonObject.put("qq", qqNumber);
            }
            if (ageInt > 0) {
                jsonObject.put("age", ageInt);
            }
            jsonObject.put("name", Preferences.getInstance(this).getUserNumber());
            jsonObject.put("nickname", Preferences.getInstance(this).getUserNickname());
            jsonObject.put("gender", EVApplication.getUser().getGender());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackHelper.getInstance(this).setAppExtInfo(jsonObject);
    }
}
