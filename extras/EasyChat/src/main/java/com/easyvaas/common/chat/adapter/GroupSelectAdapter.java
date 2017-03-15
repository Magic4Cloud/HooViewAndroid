package com.easyvaas.common.chat.adapter;

import java.util.List;

import android.support.annotation.NonNull;

import com.easemob.chat.EMGroup;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.adapter.item.GroupSelectAdapterItem;

public class GroupSelectAdapter extends CommonRcvAdapter<EMGroup> {
    public GroupSelectAdapter(List<EMGroup> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<EMGroup> getItemView(Object type) {
        return new GroupSelectAdapterItem();
    }
}
