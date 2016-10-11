/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.base;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easyvaas.common.recycler.PullCallback;
import com.easyvaas.common.recycler.PullToLoadView;
import com.easyvaas.common.widget.EmptyView;

public abstract class BaseRvcActivity extends AbstractListActivity {
    protected PullToLoadView mPullToLoadRcvView;

    @Override
    public void initListView() {
        mPullToLoadRcvView = (PullToLoadView) findViewById(com.hooview.app.R.id.pull_load_view);
        mEmptyView = mPullToLoadRcvView.getEmptyView();
        mPullToLoadRcvView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isFirstRow, isLastRow;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisiblePos = -1;
                int lastVisiblePos = -1;
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager glm = (GridLayoutManager) layoutManager;
                    firstVisiblePos = glm.findFirstVisibleItemPosition();
                    lastVisiblePos = glm.findLastVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
                    firstVisiblePos = llm.findFirstVisibleItemPosition();
                    lastVisiblePos = llm.findLastVisibleItemPosition();
                }
                isFirstRow = firstVisiblePos == 0;
                isLastRow = lastVisiblePos == recyclerView.getAdapter().getItemCount() - 1;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isFirstRow) {
                        if (mTapTopView != null && mIsUserTapTopView) {
                            mTapTopView.setVisibility(View.GONE);
                        }
                    } else if (isLastRow) {
                        //loadData(true);
                    }
                }
            }
        });
        mPullToLoadRcvView.getRecyclerView().setOnTouchListener(mOnTouchListener);
        mPullToLoadRcvView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mPullToLoadRcvView.getRecyclerView().setHasFixedSize(true);
        mPullToLoadRcvView.isLoadMoreEnabled(true);
        mPullToLoadRcvView.setLoadMoreOffset(4);
        mPullToLoadRcvView.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                loadData(true);
            }

            @Override
            public void onRefresh() {
                loadData(false);
            }

            @Override
            public boolean isLoading() {
                return mIsLoadingMore;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        setAutoLoadMore(true);
    }

    @Override
    protected void scrollToFirstItem() {
        if (mPullToLoadRcvView != null) {
            mPullToLoadRcvView.getRecyclerView().scrollToPosition(0);
        }
    }

    @Override
    protected void onRefreshComplete(int updateCount) {
        mIsLoadingMore = false;
        if (mPullToLoadRcvView == null) {
            return;
        }
        mPullToLoadRcvView.setComplete();
        if (isListViewEmpty()) {
            showEmptyView(EmptyView.TYPE_EMPTY, getString(com.hooview.app.R.string.empty_no_data_title));
        } else {
            hideEmptyView();
            if (updateCount == 0) {
                mPullToLoadRcvView.showNoMoreDataView();
            }
        }
    }

    @Override
    protected void onRequestFailed(String msg) {
        mIsLoadingMore = false;
        if (mPullToLoadRcvView != null) {
            mPullToLoadRcvView.setComplete();
        }
        if (isListViewEmpty()) {
            showEmptyView(EmptyView.TYPE_ERROR, getString(com.hooview.app.R.string.msg_network_bad_check_click_retry));
        }
    }

    @Override
    protected boolean isListViewEmpty() {
        int listCount = 0;
        if (mPullToLoadRcvView != null) {
            listCount = mPullToLoadRcvView.getRecyclerView().getAdapter().getItemCount();
            listCount = listCount - mPullToLoadRcvView.getHeaderCount();
        }
        return listCount == 0;
    }

    @Override
    protected void setAutoLoadMore(boolean enable) {
        if (mPullToLoadRcvView != null) {
            mPullToLoadRcvView.isLoadMoreEnabled(enable);
        }
    }
}
