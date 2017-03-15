/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.PlayerChatHeaderItem;
import com.easyvaas.elapp.adapter.item.WatcherCommentAdapterItem;
import com.easyvaas.elapp.bean.chat.ChatComment;

import java.util.List;

public class WatcherCommentRcvAdapter extends CommonRcvAdapter<ChatComment> {
    private Context mContext;
    private WatcherCommentAdapterItem.OnItemViewClickListener mOnItemClickListener;
    private List<ChatComment> mChatComments;

    public WatcherCommentRcvAdapter(Context context, List<ChatComment> data) {
        super(data);
        mContext = context.getApplicationContext();
        mChatComments = data;
    }

    @Override
    public long getItemId(int position) {
        return mChatComments.get(position).getId();
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        if ((Integer) type == ChatComment.TYPE_INTO_TIPS) {
            return new PlayerChatHeaderItem();
        } else {
            WatcherCommentAdapterItem adapterItem = new WatcherCommentAdapterItem(mContext);
            adapterItem.setOnItemViewClickListener(mOnItemClickListener);
            return adapterItem;
        }
    }

    @Override
    public Object getItemViewType(ChatComment o) {
        return o.getType();
    }

    public void setOnCommentViewClickListener(WatcherCommentAdapterItem.OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
