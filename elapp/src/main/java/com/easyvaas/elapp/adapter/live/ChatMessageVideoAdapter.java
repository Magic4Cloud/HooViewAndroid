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

import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.CircleImageView;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/5/9
 * Author  xiaomao
 * 视频直播聊天adapter
 */
public class ChatMessageVideoAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TIPS = 1;
    private static final int TYPE_JOIN = 2;
    private static final int TYPE_MESSAGE = 3;
    private static final int TYPE_GIFT = 4;
    private Context mContext;
    private List<ChatComment> mData;
    private OnReplyListener mOnReplyListener;

    public ChatMessageVideoAdapter(Context context, List<ChatComment> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case TYPE_TIPS:
                return new TipsViewHolder(inflater.inflate(R.layout.item_chat_message_tips, parent, false));
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
            ChatComment comment = mData.get(position % mData.size());
            if (holder instanceof MessageViewHolder) {
                ((MessageViewHolder) holder).setMessage(comment);
            } else if (holder instanceof JoinViewHolder) {
                ((JoinViewHolder) holder).setModel(comment);
            } else if (holder instanceof GiftViewHolder) {
                ((GiftViewHolder) holder).setModel(comment);
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
            ChatComment comment = mData.get(position);
            if (comment != null) {
                if (ChatComment.MSG_TYPE_TIPS.equals(comment.getMsgType())) {
                    return TYPE_TIPS;
                } else if (ChatComment.MSG_TYPE_JOIN.equals(comment.getMsgType())) {
                    return TYPE_JOIN;
                } else if (EMMessageWrapper.MSG_TYPE_GIFT.equals(comment.getMsgType())) {
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
     * 提示信息
     */
    class TipsViewHolder extends RecyclerView.ViewHolder {
        public TipsViewHolder(View itemView) {
            super(itemView);
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

        void setMessage(final ChatComment comment) {
            if (comment != null) {
                if (comment.isSelf()) {
                    mItemOtherRll.setVisibility(View.GONE);
                    mItemMineRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(RealmHelper.getInstance().getChatRecordAvatar(comment.getName()), mAvatarMineCiv);
                    // name
                    if (1 == comment.getVip()) {
                        // 主播
                        mBgMineFl.setBackgroundResource(R.drawable.bg_chat_myself_vip);
                    } else {
                        // 水友
                        mBgMineFl.setBackgroundResource(R.drawable.bg_chat_myself);
                    }
                    mNameMineTv.setText(R.string.me);
                    // message
                    mMessageMineTv.setText(comment.getContent());
                    // reply
                    if (ChatComment.MSG_TYPE_REPLY.equals(comment.getMsgType())) {
                        mReplyMineTv.setVisibility(View.VISIBLE);
                        mReplyMineTv.setText(mContext.getResources().getString(R.string.reply_content, comment.getReply_nickname(), comment.getReply_content()));
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
                                mOnReplyListener.onReply(comment);
                            }
                            return false;
                        }
                    });
                } else {
                    mItemMineRll.setVisibility(View.GONE);
                    mItemOtherRll.setVisibility(View.VISIBLE);
                    // avatar
                    Utils.showNewsImage(RealmHelper.getInstance().getChatRecordAvatar(comment.getName()), mAvatarOtherCiv);
                    // name
                    if (1 == comment.getVip()) {
                        // 主播
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.title_text_color));
                        mVOtherIv.setVisibility(View.VISIBLE);
                        mBgOtherFl.setBackgroundResource(R.drawable.bg_chat_others_vip);
                    } else {
                        // 水友
                        mNameOtherTv.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                        mVOtherIv.setVisibility(View.GONE);
                        mBgOtherFl.setBackgroundResource(R.drawable.bg_chat_others);
                    }
                    mNameOtherTv.setText(comment.getNickname());
                    // message
                    mMessageOtherTv.setText(comment.getContent());
                    // reply
                    // reply
                    if (ChatComment.MSG_TYPE_REPLY.equals(comment.getMsgType())) {
                        mReplyOtherTv.setVisibility(View.VISIBLE);
                        mReplyOtherTv.setText(mContext.getResources().getString(R.string.reply_content, comment.getReply_nickname(), comment.getReply_content()));
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
                                mOnReplyListener.onReply(comment);
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

        void setModel(ChatComment comment) {
            if (comment != null) {
                Utils.showNewsImage(RealmHelper.getInstance().getChatRecordAvatar(comment.getName()), mAvatarCiv);
                mNameTv.setText(comment.getNickname());
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

        void setModel(ChatComment comment) {
            if (comment != null) {
                Utils.showNewsImage(RealmHelper.getInstance().getChatRecordAvatar(comment.getName()), mAvatarCiv);
                mNameTv.setText(comment.getNickname());
                mGiftTv.setText(comment.getContent());
            }
        }
    }

    public interface OnReplyListener {
        void onReply(ChatComment comment);
    }

    public void setOnReplyListener(OnReplyListener l) {
        mOnReplyListener = l;
    }
}
