package com.easyvaas.common.chat.adapter;

import java.util.List;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.adapter.item.SelectFriendsAdapterItem;

public class SelectFriendsAdapter extends CommonRcvAdapter<String> {
    public SelectFriendsAdapter(List<String> data) {
        super(data);
    }

    @NonNull @Override
    public AdapterItem<String> getItemView(Object type) {
        return new SelectFriendsAdapterItem();
    }
}
