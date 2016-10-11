/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.HeaderFieldVideoAdapterItem;
import com.hooview.app.adapter.item.HeaderSliderAdapterItem;
import com.hooview.app.adapter.item.HeaderSubTitleBarAdapterItem;
import com.hooview.app.bean.video.VideoEntity;

public class VideoRcvAdapter extends CommonRcvAdapter<VideoEntity> {
    private static final Object ITEM_TYPE_HEADER_SQUARE = 13;
    private static final Object ITEM_TYPE_HEADER_TITLE_BAR = 14;
    private static final Object ITEM_TYPE_HEADER_SLIDER = 15;
    private Context mContext;

    public VideoRcvAdapter(Context context, List<VideoEntity> data) {
        super(data);
        mContext = context;
    }

    @NonNull @Override
    public AdapterItem<VideoEntity> getItemView(Object type) {
        if (type == ITEM_TYPE_HEADER_SLIDER) {
            return new HeaderSliderAdapterItem(mContext);
        } else if (type == ITEM_TYPE_HEADER_SQUARE) {
            return new HeaderFieldVideoAdapterItem(mContext);
        } else if (type == ITEM_TYPE_HEADER_TITLE_BAR) {
            return new HeaderSubTitleBarAdapterItem();
        }
        return new HeaderFieldVideoAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(VideoEntity videoEntity) {
        if (videoEntity.getPinned() == VideoEntity.IS_PINNED_LIST_SLIDER_BAR) {
            return ITEM_TYPE_HEADER_SLIDER;
        } else if (videoEntity.getPinned() == VideoEntity.IS_PINNED_LIST_GIRL) {
            return ITEM_TYPE_HEADER_SQUARE;
        } else if (videoEntity.getPinned() == VideoEntity.IS_PINNED_LIST_TITLE_BAR) {
            return ITEM_TYPE_HEADER_TITLE_BAR;
        }
        return ITEM_TYPE_HEADER_SQUARE;
    }
}
