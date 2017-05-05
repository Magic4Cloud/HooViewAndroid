package com.easyvaas.elapp.ui.live.livenew;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.view.GiftPagerView;
import com.easyvaas.elapp.adapter.live.ChatMessageAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.chat.model.ChatRecord;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.dialog.CommonPromptDialog;
import com.easyvaas.elapp.event.AppBarLayoutOffsetChangeEvent;
import com.easyvaas.elapp.event.ImageTextLiveMessageEvent;
import com.easyvaas.elapp.event.JoinRoomSuccessEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.live.BaseImageTextLiveFragment;
import com.easyvaas.elapp.ui.live.ImageTextLiveActivity;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.ChatInputView;
import com.hooview.app.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Date    2017/5/3
 * Author  xiaomao
 * 聊天界面
 */
public class ChatMessageFragment extends BaseImageTextLiveFragment {

    private static final String TAG = ChatMessageFragment.class.getSimpleName();
    public static final String EXTRA_IS_ANCHOR = "is_anchor";

    @BindView(R.id.chat_recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.chat_input_view)
    ChatInputView mChatInputView;
    @BindView(R.id.chat_gift_container_fl)
    FrameLayout mGiftContainer;
    @BindView(R.id.chat_gift_pager_view)
    GiftPagerView mGiftPagerVIew;

    private Unbinder mUnbinder;
    private LinkedList<EMMessageWrapper> mEMMessageList;
    private String mRoomId;
    private EMConversation mEMConversation;
    protected int mPageSize = 20;
    private User mUser;
    private ChatMessageAdapter mAdapter;
    private boolean isAnchor;
    private TextLiveListModel.StreamsEntity mStreamsEntity;


