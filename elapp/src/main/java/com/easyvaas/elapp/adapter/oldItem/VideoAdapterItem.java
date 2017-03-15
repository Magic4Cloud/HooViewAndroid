/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;

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
        return R.layout.item_my_video;
    }

    @Override
    public void onBindViews(View convertView) {
        videoThumbIv = (ImageView) convertView.findViewById(R.id.mv_video_logo_iv);
        titleTv = (TextView) convertView.findViewById(R.id.my_video_title_tv);
        dateTv = (TextView) convertView.findViewById(R.id.my_video_date_tv);
        likeCountTv = (TextView) convertView.findViewById(R.id.mv_like_count_tv);
        commentCountTv = (TextView) convertView.findViewById(R.id.mv_comment_count_tv);
        watchCountTv = (TextView) convertView.findViewById(R.id.mv_watch_count_tv);
        videoDateTv = (TextView) convertView.findViewById(R.id.mv_video_date_tv);
        videoStatusTv = (TextView) convertView.findViewById(R.id.video_status_tv);
        videoSetIv = (ImageView) convertView.findViewById(R.id.my_video_set_iv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(VideoEntity videoEntity, int position) {
        Context context = videoDateTv.getContext();
        if (VideoEntity.IS_LIVING == videoEntity.getLiving()) {
            videoDateTv.setText(R.string.is_living);
            videoDateTv.setBackgroundResource(R.color.alpha_living_bg_percent_50);
        } else {
            videoDateTv.setText(DateTimeUtil.getDurationTime(context, videoEntity.getDuration() * 1000));
            videoDateTv.setBackgroundResource(R.color.black_alpha_percent_30);
        }
        if (VideoEntity.STATUS_GENERATING == videoEntity.getStatus()) {
            videoStatusTv.setText(videoEntity.getMode() == VideoEntity.MODE_AUDIO
                    ? R.string.audio_reviewing : R.string.video_reviewing);
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
        Utils.setTextLeftDrawable(context, watchCountTv, R.drawable.video_list_icon_watch);
        titleTv.setText(videoEntity.getTitle());

        if (videoEntity.getLiving() == VideoEntity.IS_LIVING) {
            Utils.showImage(videoEntity.getThumb(), R.drawable.default_loading_bg, videoThumbIv);
        } else {
            Utils.showImage(videoEntity.getThumb(), R.drawable.default_loading_play_back_bg,
                    videoThumbIv);
        }
    }
}
