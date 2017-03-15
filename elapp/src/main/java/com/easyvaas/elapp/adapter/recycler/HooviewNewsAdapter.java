package com.easyvaas.elapp.adapter.recycler;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.HooviewNewsAdapterItem;

import java.util.List;

public class HooviewNewsAdapter extends CommonRcvAdapter {
    public HooviewNewsAdapter(List data) {
        super(data);
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new HooviewNewsAdapterItem();
    }

    @Override
    public Object getItemViewType(Object o) {
        return 1;
    }

}
