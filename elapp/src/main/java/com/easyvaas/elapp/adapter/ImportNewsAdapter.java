package com.easyvaas.elapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.market.ExponentListModel;
import com.easyvaas.elapp.bean.news.ImportantNewsModel;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.ImportNewsListHeaderView;
import com.squareup.picasso.Picasso;

public class ImportNewsAdapter extends RecyclerView.Adapter {
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_NORMAL = 2;
    private Context mContext;
    private ImportantNewsModel mImportantNewsModel;
    private BannerModel mBannerModel;
    private ExponentListNewModel mExponentListModel;

    public ImportNewsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new HeaderViewHolder(new ImportNewsListHeaderView(mContext));
        } else {
            return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_common_news, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       if (position == 0) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mImportantNewsModel != null && mImportantNewsModel.getHuoyan() != null) {
                headerViewHolder.importNewsListHeaderView.setHooviewNews(mImportantNewsModel.getHuoyan());
            }
            if (mBannerModel != null) {
                headerViewHolder.importNewsListHeaderView.setBannerModel(mBannerModel);
            }
            if (mExponentListModel != null) {
                headerViewHolder.importNewsListHeaderView.setExponentListModel(mExponentListModel);

            }
        } else {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.setNewsModel(mImportantNewsModel.getHome_news().get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (mImportantNewsModel != null && mImportantNewsModel.getHome_news() != null) {
            count = count + mImportantNewsModel.getHome_news().size();
        }
        return count;
    }


    public void setImportantNewsModel(ImportantNewsModel importantNewsModel) {
        this.mImportantNewsModel = importantNewsModel;
        notifyDataSetChanged();
    }

    public void setBannerModel(BannerModel bannerModel) {
        this.mBannerModel = bannerModel;
        notifyItemChanged(0);
    }

    public void appendNews(ImportantNewsModel importantNewsModel) {
        if (mImportantNewsModel == null) {
            mImportantNewsModel = importantNewsModel;
        } else {
            mImportantNewsModel.getHome_news().addAll(importantNewsModel.getHome_news());
            mImportantNewsModel.getHuoyan().getNews().addAll(importantNewsModel.getHuoyan().getNews());
        }
        notifyDataSetChanged();
    }

    public void setExponentListModel(ExponentListNewModel model) {
        if (model != null) {
            mExponentListModel = model;
            notifyItemChanged(0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE_HEADER : ITEM_TYPE_NORMAL;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImportNewsListHeaderView importNewsListHeaderView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            importNewsListHeaderView = (ImportNewsListHeaderView) itemView;
        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvTime;
        TextView tvWatchCount;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvWatchCount = (TextView) itemView.findViewById(R.id.tv_watch_count);
        }

        public void setNewsModel(final NewsItemModel newsModel) {
            if (newsModel != null) {
//              Utils.showImage(newsModel.getCover(), R.drawable.account_bitmap_list, ivThumbnail);
                Picasso.with(mContext).load(newsModel.getCover()).error(R.drawable.account_bitmap_list).into(ivThumbnail);
                tvTitle.setText(newsModel.getTitle());
                tvTime.setText(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
                tvWatchCount.setText(mContext.getString(R.string.news_watch_count,
                        StringUtil.formatThousand(newsModel.getViewCount())));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertHistoryRecord(newsModel);
                    Utils.showNewsDetail(mContext, newsModel.getTitle(), newsModel.getId() + "");
                }
            });
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
