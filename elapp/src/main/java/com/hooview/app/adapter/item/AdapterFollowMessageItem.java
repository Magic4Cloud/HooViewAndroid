/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.bean.message.MessageFollowUser;
import com.hooview.app.bean.message.MessageItemEntity;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.UserUtil;

public class AdapterFollowMessageItem implements AdapterItem<MessageItemEntity> {
    private MyUserPhoto myUserPhoto;
    private TextView contentTv;
    private TextView dataTimeTv;
    private Context mContext;
    
    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_message_follow;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        myUserPhoto = (MyUserPhoto) root.findViewById(com.hooview.app.R.id.my_user_photo);
        contentTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_title_tv);
        dataTimeTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_date_time_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(MessageItemEntity message, int position) {
        final MessageFollowUser user
                = message.getContent().getData();
        UserUtil.showUserPhoto(mContext, user.getLogourl(), myUserPhoto);
        myUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUtil.showUserInfo(mContext, user.getName());
            }
        });
        contentTv.setText(user.getNickname());
        dataTimeTv.setText(DateTimeUtil.getFormatDate(message.getTime(), "MM/dd HH:mm"));
    }
}
