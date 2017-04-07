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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseListFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_prompt)
    protected TextView mTvPrompt;
    @BindView(R.id.iv_prompt)
    protected ImageView mIvPrompt;
    @BindView(R.id.ll_empty)
    protected LinearLayout mLLEmpty;
    private Unbinder mUnbinder;
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
        mUnbinder = ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        iniView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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

    /**
     * 下拉刷新，需要子类重写该方法
     */
    public void onRefresh() {

    }

    public void hideEmptyView() {
        mLLEmpty.setVisibility(View.GONE);
    }

}
