/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.hooview.app.R;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.FileUtil;
import com.hooview.app.utils.ShareHelper;
import com.hooview.app.utils.Utils;

import java.io.File;
import java.util.Map;

public class WebViewActivity extends BaseActivity {
    public static final int TYPE_ACTIVITY = 14;
    public static final int TYPE_CASH_FAQ = 15;

    public static final String EXTRA_KEY_TITLE = Constants.EXTRA_KEY_TITLE;
    public static final String EXTRA_KEY_DESC = "extra_key_desc";
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    public static final String EXTRA_KEY_URL = "extra_key_url";

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mType;
    private ShareContent mShareContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(EXTRA_KEY_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
        } else if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        String title = getIntent().getStringExtra(EXTRA_KEY_TITLE);
        String description = getIntent().getStringExtra(EXTRA_KEY_DESC);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        if (TextUtils.isEmpty(description)) {
            description = title;
        }
        mType = getIntent().getIntExtra(EXTRA_KEY_TYPE, 0);

        setContentView(com.hooview.app.R.layout.activity_web_view);

        mProgressBar = (ProgressBar) findViewById(com.hooview.app.R.id.top_progress_bar);
        mWebView = (WebView) findViewById(com.hooview.app.R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setUserAgentString(RequestUtil.getAppUA());
        mWebView.setWebViewClient(new MyWebViewClient());
        MyWebChromeClient webChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(webChromeClient);

        mWebView.loadUrl(url + "?sessionid=" + Preferences.getInstance(this).getSessionId());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.hooview.app.R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(com.hooview.app.R.color.base_red, com.hooview.app.R.color.base_yellow,
                com.hooview.app.R.color.base_purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        String shareImage = getFilesDir() + File.separator + FileUtil.LOGO_FILE_NAME;
        mShareContent = new ShareContentWebpage(title, description, url, shareImage);

        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);
        //tv.setText(R.string.contact_us);
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TYPE_ACTIVITY == mType) {
            getMenuInflater().inflate(com.hooview.app.R.menu.share_menu, menu);
            menu.findItem(com.hooview.app.R.id.menu_share).setTitle(com.hooview.app.R.string.share);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.hooview.app.R.id.menu_share:
                ShareHelper.getInstance(this).showShareBottomPanel(mShareContent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //if (mRefreshBtn.getAnimation() != null) {
            //    mRefreshBtn.getAnimation().cancel();
            //}
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ApiConstant.EXTRA_URI_SHARE)) {
                Map<String, String> param = RequestUtil.decodeUrlParam(url);
                url = param.get("url");
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    Preferences pref = Preferences.getInstance(getApplicationContext());
                    String title = pref.getString(Preferences.KEY_PARAM_INVITE_TITLE);
                    String desc = pref.getString(Preferences.KEY_PARAM_INVITE_DESC);
                    if (TextUtils.isEmpty(title)) {
                        title = getString(com.hooview.app.R.string.invite_friend);
                    }
                    String shareImage = getFilesDir() + File.separator + FileUtil.LOGO_FILE_NAME;
                    mShareContent = new ShareContentWebpage(title, desc, url, shareImage);
                    ShareHelper.getInstance(WebViewActivity.this).showShareBottomPanel(mShareContent);
                }
            } else if (url.startsWith(ApiConstant.EXTRA_URI_VIDEO)) {
                Map<String, String> param = RequestUtil.decodeUrlParam(url);
                String vid = param.get("vid");
                String password = param.get("password");
                String back = param.get("back");
                Utils.watchVideo(getApplicationContext(), vid, password);
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else if (!mProgressBar.isShown()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
