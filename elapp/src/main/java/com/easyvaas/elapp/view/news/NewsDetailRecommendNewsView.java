package com.easyvaas.elapp.view.news;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/5/10
 * Editor  Misuzu
 * 推荐新闻View
 */

public class NewsDetailRecommendNewsView extends LinearLayout {

    @BindView(R.id.detail_news_container)
    LinearLayout mDetailNewsContainer;

    public NewsDetailRecommendNewsView(Context context) {
        super(context);
        initView();
    }

    public NewsDetailRecommendNewsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.news_detail_recommed_news_layout, this);
        ButterKnife.bind(this, this);
    }

    public void setData(NewsDetailModel data)
    {
        List<HomeNewsBean> mNewsList = data.getRecommendNews();
        if (mNewsList.size() > 0)
        {
            Collections.reverse(mNewsList);
            for (int i = 0; i < mNewsList.size(); i++) {
                if (i == 0)
                    mDetailNewsContainer.addView(getNewsView(mNewsList.get(i),true),1);
                else
                    mDetailNewsContainer.addView(getNewsView(mNewsList.get(i),false),1);
            }
        }else
        {
            setVisibility(GONE);
        }
    }

    /**
     * 获取新闻View
     */
    private View getNewsView(final HomeNewsBean mHomeNewsBean, boolean isLast)
    {
        View newsView = LayoutInflater.from(getContext()).inflate(R.layout.recommend_news_no_img_item,null);
        TextView textView = (TextView) newsView.findViewById(R.id.recommend_news_no_img_title);
        View line = newsView.findViewById(R.id.recommend_news_no_img_line);
        textView.setText(mHomeNewsBean.getTitle());
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toNewsDetail(getContext(),mHomeNewsBean.getId());
            }
        });
        if (isLast) // 最后一个不显示分割线
            line.setVisibility(GONE);
        return newsView;
    }

}
