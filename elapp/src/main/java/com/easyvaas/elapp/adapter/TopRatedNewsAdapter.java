package com.easyvaas.elapp.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.bean.news.TopRatedModel.RecommendBean;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.ImportNewsListHeaderView;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.ViewHolder;
import static com.easyvaas.elapp.adapter.ImportNewsAdapter.ITEM_TYPE_HEADER;
import static com.easyvaas.elapp.adapter.ImportNewsAdapter.ITEM_TYPE_NORMAL;

/**
 * Date   2017/4/10
 * Editor  Misuzu
 * 首页要闻Adpater  手都写麻了。。。-A-
 */

public class TopRatedNewsAdapter extends Adapter {

    private static final int TYPE_HEADER = 1; //banner
    private static final int TYPE_NO_PIC = 2; //无图
    private static final int TYPE_ONE_PIC = 3;//一图
    private static final int TYPE_MULTI_PIC = 4;//多图
    private static final int TYPE_PERSON = 5; // 牛人推荐
    private static final int TYPE_SPECIAL_TOPIC = 6;//专栏
    private Context mContext;
    private TopRatedModel mTopRatedModel;

    public TopRatedNewsAdapter(Context context, TopRatedModel topRatedModel) {
        mContext = context;
        mTopRatedModel = topRatedModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(new ImportNewsListHeaderView(mContext));
            case TYPE_MULTI_PIC:
                return new MultiImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_multi_news,parent,false));
            case TYPE_NO_PIC:
                return new NoImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_img_news,parent,false));
            case TYPE_PERSON:
                return new PersonViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_powerful_person_news,parent,false));
            case TYPE_SPECIAL_TOPIC:
                return new TopicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_special_topic_news,parent,false));
            case TYPE_ONE_PIC:
                return new OneImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_one_img_news,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
            if (mTopRatedModel.getHomeNews() !=null && mTopRatedModel.getHomeNews().size()>0 )
            {
                HomeNewsBean mNewsBean = mTopRatedModel.getHomeNews().get(position-1);
                HomeNewsBean mNewsBeanNext = null;
                if (mTopRatedModel.getHomeNews().size()>position)   // 取下一条数据的数据类型 来判断是否需要隐藏下划线
                    mNewsBeanNext = mTopRatedModel.getHomeNews().get(position);
                switch (mNewsBean.getType())
                {
                    case 0:
                        int coverSize = mNewsBean.getCover().size(); //根据图片个数来判断加载布局类型
                        if (coverSize == 0)
                            ((NoImgViewHolder) holder).initData(mNewsBean,mNewsBeanNext);
                        else if(coverSize == 1)
                            ((OneImgViewHolder) holder).initData(mNewsBean,mNewsBeanNext);
                        else
                            ((MultiImgViewHolder) holder).initData(mNewsBean,mNewsBeanNext);
                       break;
                    case 1:
                        ((TopicViewHolder) holder).initData(mNewsBean);
                        break;
                    case 2:
                        ((PersonViewHolder) holder).initData(mTopRatedModel.getRecommend());

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (mTopRatedModel != null && mTopRatedModel.getHomeNews() != null) {
            count = count + mTopRatedModel.getHomeNews().size();
        }
        return count;
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
        this.mTopRatedModel = topRatedModel;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE_HEADER : ITEM_TYPE_NORMAL;
    }

    /**
     * banner类型
     */
    private class HeaderViewHolder extends ViewHolder {
        public ImportNewsListHeaderView importNewsListHeaderView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            importNewsListHeaderView = (ImportNewsListHeaderView) itemView;
        }
    }

    /**
     * 多图类型
     */
    public class MultiImgViewHolder extends ViewHolder {

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
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private MultiImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext,data.getTitle(),data.getId()+"");
        }

        private void initData(HomeNewsBean data,HomeNewsBean nextData)
        {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(data.getViewCount());
            mItemNewsTime.setText(DateTimeUtil.getShortTime(mContext,data.getTime()));
            Utils.showNewsImage(data.getCover().get(0),mItemNewsPic1);
            Utils.showNewsImage(data.getCover().get(1),mItemNewsPic2);
            Utils.showNewsImage(data.getCover().get(2),mItemNewsPic3);
            if (nextData != null)
            {
                if (nextData.getType() == 1 || nextData.getType() ==2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 单图类型
     */
    public class OneImgViewHolder extends ViewHolder {

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
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private OneImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext,data.getTitle(),data.getId()+"");

        }

        private void initData(HomeNewsBean data,HomeNewsBean nextData)
        {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(data.getViewCount());
            mItemNewsTime.setText(DateTimeUtil.getShortTime(mContext,data.getTime()));
            Utils.showNewsImage(data.getCover().get(0),mItemNewsPic);
            if (nextData != null)
            {
                if (nextData.getType() == 1 || nextData.getType() ==2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 无图类型
     */
    public class NoImgViewHolder extends ViewHolder {

        @BindView(R.id.item_news_title)
        TextView mItemNewsTitle;
        @BindView(R.id.item_news_time)
        TextView mItemNewsTime;
        @BindView(R.id.item_news_readcounts)
        TextView mItemNewsReadcounts;
        @BindView(R.id.divider_line)
        View mDividerLine;
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private NoImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext,data.getTitle(),data.getId()+"");
        }

        private void initData(HomeNewsBean data,HomeNewsBean nextData)
        {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(data.getViewCount());
            mItemNewsTime.setText(DateTimeUtil.getShortTime(mContext,data.getTime()));
            if (nextData != null)
            {
                if (nextData.getType() == 1 || nextData.getType() ==2)
                    mDividerLine.setVisibility(View.GONE);
                else
                    mDividerLine.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 专题类型
     */
    public class TopicViewHolder extends ViewHolder {

        @BindView(R.id.item_topic_img)
        ImageView mItemTopicImg;
        @BindView(R.id.item_topic_title)
        TextView mItemTopicTitle;
        @BindView(R.id.item_topic_readcounts)
        TextView mItemTopicReadcounts;
        @BindView(R.id.item_news_layout)
        FrameLayout mItemNewsLayout;
        HomeNewsBean data;

        private TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            // Aya : 2017/4/11 专题跳转
        }

        private void initData(HomeNewsBean data)
        {
            mItemTopicTitle.setText(data.getTitle());
            mItemTopicReadcounts.setText(data.getViewCount());
            Utils.showNewsImage(data.getCover().get(0),mItemTopicImg);
        }
    }

    /**
     * 牛人推荐类型
     */
    public class PersonViewHolder extends ViewHolder {

        @BindView(R.id.item_recyclerview)
        RecyclerView mItemRecyclerview;
        RecommendPersonAdapter mPersonAdapter;
        List<RecommendBean> datas;

        private PersonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mItemRecyclerview.setHasFixedSize(true);
            mItemRecyclerview.setAdapter(mPersonAdapter = new RecommendPersonAdapter(mContext,datas));
        }

        private void initData(List<RecommendBean> data)
        {
            datas.addAll(data);
            mPersonAdapter.notifyDataSetChanged();
        }
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
