/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import java.util.List;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.easyvaas.elapp.adapter.oldItem.AdapterFollowMessageItem;
import com.easyvaas.elapp.adapter.oldItem.AdapterMessageItem;
import com.easyvaas.elapp.bean.message.MessageItemEntity;

public class MessageQueueAdapter extends CommonRcvAdapter<MessageItemEntity> {
    private String mItemIconUrl;
    private static final Object TYPE_ITEM_FOLLOW = 1;

    public MessageQueueAdapter(List data) {
        super(data);
    }

    public void setItemIconUrl(String url) {
        mItemIconUrl = url;
    }

    @Override
    public Object getItemViewType(MessageItemEntity itemsEntity) {
        if (itemsEntity.getContent().getType() == MessageItemEntity.TYPE_FOLLOW) {
            return TYPE_ITEM_FOLLOW;
        }
        return super.getItemViewType(itemsEntity);
    }

    @NonNull @Override
    public AdapterItem getItemView(Object type) {
        if (type == TYPE_ITEM_FOLLOW) {
            return new AdapterFollowMessageItem();
        }
        AdapterMessageItem adapterMessageItem = new AdapterMessageItem();
        adapterMessageItem.setItemIconUrl(mItemIconUrl);
        return adapterMessageItem;
    }
}
