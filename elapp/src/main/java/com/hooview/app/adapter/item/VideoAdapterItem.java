/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.StringUtil;
import com.hooview.app.utils.Utils;

public class VideoAdapterItem implements AdapterItem<VideoEntity> {

    private ImageView videoThumbIv;
    private TextView titleTv;
    private TextView dateTv;
    private TextView videoDateTv;
    private TextView likeCountTv;
    private TextView commentCountTv;
    private TextView watchCountTv;
    private TextView videoStatusTv;
    protected ImageView videoSetIv;

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_my_video;
    }

    @Override
    public void onBindViews(View convertView) {
        videoThumbIv = (ImageView) convertView.findViewById(com.hooview.app.R.id.mv_video_logo_iv);
        titleTv = (TextView) convertView.findViewById(com.hooview.app.R.id.my_video_title_tv);
        dateTv = (TextView) convertView.findViewById(com.hooview.app.R.id.my_video_date_tv);
        likeCountTv = (TextView) convertView.findViewById(com.hooview.app.R.id.mv_like_count_tv);
        commentCountTv = (TextView) convertView.findViewById(com.hooview.app.R.id.mv_comment_count_tv);
        watchCountTv = (TextView) convertView.findViewById(com.hooview.app.R.id.mv_watch_count_tv);
        videoDateTv = (TextView) convertView.findViewById(com.hooview.app.R.id.mv_video_date_tv);
        videoStatusTv = (TextView) convertView.findViewById(com.hooview.app.R.id.video_status_tv);
        videoSetIv = (ImageView) convertView.findViewById(com.hooview.app.R.id.my_video_set_iv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(VideoEntity videoEntity, int position) {
        Context context = videoDateTv.getContext();
        if (VideoEntity.IS_LIVING == videoEntity.getLiving()) {
            videoDateTv.setText(com.hooview.app.R.string.is_living);
            videoDateTv.setBackgroundResource(com.hooview.app.R.color.alpha_living_bg_percent_50);
        } else {
            videoDateTv.setText(DateTimeUtil.getDurationTime(context, videoEntity.getDuration() * 1000));
            videoDateTv.setBackgroundResource(com.hooview.app.R.color.black_alpha_percent_30);
        }
        if (VideoEntity.STATUS_GENERATING == videoEntity.getStatus()) {
            videoStatusTv.setText(videoEntity.getMode() == VideoEntity.MODE_AUDIO
                    ? com.hooview.app.R.string.audio_reviewing : com.hooview.app.R.string.video_reviewing);
            videoStatusTv.setVisibility(View.VISIBLE);
            videoDateTv.setVisibility(View.GONE);
        } else {
            videoStatusTv.setVisibility(View.GONE);
            videoDateTv.setVisibility(View.VISIBLE);
        }
        dateTv.setText(DateTimeUtil.getFormatDate(videoEntity.getLive_start_time(), "yyyy-MM-dd"));

        commentCountTv.setText(StringUtil.getShortCount(context, videoEntity.getComment_count()));
        likeCountTv.setText(StringUtil.getShortCount(context, videoEntity.getLike_count()));
        watchCountTv.setText(StringUtil.getShortCount(context, videoEntity.getWatch_count()));
        Utils.setTextLeftDrawable(context, watchCountTv, com.hooview.app.R.drawable.video_list_icon_watch);
        titleTv.setText(videoEntity.getTitle());

        if (videoEntity.getLiving() == VideoEntity.IS_LIVING) {
            Utils.showImage(videoEntity.getThumb(), com.hooview.app.R.drawable.default_loading_bg, videoThumbIv);
        } else {
            Utils.showImage(videoEntity.getThumb(), com.hooview.app.R.drawable.default_loading_play_back_bg,
                    videoThumbIv);
        }
    }
}