    public static ChatMessageFragment newInstance(boolean isAnchor, String roomId, TextLiveListModel.StreamsEntity streamsEntity) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ANCHOR, isAnchor);
        args.putString(EXTRA_CHAT_ROOM_ID, roomId);
        args.putSerializable(EXTRA_STREEM, streamsEntity);
        ChatMessageFragment fragment = new ChatMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        isAnchor = getArguments().getBoolean(EXTRA_IS_ANCHOR, false);
        mRoomId = getArguments().getString(EXTRA_CHAT_ROOM_ID, "");
        mStreamsEntity = (TextLiveListModel.StreamsEntity) getArguments().getSerializable(EXTRA_STREEM);
        mUser = EVApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_message, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mEMMessageList = new LinkedList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter = new ChatMessageAdapter(getActivity(), mEMMessageList));

        if (!isAnchor) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mChatInputView.getLayoutParams();
            mChatInputView.setLayoutParams(layoutParams);
            // gift
            RelativeLayout.LayoutParams giftParams = (RelativeLayout.LayoutParams) mGiftContainer.getLayoutParams();
            mGiftContainer.setLayoutParams(layoutParams);
        }
        mChatInputView.setAnchor(isAnchor);
        mChatInputView.setOnInputListener(new ChatInputView.OnInputListener() {
            @Override
            public void onSendMessage(String type, String message) {
                sendMsg(message, type);
            }

            @Override
            public void onSendGift() {
                showGiftToolsBar();
            }

            @Override
            public void onReplyMessage(String message, ChatInputView.ReplyModel replyModel) {
                reply(message, replyModel);
            }
        });
        // 主播可以回复somebody
        if (isAnchor) {
            mAdapter.setOnReplyListener(new ChatMessageAdapter.OnReplyListener() {
                @Override
                public void onReply(EMMessageWrapper wrapper) {
                    if (!TextUtils.isEmpty(wrapper.replyNickname) || !TextUtils.isEmpty(wrapper.replyContent)) {
                        return;
                    }
                    if (wrapper.nickname != null && mUser != null && !wrapper.nickname.equals(mUser.getNickname())) {
                        mChatInputView.replySomebody(wrapper.nickname, wrapper.content);
                    }
                }
            });
        }
        // gift
        mGiftContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGiftContainer.setVisibility(View.GONE);
                return true;
            }
        });
        mGiftPagerVIew.setOnViewClickListener(mOnGiftSendCallBack);
        if (EVApplication.getUser() != null) {
            mGiftPagerVIew.setSelf(EVApplication.getUser().getNickname(), EVApplication.getUser().getLogourl());
        }
        /*if (isAnchor) {
            mMsgAdapter.setOnItemLongClickListener(new CommonRcvAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, int position) {
                    EMMessageWrapper messageWrapper = mEMMessageList.get(position);
                    Logger.d(TAG, "onItemLongClick: messageWrapper=" + messageWrapper.toString() + "    mUser nickName=" + mUser.getNickname());
                    if (!TextUtils.isEmpty(messageWrapper.replyNickname) || !TextUtils.isEmpty(messageWrapper.replyContent)) {
                        return true;
                    }
                    if (!messageWrapper.nickname.equals(mUser.getNickname())) {
                        mImageTextLiveInputView.replySomebody(messageWrapper.nickname, messageWrapper.content);
                    }
                    return true;
                }
            });
        }*/
    }

    private GiftPagerView.OnGiftSendCallBack mOnGiftSendCallBack = new GiftPagerView.OnGiftSendCallBack() {
        @Override
        public void sendGift(final GiftEntity data) {
            if (mStreamsEntity == null || mUser == null || data == null) {
                return;
            }
            sendGiftMsg(mUser.getNickname(), data.getGiftName(), data.getGiftCount());
            ApiHelper.getInstance().sendGiftString(mRoomId, data.getGiftId(), data.getGiftCount(),
                    false, mStreamsEntity.getUserEntity().getName(), new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                            mGiftPagerVIew.updateAssetInfo(data.getGiftCost());
                        }

                        @Override
                        public void onFailure(String msg) {
                            mGiftPagerVIew.updateAssetInfo(data.getGiftCost());
                        }
                    });
        }

        @Override
        public void sendBurstGift(final GiftEntity entity) {
        }

        @Override
        public void onBurstCountChanged(int burstCount, GiftEntity entity) {
        }

        @Override
        public void onUpdateView() {
            hideGiftToolsBar();
        }

        @Override
        public void jumpCashInActivity() {
            CashInActivity.start(getActivity());
        }

        @Override
        public void onECoinChanged(long eCoin) {
//            mPref.putLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT, eCoin);
        }

        @Override
        public void onECoinNoEnough() {
            showCashInPrompt();
        }
    };

    private CommonPromptDialog mCommonPromptDialog;

    public void showCashInPrompt() {
        if (mCommonPromptDialog == null) {
            mCommonPromptDialog = new CommonPromptDialog(getActivity());
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
                    Intent rechargeIntent = new Intent(getActivity(), CashInActivity.class);
                    startActivity(rechargeIntent);
                }
            });
        }
        mCommonPromptDialog.show();
    }

    private void sendMsg(String content, String msgType) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_TYPE, msgType);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_NICKNAME, mUser == null ? "" : mUser.getNickname());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_NICKNAME, "");
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_CONTENT, "");
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_AVATAR, mUser == null ? "" : mUser.getLogourl());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_USER_ID, mUser == null ? "" : mUser.getName());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_VIP, String.valueOf(mUser == null ? "0" : mUser.getVip()));
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("xmzd", "send message-----onSuccess");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("xmzd", "send message-----onError" + s);
            }

            @Override
            public void onProgress(int i, String s) {
                Log.e("xmzd", "send message-----onProgress---" + s);

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
        // 同步
        synRealm(mUser == null ? "" : mUser.getName(), mUser == null ? "" : mUser.getNickname(), mUser == null ? "" : mUser.getLogourl());
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
        if (isAnchor && !EMMessageWrapper.MSG_TYPE_JOIN.equals(msgType)) {
            uploadChatMsg(new EMMessageWrapper(message));
        }
    }

    private void reply(String content, ChatInputView.ReplyModel replyModel) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_NICKNAME, mUser == null ? "" : mUser.getNickname());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_NICKNAME, replyModel.nickname);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_CONTENT, replyModel.message);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_AVATAR, mUser == null ? "" : mUser.getLogourl());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_USER_ID, mUser == null ? "" : mUser.getName());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_VIP, String.valueOf(mUser == null ? "0" : mUser.getVip()));
        EMClient.getInstance().chatManager().sendMessage(message);
        // 同步
        synRealm(mUser == null ? "" : mUser.getName(), mUser == null ? "" : mUser.getNickname(), mUser == null ? "" : mUser.getLogourl());
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
        if (isAnchor) {
            uploadChatMsg(new EMMessageWrapper(message));
        }
    }

    private void sendGiftMsg(String nk, String gnm, int count) {
        EMMessage message = EMMessage.createTxtSendMessage(gnm + "*" + count, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_TYPE, EMMessageWrapper.MSG_TYPE_GIFT);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_NICKNAME, nk);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_AVATAR, mUser == null ? "" : mUser.getLogourl());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_USER_ID, mUser == null ? "" : mUser.getName());
        EMClient.getInstance().chatManager().sendMessage(message);
        // 同步
        synRealm(mUser == null ? "" : mUser.getName(), nk, mUser == null ? "" : mUser.getLogourl());
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
    }

    private void synRealm(String userId, String nickname, String avatr) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        ChatRecord record = new ChatRecord(avatr, nickname, userId);
        ChatRecord bean = RealmHelper.getInstance().queryChatRecord(userId);
        if (bean == null) {
            RealmHelper.getInstance().insertChatRecord(record);
        } else if (userId.equals(bean.getId())){
            if ((nickname != null && !nickname.equals(bean.getNickname())) || (avatr != null && !avatr.equals(bean.getAvatar()))) {
                RealmHelper.getInstance().deleteChatRecord(userId);
                RealmHelper.getInstance().insertChatRecord(record);
            }
        }
    }

    private void onMessageListInit() {

    }

    private void initConversation() {
        mEMConversation = EMClient.getInstance().chatManager().getConversation(mRoomId, EMConversation.EMConversationType.ChatRoom, true);
        if (mEMConversation == null) {
            return;
        }
        mEMConversation.markAllMessagesAsRead();
        final List<EMMessage> msgs = mEMConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < mEMConversation.getAllMsgCount() && msgCount < mPageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mEMConversation.loadMoreMsgFromDB(msgId, mPageSize - msgCount);
        }
    }

    private void handleMsg(EMMessage message) {
        if (message.getType() != EMMessage.Type.TXT) {
            return;
        }
        EMMessageWrapper wrapper = new EMMessageWrapper(message);
        wrapper.isSelf = message.getFrom().equals(mUser == null ? "" : mUser.getName());
        // 进入聊天室时屏蔽自己
        if (wrapper.isSelf && EMMessageWrapper.MSG_TYPE_JOIN.equals(wrapper.type)) {
            return;
        }
        mEMMessageList.addLast(wrapper);
        Logger.d(TAG, "handleMsg: " + message.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageTextLiveMessageEvent event) {
        Logger.d(TAG, "onMessageEvent: " + event.type);
        if (event.type == ImageTextLiveMessageEvent.MSG_TYPE_CMD) {
            handleCmdMsg();
        } else if (event.type == ImageTextLiveMessageEvent.MSG_TYPE_MESSAGE) {
            EMMessage message;
            for (int i = 0; i < event.mEMMessageList.size(); i++) {
                message = event.mEMMessageList.get(i);
                handleMsg(message);
            }
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mEMMessageList.size());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JoinRoomSuccessEvent event) {
        initConversation();
        onMessageListInit();
        // 加入聊天室
        sendMsg("join...", EMMessageWrapper.MSG_TYPE_JOIN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppBarLayoutOffsetChangeEvent event) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mChatInputView.getLayoutParams();
        layoutParams.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156) + event.offset;
        mChatInputView.setLayoutParams(layoutParams);
        // gift
        RelativeLayout.LayoutParams giftParams = (RelativeLayout.LayoutParams) mGiftContainer.getLayoutParams();
        giftParams.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156) + event.offset;
        mGiftContainer.setLayoutParams(layoutParams);
    }

    private void handleCmdMsg() {
        //do nothing
    }

    /**
     * 隐藏礼物面板
     */
    protected void hideGiftToolsBar() {
        mGiftContainer.setVisibility(View.GONE);
        mGiftPagerVIew.setVisibility(View.INVISIBLE);
        if (getActivity() instanceof ImageTextLiveActivity) {
            ((ImageTextLiveActivity) getActivity()).setGiftShown(false);
        }
    }

    /**
     * 显示礼物面板
     */
    protected void showGiftToolsBar() {
        mGiftContainer.setVisibility(View.VISIBLE);
        mGiftPagerVIew.setVisibility(View.VISIBLE);
        mGiftPagerVIew.updateAssetInfo();
        if (getActivity() instanceof ImageTextLiveActivity) {
            ((ImageTextLiveActivity) getActivity()).setGiftShown(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
