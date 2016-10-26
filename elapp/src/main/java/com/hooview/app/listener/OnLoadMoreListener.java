package com.hooview.app.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Author:   yyl
 * Description: RecycerView加载更多的监听
 * CreateDate:     2016/10/24
 */
public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    public static final int SCROLL_UP = 0;
    public static final int SCROLL_DOWN = 1;

    private LinearLayoutManager mLinearLayoutManager;

    private boolean loading;
    private int currentPage;
    private int lastPosition;
    private int startPosition;

    public OnLoadMoreListener(LinearLayoutManager linearLayoutManager, int startPage) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.startPosition = startPage;
        currentPage = startPage;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void resetState() {
        currentPage = startPosition;
        lastPosition = 0;
    }

    public OnLoadMoreListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        onScrollStateChanged(recyclerView, newState, lastPosition);
    }

    public abstract void onScrollStateChanged(RecyclerView recyclerView, int newState, int lastPosition);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final int totalItemCount = mLinearLayoutManager.getItemCount();
        final int currentLastPosition = mLinearLayoutManager.findLastVisibleItemPosition();
        if (lastPosition != currentLastPosition) {
            int scrollType = lastPosition > currentLastPosition ? SCROLL_UP : SCROLL_DOWN;
            lastPosition = currentLastPosition;
            onItemSwitch(lastPosition, scrollType);
        }
        if (!loading && (currentLastPosition == totalItemCount - 1)) {
            currentPage++;
            onLoadMore(currentPage);
        }
    }

    public abstract void onLoadMore(int current_page);

    public abstract void onItemSwitch(int lastPosition, int scrollType);
}
