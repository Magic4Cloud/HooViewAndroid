/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.BuildConfig;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.UpdateInfoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.utils.ChannelUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UpdateManager;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private int mClickLogoCount;
    private long mClickTime;
    private ImageView mCloseIv;
    private TextView mCenterContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(R.string.about_us));
        mCloseIv.setOnClickListener(this);

        findViewById(R.id.logo_area_iv).setOnClickListener(this);
        findViewById(R.id.about_us_rl).setOnClickListener(this);
        findViewById(R.id.seizure_account_rl).setOnClickListener(this);
        findViewById(R.id.copyright_rl).setOnClickListener(this);
        findViewById(R.id.manual_check_update_rl).setOnClickListener(this);
        if (ChannelUtil.isGoogleChannel(this)) {
            findViewById(R.id.manual_check_update_rl).setVisibility(View.GONE);
        }


        String version = "V" + BuildConfig.VERSION_NAME;
        if (!BuildConfig.BUILD_TYPE.equals("release") || BuildConfig.FLAVOR.startsWith("inner")) {
            version = version + " " + Preferences.getInstance(this).getString(Preferences.KEY_SERVER_TYPE)
                    + "\nbuild time: " + getString(R.string.build_time);
        }
        ((TextView) findViewById(R.id.version_name_tv)).setText(version);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.logo_area_iv:
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
                            + "\nbuild time: " + getString(R.string.build_time));
                }
                break;
            case R.id.about_us_rl:
                startActivity(new Intent(getApplicationContext(), AboutYishiyunActivity.class));
                break;
            case R.id.seizure_account_rl:
                Intent seizureIntent = new Intent(this, WebViewActivity.class);
                seizureIntent.putExtra(WebViewActivity.EXTRA_KEY_TITLE, getString(R.string.seizure_account));
                seizureIntent.putExtra(WebViewActivity.EXTRA_KEY_URL,
                        Preferences.getInstance(this).getString(Preferences.KEY_PARAM_FREE_USER_INFO_US_URL));
                startActivity(seizureIntent);
                break;
            case R.id.copyright_rl:
                startActivity(CopyrightActivity.class);
                break;
            case R.id.manual_check_update_rl:
                showLoadingDialog(R.string.loading_data, false, true);
                UpdateManager.getInstance(AboutActivity.this).checkUpdateInfo(
                        new UpdateManager.UpdateListener() {
                            @Override
                            public void onUpdateReturned(UpdateInfoEntity result) {
                                dismissLoadingDialog();
                                switch (result.getUpdate()) {
                                    case UpdateManager.UpdateStatus.Yes: // has update
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
            case R.id.close_iv:
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mClickLogoCount = 0;

        if (UpdateManager.getInstance(this).isHaveUpdate()) {
            findViewById(R.id.manual_check_update_remind_tv).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.manual_check_update_remind_tv).setVisibility(View.GONE);
        }
    }
}
