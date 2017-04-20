package com.easyvaas.elapp.ui.news;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.easyvaas.elapp.adapter.news.NewsColumnAdapter;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 咨询---专栏
 */

public class NewsColumnFragment extends MyBaseListFragment<NewsColumnAdapter> {
    /**
     * 初始化Adapter
     */
    @Override
    protected NewsColumnAdapter initAdapter() {
        return new NewsColumnAdapter(new NewsColumnModel());
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(Boolean isLoadMore) {
        loadData(isLoadMore);
    }

    @Override
    protected void changeRecyclerView() {
        mRecyclerview.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(manager);
    }

    /**
     * 加载数据
     *
     * @param isLoadMore
     */
    private void loadData(final boolean isLoadMore) {
       Subscription subscription = RetrofitHelper.getInstance().getService().getNewsColumn(start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NewsColumnModel>() {
                    @Override
                    public void OnSuccess(NewsColumnModel data) {
                        if (data != null) {
                            mAdapter.dealLoadData(NewsColumnFragment.this,isLoadMore, data.getNews());
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        Log.e("OnFailue", msg);
                        mAdapter.dealLoadError(NewsColumnFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static NewsColumnFragment newInstance() {
        return new NewsColumnFragment();
    }
}
