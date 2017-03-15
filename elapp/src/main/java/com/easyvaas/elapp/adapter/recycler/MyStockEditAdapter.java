package com.easyvaas.elapp.adapter.recycler;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.MarketExponentItem;
import com.easyvaas.elapp.adapter.item.MarketStockItem;

import java.util.List;

public class MyStockEditAdapter extends CommonRcvAdapter {
    private static final Object ITEM_TYPE_EXPONENT = 1;
    private static final Object ITEM_TYPE_STOCK = 2;

    public MyStockEditAdapter(List data) {
        super(data);
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        if (type == ITEM_TYPE_EXPONENT) {
            return new MarketExponentItem();
        } else {
            return new MarketStockItem();
        }
    }

    @Override
    public Object getItemViewType(Object position) {
        if ((int
                ) position == 0) {
            return ITEM_TYPE_EXPONENT;
        } else {
            return ITEM_TYPE_STOCK;
        }
    }
}
