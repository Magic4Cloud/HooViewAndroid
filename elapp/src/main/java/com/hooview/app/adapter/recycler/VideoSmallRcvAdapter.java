/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.recycler;

import java.util.List;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.VideoMineAdapterItem;
import com.hooview.app.bean.video.VideoEntity;

public class VideoSmallRcvAdapter extends CommonRcvAdapter<VideoEntity> {

    private VideoMineAdapterItem.VideoItemOptionListener mListener;

    public VideoSmallRcvAdapter(@NonNull List<VideoEntity> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<VideoEntity> getItemView(Object type) {
        return new VideoMineAdapterItem(mListener);
    }

    public void setVideoOptionListener(VideoMineAdapterItem.VideoItemOptionListener listener) {
        mListener = listener;
    }
}
