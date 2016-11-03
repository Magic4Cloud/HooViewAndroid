/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.live.activity;

import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.easyvaas.common.emoji.XhsEmoticonsKeyBoardBar;
import com.easyvaas.common.emoji.utils.EmoticonsUtils;
import com.easyvaas.common.gift.GiftManager;
import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.view.GiftPagerView;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.widget.MyRecyclerView;
import com.easyvaas.common.widget.MyUserPhoto;
import com.hooview.app.activity.HooViewHomeActivity;
import com.hooview.app.activity.pay.CashInActivity;
import com.hooview.app.adapter.WatchingUserAdapter;
import com.hooview.app.adapter.item.CommentAdapterItem;
import com.hooview.app.adapter.recycler.CommentRcvAdapter;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BasePlayerActivity;
import com.hooview.app.bean.chat.ChatBarrage;
import com.hooview.app.bean.chat.ChatComment;
import com.hooview.app.bean.chat.ChatRedPackInfo;
import com.hooview.app.bean.chat.ChatUser;
import com.hooview.app.bean.chat.ChatVideoInfo;
import com.hooview.app.bean.pay.MyAssetEntity;
import com.hooview.app.bean.user.User;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.live.chat.ChatHelper;
import com.hooview.app.live.chat.IChatHelper;
import com.hooview.app.live.manager.LiveRoomManager;
import com.hooview.app.live.manager.LiveRoomShutUpUtil;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.DialogUtil;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.NetworkUtil;
import com.hooview.app.utils.ShareHelper;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ViewUtil;
import com.hooview.app.utils.blur.StackBlurManager;
import com.hooview.app.view.BarrageAnimationView;
import com.hooview.app.view.bubble.BubbleView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rx.functions.Action1;

