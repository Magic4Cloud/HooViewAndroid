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
import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatComment;

public class CommentAdapterItem implements AdapterItem<ChatComment> {
    private Context mContext;

    private TextView contentTv;
    private OnItemViewClickListener mOnItemClickListener;

    public CommentAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_comment1;
    }

    @Override
    public void onBindViews(View root) {
        contentTv = (TextView) root.findViewById(R.id.comment_content_tv);
    }

    @Override
    public void onSetViews() {
    }

    @Override
    public void onUpdateViews(final ChatComment model, int position) {
    }

    public void setOnItemViewClickListener(OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemViewClickListener {
        void onNameClick(View view, ChatComment entity);

        void onContentClick(View view, ChatComment entity);
    }
}
