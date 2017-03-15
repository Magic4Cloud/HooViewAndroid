package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.LiveCommentAdapterItem;

import java.util.List;

public class LiveCommentAdapter extends CommonRcvAdapter {
    private Context mContext;

    public LiveCommentAdapter(Context context, List data) {
        super(data);
        mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new LiveCommentAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(Object o) {
        return 1;
    }
}
