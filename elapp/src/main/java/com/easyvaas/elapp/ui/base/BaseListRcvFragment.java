package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.recycler.PullToLoadView;
import com.hooview.app.R;
import com.easyvaas.elapp.base.BaseRvcFragment;

public abstract class BaseListRcvFragment extends BaseRvcFragment {
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView mTvPrompt;
    protected LinearLayout mLLEmpty;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            BaseListRcvFragment.this.onRefresh();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_common_rcv_list, null);
        mPullToLoadRcvView = (PullToLoadView) view.findViewById(R.id.pull_load_view);
        mRecyclerView = mPullToLoadRcvView.getRecyclerView();
        mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        mLLEmpty = (LinearLayout) view.findViewById(R.id.ll_empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        iniView(view);
        return view;
    }

    public abstract void iniView(View view);

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

