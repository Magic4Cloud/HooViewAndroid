package com.easyvaas.elapp.adapter.news;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.adapter.RecommendPersonAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.bean.news.TopRatedModel.RecommendBean;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.news.TopicActivity;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.ImportNewsListHeaderView;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/4/10
 * Editor  Misuzu
 * 首页要闻Adpater  手都写麻了。。。-A-
 */

public class TopRatedNewsMyAdapter extends MyBaseAdapter<TopRatedModel.HomeNewsBean> {

    private static final int TYPE_HEADER = 1; //banner
    private static final int TYPE_NO_PIC = 2; //无图
    private static final int TYPE_ONE_PIC = 3;//一图
    private static final int TYPE_MULTI_PIC = 4;//多图
    private static final int TYPE_PERSON = 5; // 牛人推荐
    private static final int TYPE_SPECIAL_TOPIC = 6;//专栏
    private TopRatedModel mTopRatedModel;

    public TopRatedNewsMyAdapter(TopRatedModel topRatedModel) {
        super(topRatedModel.getHomeNews());
        mTopRatedModel = topRatedModel;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(new ImportNewsListHeaderView(mContext));
            case TYPE_MULTI_PIC:
                return new MultiImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_multi_news, parent, false));
            case TYPE_NO_PIC:
                return new NoImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_img_news, parent, false));
            case TYPE_PERSON:
                return new PersonViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_powerful_person_news, parent, false));
            case TYPE_SPECIAL_TOPIC:
                return new TopicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_special_topic_news, parent, false));
            case TYPE_ONE_PIC:
                return new OneImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_one_img_news, parent, false));
        }
        return null;
    }

    @Override
    protected void convert(BaseViewHolder holder, TopRatedModel.HomeNewsBean item) {
        int position = holder.getLayoutPosition();
        if (position == 0) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mTopRatedModel.getHooview() != null) {
                headerViewHolder.importNewsListHeaderView.setHooviewNews(mTopRatedModel.getHooview());
            }
            if (mTopRatedModel.getBannerModel() != null) {
                headerViewHolder.importNewsListHeaderView.setBannerModel(mTopRatedModel.getBannerModel());
            }
            if (mTopRatedModel.getIndex() != null) {
                headerViewHolder.importNewsListHeaderView.setExponentListModel(mTopRatedModel.getIndex());
            }
        } else {
            if (mData != null && mData.size() > 0) {
                HomeNewsBean mNewsBean = mData.get(position);
                HomeNewsBean mNewsBeanNext = null;
                if (mData.size() > position + 1)   // 取下一条数据的数据类型 来判断是否需要隐藏下划线
                    mNewsBeanNext = mData.get(position + 1);
                switch (mNewsBean.getType()) {
                    case 0:
                        int coverSize = mNewsBean.getCover().size(); //根据图片个数来判断加载布局类型
                        if (coverSize == 0)
                            ((NoImgViewHolder) holder).initData(mNewsBean, mNewsBeanNext);
                        else if (coverSize == 1)
                            ((OneImgViewHolder) holder).initData(mNewsBean, mNewsBeanNext);
                        else
                            ((MultiImgViewHolder) holder).initData(mNewsBean, mNewsBeanNext);
                        break;
                    case 1:
                        ((TopicViewHolder) holder).initData(mNewsBean);
                        break;
                    case 2:
                        ((PersonViewHolder) holder).initData(mTopRatedModel.getRecommend());
                        break;

                }
            }
        }
    }





    /**
     * 设置banner数据
     */
    public void setBannerModel(BannerModel bannerModel) {
        mTopRatedModel.setBannerModel(bannerModel);
        notifyItemChanged(0);
    }

    /**
     * 设置新闻列表数据
     */
    public void setTopRatedModel(TopRatedModel topRatedModel) {
        mTopRatedModel.setRecommend(topRatedModel.getRecommend());
        mTopRatedModel.setHooview(topRatedModel.getHooview());
        mTopRatedModel.setIndex(topRatedModel.getIndex());
        mTopRatedModel.setHomeNews(topRatedModel.getHomeNews());
        setNewData(topRatedModel.getHomeNews());
        notifyDataSetChanged();
    }

    public TopRatedModel getTopRatedModel() {
        return mTopRatedModel;
    }

    @Override
    protected int getItemViewByType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        if (mTopRatedModel.getHomeNews() != null && mTopRatedModel.getHomeNews().size() > 0) {
            HomeNewsBean data = mTopRatedModel.getHomeNews().get(position);
            switch (data.getType()) {
                case 0:
                    int coverSize = data.getCover().size(); //根据图片个数来判断加载布局类型
                    if (coverSize == 0)
                        return TYPE_NO_PIC;
                    else if (coverSize == 1)
                        return TYPE_ONE_PIC;
                    else
                        return TYPE_MULTI_PIC;
                case 1:
                    return TYPE_SPECIAL_TOPIC;
                case 2:
                    return TYPE_PERSON;
            }
        }
        return TYPE_NO_PIC;
    }


    /**
     * banner类型
     */
    private class HeaderViewHolder extends BaseViewHolder {
        public ImportNewsListHeaderView importNewsListHeaderView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            importNewsListHeaderView = (ImportNewsListHeaderView) itemView;
        }
    }

    /**
     * 多图类型
     */
    public class MultiImgViewHolder extends BaseViewHolder {

        @BindView(R.id.item_news_title)
        TextView mItemNewsTitle;
        @BindView(R.id.item_news_pic1)
        ImageView mItemNewsPic1;
        @BindView(R.id.item_news_pic2)
        ImageView mItemNewsPic2;
        @BindView(R.id.item_news_pic3)
        ImageView mItemNewsPic3;
        @BindView(R.id.item_news_time)
        TextView mItemNewsTime;
        @BindView(R.id.item_news_readcounts)
        TextView mItemNewsReadcounts;
        @BindView(R.id.divider_line)
        View mDividerLine;
        HomeNewsBean data;

        private MultiImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initData(HomeNewsBean data, HomeNewsBean nextData) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText( NumberUtil.format(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
            Utils.showNewsImage(data.getCover().get(0), mItemNewsPic1);
            Utils.showNewsImage(data.getCover().get(1), mItemNewsPic2);
            Utils.showNewsImage(data.getCover().get(2), mItemNewsPic3);
            if (nextData != null) {
                if (nextData.getType() == 1 || nextData.getType() == 2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 单图类型
     */
    public class OneImgViewHolder extends BaseViewHolder {

        @BindView(R.id.item_news_pic)
        ImageView mItemNewsPic;
        @BindView(R.id.item_news_title)
        TextView mItemNewsTitle;
        @BindView(R.id.item_news_time)
        TextView mItemNewsTime;
        @BindView(R.id.item_news_readcounts)
        TextView mItemNewsReadcounts;
        @BindView(R.id.divider_line)
        View mDividerLine;
        HomeNewsBean data;

        private OneImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initData(HomeNewsBean data, HomeNewsBean nextData) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText( NumberUtil.format(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
            Utils.showNewsImage(data.getCover().get(0), mItemNewsPic);
            if (nextData != null) {
                if (nextData.getType() == 1 || nextData.getType() == 2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 无图类型
     */
    public class NoImgViewHolder extends BaseViewHolder {

        @BindView(R.id.item_news_title)
        TextView mItemNewsTitle;
        @BindView(R.id.item_news_time)
        TextView mItemNewsTime;
        @BindView(R.id.item_news_readcounts)
        TextView mItemNewsReadcounts;
        @BindView(R.id.divider_line)
        View mDividerLine;
        HomeNewsBean data;

        private NoImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initData(HomeNewsBean data, HomeNewsBean nextData) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(NumberUtil.format(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
            if (nextData != null) {
                if (nextData.getType() == 1 || nextData.getType() == 2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 专题类型
     */
    public class TopicViewHolder extends BaseViewHolder {

        @BindView(R.id.item_topic_img)
        ImageView mItemTopicImg;
        @BindView(R.id.item_topic_title)
        TextView mItemTopicTitle;
        @BindView(R.id.item_topic_readcounts)
        TextView mItemTopicReadcounts;
        HomeNewsBean data;

        private TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initData(HomeNewsBean data) {
            mItemTopicTitle.setText(data.getTitle());
            mItemTopicReadcounts.setText(NumberUtil.format(data.getViewCount()));
            Utils.showNewsImage(data.getCover().get(0), mItemTopicImg);
        }
    }

    /**
     * 牛人推荐类型
     */
    public class PersonViewHolder extends BaseViewHolder {

        @BindView(R.id.item_recyclerview)
        RecyclerView mItemRecyclerview;
        RecommendPersonAdapter mPersonAdapter;
        List<RecommendBean> datas;

        private PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            datas = new ArrayList<>();
            mItemRecyclerview.setHasFixedSize(true);
            mItemRecyclerview.setLayoutManager(manager);
            mItemRecyclerview.setPadding((int) ViewUtil.dp2Px(mContext,12),0,0,0);
            mItemRecyclerview.setClipToPadding(false);

        }

        private void initData(List<RecommendBean> data) {
            if (mItemRecyclerview.getAdapter() == null)
            {
                datas.addAll(data);
                mItemRecyclerview.setAdapter(mPersonAdapter = new RecommendPersonAdapter(mContext, datas));
                mPersonAdapter.notifyDataSetChanged();
            }

        }
    }

    // 设置点击事件
    @Override
    protected void initOnItemClickListener() {

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId())
                {
                    case R.id.item_news_layout:
                        Utils.showNewsDetail(mContext,mData.get(position).getTitle() ,mData.get(position).getId());
                        break;
                    case R.id.item_topic_layout:
                        Intent intent = new Intent(mContext,TopicActivity.class);
                        intent.putExtra("id",mData.get(position).getId());
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }

    /**
     * 插入已读新闻到数据库
     */
    private void insertHistoryRecord(HomeNewsBean newsModel) {
        String mVideoId = newsModel.getId() + "";
        if (!TextUtils.isEmpty(mVideoId) && !RealmHelper.getInstance().queryReadRecordId(mVideoId)) {
            ReadRecord bean = new ReadRecord();
            bean.setId(String.valueOf(mVideoId));
            bean.setPic(newsModel.getCover().size() > 0 ? newsModel.getCover().get(0) : "");
            bean.setTitle(newsModel.getTitle());
            bean.setTime(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
            bean.setCount(newsModel.getViewCount());
            RealmHelper.getInstance().insertReadRecord(bean, 30);
        }
    }
}
