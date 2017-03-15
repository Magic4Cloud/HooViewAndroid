package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;

public class SearchNewsItem implements AdapterItem<NewsItemModel> {
    private Context mContext;
    private ImageView ivThumbnail;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvWatchCount;
    private View root;
    private NewsItemModel newsItemModel;

    public SearchNewsItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_common_news;
    }

    @Override
    public void onBindViews(View root) {
        this.root = root;
        ivThumbnail = (ImageView) root.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView) root.findViewById(R.id.tv_title);
        tvTime = (TextView) root.findViewById(R.id.tv_time);
        tvWatchCount = (TextView) root.findViewById(R.id.tv_watch_count);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showNewsDetail(mContext,newsItemModel.getTitle(),newsItemModel.getId()+"");
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final NewsItemModel newsModel, int position) {
        if (newsModel != null) {
            newsItemModel = newsModel;
            Utils.showImage(newsModel.getCover(), R.drawable.account_bitmap_list, ivThumbnail);
            tvTitle.setText(newsModel.getTitle());
            tvTime.setText(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
            tvWatchCount.setText(mContext.getString(R.string.news_watch_count, StringUtil.formatThousand(newsModel.getViewCount())));
        }
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showNewsDetail(mContext, newsModel.getTitle(), newsModel.getId() + "");
            }
        });
    }
}
