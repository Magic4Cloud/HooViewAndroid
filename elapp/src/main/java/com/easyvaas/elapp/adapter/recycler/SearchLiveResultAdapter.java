package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.SearchVideoAdapterItem;
import com.easyvaas.elapp.bean.video.VideoEntity;

import java.util.List;

public class SearchLiveResultAdapter extends CommonRcvAdapter {
    private Context mContext;
    private List<VideoEntity> liveVideoList;

    public SearchLiveResultAdapter(Context context, List data) {
        super(data);
        mContext = context;
        liveVideoList = data;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new SearchVideoAdapterItem(mContext);
    }

    @Override
    public Object getItemViewType(Object object) {
        return 1;
    }
}
