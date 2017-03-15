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

import com.easyvaas.elapp.adapter.oldItem.UserFansAdapterItem;
import com.easyvaas.elapp.adapter.oldItem.UserFollowerAdapterItem;
import com.easyvaas.elapp.bean.user.UserEntity;

public class UserRcvAdapter extends CommonRcvAdapter<UserEntity> {
    public static final Object TYPE_FOLLOWER = 1001;
    public static final Object TYPE_FANS = 1002;

    private Object mType;

    /**
     * @param type {@link #TYPE_FANS}, {@link #TYPE_FOLLOWER}
     */
    public UserRcvAdapter(@NonNull List<UserEntity> data, Object type) {
        super(data);
        mType = type;
    }

    @NonNull
    @Override
    public AdapterItem<UserEntity> getItemView(Object type) {
        if (TYPE_FOLLOWER == mType) {
            return new UserFollowerAdapterItem();
        } else if (TYPE_FANS == mType) {
            return new UserFansAdapterItem();
        }
        return new UserFansAdapterItem();
    }
}
