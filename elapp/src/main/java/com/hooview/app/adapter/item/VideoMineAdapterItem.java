/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.view.View;

import com.hooview.app.bean.video.VideoEntity;

public class VideoMineAdapterItem extends VideoAdapterItem {

    private VideoItemOptionListener listener;

    public VideoMineAdapterItem(VideoItemOptionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onUpdateViews(final VideoEntity videoEntity, int position) {
        super.onUpdateViews(videoEntity, position);

        videoSetIv.setVisibility(View.VISIBLE);
        videoSetIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOperationClick(videoEntity);
            }
        });
    }


    public interface VideoItemOptionListener {
        void onOperationClick(VideoEntity selectVideo);
    }
}
