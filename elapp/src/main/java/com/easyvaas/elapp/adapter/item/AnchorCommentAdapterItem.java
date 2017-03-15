/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatComment;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

public class AnchorCommentAdapterItem implements AdapterItem<ChatComment> {
    private LinearLayout mLlOther;
    private TextView mTvLevelOther;
    private TextView mTvNameOther;
    private TextView mTvCommentOther;
    private OnItemViewClickListener mOnItemClickListener;
    private TextView mTvTips;
    private TextView mTvUserJoinTips;

    public AnchorCommentAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_comment_anchor;
    }

    @Override
    public void onBindViews(View root) {
        mLlOther = (LinearLayout) root.findViewById(R.id.ll_other);
        mTvLevelOther = (TextView) root.findViewById(R.id.tvLevelOther);
        mTvNameOther = (TextView) root.findViewById(R.id.tvNameOther);
        mTvCommentOther = (TextView) root.findViewById(R.id.tvCommentOther);
        mTvTips = (TextView) root.findViewById(R.id.tv_tips);
        mTvUserJoinTips = (TextView) root.findViewById(R.id.tv_user_join);
    }

    @Override
    public void onSetViews() {
    }

    @Override
    public void onUpdateViews(final ChatComment model, int position) {
        if (model.getType() == ChatComment.TYPE_INTO_TIPS) {
            mLlOther.setVisibility(View.GONE);
            mTvUserJoinTips.setVisibility(View.GONE);
            mTvTips.setVisibility(View.VISIBLE);
            mTvTips.setText(model.getContent());
        } else if (model.getType() == ChatComment.TYPE_INTO_USER_JOIN) {
            mLlOther.setVisibility(View.GONE);
            mTvTips.setVisibility(View.GONE);
            mTvUserJoinTips.setVisibility(View.VISIBLE);
            mTvUserJoinTips.setText(mContext.getString(R.string.user_join_tips, model.getName()));
        } else {
            mLlOther.setVisibility(View.VISIBLE);
            mTvUserJoinTips.setVisibility(View.GONE);
            mTvTips.setVisibility(View.GONE);
            mTvCommentOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentClick(v, model);
                    }
                }
            });
            mTvNameOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onNameClick(v, model);
                    }
                }
            });
            mTvCommentOther.setText(model.getContent());
            mTvNameOther.setText(model.getNickname());
        }

        //TODO 缺少等级字段

    }

    public void setOnItemViewClickListener(OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemViewClickListener {
        void onNameClick(View view, ChatComment entity);

        void onContentClick(View view, ChatComment entity);
    }
}
