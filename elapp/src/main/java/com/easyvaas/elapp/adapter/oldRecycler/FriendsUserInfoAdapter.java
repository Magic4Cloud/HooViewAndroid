/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.easyvaas.elapp.adapter.oldItem.HeaderFriendsInfoAdapterItem;
import com.easyvaas.elapp.adapter.oldItem.LivingFriendsInfoAdapterItem;
import com.easyvaas.elapp.bean.video.VideoEntity;

public class FriendsUserInfoAdapter extends CommonRcvAdapter {
    private static final Object ITEM_TYPE_HEADER = 1;
    private static final Object ITEM_TYPE_LIVING = 2;
    private Context mContext;
    private OnClickUserItemViewListener mOnClickUserItemViewListener;

    public FriendsUserInfoAdapter(List data, Context context) {
        super(data);
        mContext = context;
    }

    @NonNull @Override
    public AdapterItem getItemView(Object type) {
        if (type == ITEM_TYPE_LIVING) {
            LivingFriendsInfoAdapterItem mLivingFriendsInfoAdapterItem = new LivingFriendsInfoAdapterItem(mContext);
            mLivingFriendsInfoAdapterItem.setOnItemClickViewListener(mOnClickUserItemViewListener);
            return mLivingFriendsInfoAdapterItem;
        } else {
            HeaderFriendsInfoAdapterItem mHeaderFriendsInfoAdapterItem = new HeaderFriendsInfoAdapterItem(mContext);
            mHeaderFriendsInfoAdapterItem.setOnItemClickViewListener(mOnClickUserItemViewListener);
            return mHeaderFriendsInfoAdapterItem;
        }
    }

    @Override
    public Object getItemViewType(Object type) {
        if (type instanceof VideoEntity) {
            return ITEM_TYPE_LIVING;
        } else {
            return ITEM_TYPE_HEADER;
        }
    }

    public void setOnClickUserItemViewListener(OnClickUserItemViewListener onClickUserItemViewListener) {
        mOnClickUserItemViewListener = onClickUserItemViewListener;
    }

    public interface OnClickUserItemViewListener {
        //header
        void onUserInfoBackClick();

        void onFansItemClick();

        void onFollowItemClick();

        void onUserPhotoClick();

        void onShareItemClick();
    }
}
