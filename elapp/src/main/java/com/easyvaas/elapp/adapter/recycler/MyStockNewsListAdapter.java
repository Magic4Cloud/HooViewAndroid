package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.item.MyStockNewsItem;
import com.easyvaas.elapp.adapter.item.NewsHeaderAdapterItem;
import com.easyvaas.elapp.bean.news.MyStockNewsModel;

import java.util.List;

public class MyStockNewsListAdapter extends CommonRcvAdapter<MyStockNewsModel.NewsEntity> {
    private Context mContext;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_NORMAL = 2;

    public MyStockNewsListAdapter(Context context, List data) {
        super(data);
        this.mContext = context;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        if (TYPE_HEADER == (Integer) type) {
            return new NewsHeaderAdapterItem(R.drawable.bg_news_flash);
        } else {
            return new MyStockNewsItem(mContext);
        }
    }

    @Override
    public Object getItemViewType(MyStockNewsModel.NewsEntity o) {
        return o.isHeader() ? TYPE_HEADER : TYPE_NORMAL;
    }

}
