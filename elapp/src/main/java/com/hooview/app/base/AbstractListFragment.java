/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.easyvaas.common.widget.EmptyView;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.utils.Constants;

import java.util.List;

public abstract class AbstractListFragment extends BaseFragment {

    protected EmptyView mEmptyView;
    protected View mTapTopView;
    protected boolean mIsUserTapTopView;

    //下一页index
    protected int mNextPageIndex;

    //是否加载更多
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
                        getActivity().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TAB_BAR));
                    } else {
                        if (mTapTopView != null && mIsUserTapTopView) {
                            mTapTopView.setVisibility(View.GONE);
                        }
                        if (!isListViewEmpty()) {
                            getActivity().sendBroadcast(new Intent(Constants.ACTION_HIDE_HOME_TAB_BAR));
                        }
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTapTopView = getView() == null ? null : getView().findViewById(com.hooview.app.R.id.tap_top_iv);
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
    public void onStart() {
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

    protected void removeDuplicateData(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j) instanceof VideoEntity) {
                    VideoEntity v1 = (VideoEntity) list.get(j);
                    VideoEntity v2 = (VideoEntity) list.get(i);
                    if (v1.getVid() != null && v2.getVid() != null && v1.getVid().equals(v2.getVid())) {
                        list.remove(j);
                    }
                } else if (list.get(j) instanceof UserEntity) {
                    UserEntity u1 = (UserEntity) list.get(j);
                    UserEntity u2 = (UserEntity) list.get(i);
                    if (u1.getName() != null && u2.getName() != null && u1.getName().equals(u2.getName())) {
                        list.remove(j);
                    }
                }
            }
        }
    }
}
