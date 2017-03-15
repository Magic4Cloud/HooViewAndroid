/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import java.util.List;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.easyvaas.elapp.adapter.oldItem.VideoMineAdapterItem;
import com.easyvaas.elapp.bean.video.VideoEntity;

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
