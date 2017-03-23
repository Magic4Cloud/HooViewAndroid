package com.easyvaas.elapp.ui.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.LastestNewsListAdapter;
import com.easyvaas.elapp.bean.news.LastestNewsModel;
import com.easyvaas.elapp.event.NewsListScrollEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class LastestNewsListFragment extends BaseListRcvFragment {
    private LastestNewsListAdapter mAdapter;
    private boolean isHaveHeader;
    List<LastestNewsModel.NewsFlashEntity> lastestNewsModelist = new ArrayList();

    private int start;
    private int count = 10;
    private boolean isPageNotTop = false;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        int y = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            y = y + dy;
            if (y < ViewUtil.dp2Px(getContext(), 50) && dy < 0) {
                isPageNotTop = false;
                updateTabLayoutView();
            } else if (y > ViewUtil.dp2Px(getContext(), 50) && dy > 0) {
                isPageNotTop = true;
                updateTabLayoutView();
            }
        }
    };

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mAdapter = new LastestNewsListAdapter(getContext(), lastestNewsModelist));
        lastestNewsModelist.add(new LastestNewsModel.NewsFlashEntity(true));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        updateTabLayoutView();
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
                    if(!isLoadMore&&isHaveHeader){
                        isHaveHeader = true;
                        lastestNewsModelist.clear();
                    }
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
        EventBus.getDefault().post(new NewsListScrollEvent(isPageNotTop));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mRecyclerView != null){
            updateTabLayoutView();
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadNewsList(false);
    }
}
