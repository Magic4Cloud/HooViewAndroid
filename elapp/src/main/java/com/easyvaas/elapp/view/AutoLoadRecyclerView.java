package com.easyvaas.elapp.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


public class AutoLoadRecyclerView extends RecyclerView {

    private LoadMoreListener loadMoreListener;

    private boolean isLoadingMore;

    public AutoLoadRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        isLoadingMore = false;
        addOnScrollListener(new AutoLoadScrollListener(true, true));
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }
    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;

        public AutoLoadScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();

                //有回调接口，并且不是加载状态，并且剩下2个item，并且向下滑动，则自动加载
                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                        2) {
                    loadMoreListener.loadMore();
                    isLoadingMore = true;
                }
            }
        }


    }

}
