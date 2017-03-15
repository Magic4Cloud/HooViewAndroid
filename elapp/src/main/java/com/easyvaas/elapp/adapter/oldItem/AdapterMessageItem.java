/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.bean.message.MessageItemEntity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.UserUtil;

public class AdapterMessageItem implements AdapterItem<MessageItemEntity> {
    private TextView contentTv;
    private TextView dataTimeTv;
    private ImageView messageIcon;
    private String mItemIconUrl;
    
    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void onBindViews(View root) {
//        messageIcon = (ImageView) root.findViewById(R.id.user_logo_iv);
//        contentTv = (TextView) root.findViewById(R.id.msg_content_tv);
//        dataTimeTv = (TextView) root.findViewById(R.id.msg_date_time_tv);
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
