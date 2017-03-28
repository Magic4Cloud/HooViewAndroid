package com.easyvaas.elapp.ui.live;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.easyvaas.common.emoji.XhsEmoticonsKeyBoardBar;
import com.easyvaas.common.emoji.utils.EmoticonsUtils;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.view.GiftPagerView;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.dialog.CommonPromptDialog;
import com.easyvaas.elapp.event.ChatLiveInputEvent;
import com.easyvaas.elapp.event.LiveCommentEvent;
import com.easyvaas.elapp.event.LiveSearchStockEvent;
import com.easyvaas.elapp.event.NewCommentEvent;
import com.easyvaas.elapp.event.NewGiftEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.utils.FileUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class BasePlayerActivity extends BaseChatActivity {
    protected GiftPagerView mExpressionGiftLayout;
    protected XhsEmoticonsKeyBoardBar mEmotionKeyBoardBar;
    protected FrameLayout mFlGiftContainer;
    protected String mVideoId;
    protected boolean mIsGoodVideo;
    protected View mBgView;

    private XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener mKeyBoardBarViewListener
            = new XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener() {
        @Override
        public void OnKeyBoardStateChange(int state, int height) {
            Logger.d("guojun", "OnKeyBoardStateChange: state"+state+"  "+height);
        }

        @Override
        public void OnSendBtnClick(String msg, boolean isSearch) {
            mBgView.setVisibility(View.GONE);
            if (isSearch) {
                EventBus.getDefault().post(new LiveSearchStockEvent(msg));
                hideKeyBorder(true);
                return;
            } else {
                if (mIsGoodVideo) {
                    EventBus.getDefault().post(new LiveCommentEvent(msg));
                    hideKeyBorder(true);
                } else {
                    if (mChatHelper == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        ChatComment comment = new ChatComment();
//                if (mReplyCommentEntity != null && msg.startsWith("@")) {
//                    if (!isBarrage) {
//                        msg = msg.replaceAll("@[^@]+:", "");
//                    }
//                    comment.setReply_name(mReplyCommentEntity.getReply_name());
//                    comment.setReply_nickname(mReplyCommentEntity.getReply_nickname());
//                    mReplyCommentEntity = null;
//                } else {
//                }
                        comment.setVid(mVideoId);
                        comment.setId(-1);
                        comment.setReply_name("");
                        comment.setReply_nickname("");
                        comment.setContent(msg);
                        if (EVApplication.getUser() != null) {
                            comment.setName(EVApplication.getUser().getName());
                            comment.setNickname(EVApplication.getUser().getNickname());
                            comment.setLogourl(EVApplication.getUser().getLogourl());
                        }
                        mChatHelper.chatSendComment(comment);
                        hideKeyBorder(true);
                    }
                }
            }

        }

        @Override
        public void onKeyBoardHide() {
            if(mBgView!=null){
                mBgView.setVisibility(View.GONE);
                hideKeyBorder(false);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mExpressionGiftLayout = (GiftPagerView) findViewById(R.id.expression_gift_layout);
        mExpressionGiftLayout.setOnViewClickListener(mOnGiftSendCallBack);
        if(EVApplication.getUser()!=null){
            mExpressionGiftLayout
                    .setSelf(EVApplication.getUser().getNickname(), EVApplication.getUser().getLogourl());
        }
        mEmotionKeyBoardBar = (XhsEmoticonsKeyBoardBar) findViewById(R.id.kv_bar);
        mEmotionKeyBoardBar.setBuilder(EmoticonsUtils.getSimpleBuilder(this));
        mEmotionKeyBoardBar.setOnKeyBoardBarViewListener(mKeyBoardBarViewListener);
        mEmotionKeyBoardBar.setInputMaxLength(XhsEmoticonsKeyBoardBar.MAX_WORD_NUMBER_DEFAULT);
        mEmotionKeyBoardBar.hideInput();
        mFlGiftContainer = (FrameLayout) findViewById(R.id.fl_gift_container);
        mFlGiftContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mFlGiftContainer.setVisibility(View.GONE);
                EventBus.getDefault().post(new ChatLiveInputEvent(ChatLiveInputEvent.ACTION_SHOW_INPUT));
                return true;
            }
        });
        mBgView = (View) findViewById(R.id.bg);
        mBgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyBorder(false);
                mBgView.setVisibility(View.GONE);
                return true;
            }
        });
    }

    @Override
    public void onNewComment(ChatComment chatComment) {
        if (isFinishing() || chatComment == null) {
            return;
        }
        EventBus.getDefault().post(new NewCommentEvent(chatComment));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNewGift(GiftEntity giftEntity) {
        if (isFinishing() || giftEntity == null) {
            return;
        }
        EventBus.getDefault().post(new NewGiftEvent(giftEntity));
    }

    private GiftPagerView.OnGiftSendCallBack mOnGiftSendCallBack
            = new GiftPagerView.OnGiftSendCallBack() {
        @Override
        public void sendGift(final GiftEntity data) {
//            findViewById(R.id.player_bottom_share_btn).setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new NewGiftEvent(data));
            EventBus.getDefault().post(new ChatLiveInputEvent(ChatLiveInputEvent.ACTION_SHOW_INPUT));
            ApiHelper.getInstance().sendGiftString(mVideoId, data.getGiftId(), data.getGiftCount(),
                    false, mCurrentVideo.getName(), new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
//
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            mExpressionGiftLayout.updateAssetInfo(data.getGiftCost());
                        }

                        @Override
                        public void onFailure(String msg) {
                            mExpressionGiftLayout.updateAssetInfo(data.getGiftCost());
                        }
                    });
        }

        @Override
        public void sendBurstGift(final GiftEntity entity) {
//            if (entity == null) {
//                return;
//            }
//            if (!mGiftBurstIb.isShown()) {
//                mGiftBurstIb.setVisibility(View.VISIBLE);
//                findViewById(R.id.player_bottom_share_btn).setVisibility(View.INVISIBLE);
//            }
//            mGiftManager.showLocalAnim(entity);
        }

        @Override
        public void onBurstCountChanged(int burstCount, GiftEntity entity) {
            if (entity == null) {
                return;
            }
            if (burstCount != GiftPagerView.COUNT_BURST_END) {
//                mGiftManager.showLocalAnim(entity);
            }
        }

        @Override
        public void onUpdateView() {
            hideGiftToolsBar();
        }

        @Override
        public void jumpCashInActivity() {
            CashInActivity.start(BasePlayerActivity.this);
        }

        @Override
        public void onECoinChanged(long eCoin) {
            mPref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
        }

        @Override
        public void onECoinNoEnough() {
            showCashInPrompt();
        }
    };

    private CommonPromptDialog mCommonPromptDialog;

    public void showCashInPrompt() {
        if (mCommonPromptDialog == null) {
            mCommonPromptDialog = new CommonPromptDialog(this);
            mCommonPromptDialog.setPrompt(getString(R.string.cash_prompt));
            mCommonPromptDialog.setButtonText(getString(R.string.cancel), getString(R.string.go_cash_in));
            mCommonPromptDialog.setOnButtonClickListen(new CommonPromptDialog.OnButtonClickListener() {
                @Override
                public void onLeftButtonClick(View view) {
                    mCommonPromptDialog.dismiss();
                }

                @Override
                public void onRightButtonClick(View view) {
                    mCommonPromptDialog.dismiss();
                    Intent rechargeIntent = new Intent(BasePlayerActivity.this, CashInActivity.class);
                    startActivity(rechargeIntent);
                }
            });
        }
        mCommonPromptDialog.show();
    }

    protected void hideGiftToolsBar() {
        mFlGiftContainer.setVisibility(View.GONE);
        mExpressionGiftLayout.setVisibility(View.INVISIBLE);
    }

    protected void showGiftToolsBar() {
        mFlGiftContainer.setVisibility(View.VISIBLE);
        mExpressionGiftLayout.setVisibility(View.VISIBLE);
        mEmotionKeyBoardBar.hideInput();
        mExpressionGiftLayout.updateAssetInfo();
    }


    @Override
    public void onBackPressed() {
        if (mExpressionGiftLayout.isShown()) {
            hideGiftToolsBar();
            EventBus.getDefault().post(new ChatLiveInputEvent(ChatLiveInputEvent.ACTION_SHOW_INPUT));
        } else {
            super.onBackPressed();
        }
    }

    protected void sendComment(final long replyCommentId, final String userId, final String nickname) {
//        mEmotionKeyBoardBar.setTextListener(new XhsEmoticonsKeyBoardBar.TextListener() {
//            @Override
//            public String setTextString() {
//                if (mLiveRoomManager.checkIsLiveShutUp(mPref.getUserNickname())) {
//                    SingleToast.show(getApplicationContext(), R.string.msg_have_shut_up);
//                    return "";
//                }
//                if (mPref.getUserNickname().equals(nickname)) {
//                    SingleToast.show(getApplicationContext(), R.string.msg_should_reply_self);
//                    return "";
//                } else {
//                    mReplyCommentEntity = new ChatComment();
//                    mReplyCommentEntity.setId(replyCommentId);
//                    mReplyCommentEntity.setReply_name(userId);
//                    mReplyCommentEntity.setReply_nickname(nickname);
//                    mReplyCommentEntity.setVid(mVideoId);
//                    mEmotionKeyBoardBar.setFirst(true);
//                    mEmotionKeyBoardBar.setFirst2(true);
//                    mEmotionKeyBoardBar.showInput();
//                    return "@" + nickname + ":";
//                }
//            }
//        });
    }

    public void showCommentTextBox(boolean isSearch) {
        mEmotionKeyBoardBar.showInput(isSearch);
        mBgView.setVisibility(View.VISIBLE);

//        toggleProgressBar(false);
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
            content = getString(R.string.share_live_content, mCurrentVideo.getNickname());
        } else if (isSameUser) {
            content = getString(R.string.share_mine_video_content, mCurrentVideo.getNickname());
        } else {
            content = getString(R.string.share_video_content, mCurrentVideo.getNickname());
        }
        ShareContent shareContent = new ShareContentWebpage(mCurrentVideo.getTitle(), content,
                mCurrentVideo.getShare_url(), mCurrentVideo.getShare_thumb_url());
        ShareHelper.getInstance(this).showShareBottomPanel(shareContent);
    }

    public void hideKeyBorder(boolean isClearText){
        if (isClearText) mEmotionKeyBoardBar.clearText();
        mEmotionKeyBoardBar.hideInput();
        EventBus.getDefault().post(new LiveSearchStockEvent(LiveSearchStockEvent.TYPE_KEYBORDER_HIDE, ""));
    }
}
