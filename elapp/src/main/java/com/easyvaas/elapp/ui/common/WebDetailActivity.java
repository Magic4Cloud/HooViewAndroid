package com.easyvaas.elapp.ui.common;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.news.NewsCollectStatus;
import com.easyvaas.elapp.bean.user.Collection;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.HooviewApiConstant;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.hooview.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;
import static com.hooview.app.R.id.tv_news_comment;

public class WebDetailActivity extends BaseActivity {
    private static final String TAG = "WebDetailActivity";

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CODE = "code";
    public static final String EXTRA_TITLE = "title";

    public static final int TYPE_STOCK = 101;
    public static final int TYPE_EXPONENT = 102;
    public static final int TYPE_NEWS = 103;
    public static final int TYPE_COMMENTS = 104;
    public static final int TYPE_PUBLIC = 105; // 公告

    private FrameLayout mFlContainer;
    private EditText mEtComment;
    private BridgeWebView mWebView;
    private View mInputCommentBar;
    private TextView tvCommentCount;

    private RelativeLayout mRlBottomNews;
    private TextView mTvNewsCommentHint;
    private ImageView mTvNewsShare;
    private ImageView mTvNewsCollect;
    private ImageView mTvNewsComment;


    private RelativeLayout mRlBottomStock;
    private TextView mTvStockCommentHint;
    private ImageView mTvStockAdd;
    private ImageView mTvStockRefresh;
    private ImageView mTvStockShare;
    private ImageView mNewsBackImageView;
    private RelativeLayout mTitleLayout;
    private String code;
    private int isCollected; // 0 未添加 1 已添加
    private  int detailType;
    private CompositeSubscription mCompositeSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        initViews();
        if (detailType == TYPE_NEWS)
        {
            addNewsToHistory(); // 添加阅读记录
            getCollectStatus();//判断收藏状态
        }
    }

    /**
     * 添加阅读记录
     */
    private void addNewsToHistory()
    {
        if (EVApplication.isLogin())
        {
           Subscription subscription = RetrofitHelper.getInstance().getService()
                    .addReadNewsInfo(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid(),code)
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

    private void initViews() {
        final String name = getIntent().getStringExtra(EXTRA_NAME);
        code = getIntent().getStringExtra(EXTRA_CODE);
        final String title = getIntent().getStringExtra(EXTRA_TITLE);
         detailType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_STOCK);
        if (TextUtils.isEmpty(code)) {
            finish();
            return;
        }
        mCompositeSubscription = new CompositeSubscription();
        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        mEtComment = (EditText) findViewById(R.id.et_comment);
        mInputCommentBar = findViewById(R.id.rl_input_text);
        mTitleLayout = (RelativeLayout) findViewById(R.id.title);

        mRlBottomStock = (RelativeLayout) findViewById(R.id.rl_bottom_stock);
        mTvStockAdd = (ImageView) findViewById(R.id.tv_stock_add);
        mTvStockRefresh = (ImageView) findViewById(R.id.tv_stock_refresh);
        mTvStockShare = (ImageView) findViewById(R.id.tv_stock_share);
        mTvStockCommentHint = (TextView) findViewById(R.id.tv_stock_comment_hint);

        mTvStockAdd.setOnClickListener(mOnClickListener);
        mTvStockRefresh.setOnClickListener(mOnClickListener);
        mTvStockShare.setOnClickListener(mOnClickListener);
        mTvStockCommentHint.setOnClickListener(mOnClickListener);

        mRlBottomNews = (RelativeLayout) findViewById(R.id.rl_bottom_news);
        mTvNewsCollect = (ImageView) findViewById(R.id.tv_news_collect);
        mTvNewsComment = (ImageView) findViewById(tv_news_comment);
        mTvNewsShare = (ImageView) findViewById(R.id.tv_news_share);

        mTvNewsCommentHint = (TextView) findViewById(R.id.tv_news_comment_hint);
        mTvNewsCollect.setOnClickListener(mOnClickListener);
        mTvNewsComment.setOnClickListener(mOnClickListener);
        mTvNewsShare.setOnClickListener(mOnClickListener);
        mTvNewsCommentHint.setOnClickListener(mOnClickListener);

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        TextView tvSubhead = (TextView) findViewById(R.id.tv_subhead);
        tvCommentCount = (TextView) findViewById(R.id.tv_news_comment_count);
        final TextView tvWordCount = (TextView) findViewById(R.id.tv_word_count);

        tvWordCount.setText(getString(R.string.text_length_prompt, 0));
        initCollectionStatus();

        if (detailType == TYPE_STOCK || detailType == TYPE_EXPONENT) {
            tvTitle.setText(name);
            tvSubhead.setText(code);
            mTitleLayout.setVisibility(View.VISIBLE);
            mRlBottomNews.setVisibility(View.GONE);
            mRlBottomStock.setVisibility(View.VISIBLE);
            if (EVApplication.getUser() != null)
                isStockAdded();
        } else if (detailType == TYPE_NEWS) {
            tvTitle.setVisibility(View.GONE);
            tvSubhead.setVisibility(View.GONE);
            mRlBottomNews.setVisibility(View.VISIBLE);
            mRlBottomStock.setVisibility(View.GONE);
            tvCommentCount.setVisibility(View.VISIBLE);
            tvCommentCount.setText("");
        } else if (detailType == TYPE_COMMENTS) {
            mRlBottomNews.setVisibility(View.GONE);
            mRlBottomStock.setVisibility(View.GONE);
            mInputCommentBar.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.all_comments);
            tvSubhead.setVisibility(View.GONE);
        }else if (detailType == TYPE_PUBLIC)
        {
//            tvTitle.setText(name);
            tvSubhead.setVisibility(View.GONE);
            mRlBottomNews.setVisibility(View.GONE);
            mRlBottomStock.setVisibility(View.GONE);
            tvCommentCount.setVisibility(View.GONE);
        }

        mInputCommentBar.findViewById(R.id.iv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = mEtComment.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    SingleToast.show(getApplicationContext(), R.string.input_comment);
                } else {
                    postComment(code, detailType == TYPE_STOCK);
                }
            }
        });

        mEtComment.setOnClickListener(mOnClickListener);
        mEtComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_SEND == i) {
                    String comment = textView.getText().toString();
                    if (TextUtils.isEmpty(comment)) {
                        SingleToast.show(getApplicationContext(), R.string.input_comment);
                        return false;
                    } else {
                        postComment(code, detailType == TYPE_STOCK);
                        return true;
                    }
                }
                return false;
            }
        });
        mEtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                tvWordCount.setText(getString(R.string.text_length_prompt, str.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mNewsBackImageView = (ImageView) findViewById(R.id.iv_news_back);
        mNewsBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //init web view
        mWebView = new BridgeWebView(this);
        /*mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCachePath(FileUtil.CACHE_DIR);*/
//        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUserAgentString(RequestUtil.getAppUA());
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new MyWebChromeClient());

        mFlContainer.addView(mWebView, new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        registerWebCallback();
        startLoadWeb(detailType, code, name);
    }

    private void initCollectionStatus() {
        if ((!TextUtils.isEmpty(code) && !RealmHelper.getInstance().queryCollectionId(code))) {
            mTvNewsCollect.setSelected(false);
        } else {
            mTvNewsCollect.setSelected(true);
        }
    }

    private void startLoadWeb(int type, String code, String name) {
        String url = "";
        switch (type) {
            case TYPE_STOCK:
                url = "/?page=stock&name=" + name + "&code=" + code + "&special=1";
                break;
            case TYPE_EXPONENT:
                url = "/?page=stock&name=" + name + "&code=" + code + "&special=0";
                break;
            case TYPE_NEWS:
                url = "/?page=news&title=" + name + "&newsid=" + code;
                break;
            case TYPE_COMMENTS:
                url = "/?page=posts&newsid=" + code;
                break;
            case TYPE_PUBLIC:
                url = "/?page=announcement&url="+code;
        }
        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "load url is empty! load type: " + type);
            finish();
        } else {
            mWebView.loadUrl(HooviewApiConstant.HOST_WEB_APP + url);
//            String data = "<p><span class=\\\"EmImageRemark\\\" href=\\\"http://quote.eastmoney.com/us/aapl.html\\\" target=\\\"_blank\\\"><img alt=\\\"K图 aapl_31\\\" border=\\\"0\\\" height=\\\"276\\\" jquery17203844175735919589=\\\"52\\\" src=\\\"http://hooviewimg.oss-cn-shanghai.aliyuncs.com/eastmoney/507910-1b47e8870280106364f4f077fd03e002\\\" style=\\\"border-bottom:#d1d1d1 1px solid;border-left:#d1d1d1 1px solid;border-top:#d1d1d1 1px solid;border-right:#d1d1d1 1px solid\\\" width=\\\"578\\\"></span></p><p>　　<span id=\\\"stock_NASDAQ_AAPL7\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/us/AAPL.html?Market=NASDAQ\\\" target=\\\"_blank\\\">苹果</span></span><span id=\\\"quote_AAPL7\\\"></span>公司手握巨额现金，超过德国<span id=\\\"Info.78290\\\"><span class=\\\"infokey\\\" href=\\\"http://topic.eastmoney.com/wcdd/\\\" target=\\\"_blank\\\">外汇储备</span></span>的新闻震动市场。</p><p>　　上市公司中的“现金土豪”往往与高分红联系起来。</p><p>　　令人但令人奇怪的是，坐拥海量现金的苹果公司却依靠发债来分红和回购股票，回馈股东利益。</p><p>　　这当中有何玄机？</p><p>　　苹果于上周发布了季度业绩报告。资料显示，<strong>苹果的现金储备达到了2568亿美元，</strong>这一数字超过了<span id=\\\"stock_NYSE_WMT7\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/us/WMT.html?Market=NYSE\\\" target=\\\"_blank\\\">沃尔玛</span></span><span id=\\\"quote_WMT7\\\"></span>公司或<span id=\\\"stock_NYSE_GE7\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/us/GE.html?Market=NYSE\\\" target=\\\"_blank\\\">通用电气</span></span><span id=\\\"quote_GE7\\\"></span>的市值，更超越了德国的<span id=\\\"Info.352\\\"><span class=\\\"infokey\\\" href=\\\"http://data.eastmoney.com/cjsj/hjwh.html\\\" target=\\\"_blank\\\">外汇储备</span></span>金额，是名副其实的富可敌国。</p><p>　　CNBC数据显示，苹果现金储备在过去逾2年的时间里翻了近一倍；<strong>其现金储备规模是除了金融公司外美国近代以来的公司当中最高的。</strong></p><p>　　上市公司中的“现金土豪”往往意味这高分红和股票回购，苹果公司也不例外，其分红和股票回购等股东回馈措施的水平飙至历史新高，但并不是用自己的钱，而是通过发<span id=\\\"stock_3950222\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/ZS395022.html\\\" target=\\\"_blank\\\">企业债</span></span><span id=\\\"quote_3950222\\\"></span>来进行融资。</p><p>　　据《金融时报》5月5日报道，苹果当地时间上周四重返企业债券市场，发行数十亿<span id=\\\"stock_5013001\\\"><span class=\\\"keytip\\\" href=\\\"http://fund.eastmoney.com/501300.html\\\" target=\\\"_blank\\\">美元债</span></span><span id=\\\"quote_5013001\\\"></span>券，为当周早些时候公布的增加股息和股票回购计划融资。苹果将发行6种不同类型债券，其中包括2020年和2022年到期的两种浮动<span id=\\\"Info.344\\\"><span class=\\\"infokey\\\" href=\\\"http://data.eastmoney.com/cjsj/yhll.html\\\" target=\\\"_blank\\\">利率</span></span>债券，固定<span id=\\\"Info.391\\\"><span class=\\\"infokey\\\" href=\\\"http://data.eastmoney.com/cjsj/yhll.html\\\" target=\\\"_blank\\\">利率</span></span>债券期限为3-10年。</p><p>　　<strong>投资者对苹果债券兴趣非常强烈，苹果10年期债券利率比同期美国<span id=\\\"stock_3950312\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/ZS395031.html\\\" target=\\\"_blank\\\">国债</span></span><span id=\\\"quote_3950312\\\"></span>高90个基点，约为3.25%。</strong></p><p>　　承受比国债高90个基点的利率，借钱来回馈股东，那为何不用自己的巨额现金呢，难道是因为嫌钱太多没地方花？</p><p>　　答案当然不是，实际上这与美国的复杂的税收体制有关，与大多数国家不同，美国实施全球税收政策，即对美国的个人和企业在全球的所有收入都征税。其中，对于跨国公司的海外收入，允许递延至收入汇回美国时再征收。</p><p>　　但是，美国对跨国公司在海外税收采取相对比较高额的抑制性税收调节政策。<strong>例如，企业海外收入汇回美国时，联邦“汇回税”率高达35%；各个州还要征收州税，平均水平大约为4.</strong>也就是说如果汇回2000亿美金的话，刚到美国其中的近4成，也就是约800亿美元就上缴了。</p><p>　　虽然各国针对本国跨国公司，都有汇回税率一说，但美国的海外汇回税率位居OECD(经合组织)国家之首。</p><p>　　根据<span id=\\\"stock_NYSE_GS7\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/us/GS.html?Market=NYSE\\\" target=\\\"_blank\\\">高盛</span></span><span id=\\\"quote_GS7\\\"></span>测算，<span id=\\\"stock_1611252\\\"><span class=\\\"keytip\\\" href=\\\"http://fund.eastmoney.com/161125.html\\\" target=\\\"_blank\\\">标普500</span></span><span id=\\\"quote_1611252\\\"></span>中的大型跨国公司将大量海外收入留在当地以避免缴纳高昂的税费。规模大约有2.4万亿美元，其中现金约1万亿美元。</p><p>　　因此和众多跨国公司一样，苹果拒绝将海外收益带来美国，以避免高额的企业汇回税。</p><p>　　<strong>巨额现金变相回流 </strong></p><p>　　<strong>无税还有利息拿</strong></p><p>　　当然留在海外的巨额资金，并不是真的躺在海外离岸账户里了，而是以另一种方式回流美国，<strong>手握大量现金的苹果公司就选择投资<span id=\\\"stock_3950242\\\"><span class=\\\"keytip\\\" href=\\\"http://quote.eastmoney.com/ZS395024.html\\\" target=\\\"_blank\\\">公司债</span></span><span id=\\\"quote_3950242\\\"></span>券、货币市场基金和美国国债。</strong></p><p>　　根据苹果公司上周递交的监管文件，该公司拥有的2570亿美元现金储备中，有1480亿美元投资了企业债，规模足以购买全球最大的固定收益共同基金Vanguard Total Bond Market Index Fund的所有资产，后者整体资产规模为1450亿美元。</p><p>　　除了企业债，苹果的现金储备里持仓第二位的资产就是适销债券。苹果将530亿美元投资了美国政府国债，210亿美元投资了抵押债券和资产担保债权。</p><p>　　那么这样到底好处何在？彭博从一份监管文件中了解到，<strong>过去五年苹果将其大部分海外营收以无税的方式转回美国境内——部分用于购买政府债券。作为回报，财政部在此期间至少向苹果支付了6亿美元的利息，实际数额可能比这更大。</strong></p><p>　　据彭博社对美国证监会相关材料的研究保守估计，美国十大巨头在2012-2016的五年内凭此获得了超过14亿美元的利息收入。</p><p>　　<strong>减税计划或只能利好美股</strong></p><p>　　而新任美国总统特朗普显然也注意到了这一问题，<strong>特朗普在竞选时宣称的税收改革倡议内容之一，就是对跨国公司藏在海外的利润汇回美国时实施一次性减税，从目前的35%削减至10%。</strong></p><p>　　实际上这并非特朗普首创，2004年，为吸引美国公司将海外利润汇回国内，布什政府推出了美国国内投资法案(HIA)，给予了美国公司一个“免税期”：对于2005年内汇回国内的海外收入，只一次性征收5.25%的税率(而不是35%的联邦法定税率).</p><p>　　该项法案的实施导致2005汇回美国的的海外收入直接飙升270%，至3000亿美元，而2004这一数字仅仅为820亿美元。其中高科技及医疗公司占汇回总数的50%。</p><p>　　这对美股也是大利好，导致2004及2005年<span id=\\\"stock_5135001\\\"><span class=\\\"keytip\\\" href=\\\"http://fund.eastmoney.com/513500.html\\\" target=\\\"_blank\\\">标普500</span></span><span id=\\\"quote_5135001\\\"></span>股票回购量飙升，相应的股票分红及并购也得到增长。但是这种增长在“免税期”条款到期后又回落。</p><p>　　特朗普减税计划的目的是让跨国公司将资金调回美国，促进实体经济和基础设施投资得到改善，但前景备受质疑。</p><p>　　<strong>以小布什当年的减税计划为例，2005年美国经济并没有因此明显提升，当年<span id=\\\"Info.342\\\"><span class=\\\"infokey\\\" href=\\\"http://data.eastmoney.com/cjsj/gdp.html\\\" target=\\\"_blank\\\">GDP</span></span>增速为2.9%，比2004年的3.6%还低。</strong>但美国股市从2005年开始明显上涨。显然跨国公司利润汇回国后更多用于分红、回购股票，在推动股市上涨中起到了重要作用。</p><p>　　分析人士认为，美国股市先于实体经济恢复至危机前的高位，预示着里面存在一定的泡沫。<strong>如果跨国公司汇回的利润再度主要流入美国股市，将导致股市泡沫扩张更加严重。而泡沫破灭后，又有可能成为下一次经济衰退的“导火索”。</strong></p>";
//            String data = "<p><a class=\"EmImageRemark\" href=\"http://quote.eastmoney.com/us/aapl.html\" target=\"_blank\"><img alt=\"K图 aapl_31\" border=\"0\" height=\"276\" jquery17203844175735919589=\"52\" src=\"http://hooviewimg.oss-cn-shanghai.aliyuncs.com/eastmoney/507910-1b47e8870280106364f4f077fd03e002\" style=\"border-bottom:#d1d1d1 1px solid;border-left:#d1d1d1 1px solid;border-top:#d1d1d1 1px solid;border-right:#d1d1d1 1px solid\" width=\"578\"></a></p><p>　　<span id=\"stock_NASDAQ_AAPL7\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/us/AAPL.html?Market=NASDAQ\" target=\"_blank\">苹果</a></span><span id=\"quote_AAPL7\"></span>公司手握巨额现金，超过德国<span id=\"Info.78290\"><a class=\"infokey\" href=\"http://topic.eastmoney.com/wcdd/\" target=\"_blank\">外汇储备</a></span>的新闻震动市场。</p><p>　　上市公司中的“现金土豪”往往与高分红联系起来。</p><p>　　令人但令人奇怪的是，坐拥海量现金的苹果公司却依靠发债来分红和回购股票，回馈股东利益。</p><p>　　这当中有何玄机？</p><p>　　苹果于上周发布了季度业绩报告。资料显示，<strong>苹果的现金储备达到了2568亿美元，</strong>这一数字超过了<span id=\"stock_NYSE_WMT7\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/us/WMT.html?Market=NYSE\" target=\"_blank\">沃尔玛</a></span><span id=\"quote_WMT7\"></span>公司或<span id=\"stock_NYSE_GE7\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/us/GE.html?Market=NYSE\" target=\"_blank\">通用电气</a></span><span id=\"quote_GE7\"></span>的市值，更超越了德国的<span id=\"Info.352\"><a class=\"infokey\" href=\"http://data.eastmoney.com/cjsj/hjwh.html\" target=\"_blank\">外汇储备</a></span>金额，是名副其实的富可敌国。</p><p>　　CNBC数据显示，苹果现金储备在过去逾2年的时间里翻了近一倍；<strong>其现金储备规模是除了金融公司外美国近代以来的公司当中最高的。</strong></p><p>　　上市公司中的“现金土豪”往往意味这高分红和股票回购，苹果公司也不例外，其分红和股票回购等股东回馈措施的水平飙至历史新高，但并不是用自己的钱，而是通过发<span id=\"stock_3950222\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/ZS395022.html\" target=\"_blank\">企业债</a></span><span id=\"quote_3950222\"></span>来进行融资。</p><p>　　据《金融时报》5月5日报道，苹果当地时间上周四重返企业债券市场，发行数十亿<span id=\"stock_5013001\"><a class=\"keytip\" href=\"http://fund.eastmoney.com/501300.html\" target=\"_blank\">美元债</a></span><span id=\"quote_5013001\"></span>券，为当周早些时候公布的增加股息和股票回购计划融资。苹果将发行6种不同类型债券，其中包括2020年和2022年到期的两种浮动<span id=\"Info.344\"><a class=\"infokey\" href=\"http://data.eastmoney.com/cjsj/yhll.html\" target=\"_blank\">利率</a></span>债券，固定<span id=\"Info.391\"><a class=\"infokey\" href=\"http://data.eastmoney.com/cjsj/yhll.html\" target=\"_blank\">利率</a></span>债券期限为3-10年。</p><p>　　<strong>投资者对苹果债券兴趣非常强烈，苹果10年期债券利率比同期美国<span id=\"stock_3950312\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/ZS395031.html\" target=\"_blank\">国债</a></span><span id=\"quote_3950312\"></span>高90个基点，约为3.25%。</strong></p><p>　　承受比国债高90个基点的利率，借钱来回馈股东，那为何不用自己的巨额现金呢，难道是因为嫌钱太多没地方花？</p><p>　　答案当然不是，实际上这与美国的复杂的税收体制有关，与大多数国家不同，美国实施全球税收政策，即对美国的个人和企业在全球的所有收入都征税。其中，对于跨国公司的海外收入，允许递延至收入汇回美国时再征收。</p><p>　　但是，美国对跨国公司在海外税收采取相对比较高额的抑制性税收调节政策。<strong>例如，企业海外收入汇回美国时，联邦“汇回税”率高达35%；各个州还要征收州税，平均水平大约为4.</strong>也就是说如果汇回2000亿美金的话，刚到美国其中的近4成，也就是约800亿美元就上缴了。</p><p>　　虽然各国针对本国跨国公司，都有汇回税率一说，但美国的海外汇回税率位居OECD(经合组织)国家之首。</p><p>　　根据<span id=\"stock_NYSE_GS7\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/us/GS.html?Market=NYSE\" target=\"_blank\">高盛</a></span><span id=\"quote_GS7\"></span>测算，<span id=\"stock_1611252\"><a class=\"keytip\" href=\"http://fund.eastmoney.com/161125.html\" target=\"_blank\">标普500</a></span><span id=\"quote_1611252\"></span>中的大型跨国公司将大量海外收入留在当地以避免缴纳高昂的税费。规模大约有2.4万亿美元，其中现金约1万亿美元。</p><p>　　因此和众多跨国公司一样，苹果拒绝将海外收益带来美国，以避免高额的企业汇回税。</p><p>　　<strong>巨额现金变相回流 </strong></p><p>　　<strong>无税还有利息拿</strong></p><p>　　当然留在海外的巨额资金，并不是真的躺在海外离岸账户里了，而是以另一种方式回流美国，<strong>手握大量现金的苹果公司就选择投资<span id=\"stock_3950242\"><a class=\"keytip\" href=\"http://quote.eastmoney.com/ZS395024.html\" target=\"_blank\">公司债</a></span><span id=\"quote_3950242\"></span>券、货币市场基金和美国国债。</strong></p><p>　　根据苹果公司上周递交的监管文件，该公司拥有的2570亿美元现金储备中，有1480亿美元投资了企业债，规模足以购买全球最大的固定收益共同基金Vanguard Total Bond Market Index Fund的所有资产，后者整体资产规模为1450亿美元。</p><p>　　除了企业债，苹果的现金储备里持仓第二位的资产就是适销债券。苹果将530亿美元投资了美国政府国债，210亿美元投资了抵押债券和资产担保债权。</p><p>　　那么这样到底好处何在？彭博从一份监管文件中了解到，<strong>过去五年苹果将其大部分海外营收以无税的方式转回美国境内——部分用于购买政府债券。作为回报，财政部在此期间至少向苹果支付了6亿美元的利息，实际数额可能比这更大。</strong></p><p>　　据彭博社对美国证监会相关材料的研究保守估计，美国十大巨头在2012-2016的五年内凭此获得了超过14亿美元的利息收入。</p><p>　　<strong>减税计划或只能利好美股</strong></p><p>　　而新任美国总统特朗普显然也注意到了这一问题，<strong>特朗普在竞选时宣称的税收改革倡议内容之一，就是对跨国公司藏在海外的利润汇回美国时实施一次性减税，从目前的35%削减至10%。</strong></p><p>　　实际上这并非特朗普首创，2004年，为吸引美国公司将海外利润汇回国内，布什政府推出了美国国内投资法案(HIA)，给予了美国公司一个“免税期”：对于2005年内汇回国内的海外收入，只一次性征收5.25%的税率(而不是35%的联邦法定税率).</p><p>　　该项法案的实施导致2005汇回美国的的海外收入直接飙升270%，至3000亿美元，而2004这一数字仅仅为820亿美元。其中高科技及医疗公司占汇回总数的50%。</p><p>　　这对美股也是大利好，导致2004及2005年<span id=\"stock_5135001\"><a class=\"keytip\" href=\"http://fund.eastmoney.com/513500.html\" target=\"_blank\">标普500</a></span><span id=\"quote_5135001\"></span>股票回购量飙升，相应的股票分红及并购也得到增长。但是这种增长在“免税期”条款到期后又回落。</p><p>　　特朗普减税计划的目的是让跨国公司将资金调回美国，促进实体经济和基础设施投资得到改善，但前景备受质疑。</p><p>　　<strong>以小布什当年的减税计划为例，2005年美国经济并没有因此明显提升，当年<span id=\"Info.342\"><a class=\"infokey\" href=\"http://data.eastmoney.com/cjsj/gdp.html\" target=\"_blank\">GDP</a></span>增速为2.9%，比2004年的3.6%还低。</strong>但美国股市从2005年开始明显上涨。显然跨国公司利润汇回国后更多用于分红、回购股票，在推动股市上涨中起到了重要作用。</p><p>　　分析人士认为，美国股市先于实体经济恢复至危机前的高位，预示着里面存在一定的泡沫。<strong>如果跨国公司汇回的利润再度主要流入美国股市，将导致股市泡沫扩张更加严重。而泡沫破灭后，又有可能成为下一次经济衰退的“导火索”。</strong></p>";
//            data = data.replace("<img", "<img style='max-width:90%;height:auto;'");
//            mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        }
    }

    @Override
    public void onBackPressed() {
        if (mEtComment.isShown() && detailType != TYPE_COMMENTS) {
            hideInputMethod();
            mInputCommentBar.setVisibility(View.GONE);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        hideInputMethod();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mWebView.reload();  //H5数据会没 如果不重新刷新
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideInputMethod();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.loadUrl("blank://");
        mWebView.removeAllViews();
        mWebView.destroy();
        mFlContainer.removeAllViews();
        mCompositeSubscription.unsubscribe();
    }

    private void postComment(String code, boolean isStock) {
        HooviewApiHelper.getInstance().postComment(code, mEtComment.getText().toString(), isStock,
                new MyRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Logger.d(TAG, "post comment success : " + result);
                        SingleToast.show(getApplicationContext(), R.string.msg_comment_success);
                        mEtComment.getText().clear();
                        mInputCommentBar.setVisibility(View.GONE);
                        mWebView.callHandler("refreshComments", "", new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {
                                Logger.d(TAG, "onCallBack: refreshComments");
                            }
                        });
                        mWebView.reload();

                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        Logger.w(TAG, "post comment failed : " + errorInfo);
                        mWebView.callHandler("refreshComments", "", new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {
                                Logger.d(TAG, "onCallBack: refreshComments");
                            }
                        });
                        mWebView.reload();

                    }

                    @Override
                    public void onFailure(String msg) {
                        Logger.w(TAG, "post comment failed : " + msg);
                        mWebView.callHandler("refreshComments", "", new CallBackFunction() {
                            @Override
                            public void onCallBack(String data) {
                                Logger.d(TAG, "onCallBack: refreshComments");
                            }
                        });
                        mWebView.reload();

                    }
                });
        hideInputMethod();
    }

    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtComment.getWindowToken(),
                0);
    }

    private void registerWebCallback() {
        mWebView.registerHandler("showNewsDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String newsId = JsonParserUtil.getString(data, "newsid");
                String title = JsonParserUtil.getString(data, "title");
                Utils.showNewsDetail(getApplicationContext(), title, newsId);
                Logger.d(TAG, "handler = submitFromWeb, data from web = " + data);
                //function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });
        mWebView.registerHandler("showStockDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String code = JsonParserUtil.getString(data, "code");
                String name = JsonParserUtil.getString(data, "name");
                Utils.showStockDetail(getApplicationContext(), name, code, true);
            }
        });
        mWebView.registerHandler("showAllComments", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String newsId = JsonParserUtil.getString(data, "newsid");
                String title = JsonParserUtil.getString(data, "title");
                Utils.showAllComments(getApplicationContext(), title, newsId);
            }
        });
        mWebView.registerHandler("unLikeNews", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                SingleToast.show(getApplicationContext(), R.string.msg_feature_not_complete);
            }
        });
        mWebView.registerHandler("setCommentCount", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String count = JsonParserUtil.getString(data, "count");
                if (count.equals("0"))
                    tvCommentCount.setText("");
                else
                    tvCommentCount.setText(count);
            }
        });
        mWebView.registerHandler("showVipUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String name = JsonParserUtil.getString(data, "name");
                Utils.toUserPager(WebDetailActivity.this,name,1);
            }
        });
        mWebView.registerHandler("showStockAnnouncementDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String url =  JsonParserUtil.getString(data,"url");
                String title=  JsonParserUtil.getString(data,"title");
                Utils.showPublicNewsDetail(WebDetailActivity.this,title,url);
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_stock_add:
                    if (!Preferences.getInstance(WebDetailActivity.this).isLogin() || !EVApplication.isLogin()) {
                        LoginActivity.start(WebDetailActivity.this);
                        return;
                    }
                    collectStock(code,isCollected);
                    break;
                case R.id.tv_stock_share:
                    shareUrl();
                    break;
                case R.id.tv_stock_refresh:
                    mWebView.reload();
                    break;
                case R.id.tv_news_share:
                    shareUrl();
                    break;
                case R.id.tv_news_collect:  // 文章收藏
                    setCollectStatus();
                    break;
                case tv_news_comment:
                    if (!Preferences.getInstance(WebDetailActivity.this).isLogin() || !EVApplication.isLogin()) {
                        LoginActivity.start(WebDetailActivity.this);
                        return;
                    }
                    Utils.showAllComments(getApplicationContext(), getIntent().getStringExtra(EXTRA_TITLE), getIntent().getStringExtra(EXTRA_CODE));
                    break;
                case R.id.tv_news_comment_hint:
                case R.id.tv_stock_comment_hint:
                    if (!Preferences.getInstance(WebDetailActivity.this).isLogin() || !EVApplication.isLogin()) {
                        LoginActivity.start(WebDetailActivity.this);
                        return;
                    }
                    mInputCommentBar.setVisibility(View.VISIBLE);
                    mEtComment.requestFocus();
                    showInputMethod(mEtComment);
                case R.id.et_comment:
                    ApiUtil.checkSession(view.getContext());
                    break;
                case R.id.iv_send:
                    break;
            }
        }
    };

    /**
     * 判断收藏状态
     */
    private void getCollectStatus()
    {
        if (EVApplication.isLogin()) {
           Subscription subscription =  RetrofitHelper.getInstance().getService()
                    .getNewsCollectStatus(EVApplication.getUser().getSessionid(),code,0,1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NewsCollectStatus>() {
                        @Override
                        public void OnSuccess(NewsCollectStatus newsCollectStatus) {
                            if (newsCollectStatus.getExist() == 1)
                                mTvNewsCollect.setSelected(true);
                            else
                                mTvNewsCollect.setSelected(false);
                        }

                        @Override
                        public void OnFailue(String msg) {

                        }
                    });
            mCompositeSubscription.add(subscription);
        }
    }

    /**
     * 收藏点击状态
     */
    private void setCollectStatus()
    {
        if (EVApplication.isLogin()) {
            final int action = mTvNewsCollect.isSelected() ? 0 : 1;
         Subscription subscription = RetrofitHelper.getInstance().getService()
                    .addNewsToColloction(EVApplication.getUser().getName(),
                            EVApplication.getUser().getSessionid(),
                            code,action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                            if (action == 0) {
                                mTvNewsCollect.setSelected(false);
                            } else {
                                mTvNewsCollect.setSelected(true);
                            }
                        }
                        @Override
                        public void OnFailue(String msg) {
                            SingleToast.show(WebDetailActivity.this,R.string.opreat_fail);
                        }
                    });
            mCompositeSubscription.add(subscription);
        }else
            LoginActivity.start(mContext);
    }

    private void getCollectionInfo(String id) {
        RealmResults<ReadRecord> results = RealmHelper.getInstance().getRealm().where(ReadRecord.class).findAll();
        for (ReadRecord item : results) {
            if (item.getId().equals(id)) {
                insertHistoryRecord(item);
                break;
            }
        }
    }

    private void collectStock(String code, final int type) {
        if (TextUtils.isEmpty(code)) {
            return;
        }
        HooviewApiHelper.getInstance().updateStocks(EVApplication.getUser().getName(), code, type == 1?"2":"1", new MyRequestCallBack() {

            @Override
            public void onSuccess(Object result) {
                if (type == 0)
                {
                    SingleToast.show(WebDetailActivity.this, getString(R.string.add_stock_success));
                    mTvStockAdd.setSelected(true);
                    isCollected = 1;
                }else
                {
                    SingleToast.show(WebDetailActivity.this, getString(R.string.delect_stock_sucess));
                    isCollected = 0;
                    mTvStockAdd.setSelected(false);
                }

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    /**
     * 判断是否已经勾选了股票
     */
    private void isStockAdded()
    {
        HooviewApiHelper.getInstance().isStockAdded(EVApplication.getUser().getName(), code, new MyRequestCallBack() {
            @Override
            public void onSuccess(Object result) {
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    int exist = jsonObject.getInt("exist");  // 0 不存在 1 存在
                    if (exist == 1)
                    {
                        mTvStockAdd.setSelected(true);
                        isCollected = 1;
                    }else
                    {
                        isCollected = 0;
                        mTvStockAdd.setSelected(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }


    protected void shareUrl() {
        String content = "";
        ShareContent shareContent = new ShareContentWebpage(getIntent().getStringExtra(EXTRA_TITLE), content,
                mWebView.getUrl(), "");
        ShareHelper.getInstance(this).showShareBottomPanel(shareContent);
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Logger.d(TAG, "Loading progress : " + newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    private void insertHistoryRecord(ReadRecord newsModel) {
        String mVideoId = newsModel.getId() + "";
        if (!TextUtils.isEmpty(mVideoId) && !RealmHelper.getInstance().queryCollectionId(mVideoId)) {
            Collection bean = new Collection();
            bean.setId(String.valueOf(mVideoId));
            bean.setPic(newsModel.getPic());
            bean.setTitle(newsModel.getTitle());
            bean.setTime(newsModel.getTime());
            bean.setCount(newsModel.getCount());
            RealmHelper.getInstance().insertCollection(bean);
        } else {
            RealmHelper.getInstance().deleteCollection(newsModel.getId());
        }
    }
}
