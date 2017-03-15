package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.elapp.adapter.item.GiftAdapterItem;

import java.util.List;

public class GiftAdapter extends CommonRcvAdapter<GiftEntity> {
    private Context mContext;
    private List<GiftEntity> giftEntityList;

    public GiftAdapter(Context context, List data) {
        super(data);
        mContext = context;
        giftEntityList = data;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new GiftAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(GiftEntity giftEntity) {
        return 1;
    }
}
