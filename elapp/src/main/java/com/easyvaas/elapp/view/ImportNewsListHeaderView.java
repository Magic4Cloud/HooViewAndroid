package com.easyvaas.elapp.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyvaas.common.imageslider.SliderLayout;
import com.easyvaas.common.imageslider.SliderTypes.BaseSliderView;
import com.easyvaas.common.imageslider.SliderTypes.TextSliderView;
import com.easyvaas.common.imageslider.Tricks.ViewPagerEx;
import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HooviewBean.NewsBean;
import com.easyvaas.elapp.bean.news.TopRatedModel.IndexBean;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.ui.news.HooviewEyesNewsActivity;
import com.easyvaas.elapp.ui.user.VIPUserInfoDetailActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

public class ImportNewsListHeaderView extends LinearLayout implements View.OnClickListener {
    private SliderLayout mSliderLayout;
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_KEY = "extra_key";

    private LinearLayout mLlExponent1;
    private TextView mTvExponentName1;
    private TextView mTvExponentNumber1;
    private TextView mTvExponentPercent1;
    private LinearLayout mLlExponent2;
    private TextView mTvExponentName2;
    private TextView mTvExponentNumber2;
    private TextView mTvExponentPercent2;
    private LinearLayout mLlExponent3;
    private TextView mTvExponentName3;
    private TextView mTvExponentNumber3;
    private TextView mTvExponentPercent3;
    private TextView mTvTitle;
    private TextView mTvRefreshTime;
    private TextView mTvLoadMore;

    private TextView mTvEyesNews1;
    private TextView mTvEyesNews2;
    private TextView mTvEyesNews3;
    private List<NewsBean> hooviewList;
    private List<TextView> mNewsViewList;

