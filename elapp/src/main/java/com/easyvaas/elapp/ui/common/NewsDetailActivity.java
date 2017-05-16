package com.easyvaas.elapp.ui.common;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.net.HooviewApiConstant;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.news.NewsDetailCommentActivity;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.view.news.NewsDetailHeaderView;
import com.easyvaas.elapp.view.news.NewsDetailInputView;
import com.easyvaas.elapp.view.news.NewsDetailPraiseAndCommentView;
import com.easyvaas.elapp.view.news.NewsDetailRecommendNewsView;
import com.hooview.app.R;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/8
 * Editor  Misuzu
 * 新闻详情页
 */

public class NewsDetailActivity extends MyBaseActivity implements NewsDetailInputView.NewsDetailBottomListener{


    @BindView(R.id.news_detail_header)
    NewsDetailHeaderView mNewsDetailHeader;
    @BindView(R.id.news_detail_container)
    FrameLayout mNewsDetailContainer;
    @BindView(R.id.news_detail_comment_layout)
    NewsDetailPraiseAndCommentView mNewsDetailCommentLayout;
    @BindView(R.id.news_detail_recommend_news_layout)
    NewsDetailRecommendNewsView mNewsDetailRecommendNewsLayout;
    @BindView(R.id.news_detail_input_layout)
    NewsDetailInputView mNewsDetailInputLayout;
    @BindView(R.id.news_detail_loading)
    ImageView mLoadingView;
    @BindView(R.id.news_detail_loading_layout)
    FrameLayout mLoadingLayout;
    private RotateAnimation rotateAnimation;
    private NewsDetailModel mNewsDetailModel;
    private WebView mWebView;
    private String newsId;

    @Override
    protected int getLayout() {
        return R.layout.activity_news_detail_layout;
    }

    @Override
    protected String getTitleText() {
        return null;
    }

    @Override
    protected void initViewAndData() {
        mNewsDetailModel = new NewsDetailModel();
        newsId = getIntent().getStringExtra(AppConstants.NEWS_ID);
        mNewsDetailInputLayout.setNewsDetailBottomListener(this);
        startLoading();
        initWebView();
        getNewsInfo(false);
        addNewsToHistory();
    }

    /**
     * 初始化web界面
     */
    private void initWebView()
    {
        mWebView = new WebView(this);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUserAgentString(RequestUtil.getAppUA());
        mNewsDetailContainer.addView(mWebView, new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 获取新闻资讯
     */
    private void getNewsInfo(final boolean isComment)
    {

        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getNewsDetail(newsId,EVApplication.getUser() == null ? "":EVApplication.getUser().getUserid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NewsDetailModel>() {
                    @Override
                    public void OnSuccess(NewsDetailModel newsDetailModel) {
                        if (isComment) // 单独刷新评论列表
                            mNewsDetailCommentLayout.setData(newsDetailModel);
                        else  // 加载所有
                            fillContent(newsDetailModel);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(NewsDetailActivity.this,R.string.network_error);
                    }
                });
        addSubscribe(subscription);
    }

    /**
     * 填充数据
     */
    private void fillContent(NewsDetailModel data)
    {
        mNewsDetailModel = data;
        mNewsDetailHeader.setData(data);
        mNewsDetailRecommendNewsLayout.setData(data);
        mNewsDetailCommentLayout.setData(data);
        mNewsDetailInputLayout.initCollectStatus(data);
        String webContent = data.getContent();
        webContent = webContent.replace("<img", "<img style='max-width:100%;height:auto;'");
        mWebView.loadDataWithBaseURL(null, webContent, "text/html", "utf-8", null);
    }

    /**
     * 跳转评论
     */
    @Override
    public void showComment() {
        Intent intent = new Intent(this,NewsDetailCommentActivity.class);
        intent.putExtra(AppConstants.NEWS_ID,newsId);
        startActivity(intent);
    }

    /**
     * 跳转分享
     */
    @Override
    public void showShare() {
        String url = HooviewApiConstant.HOST_WEB_APP+"/?page=news&title=" + mNewsDetailModel.getTitle() + "&newsid=" + newsId;
        ShareContent shareContent = new ShareContentWebpage(mNewsDetailModel.getTitle(), "",
                url, ""); // Aya : 2017/4/27 分享url 待定
        ShareHelper.getInstance(this).showShareBottomPanel(shareContent);
    }

    /**
     * 点击收藏
     */
    @Override
    public void collectClick(boolean isSelected) {
        final int action = isSelected ? 0 : 1;
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .addNewsToColloction(EVApplication.getUser().getName(),
                        EVApplication.getUser().getSessionid(),
                        newsId,action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        if (action == 0) {
                            mNewsDetailInputLayout.getTvNewsCollect().setSelected(false);
                        } else {
                            mNewsDetailInputLayout.getTvNewsCollect().setSelected(true);
                        }
                    }
                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(NewsDetailActivity.this,R.string.opreat_fail);
                    }
                });
        addSubscribe(subscription);
    }

    /**
     * 关闭界面
     */
    @Override
    public void backFinish() {
        finish();
    }

    /**
     * 发送评论
     */
    @Override
    public void sendComment(String msg) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .sendCommentByType(mNewsDetailModel.getId(),EVApplication.getUser().getName(),msg,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        SingleToast.show(NewsDetailActivity.this,getString(R.string.msg_comment_success));
                        mNewsDetailInputLayout.setCommentAdd();
                        getNewsInfo(false);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(NewsDetailActivity.this,getString(R.string.opreat_fail));
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    protected void onDestroy() {
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
        mNewsDetailContainer.removeAllViews();
        super.onDestroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            // 重新测量
            mWebView.measure(w, h);
            if (mLoadingView != null)
            {
                mLoadingView.clearAnimation();
                mLoadingLayout.setVisibility(View.GONE);
            }
        }

    }
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }

    /**
     * 开始加载
     */
    private void startLoading()
    {
        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setRepeatCount(Integer.MAX_VALUE);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(2000);
        }
        mLoadingView.startAnimation(rotateAnimation);
    }

    /**
     * 添加阅读记录
     */
    private void addNewsToHistory()
    {
        if (EVApplication.isLogin())
        {
            Subscription subscription = RetrofitHelper.getInstance().getService()
                    .addReadNewsInfo(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid(),newsId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel noResponeBackModel) {

                        }

                        @Override
                        public void OnFailue(String msg) {

                        }
                    });
            mCompositeSubscription.add(subscription);
        }
    }
}
