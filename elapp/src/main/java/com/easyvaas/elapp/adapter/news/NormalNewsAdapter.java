package com.easyvaas.elapp.adapter.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 新闻列表通用Adapter
 */

public class NormalNewsAdapter extends MyBaseAdapter<HomeNewsBean> {

    private static final int TYPE_NO_PIC = 2; //无图
    private static final int TYPE_ONE_PIC = 3;//一图
    private static final int TYPE_MULTI_PIC = 4;//多图

    public NormalNewsAdapter(List<HomeNewsBean> datas) {
        super(datas);
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MULTI_PIC:
                return new MultiImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_multi_news, parent, false));
            case TYPE_NO_PIC:
                return new NoImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_img_news, parent, false));
            case TYPE_ONE_PIC:
                return new OneImgViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_one_img_news, parent, false));
        }
        return null;
    }

    // 设置点击事件
    @Override
    protected void initOnItemClickListener() {

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Utils.showNewsDetail(mContext,mData.get(position).getTitle() ,mData.get(position).getId());
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder holder, HomeNewsBean item) {

        int position = holder.getLayoutPosition();

        int coverSize = item.getCover().size();
        if (coverSize == 0)
            ((NoImgViewHolder) holder).initData(item);
        else if (coverSize == 1)
            ((OneImgViewHolder) holder).initData(item);
        else
            ((MultiImgViewHolder) holder).initData(item);

    }

    @Override
    protected int getItemViewByType(int position) {

        int coverSize = mData.get(position).getCover().size(); //根据图片个数来判断加载布局类型
        if (coverSize == 0)
            return TYPE_NO_PIC;
        else if (coverSize == 1)
            return TYPE_ONE_PIC;
        else
            return TYPE_MULTI_PIC;

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
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private MultiImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext, data.getTitle(), data.getId() + "");
        }

        private void initData(HomeNewsBean data) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(String.valueOf(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
            Utils.showNewsImage(data.getCover().get(0), mItemNewsPic1);
            Utils.showNewsImage(data.getCover().get(1), mItemNewsPic2);
            Utils.showNewsImage(data.getCover().get(2), mItemNewsPic3);

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
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private OneImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext, data.getTitle(), data.getId() + "");

        }

        private void initData(HomeNewsBean data) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(String.valueOf(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
            Utils.showNewsImage(data.getCover().get(0), mItemNewsPic);
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
        @BindView(R.id.item_news_layout)
        LinearLayout mItemNewsLayout;
        HomeNewsBean data;

        private NoImgViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_news_layout)
        public void onViewClicked() {
            Utils.showNewsDetail(mContext, data.getTitle(), data.getId() + "");
        }

        private void initData(HomeNewsBean data) {
            this.data = data;
            mItemNewsTitle.setText(data.getTitle());
            mItemNewsReadcounts.setText(String.valueOf(data.getViewCount()));
            mItemNewsTime.setText(DateTimeUtil.getNewsTime(mContext, data.getTime()));
        }
    }

}


