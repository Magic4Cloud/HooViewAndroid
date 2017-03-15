package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.LiveStockAdapterItem;
import com.easyvaas.elapp.bean.search.SearchStockModel;

import java.util.List;

public class LiveDataAdapter extends CommonRcvAdapter<SearchStockModel.DataEntity> {
    private Context mContext;

    public LiveDataAdapter(Context context, List data) {
        super(data);
        mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new LiveStockAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(SearchStockModel.DataEntity o) {
        return 1;
    }
}
