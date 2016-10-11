/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import java.util.Date;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.CommonUtils;
import com.easyvaas.common.emoji.utils.SmileUtils;

import com.hooview.app.R;

public class MessageChatAdapterItem implements AdapterItem<EMConversation> {
    private Context mContext;
    private ImageView logoIv;
    private TextView titleTv;
    private TextView subtitleTv;
    private TextView dataTimeTv;
    private TextView unreadCountTv;

    private View.OnCreateContextMenuListener mOnCreateContextMenuListener;
    private View.OnLongClickListener mOnLongClickListener;

    public MessageChatAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_chat_group;
    }

    @Override
    public void onBindViews(View root) {
        logoIv = (ImageView) root.findViewById(R.id.msg_logo_iv);
        titleTv = (TextView) root.findViewById(R.id.msg_title_tv);
        subtitleTv = (TextView) root.findViewById(R.id.msg_subtitle_tv);
        dataTimeTv = (TextView) root.findViewById(R.id.msg_date_time_tv);
        unreadCountTv = (TextView) root.findViewById(R.id.msg_unread_count_tv);

        if (mOnCreateContextMenuListener != null) {
            root.setOnCreateContextMenuListener(mOnCreateContextMenuListener);
        }
        if (mOnLongClickListener != null) {
            root.setOnLongClickListener(mOnLongClickListener);
        }
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(EMConversation conversation, int position) {
        ChatUserUtil.setUserAvatar(mContext, conversation.getUserName(), logoIv);
        titleTv.setText(ChatUserUtil.getNickName(conversation.getUserName(),
                EMChat.getInstance().getAppContext()));
        if (conversation.getMsgCount() > 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            subtitleTv.setText(SmileUtils.getSmiledText(mContext,
                    CommonUtils.getMessageDigest(lastMessage, mContext),
                    mContext.getResources().getDimensionPixelSize(R.dimen.emoji_size_small)),
                    TextView.BufferType.SPANNABLE);
            dataTimeTv.setText(
                    DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND
                    && lastMessage.status == EMMessage.Status.FAIL) {
                ChatUserUtil.setTextLeftDrawable(mContext, subtitleTv, R.drawable.msg_state_failed_resend);
            } else {
                subtitleTv.setCompoundDrawables(null, null, null, null);
            }

            int unreadCount = conversation.getUnreadMsgCount();
            if (unreadCount > 0) {
                String count = unreadCount > 99 ? "99+" : unreadCount + "";
                unreadCountTv.setText(count);
                unreadCountTv.setVisibility(View.VISIBLE);
            } else {
                unreadCountTv.setVisibility(View.GONE);
            }
        }
    }

    public void setOnCreateContextMenuListener(View.OnCreateContextMenuListener l) {
        mOnCreateContextMenuListener = l;
    }

    public void setOnLongClickListener(View.OnLongClickListener l) {
        mOnLongClickListener = l;
    }
}
