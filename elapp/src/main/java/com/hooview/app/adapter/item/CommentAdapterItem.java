/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.emoji.utils.FaceConversionUtil;

import com.hooview.app.bean.chat.ChatComment;

public class CommentAdapterItem implements AdapterItem<ChatComment> {
    private Context mContext;

    private TextView contentTv;
    private OnItemViewClickListener mOnItemClickListener;

    public CommentAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_video_comment;
    }

    @Override
    public void onBindViews(View root) {
        contentTv = (TextView) root.findViewById(com.hooview.app.R.id.comment_content_tv);
    }

    @Override
    public void onSetViews() {
    }

    @Override
    public void onUpdateViews(final ChatComment model, int position) {
        contentTv.setCompoundDrawables(null, null, null, null);
        if (model.getType() == ChatComment.TYPE_INTO_TIPS) {
            contentTv.setText(mContext.getString(com.hooview.app.R.string.watching_user_enter_comment, model.getNickname()));
            contentTv.setTextColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_three_level));
            contentTv.setOnClickListener(null);
        } else if (model.getType() == ChatComment.TYPE_INTO_GIFT
                || model.getType() == ChatComment.TYPE_INTO_EXPRESSION) {
            int strResId = com.hooview.app.R.string.watching_user_send_gift;
            if (model.getType() == ChatComment.TYPE_INTO_EXPRESSION) {
                strResId = com.hooview.app.R.string.watching_user_send_expression;
                contentTv.setTextColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_four_level));
            } else {
                contentTv.setTextColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_four_level));
            }
            contentTv.setText(mContext.getString(strResId, model.getNickname(), model.getContent()));
            contentTv.setOnClickListener(null);
        } else {
            if (ChatComment.TYPE_INTO_RED_PACK == model.getType()) {
                Drawable drawable = mContext.getResources().getDrawable(com.hooview.app.R.drawable.icon_red_pack_received);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                }
                contentTv.setCompoundDrawables(null, null, drawable, null);
            }
            assembleCommentContent(model, contentTv);
        }
    }

    public void setOnItemViewClickListener(OnItemViewClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private void assembleCommentContent(final ChatComment model, TextView contentTv) {
        String userName = model.getNickname();
        if (TextUtils.isEmpty(model.getContent()) || TextUtils.isEmpty(userName)) {
            return;
        }
        contentTv.setTextColor(mContext.getResources().getColor(com.hooview.app.R.color.text_common));
        String content = "";
        if (TextUtils.isEmpty(model.getReply_nickname()) || "null".equals(model.getReply_nickname())) {
            content = model.getContent().replaceFirst("@.*:", "");
        } else {
            content = "@" + model.getReply_nickname() + ":" + model.getContent();
        }
        content = userName + " " + content;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(content);
        FaceConversionUtil.getInstace().getExpressionString(mContext, spannableString);
        spannableString.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onNameClick(view, model);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        if (ChatComment.NAME_SYSTEM_SECRETARY.equals(model.getName())
                                || ChatComment.TYPE_INTO_RED_PACK == model.getType()) {
                            ds.setColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_remind_color_violet));
                        } else if (ChatComment.NAME_SYSTEM_FOLLOW.equals(model.getName())) {
                            ds.setColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_three_level));
                        } else if (model.getType() == 0) {
                            ds.setColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_main));
                        }
                        ds.setUnderlineText(false);
                    }
                }, 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        if (ChatComment.NAME_SYSTEM_SECRETARY.equals(model.getName())
                                || ChatComment.NAME_SYSTEM_FOLLOW.equals(model.getName())) {
                            // DO NOTHING
                        } else {
                            mOnItemClickListener.onContentClick(view, model);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        if (ChatComment.NAME_SYSTEM_FOLLOW.equals(model.getName())) {
                            ds.setColor(mContext.getResources().getColor(com.hooview.app.R.color.text_comment_yellow_color));
                        } else {
                            ds.setColor(mContext.getResources().getColor(com.hooview.app.R.color.text_white));
                            ds.setShadowLayer(1,2,2,mContext.getResources().getColor(com.hooview.app.R.color.base_black));
                        }
                        ds.setUnderlineText(false);
                    }
                }, userName.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentTv.setHighlightColor(mContext.getResources().getColor(android.R.color.transparent));
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
        spannableStringBuilder.append(spannableString);
        contentTv.setText(spannableStringBuilder);
    }

    public interface OnItemViewClickListener {
        void onNameClick(View view, ChatComment entity);

        void onContentClick(View view, ChatComment entity);
    }
}
