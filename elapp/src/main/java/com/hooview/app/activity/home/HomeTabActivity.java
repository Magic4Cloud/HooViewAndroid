/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.easemob.chat.EMChatManager;
import com.easyvaas.common.chat.ChatManager;
import com.hooview.app.R;
import com.hooview.app.activity.home.fragment.TabMessageFragment;
import com.hooview.app.activity.home.fragment.TabMyFragment;
import com.hooview.app.activity.home.fragment.TabTimelineMainFragment;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseFragmentActivity;
import com.hooview.app.base.TabManager;
import com.hooview.app.db.Preferences;
import com.hooview.app.live.activity.LivePrepareActivity;
import com.hooview.app.net.ApiUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UpdateManager;

public class HomeTabActivity extends BaseFragmentActivity {
    public static final int TAB_ID_DISCOVER = R.id.tab_timeline;
    public static final int TAB_ID_MINE = R.id.tab_message;

    private static final String TAB_TAG_TIMELINE = Constants.ACTION_GO_HOME_TIMELINE;
    private static final String TAB_TAG_MESSAGE = Constants.ACTION_GO_HOME_MESSAGE;
    private static final String TAB_TAG_MINE = Constants.ACTION_GO_HOME_MINE;
    private static final String KEY_LAST_TAB_ID = "key_last_tab_id";

    private long mLastPressBack;
    private TabManager mTabManager;
    private TabHost mTabHost;
    private LinearLayout mTabBar;
    private Preferences mPref;
    private int mStartRecordCount;
    private int mTabId;

    //底部Tab（左）
    private ImageView mTabTimeLine;

    //底部Tab（右）
    private ImageView mTabMessage;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tab_timeline:
                    mTabTimeLine.setSelected(true);
                    mTabMessage.setSelected(false);
                    showTabByTag(TAB_TAG_TIMELINE);
                    break;
                case R.id.tab_message:
                    mTabMessage.setSelected(true);
                    mTabTimeLine.setSelected(false);
                    showTabByTag(TAB_TAG_MESSAGE);
                    break;
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.ACTION_GO_HOME.equals(action)
                    || Constants.ACTION_GO_HOME_TIMELINE.equals(action)) {
                mTabTimeLine.setSelected(true);
                mTabMessage.setSelected(false);
                showTabByTag(TAB_TAG_TIMELINE);
            } else if (Constants.ACTION_GO_HOME_MESSAGE.equals(action)) {
                mTabTimeLine.setSelected(false);
                mTabMessage.setSelected(true);
            } else if (Constants.ACTION_GO_HOME_MINE.equals(action)) {
                showTabByTag(TAB_TAG_MINE);
                toggleTabBar(false);
            } else if (Constants.ACTION_SHOW_NEW_MESSAGE_ICON.equals(action)) {
                mTabMessage.setImageResource(R.drawable.btn_home_tab_message_unread);
            } else if (Constants.ACTION_HIDE_NEW_MESSAGE_ICON.equals(action)) {
                if (EMChatManager.getInstance().getUnreadMsgsCount() == 0
                        && !TAB_TAG_MESSAGE.equals(mTabHost.getCurrentTabTag())) {
                    mTabMessage.setImageResource(R.drawable.btn_home_tab_message);
                }
            } else if (Constants.ACTION_SHOW_HOME_TAB_BAR.equals(action)) {
                toggleTabBar(true);
            } else if (Constants.ACTION_HIDE_HOME_TAB_BAR.equals(action)) {
                toggleTabBar(false);
            } else if (Constants.ACTION_GO_LOGIN_OUT.equals(action)) {
                finish();
            }
        }
    };

    private void toggleTabBar(boolean show) {
        if (show) {
            if (mTabBar.getVisibility() != View.VISIBLE) {
                mTabBar.setVisibility(View.VISIBLE);
                findViewById(R.id.live_start_ll).setVisibility(View.VISIBLE);
            }
        } else {
            mTabBar.setVisibility(View.GONE);
            findViewById(R.id.live_start_ll).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsActionBarColorStatusBar = true;
        super.onCreate(savedInstanceState);
        EVApplication.getApp().setHaveLaunchedHome(true);

        setContentView(R.layout.activity_home);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.real_tab_content);

        mTabBar = (LinearLayout) findViewById(R.id.tab_bar_radio_group);
        findViewById(R.id.tab_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean haveShowGagTipDialog = mPref
                        .getBoolean(Preferences.KEY_LIVE_GAG_TIPS_DIALOG, true);
                if (haveShowGagTipDialog) {
                    startToLive();
                } else {
                    DialogUtil.checkShowFirstRecordDialog(HomeTabActivity.this,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPref.putBoolean(Preferences.KEY_LIVE_GAG_TIPS_DIALOG, true);
                                    startToLive();
                                }
                            });
                }
            }
        });
        mPref = Preferences.getInstance(this);
        mStartRecordCount = mPref.getInt(Preferences.KEY_SHOW_HOME_LIVE_TIP_COUNT, 0);

        mTabTimeLine = (ImageView) findViewById(R.id.tab_timeline);
        mTabMessage = (ImageView) findViewById(R.id.tab_message);
        mTabTimeLine.setOnClickListener(mOnClickListener);
        mTabMessage.setOnClickListener(mOnClickListener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_GO_HOME);
        filter.addAction(Constants.ACTION_GO_HOME_ACTIVITY);
        filter.addAction(Constants.ACTION_GO_HOME_TIMELINE);
        filter.addAction(Constants.ACTION_GO_HOME_MESSAGE);
        filter.addAction(Constants.ACTION_GO_HOME_MINE);
        filter.addAction(Constants.ACTION_GO_LOGIN_OUT);
        filter.addAction(Constants.ACTION_SHOW_NEW_MESSAGE_ICON);
        filter.addAction(Constants.ACTION_HIDE_NEW_MESSAGE_ICON);
        filter.addAction(Constants.ACTION_SHOW_HOME_TAB_BAR);
        filter.addAction(Constants.ACTION_HIDE_HOME_TAB_BAR);
        registerReceiver(mReceiver, filter);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        mTabId = getIntent().getIntExtra(Constants.EXTRA_KEY_TAB_ID, TAB_ID_DISCOVER);
        ((ImageView) findViewById(mTabId)).setSelected(true);

        if (!mPref.getBoolean(Preferences.KEY_IS_HAVE_SHOW_UPDATE_DIALOG, false)
                && UpdateManager.getInstance(this).isHaveUpdate()) {
            UpdateManager.getInstance(this).checkUpdateAfterSplash();
            mPref.putBoolean(Preferences.KEY_IS_HAVE_SHOW_UPDATE_DIALOG, false);
        }
        showTabByTag(TAB_TAG_TIMELINE);

