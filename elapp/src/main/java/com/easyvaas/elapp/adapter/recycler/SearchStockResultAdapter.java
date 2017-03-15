package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.SearchStockItem;

import java.util.List;

public class SearchStockResultAdapter extends CommonRcvAdapter {
    private Context mContext;
    private boolean hasAddBtn;
    public SearchStockResultAdapter(Context context, List data,boolean hasaddBtn) {
        super(data);
        mContext = context;
        this.hasAddBtn=hasaddBtn;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new SearchStockItem(mContext,hasAddBtn);
    }

    @Override
    public Object getItemViewType(Object object) {
        return 1;
    }
}
