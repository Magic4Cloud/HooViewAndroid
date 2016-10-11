/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.recycler;

import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.AdapterFooterItem;
import com.hooview.app.adapter.item.AdapterHeadItem;
import com.hooview.app.adapter.item.UserFollowerAdapterItem;
import com.hooview.app.adapter.item.VideoAdapterItem;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.video.VideoEntity;

public class SearchAdapter extends CommonRcvAdapter {
    private static final Object TYPE_ITEM_USER = 1;
    private static final Object TYPE_ITEM_HEADER = 2;
    private static final Object TYPE_ITEM_FOOT = 3;
    private static final Object TYPE_ITEM_VIDEO = 4;

    private Context mContext;

    public SearchAdapter(Context context, ArrayList data) {
        super(data);
        this.mContext = context;
    }

    @NonNull @Override
    public AdapterItem getItemView(Object type) {
        if (type == TYPE_ITEM_HEADER) {
            return new AdapterHeadItem(mContext);
        } else if (type == TYPE_ITEM_FOOT) {
            return new AdapterFooterItem();
        } else if (type == TYPE_ITEM_USER) {
            return new UserFollowerAdapterItem();
        } else if (type == TYPE_ITEM_VIDEO) {
            return new VideoAdapterItem();
        }
        return new AdapterHeadItem(mContext);
    }

    @Override
    public Object getItemViewType(Object object) {
        if (object instanceof UserEntity) {
            if (((UserEntity) object).getPinned() == UserEntity.IS_PINNED_LIST_HEADER) {
                return TYPE_ITEM_HEADER;
            } else if (((UserEntity) object).getPinned() == UserEntity.IS_PINNED) {
                return TYPE_ITEM_FOOT;
            }
            return TYPE_ITEM_USER;
        } else if (object instanceof VideoEntity) {
            if (((VideoEntity) object).getPinned() == VideoEntity.IS_PINNED_HEADER) {
                return TYPE_ITEM_HEADER;
            }
            return TYPE_ITEM_VIDEO;
        }
        return super.getItemViewType(object);
    }
}
