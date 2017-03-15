package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.item.SearchNewsItem;
import com.easyvaas.elapp.bean.news.NewsItemModel;

import java.util.List;

public class SearchNewsResultAdapter extends CommonRcvAdapter<NewsItemModel> {
    private Context mContext;
    private List<NewsItemModel> newsItemModelList;

    public SearchNewsResultAdapter(Context context, List data) {
        super(data);
        mContext = context;
        newsItemModelList = data;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object type) {
        return new SearchNewsItem(mContext);
    }

    @Override
    public Object getItemViewType(NewsItemModel object) {
        return 1;
    }
}
