package com.easyvaas.elapp.adapter.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static final int TYPE_JOIN = 1;
    private static final int TYPE_MESSAGE = 2;
    private static final int TYPE_GIFT = 3;
    private Context mContext;
    private List<EMMessageWrapper> mData;
    private OnReplyListener mOnReplyListener;

    public ChatMessageAdapter(Context context, List<EMMessageWrapper> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_JOIN:
                return new JoinViewHolder(inflater.inflate(R.layout.item_chat_join, parent, false));
            case TYPE_MESSAGE:
                return new MessageViewHolder(inflater.inflate(R.layout.item_chat_message, parent, false));
            case TYPE_GIFT:
                return new GiftViewHolder(inflater.inflate(R.layout.item_chat_gift, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData != null && mData.size() > 0) {
            EMMessageWrapper wrapper = mData.get(position % mData.size());
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).setMessage(wrapper);
            } else if (holder instanceof JoinViewHolder) {
                ((JoinViewHolder) holder).setModel(wrapper);
            } else if (holder instanceof GiftViewHolder) {
                ((GiftViewHolder)holder).setModel(wrapper);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null && mData.size() > 0) {
            EMMessageWrapper wrapper = mData.get(position);
            if (wrapper != null) {
                if (EMMessageWrapper.MSG_TYPE_JOIN.equals(wrapper.type)) {
                    return TYPE_JOIN;
                } else if (EMMessageWrapper.MSG_TYPE_GIFT.equals(wrapper.type)) {
                    return TYPE_GIFT;
                } else {
                    return TYPE_MESSAGE;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 聊天消息
     */
    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_chat_other)
        RelativeLayout mItemOtherRll;
        @BindView(R.id.chat_other_avatar_civ)
        CircleImageView mAvatarOtherCiv;
        @BindView(R.id.chat_other_name_tv)
        TextView mNameOtherTv;
        @BindView(R.id.chat_other_v_iv)
        ImageView mVOtherIv;
        @BindView(R.id.chat_other_message_bg_fl)
        FrameLayout mBgOtherFl;
        @BindView(R.id.chat_other_message_panel_ll)
        LinearLayout mPanelOtherLl;
        @BindView(R.id.chat_other_message_reply_tv)
        TextView mReplyOtherTv;
        @BindView(R.id.chat_other_message_tv)
        TextView mMessageOtherTv;
        @BindView(R.id.item_chat_mine)
        RelativeLayout mItemMineRll;
        @BindView(R.id.chat_mine_avatar_civ)
        CircleImageView mAvatarMineCiv;
        @BindView(R.id.chat_mine_name_tv)
        TextView mNameMineTv;
        @BindView(R.id.chat_mine_message_bg_fl)
        FrameLayout mBgMineFl;
        @BindView(R.id.chat_mine_message_panel_ll)
        LinearLayout mPanelMineLl;
        @BindView(R.id.chat_mine_message_reply_tv)
        TextView mReplyMineTv;
        @BindView(R.id.chat_mine_message_tv)
        TextView mMessageMineTv;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setMessage(final EMMessageWrapper wrapper) {
            if (wrapper != null) {
                if (wrapper.isSelf) {
                    mItemOtherRll.setVisibility(View.GONE);
                    mItemMineRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(wrapper.avatar, mAvatarMineCiv);
                    // name
                    if (wrapper.isAnchor) {
                        // 主播
                        mBgMineFl.setBackgroundResource(R.drawable.bg_chat_myself_vip);
                    } else {
                        // 水友
                        mBgMineFl.setBackgroundResource(R.drawable.bg_chat_myself);
                    }
                    mNameMineTv.setText(R.string.me);
                    // message
                    mMessageMineTv.setText(wrapper.content);
                    // reply
                    if (EMMessageWrapper.MSG_TYPE_REPLY.equals(wrapper.type)) {
                        mReplyMineTv.setVisibility(View.VISIBLE);
                        mReplyMineTv.setText(mContext.getResources().getString(R.string.reply_content, wrapper.replyNickname, wrapper.replyContent));
                    } else {
                        mReplyMineTv.setVisibility(View.GONE);
                    }
                    // click
                    mPanelMineLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2017/5/4  
                        }
                    });
                    mPanelMineLl.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (mOnReplyListener != null) {
                                mOnReplyListener.onReply(wrapper);
                            }
                            return false;
                        }
                    });
                } else {
                    mItemMineRll.setVisibility(View.GONE);
                    mItemOtherRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(wrapper.avatar, mAvatarOtherCiv);
                    // name
                    if (wrapper.isAnchor) {
                        // 主播
//                        mNameOtherTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.title_text_color));
                        mVOtherIv.setVisibility(View.VISIBLE);
                        mBgOtherFl.setBackgroundResource(R.drawable.bg_chat_others_vip);
                    } else {
                        // 水友
//                        mNameOtherTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                        mVOtherIv.setVisibility(View.GONE);
                        mBgOtherFl.setBackgroundResource(R.drawable.bg_chat_others);
                    }
                    mNameOtherTv.setText(wrapper.nickname);
                    // message
                    mMessageOtherTv.setText(wrapper.content);
                    // reply
                    // reply
                    if (EMMessageWrapper.MSG_TYPE_REPLY.equals(wrapper.type)) {
                        mReplyOtherTv.setVisibility(View.VISIBLE);
                        mReplyOtherTv.setText(mContext.getResources().getString(R.string.reply_content, wrapper.replyNickname, wrapper.replyContent));
                    } else {
                        mReplyOtherTv.setVisibility(View.GONE);
                    }
                    // click
                    mPanelOtherLl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2017/5/4  
                        }
                    });
                    mPanelOtherLl.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (mOnReplyListener != null) {
                                mOnReplyListener.onReply(wrapper);
                            }
                            return false;
                        }
                    });
                }
            }
        }
    }

    /**
     * somebody join into
     */
    class JoinViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chat_join_avatar_civ)
        CircleImageView mAvatarCiv;
        @BindView(R.id.chat_join_name_tv)
        TextView mNameTv;

        public JoinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setModel(EMMessageWrapper wrapper) {
            if (wrapper != null) {
                Utils.showNewsImage(wrapper.avatar, mAvatarCiv);
                mNameTv.setText(wrapper.nickname);
            }
        }
    }

    /**
     * 礼物
     */
    class GiftViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chat_gift_avatar_civ)
        CircleImageView mAvatarCiv;
        @BindView(R.id.chat_gift_name_tv)
        TextView mNameTv;
        @BindView(R.id.chat_gift_gift_tv)
        TextView mGiftTv;

        public GiftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setModel(EMMessageWrapper wrapper) {
            if (wrapper != null) {
                Utils.showNewsImage(wrapper.avatar, mAvatarCiv);
                mNameTv.setText(wrapper.nickname);
                mGiftTv.setText(wrapper.content);
            }
        }
    }

    public interface OnReplyListener {
        void onReply(EMMessageWrapper wrapper);
    }

    public void setOnReplyListener(OnReplyListener l) {
        mOnReplyListener = l;
    }
}
