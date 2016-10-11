/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.bean.message.MessageFollowUser;
import com.hooview.app.bean.message.MessageGroupEntity;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;

public class MessageGroupAdapterItem implements AdapterItem<MessageGroupEntity> {
    private ImageView logoIv;
    private TextView titleTv;
    private TextView subtitleTv;
    private TextView dataTimeTv;
    private TextView unreadCountTv;
    
    public MessageGroupAdapterItem() {
    }
    
    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_chat_group;
    }

    @Override
    public void onBindViews(View root) {
        logoIv = (ImageView) root.findViewById(com.hooview.app.R.id.msg_logo_iv);
        titleTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_title_tv);
        subtitleTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_subtitle_tv);
        dataTimeTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_date_time_tv);
        unreadCountTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_unread_count_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(MessageGroupEntity message, int position) {
        Context context = titleTv.getContext();
        titleTv.setText(message.getTitle());
        UserUtil.showUserPhoto(context, message.getIcon(), logoIv);
        if (message.getType() == MessageGroupEntity.TYPE_CHAT_GROUP) {
            Utils.showImage(com.hooview.app.R.drawable.home_news_icon_group_item, logoIv);
            subtitleTv.setText(message.getDesc());
        } else if (message.getType() == MessageGroupEntity.TYPE_OFFICIAL_MESSAGE) {
            if (message.getLastest_content().getData() != null) {
                subtitleTv.setText(message.getLastest_content().getData().getText());
            } else {
                subtitleTv.setText("");
            }
        } else if (message.getType() == MessageGroupEntity.TYPE_FOLLOWER_MESSAGE) {
            MessageFollowUser user = null;
            if (message.getLastest_content() != null) {
                user = message.getLastest_content().getData();
            }
            if (user != null) {
                subtitleTv.setText(user.getNickname());
            } else {
                subtitleTv.setText("");
            }
        }
        subtitleTv.setCompoundDrawables(null, null, null, null);
        dataTimeTv.setText(DateTimeUtil.getFormatDate(message.getUpdate_time(), "MM/dd HH:mm"));
        if (message.getUnread() > 0) {
            String count = message.getUnread() > 99 ? "99+" : message.getUnread() + "";
            unreadCountTv.setText(count);
            unreadCountTv.setVisibility(View.VISIBLE);
        } else {
            unreadCountTv.setVisibility(View.GONE);
        }
    }
}
