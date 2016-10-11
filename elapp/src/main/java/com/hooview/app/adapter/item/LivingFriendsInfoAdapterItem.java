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

import com.hooview.app.adapter.recycler.FriendsUserInfoAdapter;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.StringUtil;
import com.hooview.app.utils.Utils;

public class LivingFriendsInfoAdapterItem implements AdapterItem<VideoEntity> {
    private View mVideoRoot;
    private ImageView mVideoThumbIv;
    private TextView mVideoTitleTv;
    private TextView mLikeCountTv;
    private TextView mCommentCountTv;
    private TextView mWatchCountTv;
    private TextView mVideoDateTv;
    private TextView mMyVideoDateTv;
    private Context mContext;
    private FriendsUserInfoAdapter.OnClickUserItemViewListener mOnClickUserItemViewListener;

    public LivingFriendsInfoAdapterItem(Context context) {
        mContext = context;
    }

    public void setOnItemClickViewListener(
            FriendsUserInfoAdapter.OnClickUserItemViewListener onClickUserItemViewListener) {
        mOnClickUserItemViewListener = onClickUserItemViewListener;
    }

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_friend_user_list;
    }

    @Override
    public void onBindViews(View root) {
        mVideoRoot = root.findViewById(com.hooview.app.R.id.item_video_root);
        mVideoThumbIv = (ImageView) root.findViewById(com.hooview.app.R.id.mv_video_logo_iv);
        mVideoTitleTv = (TextView) root.findViewById(com.hooview.app.R.id.my_video_title_tv);
        mLikeCountTv = (TextView) root.findViewById(com.hooview.app.R.id.mv_like_count_tv);
        mCommentCountTv = (TextView) root.findViewById(com.hooview.app.R.id.mv_comment_count_tv);
        mWatchCountTv = (TextView) root.findViewById(com.hooview.app.R.id.mv_watch_count_tv);
        mVideoDateTv = (TextView) root.findViewById(com.hooview.app.R.id.mv_video_date_tv);
        mMyVideoDateTv = (TextView) root.findViewById(com.hooview.app.R.id.my_video_date_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(VideoEntity videoEntity, int position) {
        if (videoEntity.getLiving() == VideoEntity.IS_LIVING) {
            Utils.showImage(videoEntity.getThumb(), com.hooview.app.R.drawable.default_loading_bg, mVideoThumbIv);
        } else {
            Utils.showImage(videoEntity.getThumb(), com.hooview.app.R.drawable.default_loading_play_back_bg, mVideoThumbIv);
        }
        mVideoTitleTv.setText(videoEntity.getTitle());
        mWatchCountTv.setText(StringUtil.getShortCount(mContext, videoEntity.getWatch_count()));
        mCommentCountTv.setText(StringUtil.getShortCount(mContext, videoEntity.getComment_count()));
        mLikeCountTv.setText(StringUtil.getShortCount(mContext, videoEntity.getLike_count()));
        if (videoEntity.getMode() == VideoEntity.MODE_VIDEO) {
            Utils.setTextLeftDrawable(mContext, mWatchCountTv, com.hooview.app.R.drawable.video_list_icon_watch);
        }
        if (VideoEntity.IS_LIVING == videoEntity.getLiving()) {
            mVideoDateTv.setText(com.hooview.app.R.string.is_living);
            mVideoDateTv.setBackgroundResource(com.hooview.app.R.color.alpha_living_bg_percent_50);
        } else {
            mVideoDateTv.setText(
                    DateTimeUtil.getDurationTime(mContext, videoEntity.getDuration() * 1000));
            mVideoDateTv.setBackgroundResource(com.hooview.app.R.color.black_alpha_percent_30);
            mMyVideoDateTv.setText(DateTimeUtil
                    .getFormatDate(videoEntity.getLive_stop_time(), "yyyy-MM-dd"));
        }
        mVideoRoot.setVisibility(View.VISIBLE);
    }
}
