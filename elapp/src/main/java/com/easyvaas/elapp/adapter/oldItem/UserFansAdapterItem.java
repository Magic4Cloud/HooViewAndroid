/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.view.View;

import com.easyvaas.elapp.bean.user.UserEntity;

public class UserFansAdapterItem extends UserAdapterItem {

    public UserFansAdapterItem() {
        super(TYPE_FANS);
    }

    @Override
    public void onUpdateViews(UserEntity model, int position) {
        super.onUpdateViews(model, position);
        followCb.setVisibility(View.GONE);
    }
}
