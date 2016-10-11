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

import com.hooview.app.adapter.item.HomeTopicAdapterItem;
import com.hooview.app.bean.TopicEntity;

public class HomeTopicListAdapter extends CommonRcvAdapter<TopicEntity> {
    public HomeTopicListAdapter(List<TopicEntity> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<TopicEntity> getItemView(Object type) {
        return new HomeTopicAdapterItem();
    }
}