//        findViewById(R.id.enterHome).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HooViewHomeActivity.launch(HomeTabActivity.this);
//            }
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int tabId = getIntent().getIntExtra(Constants.EXTRA_KEY_TAB_ID, TAB_ID_DISCOVER);
        outState.putInt(KEY_LAST_TAB_ID, tabId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mTabId = savedInstanceState.getInt(KEY_LAST_TAB_ID);
        ((ImageView) findViewById(mTabId)).setSelected(true);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiUtil.checkUnreadMessage(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((ChatManager.getInstance().isHaveUnreadMsg()
                || EMChatManager.getInstance().getUnreadMsgsCount() > 0)
                && !mTabHost.getCurrentTabTag().equals(TAB_TAG_MESSAGE)) {
            mTabMessage.setImageResource(R.drawable.btn_home_tab_message_unread);
        } else {
            mTabMessage.setImageResource(R.drawable.btn_home_tab_message);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabId = intent.getIntExtra(Constants.EXTRA_KEY_TAB_ID, TAB_ID_DISCOVER);
        findViewById(tabId).setSelected(true);
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (!currentTabTag.equals(TAB_TAG_TIMELINE)) {
            toggleTabBar(true);
            mTabTimeLine.setSelected(true);
            mTabMessage.setSelected(false);
            showTabByTag(TAB_TAG_TIMELINE);
        } else if (mLastPressBack + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            SingleToast.show(this, R.string.msg_click_again_to_exit);
            mLastPressBack = System.currentTimeMillis();
            toggleTabBar(true);
        }
    }



    //TODO
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        mPref.putInt(Preferences.KEY_SHOW_HOME_LIVE_TIP_COUNT, mStartRecordCount);
    }

    /**
     * 切换Tag
     *
     * @param tag
     */
    private void showTabByTag(String tag) {
        String tabName = "";
        if (mTabManager.getTabInfoByTag(tag) == null) {
            if (TAB_TAG_TIMELINE.equals(tag)) {
                mTabManager.addTab(mTabHost.newTabSpec(TAB_TAG_TIMELINE)
                        .setIndicator(""), TabTimelineMainFragment.class, null);
                tabName = "TabTimelineMain";
            } else if (TAB_TAG_MESSAGE.equals(tag)) {
                mTabManager.addTab(mTabHost.newTabSpec(TAB_TAG_MESSAGE)
                        .setIndicator(""), TabMessageFragment.class, null);
                tabName = "TabMessage";
            } else if (TAB_TAG_MINE.equals(tag)) {
                mTabManager.addTab(mTabHost.newTabSpec(TAB_TAG_MINE)
                        .setIndicator(""), TabMyFragment.class, null);
            }
        }
        mTabHost.setCurrentTabByTag(tag);
    }

    private void startToLive() {
        startActivity(new Intent(getApplicationContext(), LivePrepareActivity.class));
        mStartRecordCount++;
    }
}
