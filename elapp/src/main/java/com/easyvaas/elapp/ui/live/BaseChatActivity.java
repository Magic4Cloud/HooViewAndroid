package com.easyvaas.elapp.ui.live;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.chat.ChatBarrage;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.chat.ChatRedPackInfo;
import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.bean.chat.ChatVideoInfo;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.live.chat.ChatHelper;
import com.easyvaas.elapp.live.chat.IChatHelper;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.NetworkUtil;

import java.util.List;
import java.util.Map;

public class BaseChatActivity extends BaseActivity implements IChatHelper.ChatCallback {
    private static final String TAG = "BaseChatActivity";
    protected IChatHelper mChatHelper;
    protected PowerManager.WakeLock mWakeLock;
    protected VideoEntity mCurrentVideo;
    protected Dialog m4GNetworkTipDialog;
    protected Preferences mPref;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(Constants.ACTION_CLOSE_CURRENT_VIDEO_PLAYER)) {
                if (!isFinishing()) {
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireWakeLock();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        mPref = Preferences.getInstance(this);

        IntentFilter intent = new IntentFilter();
        intent.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intent.setPriority(1000);
        boolean haveShow4GTip = mPref.getBoolean(Preferences.KEY_HAVE_SHOW_4G_TIP, false);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CLOSE_CURRENT_VIDEO_PLAYER);
        registerReceiver(mBroadcastReceiver, intentFilter);
        if (NetworkUtil.NETWORK_TYPE_WIFI != NetworkUtil.getNetworkType(this) && !haveShow4GTip) {
            mPref.putBoolean(Preferences.KEY_HAVE_SHOW_4G_TIP, true);
            m4GNetworkTipDialog = DialogUtil.showNetworkRemindDialog(this);
            m4GNetworkTipDialog.findViewById(R.id.dialog_continue_tv)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m4GNetworkTipDialog.dismiss();
                        }
                    });
            m4GNetworkTipDialog.findViewById(R.id.dialog_cancel_tv)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m4GNetworkTipDialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    protected void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatServerInit(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatServerDestroy();
        dismissLoadingDialog();
    }

    protected void chatServerDestroy() {
        if (mChatHelper != null) {
            mChatHelper.chatServerDestroy();
        }
    }

    protected void chatServerInit(boolean isResetData) {
        if (mChatHelper != null) {
            mChatHelper.chatServerInit(isResetData);
        } else if (mCurrentVideo != null) {
            mChatHelper = new ChatHelper(this, mCurrentVideo.getVid(), this);
            mChatHelper.chatServerInit(isResetData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_FROM_SPLASH, false)
                || getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_FROM_PUSH, false))
                && !EVApplication.getApp().isHaveLaunchedHome()) {
            Intent intent = new Intent(this, HomeTabActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        dismissLoadingDialog();

        if (m4GNetworkTipDialog != null && m4GNetworkTipDialog.isShowing()) {
            m4GNetworkTipDialog.dismiss();
        }
        m4GNetworkTipDialog = null;
        releaseWakeLock();
    }

    @Override
    public void onConnectError(String errorInfo) {
        Logger.e("xmzd", "Chat---onConnectError: " + errorInfo);
    }

    @Override
    public void onJoinOK() {
        Logger.e("xmzd", "onJoinOK: ");

    }

    @Override
    public void onNewComment(ChatComment chatComment) {
        Logger.e("xmzd", "onNewComment: " + chatComment.toString());

    }

    @Override
    public void onNewGift(GiftEntity giftEntity) {
        Logger.e("xmzd", "onNewGift: " + giftEntity.toString());

    }

    @Override
    public void onNewRedPack(Map<String, ChatRedPackInfo> redPackEntityMap) {
        Logger.e("xmzd", "onNewRedPack: ");

    }

    @Override
    public void onBarrage(ChatBarrage chatBarrage) {
        Logger.e("xmzd", "onBarrage: ");

    }

    @Override
    public void onInfoUpdate(ChatVideoInfo chatVideoInfo) {
        Logger.e("xmzd", "onInfoUpdate: ");

    }

    @Override
    public void onStatusUpdate(int status) {
        Logger.e("xmzd", "onStatusUpdate: ");

    }

    @Override
    public void onLike(int likeCount) {
        Logger.e("xmzd", "onLike: " + likeCount);
    }

    @Override
    public int getLikeCount() {
        Logger.e("xmzd", "getLikeCount");
        return 0;
    }

    @Override
    public void onUserJoinList(List<ChatUser> watchingUsers) {
        Logger.e("xmzd", "onUserJoinList");
    }

    @Override
    public void onUserLeaveList(List<ChatUser> watchingUsers) {
        Logger.e("xmzd", "onUserLeaveList");
    }

    @Override
    public void onCallRequest(String name, String nickname, String logoUrl) {
        Logger.e("xmzd", "onCallRequest");
    }

    @Override
    public void onCallAccept(String callId) {
        Logger.e("xmzd", "onCallAccept");
    }

    @Override
    public void onCallCancel(String name) {
        Logger.e("xmzd", "onCallCancel");
    }

    @Override
    public void onCallEnd(String name) {
        Logger.e("xmzd", "onCallEnd");
    }
}
