package com.easyvaas.elapp.ui.live;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.recycler.AnchorCommentRcvAdapter;
import com.easyvaas.elapp.bean.chat.ChatBarrage;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.chat.ChatRedPackInfo;
import com.easyvaas.elapp.bean.chat.ChatUser;
import com.easyvaas.elapp.bean.chat.ChatVideoInfo;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.live.chat.IChatHelper;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.view.gift.GiftViewContainer;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseLiveRoomActivity extends BaseChatActivity implements View.OnClickListener, IChatHelper.ChatCallback {
    private static final String TAG = "BaseLiveRoomActivity";
    protected static final int MSG_ARG_1_FIRST_REFRESH_NET_SPEED = 10;
    protected static final int MSG_REFRESH_START_TIME = 101;
    protected static final int MSG_REFRESH_NATURE_TIME = 103;
    protected static final int MSG_REFRESH_LIVE_NET_SPEED = 104;
    protected static final int MSG_DISMISS_ANCHOR_INFO_VIEW = 202;
    protected static final int MSG_COMMENT_NEW = 2200;
    protected MyHandler mHandler;
    protected RecyclerView mCommentListView;
    protected TextView mTvAssets;
    protected TextView mTvWatchCount;
    protected CommonRcvAdapter mCommentAdapter;
    protected List<ChatComment> mCommentList;
    protected List<ChatComment> mChatComments;
    protected List<ChatComment> mNewJoins;
//    protected List<GiftEntity> mGiftList;
    protected int mLikeCount;
    protected int mCommentCount;
    protected int mWatchCount;
    protected int mWatchingCount;
    protected long mHooviewCoinCount;
    protected long mStartTime;
//    protected RecyclerView mGiftRecyclerView;
//    protected GiftAdapter mGiftAdapter;
    protected boolean isShowEndView;
    protected GiftViewContainer mGiftViewContainer;

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        private static final int PAGE_SIZE = 10;
        private int lastVisiblePosition;
        private long lastCommentId = -1;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            if (newState == RecyclerView.SCROLL_STATE_IDLE
//                    && mCommentAdapter.getItemCount() >= PAGE_SIZE
//                    && lastVisiblePosition + 1 == mCommentAdapter.getItemCount()) {
//                if (mCommentAdapter.getItemId(mCommentAdapter.getItemCount() - 1) == lastCommentId) {
//                    return;
//                }
//                long commentId = mCommentAdapter.getItemId(mCommentAdapter.getItemCount() - 1);
//                lastCommentId = commentId;
//                mChatHelper.chatGetComments(commentId);
//            }
//            if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0
//                    && mNewCommentTipTv.getVisibility() == View.VISIBLE) {
//                showNewComment();
//            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);

        initUIComponents();
        initComponentData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_REFRESH_NATURE_TIME);
        mHandler.removeMessages(MSG_REFRESH_START_TIME);
        mCommentList.clear();
    }

    protected void initView() {

    }

    private void initUIComponents() {
        mCommentListView = (RecyclerView) findViewById(R.id.video_comment_lv);
        mTvAssets = (TextView) findViewById(R.id.tv_assets);
        mTvWatchCount = (TextView) findViewById(R.id.tv_watch_count);
    }

    private void initComponentData() {
        mCommentList = new ArrayList<>();
        mChatComments = new ArrayList<>();
        mNewJoins = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentListView.setLayoutManager(linearLayoutManager);
        mCommentListView.setItemAnimator(new DefaultItemAnimator());
        mCommentListView.setHasFixedSize(true);
        mCommentAdapter = new AnchorCommentRcvAdapter(this, mCommentList);
        mCommentListView.setAdapter(mCommentAdapter);
        mCommentListView.addOnScrollListener(mOnScrollListener);
        mCommentListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        mTvAssets.setText(getString(R.string.hooview_coin_tips2, 0 + ""));
        mTvWatchCount.setText(getString(R.string.watch_count_tips, 0 + ""));
        mGiftViewContainer = (GiftViewContainer) findViewById(R.id.GiftViewContainer);
    }

    /**
     * 分享视频
     */
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
            content = getString(R.string.share_live_content, mCurrentVideo.getNickname());
        } else if (isSameUser) {
            content = getString(R.string.share_mine_video_content, mCurrentVideo.getNickname());
        } else {
            content = getString(R.string.share_video_content, mCurrentVideo.getNickname());
        }
        ShareContent shareContent = new ShareContentWebpage(mCurrentVideo.getTitle(), content,
                mCurrentVideo.getShare_url(), mCurrentVideo.getShare_thumb_url());
        ShareHelper.getInstance(this).showShareRightPannel(shareContent);
    }

    private void showNewComment() {
        if (mChatComments.size() == 0) {
            return;
        }
        mCommentList.addAll(0, mChatComments);
        Logger.d(TAG, "showNewComment: mCommentList size=" + mCommentList.size());
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
        if (mCommentList.size() > 0 && mCommentListView.getVisibility() != View.VISIBLE) {
            mCommentListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectError(String errorInfo) {
    }

    @Override
    public void onJoinOK() {
        if (isFinishing()) {
            return;
        }
        mCommentList.clear();
        mCommentList.add(new ChatComment(ChatComment.TYPE_INTO_TIPS, getString(R.string.live_tips)));
        mCommentAdapter.notifyDataSetChanged();
        Logger.d(TAG, "onJoinOK: ");
    }

    @Override
    public void onNewComment(ChatComment chatComment) {
        if (isFinishing() || chatComment == null) {
            return;
        }
        mChatComments.add(0, chatComment);
        showNewComment();
        Logger.d(TAG, "onNewComment: " + chatComment.toString());
    }

    @Override
    public void onNewGift(GiftEntity giftEntity) {
        ///// TODO: 2017/1/10
        Logger.d(TAG, "onNewGift: " + giftEntity);
//        mGiftList.add(0, giftEntity);
//        mGiftAdapter.notifyDataSetChanged();
//        mGiftRecyclerView.smoothScrollToPosition(0);
        mGiftViewContainer.addAndPlayGift(giftEntity);

    }

    @Override
    public void onNewRedPack(Map<String, ChatRedPackInfo> redPackEntityMap) {
        //无此功能
    }

    @Override
    public void onBarrage(ChatBarrage chatBarrage) {
        //无此功能

    }

    @Override
    public void onInfoUpdate(ChatVideoInfo chatVideoInfo) {
        if (isFinishing()) {
            return;
        }
        Logger.d(TAG, "onInfoUpdate: " + chatVideoInfo.toString());
        if (chatVideoInfo != null) {
            mWatchCount = chatVideoInfo.getWatch_count();
            mCommentCount = chatVideoInfo.getComment_count();
            mLikeCount = chatVideoInfo.getLike_count();
            mWatchingCount = chatVideoInfo.getWatching_count();
            mHooviewCoinCount = chatVideoInfo.getRiceRoll_count();
            mTvAssets.setText(getString(R.string.hooview_coin_tips2, mHooviewCoinCount + ""));
            updateWatchLikeCounts(mWatchingCount);
            // TODO: 2017/1/10
//            if (mLiveRoomManager != null) {
//                mLiveRoomManager.updateRoomInfo(mCommentCount, mLikeCount, mWatchingCount);
//            }
        }
    }

    protected void updateWatchLikeCounts(int watchingCount) {
        mTvWatchCount.setText(getString(R.string.watch_count_tips, watchingCount + ""));
    }

    @Override
    public void onStatusUpdate(int status) {
        Logger.d(TAG, "onStatusUpdate: " + status);

    }

    @Override
    public void onLike(int likeCount) {

    }

    @Override
    public int getLikeCount() {
        return 0;
    }


    @Override
    public void onUserJoinList(List<ChatUser> watchingUsers) {
        Logger.d(TAG, "onUserJoinList: " + watchingUsers.size());
        for (int i = 0; i < watchingUsers.size(); i++) {
            Logger.d(TAG, "onUserJoinList: " + watchingUsers.get(i).toString());
            mChatComments.add(new ChatComment(watchingUsers.get(i).getNickname(), ChatComment.TYPE_INTO_USER_JOIN));
        }
        showNewComment();
    }

    @Override
    public void onUserLeaveList(List<ChatUser> watchingUsers) {
        Logger.d(TAG, "onUserLeaveList: " + watchingUsers.size());

    }

    @Override
    public void onCallRequest(String name, String nickname, String logoUrl) {

    }

    @Override
    public void onCallAccept(String callId) {

    }

    @Override
    public void onCallCancel(String name) {

    }

    @Override
    public void onCallEnd(String name) {

    }

    protected static class MyHandler extends Handler {
        private SoftReference<BaseLiveRoomActivity> softReference;

        public MyHandler(BaseLiveRoomActivity activity) {
            softReference = new SoftReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseLiveRoomActivity activity = softReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_START_TIME:
                    // TODO: 2017/1/10
//                    activity.mDurationTv.setText(DateTimeUtil.getDurationTime(activity,
//                            activity.mStartTime, System.currentTimeMillis()));
//                    sendEmptyMessageDelayed(MSG_REFRESH_START_TIME, 1000);
                    break;
                case MSG_REFRESH_LIVE_NET_SPEED:
                    // TODO: 2017/1/10
//                    long currentTotalRxBytes = NetworkUtil.getTotalRxBytes();
//                    long currentSpeed = currentTotalRxBytes - (Long) msg.obj;
//                    String speed = NetworkUtil.formatNetSpeed(currentSpeed);
//                    String netSpeedTips;
//                    if (activity.mIsPlayLive) {
//                        netSpeedTips = activity.getString(R.string.loading_live_net_speed_tips, speed);
//                    } else {
//                        netSpeedTips = activity.getString(R.string.loading_video_net_speed_tips, speed);
//                    }
//                    if (currentSpeed == 0) {
//                        if (msg.arg1 == MSG_ARG_1_FIRST_REFRESH_NET_SPEED) {
//                            activity.mLiveNetSpeedTv.setText(R.string.loading_video_connect);
//                        } else {
//                            activity.mLiveNetSpeedTv.setText(R.string.loading_video_reconnect);
//                        }
//                    } else {
//                        activity.mLiveNetSpeedTv.setText(netSpeedTips);
//                    }
//                    Message nextMsg = obtainMessage(MSG_REFRESH_LIVE_NET_SPEED, currentTotalRxBytes);
//                    sendMessageDelayed(nextMsg, 1000);
                    break;
//                case MSG_SHOW_JOIN_USER_NAME:
//                    if (activity.mNewJoins.size() == 0) {
//                        return;
//                    }
//                    activity.mUserJoinLl.setVisibility(View.VISIBLE);
//                    ChatComment showingComment = activity.mNewJoins.remove(0);
//                    activity.mUserJoinNameTv.setText(showingComment.getNickname());
//                    sendEmptyMessageDelayed(MSG_HIDE_JOIN_USER_NAME, 800);
//                    break;
//                case MSG_HIDE_JOIN_USER_NAME:
//                    activity.mUserJoinLl.setVisibility(View.INVISIBLE);
//                    sendEmptyMessageDelayed(MSG_SHOW_JOIN_USER_NAME, 200);
//                    break;
//                case MSG_DISMISS_ANCHOR_INFO_VIEW:
//                    activity.hideUserPopupView();
//                break;
//                case MSG_SHOW_RED_PACK_CARD:
//                    Integer redPackIndex = (Integer) msg.obj;
//                    ChatRedPackInfo entity = activity.mRedPackInfoEntities.get(redPackIndex);
//                    activity.showRedPackCard(entity);
//                    sendEmptyMessageDelayed(MSG_HIDE_RED_PACK_CARD, 10 * 1000);
//                    activity.mRedPackUnreadIndexList.remove(redPackIndex);
//                    sendEmptyMessage(MSG_REFRESH_RED_PACK);
//                    break;
//                case MSG_HIDE_RED_PACK_CARD:
//                    activity.mSmallRedPackView.setVisibility(View.GONE);
//                    break;
//                case MSG_REFRESH_RED_PACK:
//                    if (activity.mRedPackUnreadIndexList.size() > 0) {
//                        sendMessage(obtainMessage(MSG_SHOW_RED_PACK_CARD,
//                                activity.mRedPackUnreadIndexList.get(0)));
//                    }
//                    break;
                case MSG_COMMENT_NEW:
//                    NotificationAction action = (NotificationAction) msg.obj;
//                    if (action.isEndAlignment()) {
//                        ChatComment comment = new ChatComment();
//                        if (action.getAnimType() == AnimType.NOTIFICATION) {
//                            comment.setType(ChatComment.TYPE_INTO_GIFT);
//                        } else if (action.getAnimType() == AnimType.EMOJI) {
//                            comment.setType(ChatComment.TYPE_INTO_EXPRESSION);
//                        }
//                        comment.setNickname(action.getSenderName());
//                        comment.setContent(action.getGiftName() + " x " + (action.getIndex() + 1));
//                        activity.onNewComment(comment);
//                    }
            }
            activity.handleMessage(msg);
        }
    }

    protected void showPlayEndView(VideoEntity videoEntity) {

    }


    protected boolean isPlayEndViewShow() {
        return isShowEndView;
    }
}
