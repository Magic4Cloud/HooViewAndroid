/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.view.View;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.hooview.app.bean.user.RankUserEntity;

public class AdapterFooterItem implements AdapterItem<RankUserEntity> {
    @Override
    public int getLayoutResId() {
        return R.layout.item_assets_rank_footer;
    }

    @Override
    public void onBindViews(View root) {
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(RankUserEntity model, int position) {

    }
}
