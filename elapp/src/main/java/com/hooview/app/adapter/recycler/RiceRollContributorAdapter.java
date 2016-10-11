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

import com.hooview.app.adapter.item.RankUserAdapterTopItem;
import com.hooview.app.adapter.item.RiceRollContributorAdapterItem;
import com.hooview.app.bean.user.RankUserEntity;

public class RiceRollContributorAdapter extends CommonRcvAdapter<RankUserEntity> {
    private static final Object ITEM_TYPE_HEADER = 1;
    public final static Object ITEM_TYPE_FIRST = 5;
    public final static Object ITEM_TYPE_SECOND = 6;
    public final static Object ITEM_TYPE_THIRD = 7;

    private Context mContext;

    public RiceRollContributorAdapter(Context context, List<RankUserEntity> data) {
        super(data);
        mContext = context;
    }

    @NonNull @Override
    public AdapterItem<RankUserEntity> getItemView(Object type) {
        /*if (type == ITEM_TYPE_HEADER) {
            return new HeaderRiceRollContributorAdapterItem(mContext);
        }*/
        if (type == ITEM_TYPE_FIRST || type == ITEM_TYPE_SECOND || type == ITEM_TYPE_THIRD) {
            return new RankUserAdapterTopItem(mContext, type);
        }
        return new RiceRollContributorAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(RankUserEntity entity) {
        /*if (entity.getItemType() == RankUserEntity.ITEM_TYPE_HEADER) {
            return ITEM_TYPE_HEADER;
        }*/
        if (entity.getRank() == 1) {
            return ITEM_TYPE_FIRST;
        } else if (entity.getRank() == 2) {
            return ITEM_TYPE_SECOND;
        } else if (entity.getRank() == 3) {
            return ITEM_TYPE_THIRD;
        }
        return super.getItemViewType(entity);
    }
}
