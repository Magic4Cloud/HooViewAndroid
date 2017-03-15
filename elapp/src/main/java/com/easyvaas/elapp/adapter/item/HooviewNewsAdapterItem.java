package com.easyvaas.elapp.adapter.item;


import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.db.RealmHelper;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

public class HooviewNewsAdapterItem implements AdapterItem<NewsItemModel> {
    ImageView ivThumbnail;
    TextView tvTitle;
    TextView tvTime;
    TextView tvWatchCount;
    private NewsItemModel mNewsItemModel;

    @Override
    public int getLayoutResId() {
        return R.layout.item_common_news;
    }

    @Override
    public void onBindViews(View root) {
        ivThumbnail = (ImageView) root.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView) root.findViewById(R.id.tv_title);
        tvTime = (TextView) root.findViewById(R.id.tv_time);
        tvWatchCount = (TextView) root.findViewById(R.id.tv_watch_count);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertHistoryRecord(mNewsItemModel);
                Utils.showNewsDetail(mContext, mNewsItemModel.getTitle(), mNewsItemModel.getId() + "");
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final NewsItemModel newsModel, int position) {
        if (newsModel != null) {
            this.mNewsItemModel = newsModel;
            Utils.showImage(newsModel.getCover(), R.drawable.account_bitmap_list, ivThumbnail);
            tvTitle.setText(newsModel.getTitle());
            //tvTime.setText(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
            tvTime.setText(mContext.getString(R.string.eye_news_watch_count,
                    StringUtil.formatThousand(newsModel.getViewCount())));
            tvWatchCount.setVisibility(View.GONE);
            //tvWatchCount.setText(mContext.getString(R.string.news_watch_count,
            //    StringUtil.formatThousand(newsModel.getViewCount())));
        }
    }

    private void insertHistoryRecord(NewsItemModel newsModel) {
        String mVideoId = newsModel.getId()+"";
        if (!TextUtils.isEmpty(mVideoId)&&!RealmHelper.getInstance().queryReadRecordId(mVideoId)) {
            ReadRecord bean = new ReadRecord();
            bean.setId(String.valueOf(mVideoId));
            bean.setPic(newsModel.getCover());
            bean.setTitle(newsModel.getTitle());
            bean.setTime(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
            bean.setCount(newsModel.getViewCount());
            RealmHelper.getInstance().insertReadRecord(bean, 30);
        }
    }
}
