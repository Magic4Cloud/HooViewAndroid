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

import com.easyvaas.elapp.adapter.oldItem.VCallUserItem;
import com.easyvaas.elapp.bean.user.BaseUserEntity;

public class VCallUserAdapter extends CommonRcvAdapter<BaseUserEntity> {

    public VCallUserAdapter(List<BaseUserEntity> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<BaseUserEntity> getItemView(Object type) {
        return new VCallUserItem();
    }
}
