package com.easyvaas.elapp.adapter.item;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.hyphenate.chat.EMTextMessageBody;

public class ImageTextReplyMessageItem implements AdapterItem<EMMessageWrapper> {
    private LinearLayout mLlOther;
    private RelativeLayout mRlMySelf;
    private TextView tvNameOther;
    private TextView tvOtherComment;
    private TextView tvOtherReply;
    private TextView tvSelfComment;
    private TextView tvSelfReply;


    @Override
    public int getLayoutResId() {
        return R.layout.item_image_text_chat_reply_msg;
    }

    @Override
    public void onBindViews(View view) {
        mLlOther = (LinearLayout) view.findViewById(R.id.ll_other);
        mRlMySelf = (RelativeLayout) view.findViewById(R.id.ll_self);
        tvNameOther = (TextView) view.findViewById(R.id.tvNameOther);
        tvOtherComment = (TextView) view.findViewById(R.id.tvComment);
        tvOtherReply = (TextView) view.findViewById(R.id.tv_reply);
        tvSelfComment = (TextView) view.findViewById(R.id.tv_self_comment);
        tvSelfReply = (TextView) view.findViewById(R.id.tv_self_reply);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(EMMessageWrapper model, int position) {
        if (model.isSelf) {
            mRlMySelf.setVisibility(View.VISIBLE);
            mLlOther.setVisibility(View.GONE);
            tvSelfComment.setText(((EMTextMessageBody) model.emaMessage.getBody()).getMessage());
            tvSelfReply.setText(tvNameOther.getContext().getString(R.string.reply_content, model.replyNickname, model.replyContent));
        } else {
            mRlMySelf.setVisibility(View.GONE);
            mLlOther.setVisibility(View.VISIBLE);
            tvOtherComment.setText(((EMTextMessageBody) model.emaMessage.getBody()).getMessage());
            tvNameOther.setText(model.nickname);
            tvOtherReply.setText(tvNameOther.getContext().getString(R.string.reply_content, model.replyNickname, model.replyContent));
        }
    }
}
