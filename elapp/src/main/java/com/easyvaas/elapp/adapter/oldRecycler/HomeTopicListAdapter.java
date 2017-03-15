/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldRecycler;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.oldItem.HomeTopicAdapterItem;
import com.easyvaas.elapp.bean.TopicEntity;

import java.util.List;

public class HomeTopicListAdapter extends CommonRcvAdapter<TopicEntity> {
    public HomeTopicListAdapter(List<TopicEntity> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<TopicEntity> getItemView(Object type) {
        return new HomeTopicAdapterItem();
    }
}
