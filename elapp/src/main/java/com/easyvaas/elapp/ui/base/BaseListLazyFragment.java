package com.easyvaas.elapp.ui.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public abstract class BaseListLazyFragment extends BaseLazyFragment {

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
    @BindView(R.id.tv_add_select)
    protected TextView mTvAddSelect;
    @BindView(R.id.iv_operation)
    protected ImageView mImageViewOperation;
    protected LinearLayoutManager mLayoutManager;
    private Unbinder mUnbinder;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            BaseListLazyFragment.this.onRefresh();
        }
    };

    /**
     * 返回布局id
     *
     * @return int
     */
    @Override
    protected int getLayout() {
        return R.layout.layout_common_list;
    }

    /**
     * 初始化view
     */
    @Override
    protected void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        initAdapter();
    }

    /**
     * 初始化adapter
     */
    protected abstract void initAdapter();

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

    public void hideEmptyView() {
        mLLEmpty.setVisibility(View.GONE);
    }

    /**
     * 显示操作按钮
     *
     * @param resId int
     */
    public void showOperationView(int resId) {
        mImageViewOperation.setImageResource(resId);
        mImageViewOperation.setVisibility(View.VISIBLE);
    }

    public void hideOperationView() {
        mImageViewOperation.setVisibility(View.GONE);
    }

    /**
     * 下拉刷新，需要子类重写该方法
     */
    public void onRefresh() {

    }

    @OnClick(R.id.iv_operation)
    public void onOperation() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
