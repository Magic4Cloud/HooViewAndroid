package com.easyvaas.elapp.ui.live.livenew;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.easyvaas.elapp.adapter.live.ChatMessageAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.event.AppBarLayoutOffsetChangeEvent;
import com.easyvaas.elapp.event.ImageTextLiveMessageEvent;
import com.easyvaas.elapp.event.JoinRoomSuccessEvent;
import com.easyvaas.elapp.ui.live.BaseImageTextLiveFragment;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.ImageTextLiveInputView;
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

public class ChatMessageFragment extends BaseImageTextLiveFragment {
    private static final String TAG = "ImageTextLiveChatFragment";
    public static final String EXTRA_IS_ANCHOR = "is_anchor";
    private RecyclerView mRecyclerView;
    private LinkedList<EMMessageWrapper> mEMMessageList;
    private String mRoomId;
    private EMConversation mEMConversation;
    protected int pagesize = 20;
    private User mUser;
    private ChatMessageAdapter mAdapter;
    private ImageTextLiveInputView mImageTextLiveInputView;
    private boolean isAnchor;


    public static ChatMessageFragment newInstance(boolean isAnchor, String roomId) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ANCHOR, isAnchor);
        args.putString(EXTRA_CHAT_ROOM_ID, roomId);
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
        mUser = EVApplication.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_text_chat, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEMMessageList = new LinkedList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcv_msg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new ChatMessageAdapter(getActivity(), mEMMessageList));
        mImageTextLiveInputView = (ImageTextLiveInputView) view.findViewById(R.id.imageTextLiveInputView);
        if (!isAnchor) {
            mImageTextLiveInputView.hideOptionBar();
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mImageTextLiveInputView.getLayoutParams();
            layoutParams1.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156);
            mImageTextLiveInputView.setLayoutParams(layoutParams1);
        }
        mImageTextLiveInputView.setInputViewListener(new ImageTextLiveInputView.InputViewListener() {
            @Override
            public void sendMessage(String msgType, String content) {
                sendMsg(content, msgType);
            }

            @Override
            public void onImageButtonClick() {

            }

            @Override
            public void onReply(String content, ImageTextLiveInputView.ReplyModel replyModel) {
                reply(content, replyModel);
            }
        });
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

    private void sendMsg(String content, String msgType) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_TYPE, msgType);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_NICKNAME, mUser.getNickname());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_NICKNAME, "");
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_CONTENT, "");
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_AVATAR, mUser.getLogourl());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_USER_ID, mUser.getName());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_VIP, String.valueOf(mUser.getVip()));
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("Misuzu","-----onSuccess");
            }

            @Override
            public void onError(int i, String s) {
                Log.d("Misuzu","-----onError---"+s);
            }

            @Override
            public void onProgress(int i, String s) {
                Log.d("Misuzu","-----onProgress---"+s);

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
        if (isAnchor) {
            uploadChatMsg(new EMMessageWrapper(message));
        }
    }

    private void reply(String content, ImageTextLiveInputView.ReplyModel replyModel) {
        EMMessage message = EMMessage.createTxtSendMessage(content, mRoomId);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_NICKNAME, mUser.getNickname());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_NICKNAME, replyModel.replyNickName + "1");
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_REPLY_CONTENT, replyModel.replyContent);
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_AVATAR, mUser.getLogourl());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_USER_ID, mUser.getName());
        message.setAttribute(EMMessageWrapper.EXTRA_MSG_VIP, String.valueOf(mUser.getVip()));
        EMClient.getInstance().chatManager().sendMessage(message);
        List<EMMessage> list = new ArrayList<>();
        list.add(message);
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
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
        if (msgCount < mEMConversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            mEMConversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }

    private void handleMsg(EMMessage message) {
        if (message.getType() != EMMessage.Type.TXT) {
            return;
        }
        EMMessageWrapper wrapper = new EMMessageWrapper(message);
        wrapper.isSelf = message.getFrom().equals(mUser.getName());
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppBarLayoutOffsetChangeEvent event) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mImageTextLiveInputView.getLayoutParams();
        layoutParams.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156) + event.offset;
        mImageTextLiveInputView.setLayoutParams(layoutParams);
    }

    private void handleCmdMsg() {
        //do nothing
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
