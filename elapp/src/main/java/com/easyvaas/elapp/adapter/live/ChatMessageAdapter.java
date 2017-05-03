package com.easyvaas.elapp.adapter.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.CircleImageView;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/5/3
 * Author  xiaomao
 * 聊天adapter
 */
public class ChatMessageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<EMMessageWrapper> mData;

    public ChatMessageAdapter(Context context, List<EMMessageWrapper> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new ChatViewHolder(inflater.inflate(R.layout.item_chat_message, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData != null && mData.size() > 0) {
            EMMessageWrapper wrapper = mData.get(position % mData.size());
            if (holder instanceof ChatViewHolder) {
                ((ChatViewHolder) holder).setMessage(wrapper);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 聊天消息
     */
    class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_chat_other)
        RelativeLayout mItemOtherRll;
        @BindView(R.id.chat_other_avatar_civ)
        CircleImageView mAvatarOtherCiv;
        @BindView(R.id.chat_other_name_tv)
        TextView mNameOtherTv;
        @BindView(R.id.chat_other_v_iv)
        ImageView mVOtherIv;
        @BindView(R.id.chat_other_message_tv)
        TextView mMessageOtherTv;
        @BindView(R.id.item_chat_mine)
        RelativeLayout mItemMineRll;
        @BindView(R.id.chat_mine_avatar_civ)
        CircleImageView mAvatarMineCiv;
        @BindView(R.id.chat_mine_name_tv)
        TextView mNameMineTv;
        @BindView(R.id.chat_mine_message_tv)
        TextView mMessageMineTv;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setMessage(EMMessageWrapper wrapper) {
            if (wrapper != null) {
                if (wrapper.isSelf) {
                    mItemOtherRll.setVisibility(View.GONE);
                    mItemMineRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(wrapper.avatar, mAvatarMineCiv);
                    // name
                    if (wrapper.isAnchor) {
                        // 主播
                        mMessageMineTv.setBackgroundResource(R.drawable.bg_chat_myself_vip);
                    } else {
                        // 水友
                        mMessageMineTv.setBackgroundResource(R.drawable.bg_chat_myself);
                    }
                    mNameMineTv.setText(R.string.me);
                    // message
                    mMessageMineTv.setText(wrapper.content);
                } else {
                    mItemMineRll.setVisibility(View.GONE);
                    mItemOtherRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(wrapper.avatar, mAvatarOtherCiv);
                    // name
                    if (wrapper.isAnchor) {
                        // 主播
                        mNameOtherTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.title_text_color));
                        mVOtherIv.setVisibility(View.VISIBLE);
                        mMessageOtherTv.setBackgroundResource(R.drawable.bg_chat_others);
                    } else {
                        // 水友
                        mNameOtherTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                        mVOtherIv.setVisibility(View.GONE);
                        mMessageOtherTv.setBackgroundResource(R.drawable.bg_chat_others);
                    }
                    mNameOtherTv.setText(wrapper.nickname);
                    // message
                    mMessageOtherTv.setText(wrapper.content);
                }
            }
        }
    }
}
