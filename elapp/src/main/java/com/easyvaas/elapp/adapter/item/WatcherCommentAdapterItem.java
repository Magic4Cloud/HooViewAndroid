/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.chat.ChatComment;

public class WatcherCommentAdapterItem implements AdapterItem<ChatComment> {
    private Context mContext;
    private LinearLayout mLlOther;
    private TextView mTvLevelOther;
    private TextView mTvNameOther;
    private TextView mTvCommentOther;
    private RelativeLayout mRlMy;
    private TextView mTvComment;
    private LinearLayout mLlUser;
    private TextView mTvName;
    private TextView mTvLevel;
    private TextView contentTv;
    private OnItemViewClickListener mOnItemClickListener;

    public WatcherCommentAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_comment_watcher;
    }

    @Override
    public void onBindViews(View root) {
        mLlOther = (LinearLayout) root.findViewById(R.id.ll_other);
        mTvLevelOther = (TextView) root.findViewById(R.id.tvLevelOther);
        mTvNameOther = (TextView) root.findViewById(R.id.tvNameOther);
        mTvCommentOther = (TextView) root.findViewById(R.id.tvCommentOther);
        mRlMy = (RelativeLayout) root.findViewById(R.id.rl_my);
        mTvComment = (TextView) root.findViewById(R.id.tvComment);
        mLlUser = (LinearLayout) root.findViewById(R.id.ll_user);
        mTvName = (TextView) root.findViewById(R.id.tvName);
        mTvLevel = (TextView) root.findViewById(R.id.tvLevel);
    }

    @Override
    public void onSetViews() {
    }

    @Override
    public void onUpdateViews(final ChatComment model, int position) {
        if (model.getIs_guest() == 0) {
            mLlOther.setVisibility(View.VISIBLE);
            mRlMy.setVisibility(View.GONE);
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
            mTvNameOther.setText(model.getNickname());
            mTvCommentOther.setText(model.getContent());
            //TODO 缺少等级字段
//            mTvLevelOther.setText(model.get());
        } else {
            mLlOther.setVisibility(View.GONE);
            mRlMy.setVisibility(View.VISIBLE);
            mTvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentClick(v, model);
                    }
                }

            });
            mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onNameClick(v, model);
                    }
                }
            });
            mTvComment.setText(model.getContent());
            mTvName.setText(model.getNickname());
        }

    }

    public void setOnItemViewClickListener(OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface OnItemViewClickListener {
        void onNameClick(View view, ChatComment entity);

        void onContentClick(View view, ChatComment entity);
    }
}
