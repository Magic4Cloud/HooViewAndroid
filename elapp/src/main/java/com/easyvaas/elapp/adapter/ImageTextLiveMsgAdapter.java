package com.easyvaas.elapp.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.List;

public class ImageTextLiveMsgAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ImageTextLiveMsgAdapter";
    private List<EMMessageWrapper> mEMMessageList;
    private static final int VIEW_TYPE_STICK = 1;
    private static final int VIEW_TYPE_HIGHT_LIGHT = 2;
    private static final int VIEW_TYPE_NORMAL = 3;
    private static final int VIEW_TYPE_REPLY = 4;
    private static final int VIEW_TYPE_IMAGE = 5;

    public ImageTextLiveMsgAdapter(List<EMMessageWrapper> msgList) {
        mEMMessageList = msgList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_STICK:
                viewHolder = new StickViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_text_live_chat_stick, parent, false));
                break;
            case VIEW_TYPE_HIGHT_LIGHT:
                viewHolder = new HightLightViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_text_live_important, parent, false));
                break;
            case VIEW_TYPE_NORMAL:
                viewHolder = new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_text_live_normal, parent, false));
                break;
            case VIEW_TYPE_REPLY:
                viewHolder = new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_text_live_reply, parent, false));
                break;
            case VIEW_TYPE_IMAGE:
                viewHolder = new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_text_live_image, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder bvh = (BaseViewHolder) holder;
            bvh.setEMMessageWrapper(mEMMessageList.get(position));
        } else {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            imageViewHolder.showImage(mEMMessageList.get(position).emaMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        EMMessageWrapper messageWrapper = mEMMessageList.get(position);
        if (messageWrapper.emaMessage.getType() == EMMessage.Type.IMAGE) {
            return VIEW_TYPE_IMAGE;
        }
        if (messageWrapper.type.equals(EMMessageWrapper.MSG_TYPE_IMPORTANT)) {
            return VIEW_TYPE_HIGHT_LIGHT;
        } else if (messageWrapper.type.equals(EMMessageWrapper.MSG_TYPE_NORMAL)) {
            return VIEW_TYPE_NORMAL;
        } else if (messageWrapper.type.equals(EMMessageWrapper.MSG_TYPE_STICK)) {
            return VIEW_TYPE_STICK;
        } else if (messageWrapper.type.equals(EMMessageWrapper.MSG_TYPE_REPLY)) {
            return VIEW_TYPE_REPLY;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mEMMessageList != null ? mEMMessageList.size() : 0;
    }

    private String formatTime(long msgTime) {
        return DateTimeUtil.getImageTextLiveTime(EVApplication.getApp(), msgTime);
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvContent;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        public abstract void setEMMessageWrapper(EMMessageWrapper msg);
    }

    public class StickViewHolder extends BaseViewHolder {

        public StickViewHolder(View itemView) {
            super(itemView);
        }

        public void setEMMessageWrapper(EMMessageWrapper msg) {
            tvContent.setText(msg.content);
        }
    }

    public class HightLightViewHolder extends BaseViewHolder {
        private TextView mTvTime;

        public HightLightViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void setEMMessageWrapper(EMMessageWrapper msg) {
            tvContent.setText(msg.content);
            mTvTime.setText(formatTime(msg.emaMessage.getMsgTime()));
        }
    }

    public class NormalViewHolder extends BaseViewHolder {
        private TextView mTvTime;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);

        }

        public void setEMMessageWrapper(EMMessageWrapper msg) {
            tvContent.setText(msg.content);
            mTvTime.setText(formatTime(msg.emaMessage.getMsgTime()));

        }
    }

    public class ReplyViewHolder extends BaseViewHolder {
        private TextView mTvReplyContent;
        private TextView mTvReplyTime;
        private TextView mTvreplyName;
        private TextView mTvTime;

        public ReplyViewHolder(View itemView) {
            super(itemView);

            mTvReplyContent = (TextView) itemView.findViewById(R.id.tv_reply_content);
            mTvReplyTime = (TextView) itemView.findViewById(R.id.tv_reply_time);
            mTvreplyName = (TextView) itemView.findViewById(R.id.tv_reply_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void setEMMessageWrapper(EMMessageWrapper msg) {
            tvContent.setText(msg.content);
            mTvReplyContent.setText(msg.replyContent);
            mTvreplyName.setText(msg.replyNickname);
            mTvTime.setText(formatTime(msg.emaMessage.getMsgTime()));
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTvTime;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void showImage(EMMessage message) {
            EMImageMessageBody emImageMessageBody = (EMImageMessageBody) message.getBody();
            Utils.showImage(emImageMessageBody.getRemoteUrl(), R.drawable.account_bitmap_list, mImageView);
            mTvTime.setText(formatTime(message.getMsgTime()));
        }
    }


}
