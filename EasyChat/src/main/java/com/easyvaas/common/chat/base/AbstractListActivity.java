package com.easyvaas.common.chat.base;

import android.view.MotionEvent;
import android.view.View;

import com.easyvaas.common.chat.net.ApiConstant;
import com.easyvaas.common.widget.EmptyView;

public abstract class AbstractListActivity extends BaseActivity {
    protected EmptyView mEmptyView;
    protected View mTapTopView;
    protected boolean mIsUserTapTopView;
    protected int mNextPageIndex;
    protected boolean mIsLoadingMore;

    protected View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        int x, y;
        boolean isScrollingUp = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getX();
                    y = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    int upx = (int) event.getX();
                    int upy = (int) event.getY();
                    isScrollingUp = upy - y > 20;
                    if (isScrollingUp) {
                        if (mTapTopView != null && mIsUserTapTopView) {
                            mTapTopView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (mTapTopView != null && mIsUserTapTopView) {
                            mTapTopView.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        //mTapTopView = findViewById(R.id.tap_top_iv);
        if (mTapTopView != null && mIsUserTapTopView) {
            mTapTopView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollToFirstItem();
                    mTapTopView.setVisibility(View.GONE);
                }
            });
        }
        initListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mEmptyView != null) {
            mEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEmptyView.getEmptyType() == EmptyView.TYPE_ERROR) {
                        loadData(false);
                    }
                }
            });
        }
    }

    protected void showEmptyView(int type, String title) {
        if (mEmptyView == null) {
            return;
        }
        switch (type) {
            case EmptyView.TYPE_EMPTY:
                mEmptyView.showEmptyView();
                break;
            case EmptyView.TYPE_ERROR:
                mEmptyView.showErrorView();
                break;
            case EmptyView.TYPE_LOADING:
                mEmptyView.showLoadingView();
                break;
        }
    }

    protected void hideEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.hide();
        }
    }

    protected abstract void initListView();

    protected void loadData(boolean isLoadMore) {
        if (mIsLoadingMore) {
            return;
        }
        if (!isLoadMore) {
            mNextPageIndex = ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        } else {
            mIsLoadingMore = true;
        }
    }

    protected abstract void scrollToFirstItem();

    protected abstract void onRefreshComplete(int updateCount);

    protected abstract void onRequestFailed(String msg);

    protected abstract boolean isListViewEmpty();

    protected abstract void setAutoLoadMore(boolean enable);

}
