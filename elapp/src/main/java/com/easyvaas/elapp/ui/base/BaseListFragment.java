package com.easyvaas.elapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;

public abstract class BaseListFragment extends BaseFragment {
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView mTvPrompt;
    protected ImageView mIvPrompt;
    protected LinearLayout mLLEmpty;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            BaseListFragment.this.onRefresh();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_common_list, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        mIvPrompt = (ImageView) view.findViewById(R.id.iv_prompt);
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
    public void showEmptyView(String prompt) {
        mLLEmpty.setVisibility(View.VISIBLE);
        mTvPrompt.setText(prompt);
    }


    public void onRefresh() {

    }

    public void hideEmptyView() {
        mLLEmpty.setVisibility(View.GONE);
    }

}
