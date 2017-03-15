package com.easyvaas.elapp.ui.news;

import android.view.View;

import com.easyvaas.elapp.adapter.recycler.LastestNewsListAdapter;
import com.easyvaas.elapp.bean.news.LastestNewsModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;

import java.util.ArrayList;
import java.util.List;

public class LastestNewsListFragment extends BaseListRcvFragment {
    private LastestNewsListAdapter mAdapter;
    private boolean isHaveHeader;
    List<LastestNewsModel.NewsFlashEntity> lastestNewsModelist = new ArrayList();

    private int start;
    private int count = 10;

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mAdapter = new LastestNewsListAdapter(getContext(), lastestNewsModelist));
        lastestNewsModelist.add(new LastestNewsModel.NewsFlashEntity(true));
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

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadNewsList(false);
    }
}
