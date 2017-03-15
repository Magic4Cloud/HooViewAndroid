package com.easyvaas.elapp.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.CollectionAdapterItem;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class CollectionRcvAdapter extends CommonRcvAdapter<Object> {
    private Context mContext;

    public CollectionRcvAdapter(List<Object> data, Context context) {
        super(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
            return new CollectionAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(Object o) {
        return super.getItemViewType(o);
    }
}

