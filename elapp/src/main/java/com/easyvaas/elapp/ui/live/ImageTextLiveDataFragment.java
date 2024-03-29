package com.easyvaas.elapp.ui.live;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.recycler.LiveDataAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.easyvaas.elapp.event.AppBarLayoutOffsetChangeEvent;
import com.easyvaas.elapp.net.HooviewApiConstant;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.JsonParserUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.easyvaas.elapp.ui.common.WebDetailActivity.TYPE_EXPONENT;
import static com.easyvaas.elapp.ui.common.WebDetailActivity.TYPE_STOCK;


public class ImageTextLiveDataFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DataFragment";
    public static final String EXTRA_IS_ANCHOR = "extra_is_anchor";
    private BridgeWebView mWebView;
    private String mUrl;
    private RecyclerView mRecyclerView;
    private List<SearchStockModel.DataEntity> data;
    private LiveDataAdapter mAdapter;
    protected EditText mEtSearch;
    private View mLLEmpty;
    private String mKeyWord;
    private int start = 0;
    private int count = 10;
    private FrameLayout mFlContainer;
    private LinearLayout mLLInput;
    private boolean isAnchor;

    public static ImageTextLiveDataFragment newInstance(boolean isAnchor) {

        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_ANCHOR, isAnchor);
        ImageTextLiveDataFragment fragment = new ImageTextLiveDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAnchor = getArguments().getBoolean(EXTRA_IS_ANCHOR);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_image_text_live_data, null);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView(View view) {
        mWebView = new BridgeWebView(getContext());
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
        //mWebView.addJavascriptInterface(new JavaScriptInterface(new Handler(Looper.getMainLooper())), "hooviewObj");

        mWebView.registerHandler("showNewsDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String newsId = JsonParserUtil.getString(data, "newsid");
                String title = JsonParserUtil.getString(data, "title");
                Utils.showNewsDetail(getContext(), title, newsId);
                Logger.d(TAG, "handler = submitFromWeb, data from web = " + data);
                //function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });
        mWebView.registerHandler("showStockDetail", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String code = JsonParserUtil.getString(data, "code");
                String name = JsonParserUtil.getString(data, "name");
                Utils.showStockDetail(getContext(), name, code, true);
            }
        });
        mFlContainer = (FrameLayout) view.findViewById(R.id.fl_webview);
        mFlContainer.addView(mWebView, new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new LiveDataAdapter(getContext(), data = new ArrayList()));
        mRecyclerView.setVisibility(View.GONE);
        view.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStock();
                BaseActivity baseActivity= (BaseActivity) getActivity();
                baseActivity.hideInputMethod();
            }
        });
        mLLEmpty = view.findViewById(R.id.ll_empty);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchStockModel.DataEntity stockModel = data.get(position);
                startLoadWeb(TYPE_STOCK, stockModel.getSymbol(), stockModel.getName());
                mRecyclerView.setVisibility(View.GONE);
                mLLEmpty.setVisibility(View.GONE);
                mFlContainer.setVisibility(View.VISIBLE);
            }
        });
        mFlContainer.setVisibility(View.GONE);
        mEtSearch = (EditText) view.findViewById(R.id.et_search);
        mLLInput = (LinearLayout) view.findViewById(R.id.ll_input_area);
        if (!isAnchor) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLLInput.getLayoutParams();
            mLLInput.setLayoutParams(layoutParams);
        }
        view.findViewById(R.id.et_search).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search:
                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            if (newProgress == 100) {
////                mProgressBar.setVisibility(View.GONE);
//            } else if (!mProgressBar.isShown()) {
////                mProgressBar.setVisibility(View.VISIBLE);
//            } else {
////                mProgressBar.setProgress(newProgress);
//            }
            super.onProgressChanged(view, newProgress);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(LiveSearchStockEvent event) {
//        mKeyWord = event.keyword;
//        searchStock();
//    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    public void searchStock() {
        mKeyWord = mEtSearch.getText().toString().trim();
        if (TextUtils.isEmpty(mKeyWord)) {
            SingleToast.show(getContext(), getString(R.string.error_search_stock));
            return;
        }
        String userId = EVApplication.getUser() != null ? EVApplication.getUser().getName():"";
        HooviewApiHelper.getInstance().searchStock(mKeyWord,userId,start + "", count + "", new MyRequestCallBack<SearchStockModel>() {
            @Override
            public void onSuccess(SearchStockModel result) {
                if (result != null && result.getData() != null && result.getData().size() != 0) {
                    data.clear();
                    data.addAll(result.getData());
                    mAdapter.notifyDataSetChanged();
                    mFlContainer.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLLEmpty.setVisibility(View.GONE);
                } else {
                    mLLEmpty.setVisibility(View.VISIBLE);
                    mFlContainer.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    SingleToast.show(getContext(), getString(R.string.no_search_result));
                }
            }

            @Override
            public void onFailure(String msg) {
                mLLEmpty.setVisibility(View.VISIBLE);
                mFlContainer.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                SingleToast.show(getContext(), getString(R.string.no_search_result));
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mLLEmpty.setVisibility(View.VISIBLE);
                mFlContainer.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                SingleToast.show(getContext(), getString(R.string.no_search_result));
            }
        });
    }

    private void startLoadWeb(int type, String code, String name) {
        String url = "";
        switch (type) {
            case TYPE_STOCK:
                url = "/?page=stock&name=" + name + "&code=" + code + "&special=1&hidenews=1";
                break;
            case TYPE_EXPONENT:
                url = "/?page=stock&name=" + name + "&code=" + code + "&special=0&hidenews=1";
                break;
        }
        if (TextUtils.isEmpty(url)) {
            Logger.e(TAG, "load url is empty! load type: " + type);
        } else {
            mWebView.loadUrl(HooviewApiConstant.HOST_WEB_APP + url);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppBarLayoutOffsetChangeEvent event) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLLInput.getLayoutParams();
        layoutParams.bottomMargin = (int) ViewUtil.dp2Px(getContext(), 156) + event.offset;
        mLLInput.setLayoutParams(layoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.loadUrl("blank://");
        mWebView.removeAllViews();
        mWebView.destroy();
        mFlContainer.removeAllViews();
        EventBus.getDefault().unregister(this);
    }
}
