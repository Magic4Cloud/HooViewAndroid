package com.easyvaas.elapp.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

import com.easyvaas.elapp.adapter.news.NewsColumnAdapter;
import com.easyvaas.elapp.bean.news.NewsColumnModel;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        MyStaggerLayoutManager manager = new MyStaggerLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
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

    public class MyStaggerLayoutManager extends StaggeredGridLayoutManager {

        public MyStaggerLayoutManager(int spanCount, int orientation) {
            super(spanCount, orientation);
        }

        public MyStaggerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
            try {
                super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
            } catch (IllegalArgumentException e) {
                Logger.e("xmzd", "RecyclerView Bug--- catch IllegalArgumentException");
            }
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshEvent(MainRefreshEvent event) {
        if (event != null && MainRefreshEvent.TYPE_NEWS.equals(event.type)) {
            autoRefresh();
        }
    }
}
