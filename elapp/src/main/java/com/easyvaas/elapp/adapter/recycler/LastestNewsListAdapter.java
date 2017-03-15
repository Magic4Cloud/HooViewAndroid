package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.item.LastestNewsItem;
import com.easyvaas.elapp.adapter.item.NewsHeaderAdapterItem;
import com.easyvaas.elapp.bean.news.LastestNewsModel;

import java.util.List;

public class LastestNewsListAdapter extends CommonRcvAdapter<LastestNewsModel.NewsFlashEntity> {
    private Context mContext;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_NORMAL = 2;

    public LastestNewsListAdapter(Context context, List data) {
        super(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        if (TYPE_HEADER == (Integer) type) {
            return new NewsHeaderAdapterItem(R.drawable.bg_optional);
        } else {
            return new LastestNewsItem(mContext);
        }
    }

    @Override
    public Object getItemViewType(LastestNewsModel.NewsFlashEntity o) {
        return o.isHeader() ? TYPE_HEADER : TYPE_NORMAL;
    }
}