class LiveRoomBaseActivity extends BasePlayerActivity
        implements View.OnClickListener, IChatHelper.ChatCallback {
    private static final String TAG = "LiveRoomBaseActivity";

    protected static final int MSG_ARG_1_FIRST_REFRESH_NET_SPEED = 10;
    private static final int MSG_SHOW_RED_PACK_CARD = 22;
    private static final int MSG_SHOW_JOIN_USER_NAME = 23;
    private static final int MSG_HIDE_JOIN_USER_NAME = 24;
    protected static final int MSG_REFRESH_START_TIME = 101;
    protected static final int MSG_REFRESH_NATURE_TIME = 103;
    protected static final int MSG_REFRESH_LIVE_NET_SPEED = 104;
    private static final int MSG_REFRESH_RED_PACK = 105;
    private static final int MSG_HIDE_RED_PACK_CARD = 106;
    protected static final int MSG_DISMISS_ANCHOR_INFO_VIEW = 202;
    protected static final int MSG_COMMENT_NEW = 2200;

    private PowerManager.WakeLock mWakeLock;
    protected TextView mWatchCountInfoTv;
    protected TextView mNicknameTv;
    protected TextView mVideoTitleTv;
    protected TextView mDurationTv;
    protected MyUserPhoto mUserLogoIv;
    protected MyRecyclerView mWatchingUserRecyclerView;
    protected BubbleView mBubbleView;
    private View mPlayEndView;
    protected View mAnchorInfoPopupView;
    protected Dialog mUserInfoDialog;
    protected Dialog mReportDialog;
    protected Dialog m4GNetworkTipDialog;
    protected LinearLayout mUserJoinLl;
    protected TextView mUserJoinNameTv;

    protected RecyclerView mCommentListView;
    protected CommentRcvAdapter mCommentAdapter;
    protected List<ChatComment> mCommentList;
    protected List<ChatComment> mChatComments;
    protected List<ChatComment> mNewJoins;
    protected ChatComment mReplyCommentEntity;
    protected List<ChatRedPackInfo> mRedPackInfoEntities;
    protected List<Integer> mRedPackUnreadIndexList;

    protected List<ChatUser> mWatchingUsers;
    protected WatchingUserAdapter mWatchingAdapter;

    protected Preferences mPref;
    protected Random mRandom;
    protected String mVideoId;
    protected VideoEntity mCurrentVideo;
    protected MyHandler mHandler;
    protected int mLikeCount;
    protected int mCommentCount;
    protected int mWatchCount;
    protected int mWatchingCount;
    protected long mRiceRollCount;
    protected long mStartTime;
    protected boolean mBackPressed;
    protected IChatHelper mChatHelper;
    protected XhsEmoticonsKeyBoardBar mEmotionKeyBoardBar;
    protected View mLoadingView;
    protected ImageView mPlayerBottomShareBtn;
    private Dialog mRedPackDialog;
    private ViewGroup mSmallRedPackView;
    private TextView mRiceRollCountTv;
    private View mRiceTicketLL;
    private ImageButton mGiftBurstIb;
    private GiftManager mGiftManager;
    protected GiftPagerView mExpressionGiftLayout;
    private TextView mNewCommentTipTv;
    private TextView mLiveNetSpeedTv;
    protected ViewFlipper mViewFlipper;
    protected View mTopInfoAreaView;
    protected boolean mIsPlayLive;
    protected BarrageAnimationView mBarrageAnimationView;

    private int mViewSize = 0;
    private GestureDetector mGestureDetector;
    private LiveRoomManager mLiveRoomManager;

    /**
     * Activity之间的交互,结束掉直播页面
     */
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

    //开源表情包 ----->https://github.com/w446108264/XhsEmoticonsKeyboard
    private XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener mKeyBoardBarViewListener
            = new XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener() {
        @Override
        public void OnKeyBoardStateChange(int state, int height) {

        }

        @Override
        public void OnSendBtnClick(String msg, boolean isBarrage) {
            if (mChatHelper == null) {
                return;
            }
            if (!TextUtils.isEmpty(msg)) {
                ChatComment comment = new ChatComment();
                if (mReplyCommentEntity != null && msg.startsWith("@")) {
                    if (!isBarrage) {
                        msg = msg.replaceAll("@[^@]+:", "");
                    }
                    comment.setReply_name(mReplyCommentEntity.getReply_name());
                    comment.setReply_nickname(mReplyCommentEntity.getReply_nickname());
                    mReplyCommentEntity = null;
                } else {
                    comment.setVid(mVideoId);
                    comment.setId(-1);
                    comment.setReply_name("");
                    comment.setReply_nickname("");
                }
                comment.setContent(msg);
                comment.setName(EVApplication.getUser().getName());
                comment.setNickname(EVApplication.getUser().getNickname());
                comment.setLogourl(EVApplication.getUser().getLogourl());
                if (isBarrage) {
                    mChatHelper.chatSendBarrage(comment);
                } else {
                    mChatHelper.chatSendComment(comment);
                }
                mEmotionKeyBoardBar.clearText();
                mEmotionKeyBoardBar.hideInput();
            }
        }

    };

    private Action1<NotificationAction> mOnGiftNotification = new Action1<NotificationAction>() {
        @Override
        public void call(NotificationAction notificationAction) {
            if (notificationAction.getAnimType() == AnimType.EMOJI) {
                if (notificationAction.getFromType() == FromType.LOCAL) {
                    mBubbleView.addEmoji(notificationAction.getGiftPicture().url(), 1);
                } else {
                    mBubbleView.addEmoji(notificationAction.getGiftPicture().url(),
                            notificationAction.getIndex() + 1);
                }
            }

            updateGiftComment(notificationAction);
        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        private static final int PAGE_SIZE = 10;
        private int lastVisiblePosition;
        private long lastCommentId = -1;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && mCommentAdapter.getItemCount() >= PAGE_SIZE
                    && lastVisiblePosition + 1 == mCommentAdapter.getItemCount()) {
                if (mCommentAdapter.getItemId(mCommentAdapter.getItemCount() - 1) == lastCommentId) {
                    return;
                }
                long commentId = mCommentAdapter.getItemId(mCommentAdapter.getItemCount() - 1);
                lastCommentId = commentId;
                mChatHelper.chatGetComments(commentId);
            }
            if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0
                    && mNewCommentTipTv.getVisibility() == View.VISIBLE) {
                showNewComment();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
        }
    };

    private void initComponentData() {
        mRandom = new Random();
        mCommentList = new ArrayList<>();
        mChatComments = new ArrayList<>();
        mNewJoins = new ArrayList<>();
        mRedPackInfoEntities = new ArrayList<>();
        mRedPackUnreadIndexList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentListView.setLayoutManager(linearLayoutManager);
        mCommentListView.setItemAnimator(new DefaultItemAnimator());
        mCommentListView.setHasFixedSize(true);
        mCommentAdapter = new CommentRcvAdapter(this, mCommentList);
        mCommentListView.setAdapter(mCommentAdapter);
        mCommentListView.addOnScrollListener(mOnScrollListener);
        mCommentListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mCommentAdapter.setOnCommentViewClickListener(new CommentAdapterItem.OnItemViewClickListener() {
            @Override
            public void onNameClick(View view, ChatComment entity) {
                showUserInfoPopUpView(entity.getName());
                toggleProgressBar(false);
            }

            @Override
            public void onContentClick(View view, ChatComment entity) {
                toggleProgressBar(false);
                if (entity.getType() == ChatComment.TYPE_INTO_RED_PACK) {
                    int index = (int) entity.getId();
                    if (index < mRedPackInfoEntities.size()) {
                        Message msg = mHandler.obtainMessage(MSG_SHOW_RED_PACK_CARD, index);
                        mHandler.sendMessage(msg);
                    }
                } else {
                    sendComment(entity.getId(), entity.getName(), entity.getNickname());
                }
            }
        });
    }

    protected void toggleProgressBar(boolean isShow) {
    }

    protected void resetChatData() {
        mCommentList.clear();
        mChatComments.clear();
        mNewJoins.clear();
        mRedPackInfoEntities.clear();
        mRedPackUnreadIndexList.clear();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConnectError(String errorInfo) {
    }

    @Override
    public void onJoinOK() {
        if (isFinishing()) {
            return;
        }
        mWatchingUsers.clear();
        mWatchingAdapter.notifyDataSetChanged();
        mCommentList.clear();
        mCommentAdapter.notifyDataSetChanged();
    }

    public void hideFloatingView() {
        hideGiftToolsBar();
        hideRedPackGift();
        hideLeftSlideClearGuide();
        hideGiftToolsBar();
        hideUserPopupView();
    }

    @Override
    public void onNewComment(ChatComment chatComment) {
        if (isFinishing() || chatComment == null) {
            return;
        }
        mChatComments.add(0, chatComment);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mCommentListView.getLayoutManager();

        if (linearLayoutManager.findLastVisibleItemPosition() > 7
                && mExpressionGiftLayout.getVisibility() == View.INVISIBLE) {
            if (!mExpressionGiftLayout.isShown()) {
                mNewCommentTipTv.setVisibility(View.VISIBLE);
            }
            mNewCommentTipTv.setText(getString(com.hooview.app.R.string.video_comment_new_count, mChatComments.size()));
        } else {
            showNewComment();
        }

    }

    @Override
    public void onBarrage(ChatBarrage chatBarrage) {
        List<ChatBarrage> barrageList = new ArrayList<>();
        barrageList.add(chatBarrage);
        mBarrageAnimationView.execute(barrageList);
    }

    @Override
    public void onUserJoinList(List<ChatUser> watchingUsers) {
        mWatchingAdapter.addAll(watchingUsers);
        if (mWatchingAdapter.getItemCount() > 9) {
            mWatchingUserRecyclerView.scrollToPosition(mWatchingAdapter.getItemCount() - 1);
        }
        mNewJoins.clear();
        List<ChatComment> chatComments = new ArrayList<>();
        for (ChatUser entity : watchingUsers) {
            ChatComment chatComment = new ChatComment();
            chatComment.setNickname(entity.getNickname());
            chatComment.setType(ChatComment.TYPE_INTO_TIPS);
            chatComment.setCountDown(ChatComment.TIME_COUNT_DOWN);
            chatComments.add(chatComment);
            mNewJoins.addAll(chatComments);
        }
        if (!mHandler.hasMessages(MSG_SHOW_JOIN_USER_NAME)) {
            mHandler.sendEmptyMessage(MSG_SHOW_JOIN_USER_NAME);
        }
    }

    @Override
    public void onUserLeaveList(List<ChatUser> watchingUsers) {
        List<ChatUser> removeList = new ArrayList<>();
        for (int i = 0, n = mWatchingUsers.size(); i < n; i++) {
            for (ChatUser entity : watchingUsers) {
                if (entity.getName().equals(mWatchingUsers.get(i).getName())) {
                    removeList.add(mWatchingUsers.get(i));
                }
            }
        }
        mWatchingUsers.removeAll(removeList);
        mWatchingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInfoUpdate(final ChatVideoInfo chatVideoInfo) {
        if (isFinishing()) {
            return;
        }
        if (chatVideoInfo != null) {
            mWatchCount = chatVideoInfo.getWatch_count();
            mCommentCount = chatVideoInfo.getComment_count();
            mLikeCount = chatVideoInfo.getLike_count();
            mWatchingCount = chatVideoInfo.getWatching_count();
            mRiceRollCount = chatVideoInfo.getRiceRoll_count();
            //mRiceRollCountTv.setText(mRiceRollCount + "");
            updateWatchLikeCounts(mWatchingCount);
            if (mLiveRoomManager != null) {
                mLiveRoomManager.updateRoomInfo(mCommentCount, mLikeCount, mWatchingCount);
            }
        }
    }

    @Override
    public void onStatusUpdate(int status) {
    }

    @Override
    public void onLike(int likeCount) {
        if (isFinishing()) {
            return;
        }
        mBubbleView.addLikeCount(likeCount);
    }

    @Override
    public int getLikeCount() {
        if (isFinishing()) {
            return 0;
        }
        int likeCount = mBubbleView.getLikeCount();
        mBubbleView.setLikeCount(0);
        return likeCount;
    }

    public void onNewGift(GiftEntity gift) {
        if (!mPref.getUserNumber().equals(gift.getName())) {
            List<GiftEntity> gifts = new ArrayList<>();
            mGiftManager.showRemoteAnim(gifts);
        }
    }

    @Override
    public void onNewRedPack(Map<String, ChatRedPackInfo> redPackEntityMap) {
        ChatRedPackInfo redPackEntity;
        for (Map.Entry<String, ChatRedPackInfo> entry : redPackEntityMap.entrySet()) {
            redPackEntity = entry.getValue();
            if (redPackEntity.isNewRedPack()) {
                mRedPackInfoEntities.add(redPackEntity);
                mRedPackUnreadIndexList.add(mRedPackInfoEntities.size() - 1);
            } else {
                for (ChatRedPackInfo entity : mRedPackInfoEntities) {
                    if ((entity.getId()).equals(entry.getKey())) {
                        entity.getUsers().addAll(redPackEntityMap.get(entity.getId()).getUsers());
                        break;
                    }
                }
            }
        }
        if (mRedPackUnreadIndexList.size() > 0) {
            Message msg = mHandler.obtainMessage(MSG_SHOW_RED_PACK_CARD, mRedPackUnreadIndexList.get(0));
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case com.hooview.app.R.id.live_close_iv:
                finish();
                break;
            case com.hooview.app.R.id.player_user_logo_iv:
                if (mAnchorInfoPopupView.getVisibility() == View.VISIBLE) {
                    mAnchorInfoPopupView.setVisibility(View.GONE);
                } else {
                    if (mCurrentVideo != null) {
                        showUserInfoPopUpView(mCurrentVideo.getName());
                    }
                }
                break;
            case com.hooview.app.R.id.player_bottom_comment_btn:
                showCommentTextBox();
                break;
            case com.hooview.app.R.id.player_bottom_share_btn:
                shareVideo();
                break;
            case com.hooview.app.R.id.player_report_btn:
                hideUserPopupView();
                DialogUtil.showReportVideoDialog(this, mCurrentVideo.getName());
                break;
//            case com.hooview.app.R.id.rice_ticket_ll:
//                if (mRiceRollCount > 0) {
//                    Intent contributorIntent = new Intent(this, RiceRollContributorListActivity.class);
//                    contributorIntent.putExtra(RiceRollContributorListActivity.RICE_ROLL_USER_NAME,
//                            mCurrentVideo.getName());
//                    startActivity(contributorIntent);
//                }
//                break;
            case com.hooview.app.R.id.comment_new_count_tv:
                showNewComment();
                break;
            case com.hooview.app.R.id.player_anchor_follow_tv:
                ApiHelper.getInstance().userFollow(mCurrentVideo.getName(), true,
                        mCurrentVideo.getVid(), new MyRequestCallBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_follow_success);
                                view.setVisibility(View.GONE);
                                if (mCurrentVideo != null) {
                                    mCurrentVideo.setFollowed(User.FOLLOWED);
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
                break;
        }
    }

    protected void initView() {

    }


    protected void shareVideo() {
        if (mCurrentVideo == null) {
            return;
        }
        if (mCurrentVideo.getTitle() == null) {
            mCurrentVideo.setTitle("");
        }
        if (mCurrentVideo.getShare_thumb_url() == null) {
            String shareImage = getFilesDir() + File.separator + FileUtil.LOGO_FILE_NAME;
            mCurrentVideo.setShare_thumb_url(shareImage);
        }
        String content = "";
        boolean isSameUser = mCurrentVideo.getName().equals(mPref.getUserNumber());
        if (mCurrentVideo.getLiving() == VideoEntity.IS_LIVING) {
            content = getString(com.hooview.app.R.string.share_live_content, mCurrentVideo.getNickname());
        } else if (isSameUser) {
            content = getString(com.hooview.app.R.string.share_mine_video_content, mCurrentVideo.getNickname());
        } else {
            content = getString(com.hooview.app.R.string.share_video_content, mCurrentVideo.getNickname());
        }
        ShareContent shareContent = new ShareContentWebpage(mCurrentVideo.getTitle(), content,
                mCurrentVideo.getShare_url(), mCurrentVideo.getShare_thumb_url());
        ShareHelper.getInstance(this).showShareBottomPanel(shareContent);
    }

    protected static class MyHandler extends Handler {
        private SoftReference<LiveRoomBaseActivity> softReference;

        public MyHandler(LiveRoomBaseActivity activity) {
            softReference = new SoftReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final LiveRoomBaseActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_START_TIME:
                    activity.mDurationTv.setText(DateTimeUtil.getDurationTime(activity,
                            activity.mStartTime, System.currentTimeMillis()));
                    sendEmptyMessageDelayed(MSG_REFRESH_START_TIME, 1000);
                    break;
                case MSG_REFRESH_LIVE_NET_SPEED:
                    long currentTotalRxBytes = NetworkUtil.getTotalRxBytes();
                    long currentSpeed = currentTotalRxBytes - (Long) msg.obj;
                    String speed = NetworkUtil.formatNetSpeed(currentSpeed);
                    String netSpeedTips;
                    if (activity.mIsPlayLive) {
                        netSpeedTips = activity.getString(com.hooview.app.R.string.loading_live_net_speed_tips, speed);
                    } else {
                        netSpeedTips = activity.getString(com.hooview.app.R.string.loading_video_net_speed_tips, speed);
                    }
                    if (currentSpeed == 0) {
                        if (msg.arg1 == MSG_ARG_1_FIRST_REFRESH_NET_SPEED) {
                            activity.mLiveNetSpeedTv.setText(com.hooview.app.R.string.loading_video_connect);
                        } else {
                            activity.mLiveNetSpeedTv.setText(com.hooview.app.R.string.loading_video_reconnect);
                        }
                    } else {
                        activity.mLiveNetSpeedTv.setText(netSpeedTips);
                    }
                    Message nextMsg = obtainMessage(MSG_REFRESH_LIVE_NET_SPEED, currentTotalRxBytes);
                    sendMessageDelayed(nextMsg, 1000);
                    break;
                case MSG_SHOW_JOIN_USER_NAME:
                    if (activity.mNewJoins.size() == 0) {
                        return;
                    }
                    activity.mUserJoinLl.setVisibility(View.VISIBLE);
                    ChatComment showingComment = activity.mNewJoins.remove(0);
                    activity.mUserJoinNameTv.setText(showingComment.getNickname());
                    sendEmptyMessageDelayed(MSG_HIDE_JOIN_USER_NAME, 800);
                    break;
                case MSG_HIDE_JOIN_USER_NAME:
                    activity.mUserJoinLl.setVisibility(View.INVISIBLE);
                    sendEmptyMessageDelayed(MSG_SHOW_JOIN_USER_NAME, 200);
                    break;
                case MSG_DISMISS_ANCHOR_INFO_VIEW:
                    activity.hideUserPopupView();
                    break;
                case MSG_SHOW_RED_PACK_CARD:
                    Integer redPackIndex = (Integer) msg.obj;
                    ChatRedPackInfo entity = activity.mRedPackInfoEntities.get(redPackIndex);
                    activity.showRedPackCard(entity);
                    sendEmptyMessageDelayed(MSG_HIDE_RED_PACK_CARD, 10 * 1000);
                    activity.mRedPackUnreadIndexList.remove(redPackIndex);
                    sendEmptyMessage(MSG_REFRESH_RED_PACK);
                    break;
                case MSG_HIDE_RED_PACK_CARD:
                    activity.mSmallRedPackView.setVisibility(View.GONE);
                    break;
                case MSG_REFRESH_RED_PACK:
                    if (activity.mRedPackUnreadIndexList.size() > 0) {
                        sendMessage(obtainMessage(MSG_SHOW_RED_PACK_CARD,
                                activity.mRedPackUnreadIndexList.get(0)));
                    }
                    break;
                case MSG_COMMENT_NEW:
                    NotificationAction action = (NotificationAction) msg.obj;
                    if (action.isEndAlignment()) {
                        ChatComment comment = new ChatComment();
                        if (action.getAnimType() == AnimType.NOTIFICATION) {
                            comment.setType(ChatComment.TYPE_INTO_GIFT);
                        } else if (action.getAnimType() == AnimType.EMOJI) {
                            comment.setType(ChatComment.TYPE_INTO_EXPRESSION);
                        }
                        comment.setNickname(action.getSenderName());
                        comment.setContent(action.getGiftName() + " x " + (action.getIndex() + 1));
                        activity.onNewComment(comment);
                    }
            }
            activity.handleMessage(msg);
        }
    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if (motionEvent == null || motionEvent1 == null || !isFlipping()) {
                return false;
            }
            hideLeftSlideClearGuide();
            if (motionEvent.getX() - motionEvent1.getX() > 120) {
                if (mViewSize < 1) {
                    mViewSize++;
//                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//                            com.hooview.app.R.anim.pannel_right_in));
//                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//                            com.hooview.app.R.anim.pannel_left_out));
                    mViewFlipper.showNext();
                    mBubbleView.setEnabled(false);
                }
                return true;
            } else if (motionEvent.getX() - motionEvent1.getX() < -120) {
                if (mViewSize > 0) {
                    mViewSize--;
//                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//                            com.hooview.app.R.anim.pannel_left_in));
//                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
//                            com.hooview.app.R.anim.pannel_right_out));
                    mViewFlipper.showPrevious();
                    if (mCurrentVideo != null && !mCurrentVideo.getName()
                            .equals(Preferences.getInstance(getApplicationContext()).getUserNumber())) {
                        mBubbleView.setEnabled(true);
                    }
                }
                return true;
            }
            return false;
        }
    };

    private GiftPagerView.OnGiftSendCallBack mOnGiftSendCallBack
            = new GiftPagerView.OnGiftSendCallBack() {
        @Override
        public void sendGift(final GiftEntity data) {
            mGiftBurstIb.setVisibility(View.GONE);
            findViewById(com.hooview.app.R.id.player_bottom_share_btn).setVisibility(View.VISIBLE);
            mGiftManager.showLocalAlignmentsEndAnim(data);
            ApiHelper.getInstance().sendGift(mVideoId, data.getGiftId(), data.getGiftCount(),
                    false, mCurrentVideo.getName(), new MyRequestCallBack<MyAssetEntity>() {
                        @Override
                        public void onSuccess(MyAssetEntity result) {
                            if (result != null) {
                                mPref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, result.getEcoin());
                                GiftManager.setECoinCount(getApplicationContext(), result.getEcoin());
                                mExpressionGiftLayout.updateAssetInfo();
                            }
                        }

                        @Override
                        public void onFailure(String msg) {

                        }
                    });
        }

        @Override
        public void sendBurstGift(final GiftEntity entity) {
            if (entity == null) {
                return;
            }
            if (!mGiftBurstIb.isShown()) {
                mGiftBurstIb.setVisibility(View.VISIBLE);
                findViewById(com.hooview.app.R.id.player_bottom_share_btn).setVisibility(View.INVISIBLE);
            }
            mGiftManager.showLocalAnim(entity);
        }

        @Override
        public void onBurstCountChanged(int burstCount, GiftEntity entity) {
            if (entity == null) {
                return;
            }
            if (burstCount != GiftPagerView.COUNT_BURST_END) {
                mGiftManager.showLocalAnim(entity);
            }
        }

        @Override
        public void onUpdateView() {
            hideGiftToolsBar();
        }

        @Override
        public void jumpCashInActivity() {
            Intent rechargeIntent = new Intent(LiveRoomBaseActivity.this, CashInActivity.class);
            startActivity(rechargeIntent);
        }

        @Override
        public void onECoinChanged(long eCoin) {
            mPref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SingleToast.show(this,getClass().getSimpleName());
        acquireWakeLock();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(com.hooview.app.R.layout.activity_player);

        mGestureDetector = new GestureDetector(this, mOnGestureListener);
        mHandler = new MyHandler(this);
        mPref = Preferences.getInstance(this);

        initUIComponents();
        initComponentData();

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
            m4GNetworkTipDialog.findViewById(com.hooview.app.R.id.dialog_continue_tv)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m4GNetworkTipDialog.dismiss();
                        }
                    });
            m4GNetworkTipDialog.findViewById(com.hooview.app.R.id.dialog_cancel_tv)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m4GNetworkTipDialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    private void initUIComponents() {
        mLoadingView = findViewById(com.hooview.app.R.id.video_loaded);
        mLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mLiveNetSpeedTv = (TextView) findViewById(com.hooview.app.R.id.loading_net_speed_tv);
        mViewFlipper = (ViewFlipper) findViewById(com.hooview.app.R.id.viewFlipper);

        addViewFlipper();
    }

    protected boolean isFlipping() {
        return false;
    }

    private void addViewFlipper() {
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        ViewGroup operationView = (ViewGroup) inflater
                .inflate(com.hooview.app.R.layout.activity_player_operation_pannel, root, false);
        View clearView = inflater.inflate(com.hooview.app.R.layout.activity_player_clear_pannel, root, false);
        mViewFlipper.addView(operationView, 0);
        mViewFlipper.addView(clearView, 1);
        initFloatingView(operationView);
    }

    private void initFloatingView(ViewGroup view) {
        mTopInfoAreaView = view.findViewById(com.hooview.app.R.id.live_info_rl);
        mUserLogoIv = (MyUserPhoto) view.findViewById(com.hooview.app.R.id.player_user_logo_iv);
        mAnchorInfoPopupView = view.findViewById(com.hooview.app.R.id.live_user_info_show_rl);
        mAnchorInfoPopupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        mNicknameTv = (TextView) view.findViewById(com.hooview.app.R.id.player_nickname_tv);
        mVideoTitleTv = (TextView) view.findViewById(com.hooview.app.R.id.video_title_tv);
        mDurationTv = (TextView) view.findViewById(com.hooview.app.R.id.player_duration_tv);
        mWatchCountInfoTv = (TextView) view.findViewById(com.hooview.app.R.id.player_watch_info_count_tv);
        mWatchingUserRecyclerView = (MyRecyclerView) view.findViewById(com.hooview.app.R.id.watching_user_rv);
        mCommentListView = (RecyclerView) view.findViewById(com.hooview.app.R.id.video_comment_lv);
        mUserJoinLl = (LinearLayout) view.findViewById(com.hooview.app.R.id.user_join_ll);
        LayoutTransition joinTransition = new LayoutTransition();
        joinTransition.setAnimator(LayoutTransition.APPEARING,
                AnimatorInflater.loadAnimator(this, com.hooview.app.R.animator.fade_in));
        joinTransition.setAnimator(LayoutTransition.DISAPPEARING,
                AnimatorInflater.loadAnimator(this, com.hooview.app.R.animator.fade_out));
        mUserJoinLl.setLayoutTransition(joinTransition);
        mUserJoinNameTv = (TextView) view.findViewById(com.hooview.app.R.id.user_join_name_tv);
        mBubbleView = (BubbleView) view.findViewById(com.hooview.app.R.id.bubble_view);
        mEmotionKeyBoardBar = (XhsEmoticonsKeyBoardBar) view.findViewById(com.hooview.app.R.id.kv_bar);
        mEmotionKeyBoardBar.setBuilder(EmoticonsUtils.getSimpleBuilder(this));
        mEmotionKeyBoardBar.setOnKeyBoardBarViewListener(mKeyBoardBarViewListener);
        mEmotionKeyBoardBar.setInputMaxLength(XhsEmoticonsKeyBoardBar.MAX_WORD_NUMBER_DEFAULT);
        mEmotionKeyBoardBar.hideInput();
        mExpressionGiftLayout = (GiftPagerView) view.findViewById(com.hooview.app.R.id.expression_gift_layout);
        mExpressionGiftLayout.setOnViewClickListener(mOnGiftSendCallBack);
        mExpressionGiftLayout
                .setSelf(EVApplication.getUser().getNickname(), EVApplication.getUser().getLogourl());
        mBarrageAnimationView = (BarrageAnimationView) view.findViewById(com.hooview.app.R.id.show_barrage_animation_GAV);
        view.findViewById(com.hooview.app.R.id.live_gift_iv).setOnClickListener(this);
        mRiceRollCountTv = (TextView) view.findViewById(com.hooview.app.R.id.rice_roll_count_tv);
        mRiceRollCountTv.setText("0");
        mRiceTicketLL = view.findViewById(com.hooview.app.R.id.rice_ticket_ll);
        mRiceTicketLL.setOnClickListener(this);

        //TODO 隐藏
        mRiceTicketLL.setVisibility(View.GONE);
        view.findViewById(com.hooview.app.R.id.live_gift_iv).setVisibility(View.GONE);

        mGiftManager = new GiftManager((ViewGroup) view.findViewById(com.hooview.app.R.id.container_fl),
                mOnGiftNotification);
        mGiftBurstIb = (ImageButton) view.findViewById(com.hooview.app.R.id.burst_iv);
        mGiftBurstIb.setOnClickListener(this);

        mUserLogoIv.setOnClickListener(this);
        view.findViewById(com.hooview.app.R.id.player_bottom_comment_btn).setOnClickListener(this);
        mPlayerBottomShareBtn = (ImageView) view.findViewById(com.hooview.app.R.id.player_bottom_share_btn);
        mPlayerBottomShareBtn.setOnClickListener(this);
        view.findViewById(com.hooview.app.R.id.player_report_btn).setOnClickListener(this);
        view.findViewById(com.hooview.app.R.id.player_bottom_progress_btn).setOnClickListener(this);
        mNewCommentTipTv = (TextView) view.findViewById(com.hooview.app.R.id.comment_new_count_tv);
        mNewCommentTipTv.setSelected(true);
        mNewCommentTipTv.setOnClickListener(this);
        initView();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toolBtnView = inflater.inflate(com.hooview.app.R.layout.view_toolbtn_right_simple, null);
        toolBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmotionKeyBoardBar.del();
            }
        });

        initWatchingUserList();
    }

    private void initWatchingUserList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        mWatchingUserRecyclerView.setLayoutManager(linearLayoutManager);
        mWatchingUserRecyclerView.setOnItemScrollChangeListener(
                new MyRecyclerView.OnItemScrollChangeListener() {
                    @Override
                    public void onChange(View view, int position) {
                    }
                });

        mWatchingUsers = new ArrayList<>();
        mWatchingAdapter = new WatchingUserAdapter(this, mWatchingUsers);
        mWatchingAdapter.setHasStableIds(true);
        mWatchingAdapter.setOnItemClickListener(new WatchingUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                toggleProgressBar(false);
                if (position < mWatchingUsers.size()) {
                    showUserInfoPopUpView(mWatchingUsers.get(position).getName());
                }
            }
        });

        mWatchingUserRecyclerView.setAdapter(mWatchingAdapter);
    }

    protected void handleMessage(Message msg) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_FROM_SPLASH, false)
                || getIntent().getBooleanExtra(Constants.EXTRA_KEY_IS_FROM_PUSH, false))
                && !EVApplication.getApp().isHaveLaunchedHome()) {
            //TODO 替换之前的HomeTabActivity的入口
            Intent intent = new Intent(this, HooViewHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        dismissLoadingDialog();
        mHandler.removeMessages(MSG_REFRESH_NATURE_TIME);
        mHandler.removeMessages(MSG_REFRESH_START_TIME);
        if (mUserInfoDialog != null && mUserInfoDialog.isShowing()) {
            mUserInfoDialog.dismiss();
        }
        mUserInfoDialog = null;
        if (mRedPackDialog != null && mRedPackDialog.isShowing()) {
            mRedPackDialog.dismiss();
        }
        mRedPackDialog = null;
        if (mReportDialog != null && mReportDialog.isShowing()) {
            mReportDialog.dismiss();
        }
        mReportDialog = null;
        if (m4GNetworkTipDialog != null && m4GNetworkTipDialog.isShowing()) {
            m4GNetworkTipDialog.dismiss();
        }
        m4GNetworkTipDialog = null;
        mCommentList.clear();
        mWatchingUsers.clear();
        releaseWakeLock();
    }

    @Override
    public void onBackPressed() {
        if (hideLeftSlideClearGuide()) {
            return;
        }
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (mUserInfoDialog != null && mUserInfoDialog.isShowing()) {
            mUserInfoDialog.dismiss();
        } else if (mExpressionGiftLayout.isShown()) {
            hideGiftToolsBar();
        } else {
            mBackPressed = true;
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatServerDestroy();
        dismissLoadingDialog();
    }

    protected void showUserInfoPopUpView(String userId) {
        hideGiftToolsBar();
        toggleProgressBar(false);
        hideUserPopupView();
        if (mLiveRoomManager == null) {
            mLiveRoomManager = new LiveRoomManager(this, mCurrentVideo, mRoomManagerListener);
        }
        mLiveRoomManager.showUserInfoPopView(userId, mAnchorInfoPopupView);
    }

    protected void hideUserPopupView() {
        if (mAnchorInfoPopupView != null) {
            mAnchorInfoPopupView.setVisibility(View.GONE);
        }
    }

    protected void hideGiftToolsBar() {
        mExpressionGiftLayout.setVisibility(View.INVISIBLE);
        findViewById(com.hooview.app.R.id.player_bottom_action_bar).setVisibility(View.VISIBLE);
        mCommentListView.setVisibility(View.VISIBLE);
    }

    protected void showGiftToolsBar() {
        mExpressionGiftLayout.setVisibility(View.VISIBLE);
        mEmotionKeyBoardBar.hideInput();
        mExpressionGiftLayout.updateAssetInfo();
        mGiftBurstIb.setVisibility(View.GONE);
        findViewById(com.hooview.app.R.id.player_bottom_share_btn).setVisibility(View.VISIBLE);
        findViewById(com.hooview.app.R.id.player_bottom_action_bar).setVisibility(View.INVISIBLE);
        mCommentListView.setVisibility(View.INVISIBLE);
    }

    private LiveRoomManager.RoomManagerListener mRoomManagerListener
            = new LiveRoomManager.RoomManagerListener() {
        @Override
        public void setManager(String name, boolean setManager) {
            mChatHelper.chatLiveSetManager(name, setManager);
        }

        @Override
        public void setShutUp(String name, boolean shutUp) {
            mChatHelper.chatLiveOwnerShutUp(name, shutUp);
            hideUserPopupView();
        }

        @Override
        public void reportUser(String name) {
            mReportDialog = DialogUtil.showReportUserDialog(LiveRoomBaseActivity.this, name);
            hideUserPopupView();
        }

        @Override
        public void sendComment(long replyCommentId, String name, String nickname) {
            LiveRoomBaseActivity.this.sendComment(0, name, nickname);
            hideUserPopupView();
        }

        @Override
        public void followAnchor() {
            hideUserPopupView();
            findViewById(com.hooview.app.R.id.player_anchor_follow_tv).setVisibility(View.GONE);
        }

        @Override
        public void unFollowAnchor() {
            hideUserPopupView();
            findViewById(com.hooview.app.R.id.player_anchor_follow_tv).setVisibility(View.VISIBLE);
        }
    };

    protected void sendComment(final long replyCommentId, final String userId, final String nickname) {
        mEmotionKeyBoardBar.setTextListener(new XhsEmoticonsKeyBoardBar.TextListener() {
            @Override
            public String setTextString() {
                if (LiveRoomShutUpUtil.checkIsLiveShutUp(mPref.getUserNickname(), mVideoId)) {
                    SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_have_shut_up);
                    return "";
                }
                if (mPref.getUserNickname().equals(nickname)) {
                    //SingleToast.show(getApplicationContext(), com.hooview.app.R.string.msg_should_reply_self);
                    return "";
                } else {
                    mReplyCommentEntity = new ChatComment();
                    mReplyCommentEntity.setId(replyCommentId);
                    mReplyCommentEntity.setReply_name(userId);
                    mReplyCommentEntity.setReply_nickname(nickname);
                    mReplyCommentEntity.setVid(mVideoId);
                    mEmotionKeyBoardBar.setFirst(true);
                    mEmotionKeyBoardBar.setFirst2(true);
                    mEmotionKeyBoardBar.showInput();
                    return "@" + nickname + ":";
                }
            }
        });
    }

    protected void updateWatchLikeCounts(int watchingCount) {
        mWatchCountInfoTv.setText(getString(com.hooview.app.R.string.unit_person, watchingCount));
    }

    private void showNewComment() {
        if (mChatComments.size() == 0) {
            return;
        }
        mCommentList.addAll(0, mChatComments);
        LinearLayoutManager lm = (LinearLayoutManager) mCommentListView.getLayoutManager();
        if (mCommentList.size() < 5) {
            if (lm.getStackFromEnd()) {
                lm.setStackFromEnd(false);
            }
        } else {
            if (!lm.getStackFromEnd()) {
                lm.setStackFromEnd(true);
            }
        }
        mCommentAdapter.notifyDataSetChanged();
        mCommentListView.smoothScrollToPosition(0);
        mChatComments.clear();
        if (mCommentList.size() > 0 && mCommentListView.getVisibility() != View.VISIBLE
                && mExpressionGiftLayout.getVisibility() == View.INVISIBLE) {
            mCommentListView.setVisibility(View.VISIBLE);
        }
        mNewCommentTipTv.setVisibility(View.GONE);
    }

    private boolean hideLeftSlideClearGuide() {
        View leftSlideGuideRootView = findViewById(com.hooview.app.R.id.left_slide_guide_root_rl);
        if (leftSlideGuideRootView != null && leftSlideGuideRootView.isShown()) {
            leftSlideGuideRootView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void showRedPackCard(final ChatRedPackInfo entity) {
        class ViewHolder {
            TextView title;
            TextView message;
            TextView type;
        }

        ViewHolder holder;
        if (mSmallRedPackView == null) {
            mSmallRedPackView = (ViewGroup) findViewById(com.hooview.app.R.id.red_pack_card_rl);
            holder = new ViewHolder();
            holder.title = (TextView) mSmallRedPackView.findViewById(com.hooview.app.R.id.red_pack_card_title_tv);
            holder.message = (TextView) mSmallRedPackView.findViewById(com.hooview.app.R.id.red_pack_card_content_tv);
            holder.type = (TextView) mSmallRedPackView.findViewById(com.hooview.app.R.id.red_pack_card_type_tv);
            mSmallRedPackView.setTag(holder);
            float x = mRiceTicketLL.getX();
            float y = mRiceTicketLL.getY() + mRiceTicketLL.getHeight() + ViewUtil.dp2Px(this, 10);
            mSmallRedPackView.setX(x);
            mSmallRedPackView.setY(y);
            LayoutTransition transition = new LayoutTransition();
            transition.setAnimator(LayoutTransition.APPEARING,
                    AnimatorInflater.loadAnimator(this, com.hooview.app.R.animator.fade_in));
            transition.setAnimator(LayoutTransition.DISAPPEARING,
                    AnimatorInflater.loadAnimator(this, com.hooview.app.R.animator.fade_out));
            mSmallRedPackView.setLayoutTransition(transition);
            mSmallRedPackView.setVisibility(View.VISIBLE);
        } else {
            mSmallRedPackView.setVisibility(View.VISIBLE);
            holder = (ViewHolder) mSmallRedPackView.getTag();
        }
        mSmallRedPackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmallRedPackView.setVisibility(View.INVISIBLE);
                showRedPackDialog(entity);
            }
        });

        holder.message.setText(entity.getName());
        int typeRes = -1;
        switch (entity.getType()) {
            case ChatRedPackInfo.TYPE_SYSTEM:
                holder.title.setText(getString(com.hooview.app.R.string.app_name));
                typeRes = com.hooview.app.R.string.red_pack_type_system;
                break;
            case ChatRedPackInfo.TYPE_WATCHER:
                holder.title.setText(entity.getSenderNickName());
                typeRes = com.hooview.app.R.string.red_pack_type_watcher;
                break;
            case ChatRedPackInfo.TYPE_OWNER:
                holder.title.setText(entity.getSenderNickName());
                typeRes = com.hooview.app.R.string.red_pack_type_owner;
                break;
        }
        holder.type.setText(typeRes);
    }

    private void showRedPackDialog(ChatRedPackInfo entity) {
        if (mRedPackDialog != null && mRedPackDialog.isShowing()) {
            mRedPackDialog.dismiss();
        }
        mRedPackDialog = DialogUtil.getRedPackDialog(this, entity,
                mVideoId, new DialogUtil.OpenRedPackListener() {
                    Dialog dialog = DialogUtil.getProcessDialog(LiveRoomBaseActivity.this,
                            getString(com.hooview.app.R.string.red_pack_opening), false, true);

                    @Override
                    public void onOpen() {
                        dialog.show();
                    }

                    @Override
                    public void onOpenSuccess() {
                        dialog.dismiss();
                        mExpressionGiftLayout.updateAssetInfo();
                    }

                    @Override
                    public void onOpenFailed() {
                        dialog.dismiss();
                        SingleToast.show(LiveRoomBaseActivity.this, com.hooview.app.R.string.red_pack_open_failed);
                    }
                }
        );
        mRedPackDialog.show();
    }

    private void acquireWakeLock() {
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
    public boolean onTouchEvent(MotionEvent event) {
        hideLeftSlideClearGuide();
        hideGiftToolsBar();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mEmotionKeyBoardBar.hideInput();
                hideUserPopupView();
                break;
        }
        return this.mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatServerInit(false);
    }

    protected void chatServerInit(boolean isResetData) {
        if (mChatHelper != null) {
            mChatHelper.chatServerInit(isResetData);
        } else if (mCurrentVideo != null) {
            mChatHelper = new ChatHelper(this, mCurrentVideo.getVid(), this);
            mChatHelper.chatServerInit(isResetData);
        }
    }

    protected void chatServerDestroy() {
        if (mChatHelper != null) {
            mChatHelper.chatServerDestroy();
        }
    }

    private void updateGiftComment(final NotificationAction action) {
        Message msg = mHandler.obtainMessage(MSG_COMMENT_NEW, action);
        mHandler.sendMessage(msg);
    }

    protected void checkShowGuide() {
    }

    protected void showAllInfoViews(boolean isRecording) {
        findViewById(com.hooview.app.R.id.live_info_rl).setVisibility(View.VISIBLE);
        findViewById(com.hooview.app.R.id.bubble_view).setVisibility(View.VISIBLE);
        if (isRecording) {
            Utils.setTextLeftDrawable(this, mDurationTv, com.hooview.app.R.drawable.live_point_red_shape);
        } else {
            mTopInfoAreaView.findViewById(com.hooview.app.R.id.live_close_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            final ImageView loadingBgIv = (ImageView) mLoadingView.findViewById(com.hooview.app.R.id.loading_bg_iv);
            Utils.showImage(this, mCurrentVideo.getThumb(), com.hooview.app.R.drawable.video_loading_bg, new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    StackBlurManager blurManager = new StackBlurManager(bitmap);
                    loadingBgIv.setImageBitmap(blurManager.process(12));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    loadingBgIv.setImageResource(com.hooview.app.R.drawable.video_loading_bg);

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    //loadingBgIv.setImageResource(com.hooview.app.R.drawable.video_loading_bg);
                }
            });
            findViewById(com.hooview.app.R.id.player_report_btn).setVisibility(View.VISIBLE);
            View followAnchor = findViewById(com.hooview.app.R.id.player_anchor_follow_tv);
            followAnchor.setOnClickListener(this);
            if (mCurrentVideo.isFollowed() == User.FOLLOWED
                    || mPref.getUserNumber().equals(mCurrentVideo.getName())) {
                followAnchor.setVisibility(View.GONE);
            } else {
                followAnchor.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void showCommentTextBox() {
        mEmotionKeyBoardBar.showInput();
        toggleProgressBar(false);
    }

    protected void showPlayEndView(final VideoEntity currentVideo) {
        if (currentVideo == null) {
            finish();
        }
        if (mPlayEndView == null) {
            mPlayEndView = ((ViewStub) findViewById(com.hooview.app.R.id.live_end_view_stub)).inflate();
            mPlayEndView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else {
            mPlayEndView.setVisibility(View.VISIBLE);
        }
        if (mLiveRoomManager == null) {
            mLiveRoomManager = new LiveRoomManager(this, mCurrentVideo, mRoomManagerListener);
        }
        toggleProgressBar(false);
        mLiveRoomManager.showPlayEndView(mPlayEndView);
    }

    protected boolean isPlayEndViewShow() {
        return mPlayEndView != null && mPlayEndView.isShown();
    }

    private void hideRedPackGift() {
        mExpressionGiftLayout.exceptRedPackGift();
    }
}
