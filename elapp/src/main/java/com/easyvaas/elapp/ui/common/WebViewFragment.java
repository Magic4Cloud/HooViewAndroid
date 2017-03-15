package com.easyvaas.elapp.ui.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hooview.app.R;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.base.BaseFragment;
public class WebViewFragment extends BaseFragment {
    private WebView mWebView;
    private String mUrl;

    public static WebViewFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_common_webview, null);
        initView(view);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void loadUrl(String url) {
        mUrl = url;
        mWebView.loadUrl(url);
    }

    private void initView(View view) {
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setUserAgentString(RequestUtil.getAppUA());
        mWebView.setWebViewClient(new MyWebViewClient());
        MyWebChromeClient webChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.loadUrl("http://www.hao123.com");
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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //if (mRefreshBtn.getAnimation() != null) {
            //    mRefreshBtn.getAnimation().cancel();
            //}
//            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.startsWith(ApiConstant.EXTRA_URI_SHARE)) {
//                Map<String, String> param = RequestUtil.decodeUrlParam(url);
//                url = param.get("url");
//                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
//                    Preferences pref = Preferences.getInstance(getApplicationContext());
//                    String title = pref.getString(Preferences.KEY_PARAM_INVITE_TITLE);
//                    String desc = pref.getString(Preferences.KEY_PARAM_INVITE_DESC);
//                    if (TextUtils.isEmpty(title)) {
//                        title = getString(R.string.invite_friend);
//                    }
//                    String shareImage = getFilesDir() + File.separator + FileUtil.LOGO_FILE_NAME;
//                    mShareContent = new ShareContentWebpage(title, desc, url, shareImage);
//                    ShareHelper.getInstance(WebViewActivity.this).showShareBottomPanel(mShareContent);
//                }
//            } else if (url.startsWith(ApiConstant.EXTRA_URI_VIDEO)) {
//                Map<String, String> param = RequestUtil.decodeUrlParam(url);
//                String vid = param.get("vid");
//                String password = param.get("password");
//                String back = param.get("back");
//                Utils.watchVideo(getApplicationContext(), vid, password);
//            } else {
//                view.loadUrl(url);
//            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

}
