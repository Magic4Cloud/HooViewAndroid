/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.view.View;

import com.easyvaas.common.adapter.AdapterItem;
import com.hyphenate.chat.EMConversation;


public class MessageChatAdapterItem implements AdapterItem<EMConversation> {
    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void onBindViews(View root) {

    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(EMConversation model, int position) {

    }
//    private Context mContext;
//    private ImageView logoIv;
//    private TextView titleTv;
//    private TextView subtitleTv;
//    private TextView dataTimeTv;
//    private TextView unreadCountTv;
//
//    private View.OnCreateContextMenuListener mOnCreateContextMenuListener;
//    private View.OnLongClickListener mOnLongClickListener;
//
//    public MessageChatAdapterItem(Context context) {
//        mContext = context;
//    }
//
//    @Override
//    public int getLayoutResId() {
//        return R.layout.item_chat_group;
//    }
//
//    @Override
//    public void onBindViews(View root) {
//        logoIv = (ImageView) root.findViewById(R.id.msg_logo_iv);
//        titleTv = (TextView) root.findViewById(R.id.msg_title_tv);
//        subtitleTv = (TextView) root.findViewById(R.id.msg_subtitle_tv);
//        dataTimeTv = (TextView) root.findViewById(R.id.msg_date_time_tv);
//        unreadCountTv = (TextView) root.findViewById(R.id.msg_unread_count_tv);
//
//        if (mOnCreateContextMenuListener != null) {
//            root.setOnCreateContextMenuListener(mOnCreateContextMenuListener);
//        }
//        if (mOnLongClickListener != null) {
//            root.setOnLongClickListener(mOnLongClickListener);
//        }
//    }
//
//    @Override
//    public void onSetViews() {
//
//    }
//
//    @Override
//    public void onUpdateViews(EMConversation conversation, int position) {
//        ChatUserUtil.setUserAvatar(mContext, conversation.getUserName(), logoIv);
//        titleTv.setText(ChatUserUtil.getNickName(conversation.getUserName(),
//                EMChat.getInstance().getAppContext()));
//        if (conversation.getMsgCount() > 0) {
//            EMMessage lastMessage = conversation.getLastMessage();
//            subtitleTv.setText(SmileUtils.getSmiledText(mContext,
//                    CommonUtils.getMessageDigest(lastMessage, mContext),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.emoji_size_small)),
//                    TextView.BufferType.SPANNABLE);
//            dataTimeTv.setText(
//                    DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
//            if (lastMessage.direct == EMMessage.Direct.SEND
//                    && lastMessage.status == EMMessage.Status.FAIL) {
//                ChatUserUtil.setTextLeftDrawable(mContext, subtitleTv, R.drawable.msg_state_failed_resend);
//            } else {
//                subtitleTv.setCompoundDrawables(null, null, null, null);
//            }
//
//            int unreadCount = conversation.getUnreadMsgCount();
//            if (unreadCount > 0) {
//                String count = unreadCount > 99 ? "99+" : unreadCount + "";
//                unreadCountTv.setText(count);
//                unreadCountTv.setVisibility(View.VISIBLE);
//            } else {
//                unreadCountTv.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    public void setOnCreateContextMenuListener(View.OnCreateContextMenuListener l) {
//        mOnCreateContextMenuListener = l;
//    }
//
//    public void setOnLongClickListener(View.OnLongClickListener l) {
//        mOnLongClickListener = l;
//    }
}
