package com.easyvaas.elapp.adapter;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.imageTextLive.ImageTextLiveHistoryModel.MsgsBean;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.List;

public class ImageTextLiveMsgListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ImageTextLiveMsgListAdapter";
    private List<MsgsBean> mEMMessageList;
    private static final int VIEW_TYPE_STICK = 1;
    private static final int VIEW_TYPE_HIGHT_LIGHT = 2;
    private static final int VIEW_TYPE_NORMAL = 3;
    private static final int VIEW_TYPE_REPLY = 4;
    private static final int VIEW_TYPE_IMAGE = 5;

    public ImageTextLiveMsgListAdapter(List<MsgsBean> msgList) {
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
            imageViewHolder.showImage(mEMMessageList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        MsgsBean data = mEMMessageList.get(position);
        String msgType = data.getPayload().getBodies().get(0).getType(); // 判断 是文字还是图片消息
        String textMsgType = data.getPayload().getExt().getTp(); //判断 文字消息的 几种类型
        if (msgType.equals(EMMessageWrapper.MSG_TYPE_IMAGE)) {
            return VIEW_TYPE_IMAGE;
        }
        if (textMsgType.equals(EMMessageWrapper.MSG_TYPE_IMPORTANT)) {
            return VIEW_TYPE_HIGHT_LIGHT;
        } else if (textMsgType.equals(EMMessageWrapper.MSG_TYPE_NORMAL)) {
            return VIEW_TYPE_NORMAL;
        } else if (textMsgType.equals(EMMessageWrapper.MSG_TYPE_STICK)) {
            return VIEW_TYPE_STICK;
        } else if (textMsgType.equals(EMMessageWrapper.MSG_TYPE_REPLY)) {
            return VIEW_TYPE_REPLY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mEMMessageList != null ? mEMMessageList.size() : 0;
    }

    private String formatTime(String msgTime) {
        return DateTimeUtil.getImageTextLiveTime(EVApplication.getApp(), Long.parseLong(msgTime));
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvContent;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }

        public abstract void setEMMessageWrapper(MsgsBean data);
    }

    public class StickViewHolder extends BaseViewHolder {

        public StickViewHolder(View itemView) {
            super(itemView);
        }

        public void setEMMessageWrapper(MsgsBean msg) {
            tvContent.setText(msg.getPayload().getBodies().get(0).getMsg());
        }
    }

    public class HightLightViewHolder extends BaseViewHolder {
        private TextView mTvTime;

        public HightLightViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void setEMMessageWrapper(MsgsBean msg) {
            tvContent.setText(msg.getPayload().getBodies().get(0).getMsg());
            mTvTime.setText(formatTime(msg.getTimestamp()));
        }
    }

    public class NormalViewHolder extends BaseViewHolder {
        private TextView mTvTime;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);

        }

        public void setEMMessageWrapper(MsgsBean msg) {
            tvContent.setText(msg.getPayload().getBodies().get(0).getMsg());
            mTvTime.setText(formatTime(msg.getTimestamp()));

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

        public void setEMMessageWrapper(MsgsBean msg) {
            tvContent.setText(msg.getPayload().getBodies().get(0).getMsg());
            mTvreplyName.setText(msg.getPayload().getExt().getRnk());
            mTvTime.setText(formatTime(msg.getTimestamp()));
            String content = "回复" + msg.getPayload().getExt().getRnk() + "：" + msg.getPayload().getExt().getRct();
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvReplyContent.setText(builder);
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

        public void showImage(MsgsBean message) {
            Utils.showImage(message.getPayload().getBodies().get(0).getUrl(), R.drawable.account_bitmap_list, mImageView);
            mTvTime.setText(formatTime(message.getTimestamp()));
        }
    }


}
