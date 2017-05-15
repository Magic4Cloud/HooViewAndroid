package com.easyvaas.elapp.ui.news;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.LastestNewsListAdapter;
import com.easyvaas.elapp.bean.news.LastestNewsModel;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LastestNewsListFragment extends BaseListRcvFragment {
    private LastestNewsListAdapter mAdapter;
    private boolean isHaveHeader;
    List<LastestNewsModel.NewsFlashEntity> lastestNewsModelist = new ArrayList();

    private int start;
    private int count = 10;
    private boolean isPageNotTop = false;

    public static LastestNewsListFragment newInstance() {
        return new LastestNewsListFragment();
    }
    @Override
    public void iniView(View view) {
        mRecyclerView.setPadding(0, (int) ViewUtil.dp2Px(getContext(),4),0,0);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setAdapter(mAdapter = new LastestNewsListAdapter(getContext(), lastestNewsModelist));
        lastestNewsModelist.clear();
//        lastestNewsModelist.add(new LastestNewsModel.NewsFlashEntity(true));
        loadData(false);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadNewsList(isLoadMore);
    }

    private void loadNewsList(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getLastestNewsList(mNextPageIndex + "", count + "", new MyRequestCallBack<LastestNewsModel>() {
            @Override
            public void onSuccess(LastestNewsModel result) {
                if (result != null) {
//                    if(!isLoadMore&&isHaveHeader){
//                        isHaveHeader = true;
//                        lastestNewsModelist.clear();
//                    }
                    mNextPageIndex = result.getNext();
                    lastestNewsModelist.addAll(result.getNewsFlash());
                    mAdapter.notifyDataSetChanged();
                    onRefreshComplete(result == null ? 0 : result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {
                onRefreshComplete(0);
            }
        });
    }

    private void updateTabLayoutView(){
//        EventBus.getDefault().post(new NewsListScrollEvent(isPageNotTop));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && mRecyclerView != null){
//            updateTabLayoutView();
//        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadNewsList(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private boolean mAutoRefreshing = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshEvent(MainRefreshEvent event) {
        if (event != null && MainRefreshEvent.TYPE_NEWS.equals(event.type)) {
            if (!mAutoRefreshing && mPullToLoadRcvView != null && mAdapter != null) {
                mAutoRefreshing = true;
                mPullToLoadRcvView.showRefreshingLoadingIcon();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToLoadRcvView.getRecyclerView().smoothScrollToPosition(0);
                        onRefresh();
                        mAutoRefreshing = false;
                    }
                }, 800);
            }
        }
    }
}
