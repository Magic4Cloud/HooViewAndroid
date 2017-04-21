package com.easyvaas.elapp.adapter.usernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.CircleImageView;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * adapter: 我的购买---精品视频
 */

public class UserVideoAdapter extends MyBaseAdapter<VideoEntity> {


    public UserVideoAdapter(List<VideoEntity> data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_buy_video, null));
    }

    @Override
    protected void initOnItemClickListener() {

    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, VideoEntity item) {
        if (helper instanceof VideoViewHolder) {
            ((VideoViewHolder) helper).setModel(item);
        }
    }

    class VideoViewHolder extends BaseViewHolder {

        @BindView(R.id.item_buy_video)
        View mRootView;
        @BindView(R.id.video_avatar)
        CircleImageView mAvatarCiv;
        @BindView(R.id.video_nickname)
        TextView mNicknameTv;
        @BindView(R.id.video_time)
        TextView mTimeTv;
        @BindView(R.id.video_thumb)
        ImageView mThumbIv;
        @BindView(R.id.video_play)
        ImageView mPlayIv;
        @BindView(R.id.video_duration)
        TextView mDurationTv;
        @BindView(R.id.video_watch_count)
        TextView mWatchCountTv;
        @BindView(R.id.video_hot)
        ImageView mHotIv;
        @BindView(R.id.video_title)
        TextView mTitleTv;

        public VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(final VideoEntity videoEntity) {
            if (videoEntity != null) {
                // avatar
                Utils.showNewsImage(videoEntity.getLogourl(), mAvatarCiv);
                // nickname
                mNicknameTv.setText(videoEntity.getNickname());
                // time
                mTimeTv.setText(DateTimeUtil.getNewsTime(mContext, videoEntity.getLive_start_time()));
                // thumb
                Utils.showNewsImage(videoEntity.getThumb(), mThumbIv);
                // duration
                mDurationTv.setText(DateTimeUtil.getDurationTimeCn(mContext, videoEntity.getDuration()));
                // watch_count
                mWatchCountTv.setText(mContext.getString(R.string.watch_count, NumberUtil.format(videoEntity.getWatch_count())));
                // hot // TODO: 2017/4/20
                mHotIv.setVisibility(View.GONE);
                // title
                mTitleTv.setText(videoEntity.getTitle());
                // click // play
                mPlayIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode());
                    }
                });
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(), videoEntity.getMode());
                    }
                });
            }
        }
    }


}