    private List<ImageView> mImageViewList;
    private BannerModel mBannerModel;
    private List<IndexBean> mIndexList;
    private TopRatedModel.HooviewBean huoyanEntity;
    private BaseSliderView.OnSliderClickListener mOnSliderClickListener = new BaseSliderView.OnSliderClickListener() {
        @Override
        public void onSliderClick(BaseSliderView slider) {
            Bundle bundle = slider.getBundle();
            int type = bundle.getInt(EXTRA_TYPE, -1);
            String keyValue = bundle.getString(EXTRA_KEY);
            Intent intent = null;
            if (type == BannerModel.DataEntity.TYPE_H5) {
                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_KEY_TYPE, WebViewActivity.TYPE_ACTIVITY);
                intent.putExtra(WebViewActivity.EXTRA_KEY_URL, keyValue);
                intent.putExtra(Constants.EXTRA_KEY_TITLE, bundle.getString(Constants.EXTRA_KEY_TITLE));
            } else if (type == BannerModel.DataEntity.TYPE_GOOD_VIDEO) {
                PlayerActivity.start(getContext(), keyValue, VideoEntity.IS_VIDEO, VideoEntity.MODE_GOOD_VIDEO);
            } else if (type == BannerModel.DataEntity.TYPE_IMAGE_TEXT_LIVE) {
                Toast.makeText(getContext(), "image text live", Toast.LENGTH_SHORT).show();
//                ImageTextLiveActivity.start();
            } else if (type == BannerModel.DataEntity.TYPE_VIP_INFO) {
                VIPUserInfoDetailActivity.start(getContext(), keyValue);
            } else if (type == BannerModel.DataEntity.TYPE_VIDEO_LIVE) {
                PlayerActivity.start(getContext(), keyValue, VideoEntity.IS_LIVING, VideoEntity.MODE_VIDEO);
            } else if (type == BannerModel.DataEntity.TYPE_NEWS) {
                Utils.showNewsDetail(getContext(), bundle.getString(Constants.EXTRA_KEY_TITLE), keyValue);
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    };

    public ImportNewsListHeaderView(Context context) {
        this(context, null);
    }

    public ImportNewsListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    private void setupView() {
        View.inflate(getContext(), R.layout.view_important_news_header, this);
        mSliderLayout = (SliderLayout) findViewById(R.id.recommend_header_slider);
        mLlExponent1 = (LinearLayout) findViewById(R.id.ll_exponent1);
        mTvExponentName1 = (TextView) findViewById(R.id.tv_exponent_name1);
        mTvExponentNumber1 = (TextView) findViewById(R.id.tv_exponent_number1);
        mTvExponentPercent1 = (TextView) findViewById(R.id.tv_exponent_percent1);
        mLlExponent2 = (LinearLayout) findViewById(R.id.ll_exponent2);
        mTvExponentName2 = (TextView) findViewById(R.id.tv_exponent_name2);
        mTvExponentNumber2 = (TextView) findViewById(R.id.tv_exponent_number2);
        mTvExponentPercent2 = (TextView) findViewById(R.id.tv_exponent_percent2);
        mLlExponent3 = (LinearLayout) findViewById(R.id.ll_exponent3);
        mTvExponentName3 = (TextView) findViewById(R.id.tv_exponent_name3);
        mTvExponentNumber3 = (TextView) findViewById(R.id.tv_exponent_number3);
        mTvExponentPercent3 = (TextView) findViewById(R.id.tv_exponent_percent3);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvRefreshTime = (TextView) findViewById(R.id.tv_refresh_time);
        mTvLoadMore = (TextView) findViewById(R.id.tv_load_more);
        mTvLoadMore.setOnClickListener(this);

        mLlExponent1.setOnClickListener(this);
        mLlExponent2.setOnClickListener(this);
        mLlExponent3.setOnClickListener(this);
        findViewById(R.id.ll_eye_new1).setOnClickListener(this);
        findViewById(R.id.ll_eye_new2).setOnClickListener(this);
        findViewById(R.id.ll_eye_new3).setOnClickListener(this);

        ImageView iv_tv1 = (ImageView) findViewById(R.id.iv_1);
        ImageView iv_tv2 = (ImageView) findViewById(R.id.iv_2);
        ImageView iv_tv3 = (ImageView) findViewById(R.id.iv_3);
        mTvEyesNews1 = (TextView) findViewById(R.id.tv_eyes_news1);
        mTvEyesNews2 = (TextView) findViewById(R.id.tv_eyes_news2);
        mTvEyesNews3 = (TextView) findViewById(R.id.tv_eyes_news3);
        mNewsViewList = new ArrayList<>();
        mNewsViewList.add(mTvEyesNews1);
        mNewsViewList.add(mTvEyesNews2);
        mNewsViewList.add(mTvEyesNews3);

        mImageViewList = new ArrayList<>();
        mImageViewList.add(iv_tv1);
        mImageViewList.add(iv_tv2);
        mImageViewList.add(iv_tv3);

        // banner 宽高比 3：2
        RelativeLayout bannerLayout = (RelativeLayout) findViewById(R.id.banner_layout);
        ViewGroup.LayoutParams layoutParams = bannerLayout.getLayoutParams();
        layoutParams.width = com.easyvaas.common.emoji.utils.Utils.getDisplayWidthPixels(getContext());
        layoutParams.height = (int)(layoutParams.width - ViewUtil.dp2Px(mContext,40))* 2 / 3;
        bannerLayout.setLayoutParams(layoutParams);
    }

    public void setHooviewNews(TopRatedModel.HooviewBean hooviewNews) {
        this.huoyanEntity = hooviewNews;
        hooviewList = hooviewNews.getNews();
        if (hooviewList != null) {
            for (int i = 0; i < 3; i++) {
                if (i < hooviewList.size()) {
                    mNewsViewList.get(i).setText(hooviewList.get(i).getTitle());
                    ViewGroup viewGroup = (ViewGroup) mNewsViewList.get(i).getParent();
                    viewGroup.setTag(hooviewList.get(i));
                    viewGroup.setVisibility(VISIBLE);
                    updatePointMargin(mImageViewList.get(i), mNewsViewList.get(i));

                } else {
                    ViewGroup viewGroup = (ViewGroup) mNewsViewList.get(i).getParent();
                    viewGroup.setVisibility(GONE);
                }
            }
        }
    }

    //更新点的间距
    private void updatePointMargin(ImageView imageView, TextView textView) {
        if (textView.getLineCount() == 2) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.topMargin = -(int) ViewUtil.dp2Px(mContext, 7);
            imageView.setLayoutParams(params);
        }
    }

    public void setBannerModel(BannerModel bannerModel) {
        this.mBannerModel = bannerModel;
        if (mBannerModel != null && mBannerModel.getData() != null) {
            assembleSliderView(bannerModel.getData());
        }
    }

