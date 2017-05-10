package com.easyvaas.elapp.ui.common;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.view.news.NewsDetailHeaderView;
import com.easyvaas.elapp.view.news.NewsDetailInputView;
import com.easyvaas.elapp.view.news.NewsDetailPraiseAndCommentView;
import com.easyvaas.elapp.view.news.NewsDetailRecommendNewsView;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.google.gson.Gson;
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
    NewsDetailModel mNewsDetailModel;
    private BridgeWebView mWebView;
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
        newsId = getIntent().getStringExtra(AppConstants.NEWS_ID);
        mNewsDetailInputLayout.setNewsDetailBottomListener(this);
        initWebView();
        getNewsInfo();
    }

    /**
     * 初始化web界面
     */
    private void initWebView()
    {
        mWebView = new BridgeWebView(this);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mNewsDetailContainer.addView(mWebView, new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 获取新闻资讯
     */
    private void getNewsInfo()
    {
        String tempData = " {\n" +
                "          \"id\": 4369,\n" +
                "          \"author\": {\n" +
                "            \"id\": 0,\n" +
                "            \"name\": \"每日经济新闻\",\n" +
                "            \"avatar\": \"\",\n" +
                "            \"bind\": 0,\n" +
                "            \"description\": \"\"\n" +
                "          },\n" +
                "          \"time\": \"2017-03-09 11:21:31\",\n" +
                "          \"title\": \"国土部部长姜大明：居民住房财产一定会受到法律保护\",\n" +
                "          \"subTitle\": \"\",\n" +
                "          \"digest\": \"\",\n" +
                "          \"source\": \"每日经济新闻\",\n" +
                "          \"favorite\": 1,\n" +
                "          \"like\": 1,\n" +
                "          \"cover\": \"http://image.nbd.com.cn/uploads/articles/thumbnails/331153/001-003.thumb_hs.jpg\",\n" +
                "          \"stock\": [\n" +
                "            {\n" +
                "              \"code\": \"000586\",\n" +
                "              \"market\": \"股票市场\",\n" +
                "              \"name\": \"汇源通信\",\n" +
                "              \"persent\": \"+0.17\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"tag\": [\n" +
                "            {\n" +
                "              \"id\": 11,\n" +
                "              \"name\": \"宏观经济\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"likeCount\": 36,\n" +
                "          \"viewCount\": 1234,\n" +
                "          \"postCount\": 1,\n" +
                "          \"content\": \"<!-- <img src=\\\"http://www.nbd.com.cn/images/nbd_v5/article_test.png\\\"> -->\\n        <!-- 正文图片 -->\\n          <img alt=\\\"001-003.thumb_head\\\" class=\\\"\\\" src=\\\"http://image.nbd.com.cn/uploads/articles/thumbnails/331153/001-003.thumb_head.jpg\\\">\\n        <!-- 正文 -->\\n        <p>每经记者 冯彪 全国两会现场报道 每经编辑 陈星</p>\\r\\n<p>&ldquo;有恒产者有恒心。&rdquo;对于住房、土地等热点问题，3月8日，在全国两会的&ldquo;部长通道&rdquo;上，国土资源部部长姜大明表示：&ldquo;请大家放心，居民购买住房，其财产一定会受到法律的充分保护。&rdquo;</p>\\r\\n<p>去年，部分城市商品房价格涨幅较大，同时多地土地出让费用屡创新高。姜大明表示，要遏制炒作土地行为，保障居民住有所居。</p>\\r\\n<p>33个地区已开展改革试点</p>\\r\\n<p>农村土地征收、宅基地改革关乎农民财产权利。从2015年开始，全国有33个地区开展了农村土地征收、集体经营性建设用地入市和宅基地制度改革试点。姜大明表示，各试点地区坚守底线、大胆实践，已经探索总结出一批可复制、可推广的改革成果。</p>\\r\\n<p>值得关注的是，在试点成果的基础上，相关法律也将修改完善。姜大明称，目前国土资源部会同有关部门研究形成了土地管理法修正案草案的送审稿，经相关程序批准后，将面向社会公开征求意见。</p>\\r\\n<p>据姜大明介绍，土地管理法修正草案吸纳了试点的成果。在农村土地征收方面，体现了缩小征地范围，规范征收程序，完善对被征地农民合理、规范、多元保障机制等改革经验。在集体经营性建设用地入市方面，体现了建立城乡统一的建设用地市场，农村集体经营性建设用地与国有建设用地同权同价、同等入市的改革要求。在宅基地制度方面，体现了依法公平取得、节约集约使用、自愿有偿退出等制度安排。</p>\\r\\n<p>去年，部分地区住房土地使用权到期后的续期问题引起社会关注。姜大明表示，对70年住宅土地使用权到期后的续期问题，国土部正在深入调查研究，并将积极提出相关法律安排建议。</p>\\r\\n<p>宅地供应因城施策</p>\\r\\n<p>去年，楼市在去库存的背景下，一线城市和部分热点二线城市房价出现较大幅度上涨。在房价上涨的同时，土地市场活跃，部分热点城市屡屡出现房企高价拿地、&ldquo;地王&rdquo;频现的情况。</p>\\r\\n<p>姜大明说：&ldquo;我们提出住宅用地是保障住有所居的，不能拿来炒作囤积，进一步规范土地市场秩序，遏制炒作土地的投机行为。&rdquo;</p>\\r\\n<p>&ldquo;国家制定宏观调控政策，各地因地制宜抓好贯彻落实。坚持去库存与防过热并重，对不同地方&lsquo;分类指导、因城施策&rsquo;，对重点城市&lsquo;一城一策、靶向治理&rsquo;，房价上涨压力大的城市要合理增加住宅用地供应，去库存任务重的城市要减少以至暂停住宅用地供应。&rdquo;姜大明说。</p>\\r\\n<p>对于如何建立适应市场规律的基础性制度和长效机制，姜大明也表示，要坚持远近结合标本兼治。</p>\\r\\n<p>具体来说，姜大明提出了三个方面的举措：一是保证总量，优化结构。&ldquo;二是规划引导，稳定预期。落实人地挂钩政策，政府要公布住宅用地中期规划和三年滚动计划，形成居民和企业的良好预期。&rdquo;姜大明说。三是要总结各地土地市场调控经验，改进和完善&ldquo;招拍挂&rdquo;制度，保持城市地价总体平稳，严密防范房地产风险。</p>\\n        <!-- 页数 -->\",\n" +
                "          \"recommendPerson\": {\n" +
                "            \"id\": 10500129,\n" +
                "            \"avatar\": \"http://image-cdn.hooview.com/hooviewportal/upload_aedcfb28a3e03810fabec7eaa04c0705.jpeg\",\n" +
                "            \"name\": \"白晓毅\",\n" +
                "            \"description\": \"四川量化对冲学会副会长\"\n" +
                "          },\n" +
                "          \"recommendNews\": [\n" +
                "            {\n" +
                "              \"id\": 22127,\n" +
                "              \"cover\": [\n" +
                "                \"http://image-cdn.hooview.com/43355-4b69ba1beef5dd7e807c4490976d15ca\"\n" +
                "              ],\n" +
                "              \"title\": \"沪指跌近1%再创本轮调整新低 保险股与次新银行股逆市大涨\",\n" +
                "              \"time\": \"2017-05-10 15:00:00\",\n" +
                "              \"viewCount\": 7211\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 22107,\n" +
                "              \"cover\": [\n" +
                "                \"http://image-cdn.hooview.com/43355-4b69ba1beef5dd7e807c4490976d15ca\"\n" +
                "              ],\n" +
                "              \"title\": \"A股、商品获得暂时喘气机会 债市持续阴跌\",\n" +
                "              \"time\": \"2017-05-10 13:10:28\",\n" +
                "              \"viewCount\": 10567\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 22087,\n" +
                "              \"cover\": [\n" +
                "                \"http://image-cdn.hooview.com/43355-4b69ba1beef5dd7e807c4490976d15ca\"\n" +
                "              ],\n" +
                "              \"title\": \"午间公告一览：金科娱乐获实控人增持160万股\",\n" +
                "              \"time\": \"2017-05-10 12:39:00\",\n" +
                "              \"viewCount\": 10503\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 22067,\n" +
                "              \"cover\": [\n" +
                "                \"http://image-cdn.hooview.com/43355-4b69ba1beef5dd7e807c4490976d15ca\"\n" +
                "              ],\n" +
                "              \"title\": \"债市由涨转跌 10年期国债现券收益率创近两年新高\",\n" +
                "              \"time\": \"2017-05-10 11:53:00\",\n" +
                "              \"viewCount\": 10258\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 22108,\n" +
                "              \"cover\": [\n" +
                "                \"http://image-cdn.hooview.com/43355-4b69ba1beef5dd7e807c4490976d15ca\"\n" +
                "              ],\n" +
                "              \"title\": \"“末日博士”的困惑：为什么市场会忽略地缘政治风险？\",\n" +
                "              \"time\": \"2017-05-10 11:45:21\",\n" +
                "              \"viewCount\": 10792\n" +
                "            }\n" +
                "          ],\n" +
                "          \"posts\": [\n" +
                "            {\n" +
                "              \"id\": 9,\n" +
                "              \"time\": \"2017-04-01 11:31:03\",\n" +
                "              \"heats\": 1,\n" +
                "              \"content\": \"5555\",\n" +
                "              \"like\": 0,\n" +
                "              \"user\": {\n" +
                "                \"id\": \"18271157\",\n" +
                "                \"nickname\": \"Asami\",\n" +
                "                \"avatar\": \"http://appgw.hooview.com/resource/user/ab/12/ac56d72806d80e049e1a2386f94975a5.jpeg?tH\",\n" +
                "                \"vip\": 1\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }";
        NewsDetailModel data = new Gson().fromJson(tempData,NewsDetailModel.class);
        fillContent(data);
//        Subscription subscription = RetrofitHelper.getInstance().getService()
//                .getNewsDetail(newsId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new NetSubscribe<NewsDetailModel>() {
//                    @Override
//                    public void OnSuccess(NewsDetailModel newsDetailModel) {
//                        fillContent(newsDetailModel);
//                    }
//
//                    @Override
//                    public void OnFailue(String msg) {
//
//                    }
//                });
//        addSubscribe(subscription);
    }

    /**
     * 填充数据
     */
    private void fillContent(NewsDetailModel data)
    {
        mNewsDetailHeader.setData(data);
        mNewsDetailRecommendNewsLayout.setData(data);
        mNewsDetailCommentLayout.setData(data);
        mNewsDetailInputLayout.initCollectStatus(data);
        String webContent = data.getContent();
        webContent = webContent.replace("<img", "<img style='max-width:90%;height:auto;'");
        mWebView.loadDataWithBaseURL(null, webContent, "text/html", "utf-8", null);
    }

    /**
     * 跳转评论
     */
    @Override
    public void showComment() {

    }

    /**
     * 跳转分享
     */
    @Override
    public void showShare() {
        ShareContent shareContent = new ShareContentWebpage(mNewsDetailModel.getTitle(), "",
                "", ""); // Aya : 2017/4/27 分享url 待定
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

    }

    @Override
    protected void onDestroy() {
        mWebView.loadUrl("blank://");
        mWebView.removeAllViews();
        mWebView.destroy();
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
        }

    }
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }
}
