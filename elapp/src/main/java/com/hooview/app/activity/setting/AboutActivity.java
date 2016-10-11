/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.BuildConfig;
import com.hooview.app.activity.WebViewActivity;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.UpdateInfoEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.utils.ChannelUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UpdateManager;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private int mClickLogoCount;
    private long mClickTime;
    private ImageView mCloseIv;
    private TextView mCenterContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_about);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(com.hooview.app.R.string.about_us));
        mCloseIv.setOnClickListener(this);

        findViewById(com.hooview.app.R.id.logo_area_iv).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.about_us_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.seizure_account_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.copyright_rl).setOnClickListener(this);
        findViewById(com.hooview.app.R.id.manual_check_update_rl).setOnClickListener(this);
        if (ChannelUtil.isGoogleChannel(this)) {
            findViewById(com.hooview.app.R.id.manual_check_update_rl).setVisibility(View.GONE);
        }


        String version = "V" + BuildConfig.VERSION_NAME;
        if (!BuildConfig.BUILD_TYPE.equals("release") || BuildConfig.FLAVOR.startsWith("inner")) {
            version = version + " " + Preferences.getInstance(this).getString(Preferences.KEY_SERVER_TYPE)
                    + "\nbuild time: " + getString(com.hooview.app.R.string.build_time);
        }
        ((TextView) findViewById(com.hooview.app.R.id.version_name_tv)).setText(version);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case com.hooview.app.R.id.logo_area_iv:
                if (mClickTime == 0) {
                    mClickTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - mClickTime > 500) {
                    mClickLogoCount = 0;
                } else {
                    mClickLogoCount++;
                }
                mClickTime = System.currentTimeMillis();

                if (mClickLogoCount > 3) {
                    SingleToast.show(this, "channel: " + ChannelUtil.getChannelName(this)
                            + "\nbuild time: " + getString(com.hooview.app.R.string.build_time));
                }
                break;
            case com.hooview.app.R.id.about_us_rl:
                startActivity(new Intent(getApplicationContext(), AboutYishiyunActivity.class));
                break;
            case com.hooview.app.R.id.seizure_account_rl:
                Intent seizureIntent = new Intent(this, WebViewActivity.class);
                seizureIntent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(com.hooview.app.R.string.seizure_account));
                seizureIntent.putExtra(WebViewActivity.EXTRA_KEY_URL,
                        Preferences.getInstance(this).getString(Preferences.KEY_PARAM_FREE_USER_INFO_US_URL));
                startActivity(seizureIntent);
                break;
            case com.hooview.app.R.id.copyright_rl:
                startActivity(CopyrightActivity.class);
                break;
            case com.hooview.app.R.id.manual_check_update_rl:
                showLoadingDialog(com.hooview.app.R.string.loading_data, false, true);
                UpdateManager.getInstance(AboutActivity.this).checkUpdateInfo(
                        new UpdateManager.UpdateListener() {
                            @Override
                            public void onUpdateReturned(UpdateInfoEntity result) {
                                dismissLoadingDialog();
                                switch (result.getUpdate()) {
                                    case UpdateManager.UpdateStatus.Yes: // has update
                                        break;
                                    case UpdateManager.UpdateStatus.No: // has no update
                                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_version_is_the_latest);
                                        break;
                                    case UpdateManager.UpdateStatus.NoneWifi: // none wifi
                                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_network_bad_check_retry);
                                        break;
                                    case UpdateManager.UpdateStatus.Timeout: // time out
                                        SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_network_bad_check_retry);
                                        break;
                                }
                            }
                        }, null);
                break;
            case com.hooview.app.R.id.close_iv:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mClickLogoCount = 0;

        if (UpdateManager.getInstance(this).isHaveUpdate()) {
            findViewById(com.hooview.app.R.id.manual_check_update_remind_tv).setVisibility(View.VISIBLE);
        } else {
            findViewById(com.hooview.app.R.id.manual_check_update_remind_tv).setVisibility(View.GONE);
        }
    }
}
