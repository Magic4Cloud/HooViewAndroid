package com.easyvaas.elapp.ui.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.HooviewNewsAdapter;
import com.easyvaas.elapp.bean.news.NewsItemModel;
import com.easyvaas.elapp.bean.news.NewsListModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;

import java.util.ArrayList;
import java.util.List;


public class HooViewNewsListFragment extends BaseListFragment {
    private RecyclerView.Adapter mAdapter;
    private List<NewsItemModel> mNewsList;
    private int start;
    private int count;
    private String mChannelId;
    private String mProgramId;

    public HooViewNewsListFragment(String mProgramId, String mChannelId) {
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
        loadNewsList();
    }

    private void loadNewsList() {
        HooviewApiHelper.getInstance().getNewsListByChannelId(mChannelId, mProgramId, start, count, new MyRequestCallBack<NewsListModel>() {
            @Override
            public void onSuccess(NewsListModel result) {
                if (result != null) {
                    mNewsList.clear();
                    mNewsList.addAll(result.getNews());
                    mAdapter.notifyDataSetChanged();
                    if (result.getNews() == null || result.getNews().size() == 0) {
                        mLLEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mLLEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

}
