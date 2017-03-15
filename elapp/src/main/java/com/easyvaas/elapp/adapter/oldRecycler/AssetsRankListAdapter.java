/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.oldItem.AdapterFooterItem;
import com.easyvaas.elapp.adapter.oldItem.AdapterHeadItem;
import com.easyvaas.elapp.adapter.oldItem.AssetsRankAdapterItem;
import com.easyvaas.elapp.adapter.oldItem.HeaderSubTitleBarAdapterItem;
import com.easyvaas.elapp.bean.user.RankUserEntity;

import java.util.List;

public class AssetsRankListAdapter extends CommonRcvAdapter<RankUserEntity> {
    private final static Object ITEM_TYPE_HEADER = 1;
    private final static Object ITEM_TYPE_FOOTER = 2;
    private final static Object ITEM_TYPE_TITLE_PINNED = 4;

    private Context mContext;

    public AssetsRankListAdapter(Context context, List<RankUserEntity> data) {
        super(data);
        this.mContext = context;
    }

    @NonNull @Override
    public AdapterItem<RankUserEntity> getItemView(Object type) {
        if (type == ITEM_TYPE_HEADER) {
            return new AdapterHeadItem(mContext);
        } else if (type == ITEM_TYPE_FOOTER) {
            return new AdapterFooterItem();
        } else if (type == ITEM_TYPE_TITLE_PINNED) {
            return new HeaderSubTitleBarAdapterItem();
        }
        return new AssetsRankAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(RankUserEntity assetsRankUserEntity) {
        if (assetsRankUserEntity.getPinned() == RankUserEntity.IS_HEADER) {
            return ITEM_TYPE_HEADER;
        } else if (assetsRankUserEntity.getPinned() == RankUserEntity.IS_FOOTER) {
            return ITEM_TYPE_FOOTER;
        } else if (assetsRankUserEntity.getPinned() == RankUserEntity.IS_TITLE_PINNED) {
            return ITEM_TYPE_TITLE_PINNED;
        }
        return super.getItemViewType(assetsRankUserEntity);
    }
}
