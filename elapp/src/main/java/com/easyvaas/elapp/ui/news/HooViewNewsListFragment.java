package com.easyvaas.elapp.ui.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.HooviewNewsAdapter;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.bean.news.NewsListModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;

import java.util.ArrayList;
import java.util.List;


public class HooViewNewsListFragment extends BaseListRcvFragment {
    private RecyclerView.Adapter mAdapter;
    private List<NewsItemModel> mNewsList;
    private int start;
    private int count;
    private String mChannelId;
    private String mProgramId;

    public HooViewNewsListFragment(String mProgramId, String mChannelId) {  // 大兄弟 ide都报错了你还这样搞
        this.mProgramId = mProgramId;
        this.mChannelId = mChannelId;
        mNewsList = new ArrayList<>();
    }

    public HooViewNewsListFragment() {
        mNewsList = new ArrayList<>();
    }

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mAdapter = new HooviewNewsAdapter(mNewsList));
        onRefresh();
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadNewsList(isLoadMore);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadData(false);
    }

    private void loadNewsList(final Boolean isLoadMore) {
        HooviewApiHelper.getInstance().getNewsListByChannelId(mChannelId, mProgramId, mNextPageIndex, 20, new MyRequestCallBack<NewsListModel>() {
            @Override
            public void onSuccess(NewsListModel result) {
                if (result != null) {
                    mNextPageIndex = result.getNext();
                    if (isLoadMore)
                    {
                        mNewsList.addAll(result.getNews());
                    }else
                    {
                        mNewsList.clear();
                        mNewsList.addAll(result.getNews());
                    }
                    mAdapter.notifyDataSetChanged();
                    if (result.getNews() == null || result.getNews().size() == 0) {
                        mLLEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mLLEmpty.setVisibility(View.GONE);
                    }
                    onRefreshComplete(result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {
                    onRefreshComplete(0);
            }
        });
    }

}
