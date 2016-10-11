/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.easemob.chat.EMConversation;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.MessageChatAdapterItem;
import com.hooview.app.adapter.item.MessageGroupAdapterItem;
import com.hooview.app.bean.message.MessageGroupEntity;

public class MessageGroupAdapter extends CommonRcvAdapter {
    private static final Object ITEM_TYPE_NOTIFICATION = 1;
    private static final Object ITEM_TYPE_CHAT = 2;

    private Context mContext;
    private int mPosition;
    private View.OnCreateContextMenuListener mOnCreateContextMenuListener;

    public MessageGroupAdapter(Context context, List data) {
        super(data);
        mContext = context;
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                mPosition = position;
                return false;
            }
        });
    }

    @Override
    public Object getItemViewType(Object object) {
        if (object instanceof MessageGroupEntity) {
            return ITEM_TYPE_NOTIFICATION;
        } else if (object instanceof EMConversation) {
            return ITEM_TYPE_CHAT;
        }
        return ITEM_TYPE_NOTIFICATION;
    }

    @NonNull @Override
    public AdapterItem getItemView(Object type) {
        if (type == ITEM_TYPE_CHAT) {
            MessageChatAdapterItem adapterItem = new MessageChatAdapterItem(mContext);
            adapterItem.setOnCreateContextMenuListener(mOnCreateContextMenuListener);
            return adapterItem;
        } else if (type == ITEM_TYPE_NOTIFICATION) {
            return new MessageGroupAdapterItem();
        }
        return new MessageGroupAdapterItem();
    }

    public void setOnCreateContextMenuListener(View.OnCreateContextMenuListener l) {
        mOnCreateContextMenuListener = l;
    }

    public int getPosition() {
        return mPosition;
    }
}
