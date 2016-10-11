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

import com.hooview.app.adapter.item.PushMessageAdapterItem;
import com.hooview.app.bean.user.UserEntity;

public class PushMessageRcvAdapter extends CommonRcvAdapter<UserEntity> {

    private boolean isPush;

    public PushMessageRcvAdapter(List<UserEntity> data, boolean isPush) {
        super(data);
        this.isPush = isPush;
    }

    @NonNull @Override
    public AdapterItem<UserEntity> getItemView(Object type) {
        return new PushMessageAdapterItem(isPush);
    }

    public void setPush(boolean push) {
        isPush = push;
    }
}
