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
import com.easyvaas.elapp.adapter.item.AnchorCommentAdapterItem;
import com.easyvaas.elapp.bean.chat.ChatComment;

import java.util.List;

public class AnchorCommentRcvAdapter extends CommonRcvAdapter<ChatComment> {
    private Context mContext;
    private AnchorCommentAdapterItem.OnItemViewClickListener mOnItemClickListener;
    private List<ChatComment> mChatComments;

    public AnchorCommentRcvAdapter(Context context, List<ChatComment> data) {
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
    public AdapterItem<ChatComment> getItemView(Object type) {
        AnchorCommentAdapterItem adapterItem = new AnchorCommentAdapterItem(mContext);
        adapterItem.setOnItemViewClickListener(mOnItemClickListener);
        return adapterItem;
    }

    public void setOnCommentViewClickListener(AnchorCommentAdapterItem.OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
