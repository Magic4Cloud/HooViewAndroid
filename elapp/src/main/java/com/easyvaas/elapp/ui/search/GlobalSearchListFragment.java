package com.easyvaas.elapp.ui.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.recycler.SearchLiveResultAdapter;
import com.easyvaas.elapp.adapter.recycler.SearchNewsResultAdapter;
import com.easyvaas.elapp.adapter.recycler.SearchStockResultAdapter;
import com.easyvaas.elapp.bean.search.SearchLiveModel;
import com.easyvaas.elapp.bean.search.SearchNewsModel;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.easyvaas.elapp.event.SearchEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.utils.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchListFragment extends BaseListFragment {
    private static final String TAG = "GlobalSearchListFragmen";
    public static final int TYPE_NEWS = 1;
    public static final int TYPE_LIVE = 2;
    public static final int TYPE_STOCk = 3;
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    public static final String EXTRA_KEY_WORD = "extra_key_word";
    public static final String EXTRA_KEY_POSITION = "extra_key_position";
    public static final String EXTRA_KEY_HAS_ADD_STOCK_BTN = "extra_has_add_stock_btn";
    private int mType;
    private CommonRcvAdapter mAdapter;
    private List data = new ArrayList();
    private String mKeyWord;
    private int start = 0;
    private int count = 30;
    private int next;
    private int position;
    boolean hasAddStockBtn=false;

    public static GlobalSearchListFragment newInstance(int type, String keyWord, int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_TYPE, type);
        args.putString(EXTRA_KEY_WORD, keyWord);
        args.putString(EXTRA_KEY_POSITION, keyWord);
        GlobalSearchListFragment fragment = new GlobalSearchListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GlobalSearchListFragment newInstance(int type, String keyWord, boolean hasAddStockBtn) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_TYPE, type);
        args.putString(EXTRA_KEY_WORD, keyWord);
        args.putString(EXTRA_KEY_POSITION, keyWord);
        args.putBoolean(EXTRA_KEY_HAS_ADD_STOCK_BTN, hasAddStockBtn);
        GlobalSearchListFragment fragment = new GlobalSearchListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(EXTRA_KEY_TYPE);
        mKeyWord = getArguments().getString(EXTRA_KEY_WORD);
        position = getArguments().getInt(EXTRA_KEY_POSITION);
        hasAddStockBtn = getArguments().getBoolean(EXTRA_KEY_HAS_ADD_STOCK_BTN);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        search();
    }

    @Override
    public void iniView(View view) {
        if (mType == TYPE_NEWS) {
            mRecyclerView.setAdapter(mAdapter = new SearchNewsResultAdapter(getContext(), data));
            mTvPrompt.setText(R.string.search_news_empty_prompt);
        } else if (mType == TYPE_LIVE) {
            mRecyclerView.setAdapter(mAdapter = new SearchLiveResultAdapter(getContext(), data));
            mTvPrompt.setText(R.string.search_news_empty_live_prompt);
        } else if (mType == TYPE_STOCk) {
            mRecyclerView.setAdapter(mAdapter = new SearchStockResultAdapter(getContext(), data, hasAddStockBtn));
            mTvPrompt.setText(R.string.search_news_empty_stock_prompt);
        } else {
            throw new RuntimeException("search type error!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        this.mKeyWord = event.keyWord;
        Logger.d(TAG, "onMessageEvent: " + mKeyWord + "   type=" + mType);
        if (this.position == event.position || event.position == -1) {
            search();
        }
    }

    private void search() {
        Logger.d(TAG, "search: " + mKeyWord);
        if (isVisible()) {
            data.clear();
            if (TextUtils.isEmpty(mKeyWord)) {
                mAdapter.notifyDataSetChanged();
                mLLEmpty.setVisibility(View.VISIBLE);
                return;
            }
            if (mType == TYPE_NEWS) {
                searchNews();
            } else if (mType == TYPE_LIVE) {
                searchLive();
            } else if (mType == TYPE_STOCk) {
                searchStock();
            }
        }
    }

    public void searchNews() {
        HooviewApiHelper.getInstance().searchNews(mKeyWord, start + "", count + "", new MyRequestCallBack<SearchNewsModel>() {
            @Override
            public void onSuccess(SearchNewsModel result) {
                if (result != null && result.getNews() != null) {
                    data.addAll(result.getNews());
                    mAdapter.notifyDataSetChanged();
                    if (data.size() != 0) {
                        mLLEmpty.setVisibility(View.GONE);
                    } else {
                        showNoDataView();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onFailure(String msg) {
                showNoDataView();
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mSwipeRefreshLayout.setRefreshing(false);
                showNoDataView();
            }
        });
    }

    public void searchStock() {
        HooviewApiHelper.getInstance().searchStock(mKeyWord, start + "", count + "", new MyRequestCallBack<SearchStockModel>() {
            @Override
            public void onSuccess(SearchStockModel result) {
                if (result != null && result.getData() != null) {
                    Logger.d(TAG, "onSuccess: searchStock" + result.getData().size());
                    data.addAll(result.getData());
                    mAdapter.notifyDataSetChanged();
                    if (data.size() != 0) {
                        mLLEmpty.setVisibility(View.GONE);
                    } else {
                        showNoDataView();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onFailure(String msg) {
                showNoDataView();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mSwipeRefreshLayout.setRefreshing(false);
                showNoDataView();
            }
        });
    }

    public void searchLive() {
        HooviewApiHelper.getInstance().searchLive(mKeyWord, start + "", count + "", new MyRequestCallBack<SearchLiveModel>() {
            @Override
            public void onSuccess(SearchLiveModel result) {
                if (result != null && result.getVideos() != null) {
                    data.addAll(result.getVideos());
                    mAdapter.notifyDataSetChanged();
                    if (data.size() != 0) {
                        mLLEmpty.setVisibility(View.GONE);
                    } else {
                        showNoDataView();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onFailure(String msg) {
                showNoDataView();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                showNoDataView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showNoDataView() {
        mTvPrompt.setText(R.string.search_live_empty_prompt);
        mIvPrompt.setImageResource(R.drawable.icon_cry);
        mLLEmpty.setVisibility(View.VISIBLE);
    }

}
