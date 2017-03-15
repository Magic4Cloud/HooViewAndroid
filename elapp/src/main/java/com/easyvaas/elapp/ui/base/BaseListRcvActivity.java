package com.easyvaas.elapp.ui.base;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.recycler.PullToLoadView;
import com.hooview.app.R;

public class BaseListRcvActivity extends BaseRvcActivity {
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView mTvPrompt;
    protected LinearLayout mLLEmpty;
    protected TextView mTvTitle;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            BaseListRcvActivity.this.onRefresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);
        mPullToLoadRcvView = (PullToLoadView) findViewById(R.id.pull_load_view);
        mRecyclerView = mPullToLoadRcvView.getRecyclerView();
        mTvPrompt = (TextView) findViewById(R.id.tv_prompt);
        mLLEmpty = (LinearLayout) findViewById(R.id.ll_empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View view = findViewById(R.id.tv_title);
        if (view != null) {
            mTvTitle = (TextView) view;
        }
        View back = findViewById(R.id.iv_back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    public void showEmptyView() {
        mLLEmpty.setVisibility(View.VISIBLE);
        mTvPrompt.setText(R.string.empty_data);
    }

    public void showEmptyView(int prompt) {
        mLLEmpty.setVisibility(View.VISIBLE);
        mTvPrompt.setText(prompt);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
    }

    public void onRefresh() {

    }

    public void onLoadMore() {

    }

    public void hideEmptyView() {
        mLLEmpty.setVisibility(View.GONE);
    }
}