    private void assembleSliderView(List<BannerModel.DataEntity> list) {
        mSliderLayout.removeAllSliders();
        for (int i = 0, n = list.size(); i < n; i++) {
            TextSliderView textSliderView = new TextSliderView(mContext);
            textSliderView
                    .description(list.get(i).getTitle())
                    .image(list.get(i).getImg())
                    .empty(R.drawable.account_bitmap_list)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(mOnSliderClickListener);
            textSliderView.bundle(new Bundle());
            int type = list.get(i).getType();
            textSliderView.getBundle().putInt(EXTRA_TYPE, type);
            textSliderView.getBundle().putString(Constants.EXTRA_KEY_TITLE,
                    list.get(i).getTitle());
            textSliderView.getBundle().putString(EXTRA_KEY,
                    list.get(i).getResource());
            mSliderLayout.addSlider(textSliderView);
        }
        mSliderLayout.totalCountsTextView.setText(" / "+list.size());
        mSliderLayout.addOnPageChangeListener(new pageChangeListener());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_load_more:
                if (huoyanEntity != null && huoyanEntity.getChannels() != null) {
                    HooviewEyesNewsActivity.start(getContext(), huoyanEntity.getChannels());
                }
                break;
            case R.id.ll_eye_new1:
            case R.id.ll_eye_new2:
            case R.id.ll_eye_new3:
                NewsBean news = (NewsBean) v.getTag();
                if (news != null) {
//                    insertHistoryRecord(news);
                    Utils.showNewsDetail(mContext, news.getTitle(), news.getId() + "");
                }
                break;
            case R.id.ll_exponent1:
            case R.id.ll_exponent2:
            case R.id.ll_exponent3:
                TopRatedModel.IndexBean exponentModel
                        = (TopRatedModel.IndexBean) v.getTag();
                if (exponentModel != null) {
                    Utils.showStockDetail(mContext, exponentModel.getName(), exponentModel.getSymbol(), false);
                }
                break;
        }
    }

    private void insertHistoryRecord(NewsItemModel newsModel) {
        String mVideoId = newsModel.getId() + "";
        if (!TextUtils.isEmpty(mVideoId) && !RealmHelper.getInstance().queryReadRecordId(mVideoId)) {
            ReadRecord bean = new ReadRecord();
            bean.setId(String.valueOf(mVideoId));
            bean.setPic(newsModel.getCover());
            bean.setTitle(newsModel.getTitle());
            bean.setTime(DateTimeUtil.getSimpleTime(mContext, newsModel.getTime()));
            bean.setCount(newsModel.getViewCount());
            RealmHelper.getInstance().insertReadRecord(bean, 30);
        }
    }

    public void setExponentListModel(List<IndexBean> mIndexList) {
        this.mIndexList = mIndexList;
        if (mIndexList != null ) {
            for (int i = 0; i < mIndexList.size() && i < 3; i++) {
                IndexBean mIndexBean = mIndexList.get(i);
                if (i == 0) {
                    setExponent(mIndexBean, mTvExponentName1, mTvExponentNumber1, mTvExponentPercent1);
                    mLlExponent1.setTag(mIndexBean);
                } else if (i == 1) {
                    setExponent(mIndexBean, mTvExponentName2, mTvExponentNumber2, mTvExponentPercent2);
                    mLlExponent2.setTag(mIndexBean);
                } else if (i == 2) {
                    setExponent(mIndexBean, mTvExponentName3, mTvExponentNumber3, mTvExponentPercent3);
                    mLlExponent3.setTag(mIndexBean);
                }
            }
        }
    }

    private void setExponent(IndexBean exponent,
                             TextView name, TextView price, TextView precent) {
        if (exponent.getChangePercent() > 0) {
            String percentStr = getContext().getString(R.string.exponent_percent_up);
            percentStr = String.format(percentStr,
                    (exponent.getClose() - exponent.getOpen()), exponent.getChangePercent());
            String number = new DecimalFormat(".##").format(exponent.getClose());
            name.setSelected(true);
            price.setSelected(true);
            precent.setSelected(true);
            name.setText(exponent.getName());
            price.setText(number);
            precent.setText(percentStr);
        } else {
            String percentStr = getContext().getString(R.string.exponent_percent);
            percentStr = String.format(percentStr,
                    (exponent.getClose() - exponent.getOpen()), exponent.getChangePercent());
            String number = new DecimalFormat(".##").format(exponent.getClose());
            name.setSelected(false);
            price.setSelected(false);
            precent.setSelected(false);
            name.setText(exponent.getName());
            price.setText(number);
            precent.setText(percentStr);
        }
    }

    private class pageChangeListener  implements ViewPagerEx.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSliderLayout.currentCountTextView.setText(""+ (mSliderLayout.getCurrentPosition()+1));

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
