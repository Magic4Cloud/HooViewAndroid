/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.easyvaas.elapp.adapter.oldItem.CommentAdapterItem;
import com.easyvaas.elapp.bean.chat.ChatComment;

public class CommentRcvAdapter extends CommonRcvAdapter<ChatComment> {
    private Context mContext;
    private CommentAdapterItem.OnItemViewClickListener mOnItemClickListener;
    private List<ChatComment> mChatComments;

    public CommentRcvAdapter(Context context, List<ChatComment> data) {
        super(data);
        mContext = context.getApplicationContext();
        mChatComments = data;
    }

    @Override
    public long getItemId(int position) {
        return mChatComments.get(position).getId();
    }

    @NonNull @Override
    public AdapterItem<ChatComment> getItemView(Object type) {
        CommentAdapterItem adapterItem = new CommentAdapterItem(mContext);
        adapterItem.setOnItemViewClickListener(mOnItemClickListener);
        return adapterItem;
    }

    public void setOnCommentViewClickListener(CommentAdapterItem.OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
