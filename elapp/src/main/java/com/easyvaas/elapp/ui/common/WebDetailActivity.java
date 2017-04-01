package com.easyvaas.elapp.ui.common;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.elapp.app.EVApplication;
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
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.VIPUserInfoDetailActivity;
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
    private TextView mTvNewsShare;
    private TextView mTvNewsCollect;
    private TextView mTvNewsComment;


    private RelativeLayout mRlBottomStock;
    private TextView mTvStockCommentHint;
    private TextView mTvStockAdd;
    private TextView mTvStockRefresh;
    private TextView mTvStockShare;
    private String code;
    private int isCollected; // 0 未添加 1 已添加
    private  int detailType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        initViews();
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

        mFlContainer = (FrameLayout) findViewById(R.id.fl_container);
        mEtComment = (EditText) findViewById(R.id.et_comment);
        mInputCommentBar = findViewById(R.id.rl_input_text);

        mRlBottomStock = (RelativeLayout) findViewById(R.id.rl_bottom_stock);
        mTvStockAdd = (TextView) findViewById(R.id.tv_stock_add);
        mTvStockRefresh = (TextView) findViewById(R.id.tv_stock_refresh);
        mTvStockShare = (TextView) findViewById(R.id.tv_stock_share);
        mTvStockCommentHint = (TextView) findViewById(R.id.tv_stock_comment_hint);

        mTvStockAdd.setOnClickListener(mOnClickListener);
        mTvStockRefresh.setOnClickListener(mOnClickListener);
        mTvStockShare.setOnClickListener(mOnClickListener);
        mTvStockCommentHint.setOnClickListener(mOnClickListener);

        mRlBottomNews = (RelativeLayout) findViewById(R.id.rl_bottom_news);
        mTvNewsCollect = (TextView) findViewById(R.id.tv_news_collect);
        mTvNewsComment = (TextView) findViewById(tv_news_comment);
        mTvNewsShare = (TextView) findViewById(R.id.tv_news_share);

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

        //init web view
        mWebView = new BridgeWebView(this);
        /*mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCachePath(FileUtil.CACHE_DIR);*/
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUserAgentString(RequestUtil.getAppUA());
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
                tvCommentCount.setText(count);
            }
        });
        mWebView.registerHandler("showVipUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String name = JsonParserUtil.getString(data, "name");
                VIPUserInfoDetailActivity.start(WebDetailActivity.this, name);
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
                case R.id.tv_news_collect:
                    if (!Preferences.getInstance(WebDetailActivity.this).isLogin() || !EVApplication.isLogin()) {
                        LoginActivity.start(WebDetailActivity.this);
                        return;
                    }
                    if ((!TextUtils.isEmpty(code) && !RealmHelper.getInstance().queryCollectionId(code))) {
                        mTvNewsCollect.setSelected(true);
                        getCollectionInfo(code);
                    } else {
                        mTvNewsCollect.setSelected(false);
                        getCollectionInfo(code);
                    }
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
                    mTvStockAdd.setText(R.string.added);
                    mTvStockAdd.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.comment_level_other));
                }else
                {
                    SingleToast.show(WebDetailActivity.this, getString(R.string.delect_stock_sucess));
                    isCollected = 0;
                    mTvStockAdd.setSelected(false);
                    mTvStockAdd.setText(getString(R.string.add_stock_btn));
                    mTvStockAdd.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.text_color_gray));
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
                        mTvStockAdd.setText(R.string.added);
                        mTvStockAdd.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.comment_level_other));
                    }else
                    {
                        isCollected = 0;
                        mTvStockAdd.setSelected(false);
                        mTvStockAdd.setText(getString(R.string.add_stock_btn));
                        mTvStockAdd.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.text_color_gray));
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
