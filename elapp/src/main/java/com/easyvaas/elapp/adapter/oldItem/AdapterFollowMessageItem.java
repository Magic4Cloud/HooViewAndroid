/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.message.MessageFollowUser;
import com.easyvaas.elapp.bean.message.MessageItemEntity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.UserUtil;

public class AdapterFollowMessageItem implements AdapterItem<MessageItemEntity> {
    private MyUserPhoto myUserPhoto;
    private TextView contentTv;
    private TextView dataTimeTv;
    private Context mContext;
    
    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        myUserPhoto = (MyUserPhoto) root.findViewById(R.id.my_user_photo);
//        contentTv = (TextView) root.findViewById(R.id.msg_title_tv);
//        dataTimeTv = (TextView) root.findViewById(R.id.msg_date_time_tv);
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
