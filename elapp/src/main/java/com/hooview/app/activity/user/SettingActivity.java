/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.chat.ChatManager;
import com.easyvaas.common.feedback.FeedbackHelper;
import com.hooview.app.R;
import com.hooview.app.activity.login.LoginMainActivity;
import com.hooview.app.activity.login.TextActivity;
import com.hooview.app.activity.setting.AboutActivity;
import com.hooview.app.activity.setting.BindUserAuthActivity;
import com.hooview.app.activity.setting.LiveMessageSetListActivity;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.user.User;
import com.hooview.app.bean.user.UserSettingEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.StringUtil;
import com.hooview.app.utils.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                    ((TextView) findViewById(com.hooview.app.R.id.cached_size_tv))
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
        setContentView(com.hooview.app.R.layout.activity_setting);

        mNoticeFollowedCb = (CheckBox) findViewById(com.hooview.app.R.id.notice_follow_event_cb);
        mNoticeFollowedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
            }
        });
        mNoticeNewChatCb = (CheckBox) findViewById(com.hooview.app.R.id.notice_chat_event_cb);
        mNoticeNewChatCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
                ChatManager.getInstance().setPushEnable(isChecked);
            }
        });

        mNoticeNewChatCb.setChecked(ChatManager.getInstance().isPushEnable());
        mNoticeAllCb = (CheckBox) findViewById(com.hooview.app.R.id.notice_all_cb);
        mNoticeAllCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
            }
        });
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCommitTv = (TextView) findViewById(com.hooview.app.R.id.add_option_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCommitLl = (LinearLayout) findViewById(com.hooview.app.R.id.add_option_view);
        mCenterContentTv.setText(getString(com.hooview.app.R.string.my_settings));
        mCloseIv.setOnClickListener(this);

        findViewById(com.hooview.app.R.id.notice_push_message_setting_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.item_account_bind_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.item_clean_cached).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.item_about_us_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.contact_us_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.logout_btn).setOnClickListener(this);
        //findViewById(com.hooview.app.R.id.feedback_rl).setOnClickListener(this);

        RelativeLayout rl_feedback = (RelativeLayout) findViewById(R.id.feedback_rl);
        rl_feedback.setOnClickListener(this);
        rl_feedback.setVisibility(View.GONE);

        if (UpdateManager.getInstance(this).isHaveUpdate()) {
            findViewById(com.hooview.app.R.id.about_us_remind_tv).setVisibility(View.VISIBLE);
        } else {
            findViewById(com.hooview.app.R.id.about_us_remind_tv).setVisibility(View.GONE);
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
            case com.hooview.app.R.id.notice_push_message_setting_rl:
                Intent intent = new Intent(getApplicationContext(), LiveMessageSetListActivity.class);
                intent.putExtra(LiveMessageSetListActivity.EXTRA_KEY_IS_LIVE_PUSH, mIsLiveNoticed);
                startActivityForResult(intent, REQUEST_CODE_LIVE_PUSH_STATE);
                break;
            case com.hooview.app.R.id.item_account_bind_rl:
                Intent userInfoIntent = new Intent(this, BindUserAuthActivity.class);
                startActivity(userInfoIntent);
                break;
            case com.hooview.app.R.id.item_clean_cached:
                ((TextView) findViewById(com.hooview.app.R.id.cached_size_tv))
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
            case com.hooview.app.R.id.item_about_us_rl:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case com.hooview.app.R.id.feedback_rl:
                //startActivity(FeedbackActivity.class);
                FeedbackHelper.getInstance(this).showFeedbackUI();
                break;
            case com.hooview.app.R.id.contact_us_rl://联系我们
//                Intent contactIntent = new Intent(this, WebViewActivity.class);
//                contactIntent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(com.hooview.app.R.string.contact_us));
//                contactIntent.putExtra(WebViewActivity.EXTRA_KEY_URL,
//                        Preferences.getInstance(this).getString(Preferences.KEY_PARAM_CONTACT_US_URL));
//                startActivity(contactIntent);
                Intent contactIntent = new Intent(this, TextActivity.class);
                contactIntent.putExtra(TextActivity.EXTRA_TYPE, TextActivity.TYPE_CONTACT_US);
                contactIntent
                        .putExtra(Constants.EXTRA_KEY_TITLE, getString(R.string.contact_us));
                startActivity(contactIntent);

                break;
            case com.hooview.app.R.id.logout_btn:
                showConfirmDialog();
                break;
            case com.hooview.app.R.id.close_iv:
                finish();
                break;
        }
    }

    private void showConfirmDialog() {
        if (confirmDialog == null) {
            confirmDialog = DialogUtil.getButtonsDialog(this,
                    getString(com.hooview.app.R.string.content_confirm_exit), new DialogInterface.OnClickListener() {
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
        findViewById(com.hooview.app.R.id.account_bind_qq_iv).setEnabled(false);
        findViewById(com.hooview.app.R.id.account_bind_wexin_iv).setEnabled(false);
        findViewById(com.hooview.app.R.id.account_bind_weibo_iv).setEnabled(false);
        User user = EVApplication.getUser();
        if (user.getAuth() != null && user.getAuth() != null) {
            for (User.AuthEntity auth : user.getAuth()) {
                if (User.AUTH_TYPE_PHONE.equals(auth.getType())) {
                    findViewById(com.hooview.app.R.id.account_bind_phone_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_QQ.equals(auth.getType())) {
                    findViewById(com.hooview.app.R.id.account_bind_qq_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_WEIXIN.equals(auth.getType())) {
                    findViewById(com.hooview.app.R.id.account_bind_wexin_iv).setEnabled(true);
                } else if (User.AUTH_TYPE_SINA.equals(auth.getType())) {
                    findViewById(com.hooview.app.R.id.account_bind_weibo_iv).setEnabled(true);
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
        FeedbackHelper.getInstance(this).customUI(getString(com.hooview.app.R.string.feedback), phoneNumber,
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
