package com.easyvaas.elapp.view.news;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.bean.news.NewsDetailModel.StockBean;
import com.easyvaas.elapp.bean.user.UserPageInfo.TagBean;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.nex3z.flowlayout.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date   2017/5/9
 * Editor  Misuzu
 * 新闻标题栏头部
 */

public class NewsDetailHeaderView extends LinearLayout {

    @BindView(R.id.detail_header_title)
    TextView mDetailHeaderTitle;
    @BindView(R.id.detail_header_date)
    TextView mDetailHeaderDate;
    @BindView(R.id.detail_header_crawler_auther)
    TextView mDetailHeaderCrawlerAuther;
    @BindView(R.id.detail_header_user_header)
    RoundImageView mDetailHeaderUserHeader;
    @BindView(R.id.detail_header_user_name)
    TextView mDetailHeaderUserName;
    @BindView(R.id.detail_header_user_info)
    TextView mDetailHeaderUserInfo;
    @BindView(R.id.detail_header_user_layout)
    RelativeLayout mDetailHeaderUserLayout;
    @BindView(R.id.detail_header_stock_tag_one)
    TextView mDetailHeaderStockTagOne;
    @BindView(R.id.detail_header_stock_tag_two)
    TextView mDetailHeaderStockTagTwo;
    @BindView(R.id.detail_header_stock_tags_layout)
    LinearLayout mDetailHeaderStockTagsLayout;
    @BindView(R.id.detail_header_news_tags_layout)
    FlowLayout mDetailHeaderNewsTagsLayout;
    NewsDetailModel mNewsDetailBean;

    public NewsDetailHeaderView(Context context) {
        super(context);
        initView();
    }

    public NewsDetailHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.news_detail_header_layout, this);
        ButterKnife.bind(this, this);
    }

    /**
     * 填充数据
     */
    public void setData(NewsDetailModel data) {
        mNewsDetailBean = data;
        mDetailHeaderTitle.setText(data.getTitle());
        mDetailHeaderDate.setText(DateTimeUtil.getNewsTime(getContext(), data.getTime()));
        if (data.getAuthor().getBind() == 0) //没有绑定用户 显示普通布局
        {
            mDetailHeaderUserLayout.setVisibility(GONE);
            mDetailHeaderCrawlerAuther.setVisibility(VISIBLE);
            mDetailHeaderCrawlerAuther.setText(data.getAuthor().getName());
        } else {
            mDetailHeaderUserLayout.setVisibility(VISIBLE);
            mDetailHeaderCrawlerAuther.setVisibility(GONE);
            UserUtil.showUserPhoto(getContext(), data.getAuthor().getAvatar(), mDetailHeaderUserHeader);
            mDetailHeaderUserInfo.setText(data.getAuthor().getDescription());
        }

        if (data.getTag().size() > 0) // 设置标签
        {
            mDetailHeaderNewsTagsLayout.setVisibility(VISIBLE);
            for (TagBean tag : data.getTag()) {
                mDetailHeaderNewsTagsLayout.addView(getTextView(tag.getName()));
            }
        } else {
            mDetailHeaderNewsTagsLayout.setVisibility(GONE);
        }

        if (data.getStock().size() == 1)  //设置股票
        {
            mDetailHeaderStockTagOne.setVisibility(VISIBLE);
            mDetailHeaderStockTagTwo.setVisibility(GONE);
            fillStockContent(mDetailHeaderStockTagOne,data.getStock().get(0));
        }else if (data.getStock().size() >= 2)
        {
            mDetailHeaderStockTagOne.setVisibility(VISIBLE);
            mDetailHeaderStockTagTwo.setVisibility(VISIBLE);
            fillStockContent(mDetailHeaderStockTagOne,data.getStock().get(0));
            fillStockContent(mDetailHeaderStockTagOne,data.getStock().get(2));
        }else
        {
            mDetailHeaderStockTagOne.setVisibility(GONE);
            mDetailHeaderStockTagTwo.setVisibility(GONE);
        }

    }

    /**
     * 填充股票数据
     */
    private void fillStockContent(TextView stockView, final StockBean stockBean) {

        if (stockBean.getPersent().startsWith("+"))
            stockView.setSelected(true);
        else
            stockView.setSelected(false);
        stockView.setText(stockBean.getName() + " " + stockBean.getPersent());
        stockView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showStockDetail(v.getContext(), stockBean.getName(), stockBean.getCode(), true);
            }
        });
    }


    @OnClick({R.id.detail_header_user_layout, R.id.detail_header_stock_tag_one, R.id.detail_header_stock_tag_two})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.detail_header_user_layout:  //跳转作者主页
                Utils.toUserPager(getContext(), String.valueOf(mNewsDetailBean.getAuthor().getId()), 1);
                break;
            case R.id.detail_header_stock_tag_one: // 跳转股票详情
                break;
            case R.id.detail_header_stock_tag_two:
                break;
        }
    }

    /**
     * 添加标签
     */
    private TextView getTextView(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setBackgroundResource(R.drawable.news_detail_tags_bg);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.title_text_color));
        return textView;
    }

}
