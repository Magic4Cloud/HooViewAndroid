/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.bean.message.MessageItemEntity;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.UserUtil;

public class AdapterMessageItem implements AdapterItem<MessageItemEntity> {
    private TextView contentTv;
    private TextView dataTimeTv;
    private ImageView messageIcon;
    private String mItemIconUrl;
    
    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_message;
    }

    @Override
    public void onBindViews(View root) {
        messageIcon = (ImageView) root.findViewById(com.hooview.app.R.id.user_logo_iv);
        contentTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_content_tv);
        dataTimeTv = (TextView) root.findViewById(com.hooview.app.R.id.msg_date_time_tv);
    }

    @Override
    public void onSetViews() {

    }
    public void setItemIconUrl(String url) {
        mItemIconUrl = url;
    }

    @Override
    public void onUpdateViews(MessageItemEntity message, int position) {
        UserUtil.showUserPhoto(contentTv.getContext(), mItemIconUrl,  messageIcon);
        dataTimeTv.setText(DateTimeUtil.getFormatDate(message.getTime(), "MM/dd HH:mm"));
        if (message.getContent().getData() != null) {
            contentTv.setText(message.getContent().getData().getText());
        } else {
            contentTv.setText("");
        }
    }
}
