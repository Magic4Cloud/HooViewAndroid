package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.easyvaas.common.feedback.FeedbackHelper;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.UpdateInfoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.base.BaseTitleActivity;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UpdateManager;
import com.hooview.app.R;

import java.io.File;

public class SettingActivity extends BaseTitleActivity implements View.OnClickListener {
    private final int REQUEST_CODE_LIVE_PUSH_STATE = 1;
    private final int MSG_CLEAN_CACHE_COMPLETE = 2;
    private CheckBox mNoticeFollowEventCb;
    private TextView mTvVersion;
    private MyHandler myHandler = new MyHandler(this);

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

    public static void start(Context context) {
        if (Preferences.getInstance(context).isLogin() && EVApplication.isLogin()) {
            Intent starter = new Intent(context, SettingActivity.class);
            context.startActivity(starter);
        } else {
            LoginActivity.start(context);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_new);
        mTvTitle.setText(R.string.setting);
        assignViews();
    }

    private void assignViews() {
        findViewById(R.id.rl_account).setOnClickListener(this);
        findViewById(R.id.rl_live_push).setOnClickListener(this);
        findViewById(R.id.rl_about).setOnClickListener(this);
        findViewById(R.id.rl_update_version).setOnClickListener(this);
        findViewById(R.id.rl_clear_memory).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.rl_feedback).setOnClickListener(this);
        mNoticeFollowEventCb = (CheckBox) findViewById(R.id.notice_follow_event_cb);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
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
        mNoticeFollowEventCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                putUserSetting(isChecked);
            }
        });
        mTvVersion.setText(getVersion());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_account:
                BindUserAuthActivity.start(SettingActivity.this);
                break;
            case R.id.rl_live_push:
                mNoticeFollowEventCb.toggle();
                break;
            case R.id.rl_about:
                AboutActivity.start(this);
                break;
            case R.id.rl_update_version:
                UpdateManager.getInstance(SettingActivity.this).checkUpdateInfo(
                        new UpdateManager.UpdateListener() {
                            @Override
                            public void onUpdateReturned(UpdateInfoEntity result) {
                                dismissLoadingDialog();
                                switch (result.getUpdate()) {
                                    case UpdateManager.UpdateStatus.Yes: // has update
                                        Toast.makeText(SettingActivity.this, "有版本更新", Toast.LENGTH_SHORT).show();
                                        break;
                                    case UpdateManager.UpdateStatus.No: // has no update
                                        SingleToast.show(getApplicationContext(), R.string.msg_version_is_the_latest);
                                        break;
                                    case UpdateManager.UpdateStatus.NoneWifi: // none wifi
                                        SingleToast.show(getApplicationContext(), R.string.msg_network_bad_check_retry);
                                        break;
                                    case UpdateManager.UpdateStatus.Timeout: // time out
                                        SingleToast.show(getApplicationContext(), R.string.msg_network_bad_check_retry);
                                        break;
                                }
                            }
                        }, null);
                break;
            case R.id.rl_clear_memory:
                ((TextView) findViewById(R.id.cached_size_tv))
                        .setText("0 KB");
                SingleToast.show(getApplicationContext(), R.string.clear_cache_memory_finish);
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
            case R.id.btn_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setMessage(R.string.user_logout_sure)
                        .setNegativeButton(getString(R.string.user_cancel), null)
                        .setPositiveButton(getString(R.string.user_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                logout();

                            }
                        });
                builder.show();
                break;
            case R.id.rl_feedback:
                FeedbackHelper.getInstance(this).showFeedbackUI();
                break;

        }
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.version_name, version);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void putUserSetting(boolean mIsLiveNoticed) {
        ApiHelper.getInstance().userEditSetting(mIsLiveNoticed, false,
                true, new MyRequestCallBack<String>() {
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

    public void logout() {
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
        EVApplication.setUserNull();
//        sendBroadcast(new Intent(Constants.ACTION_GO_LOGIN_OUT));
        finish();
    }
}
