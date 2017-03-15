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
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;

public class HeaderFieldVideoAdapterItem implements AdapterItem<VideoEntity> {
    protected Context mContext;

    protected MyUserPhoto myUserPhoto;
    protected ImageView videoThumbIv;
    protected TextView titleTv;
    protected TextView nicknameTv;
    protected TextView videoWatchingCountTipTv;
    protected TextView videoStartTimeTv;

    public HeaderFieldVideoAdapterItem(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_field;
    }

    @Override
    public void onBindViews(View root) {
        myUserPhoto = (MyUserPhoto) root.findViewById(R.id.video_my_user_photo);
        nicknameTv = (TextView) root.findViewById(R.id.user_name_tv);
        videoThumbIv = (ImageView) root.findViewById(R.id.video_img);
        titleTv = (TextView) root.findViewById(R.id.video_title_tv);
        videoWatchingCountTipTv = (TextView) root.findViewById(R.id.video_watching_count_tip_tv);
        videoStartTimeTv = (TextView) root.findViewById(R.id.video_start_time_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final VideoEntity model, int position) {
        loadThumb(model.getThumb(), model.getLiving() == VideoEntity.IS_LIVING);
        UserUtil.showUserPhoto(mContext, model.getLogourl(), myUserPhoto);

        titleTv.setText(model.getTitle());
        nicknameTv.setText(model.getNickname());
        videoStartTimeTv.setBackgroundResource(R.drawable.btn_live_status_shape);
        if (model.getLiving() == VideoEntity.IS_LIVING) {
            videoStartTimeTv.setText(R.string.is_living);
        } else {
            videoStartTimeTv.setText(R.string.timeline_item_title_playback);
        }
        String watchingCountTip = mContext.getString(R.string.unit_person,
                StringUtil.getShortCount(mContext, model.getWatching_count()));
        videoWatchingCountTipTv.setText(watchingCountTip);
        myUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUtil.showUserInfo(mContext, model.getName());
            }
        });
    }

    protected void loadThumb(String url, boolean isLiving) {
        if (isLiving) {
            Utils.showImage(url, R.drawable.default_loading_bg, videoThumbIv);
        } else {
            Utils.showImage(url, R.drawable.default_loading_play_back_bg, videoThumbIv);
        }
    }
}
