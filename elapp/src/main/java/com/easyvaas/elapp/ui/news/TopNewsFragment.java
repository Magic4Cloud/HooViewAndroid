package com.easyvaas.elapp.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyvaas.elapp.adapter.news.TopRatedNewsMyAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 要闻列表界面
 */

public class TopNewsFragment extends MyBaseListFragment<TopRatedNewsMyAdapter> {


    @Override
    protected TopRatedNewsMyAdapter initAdapter() {
        return new TopRatedNewsMyAdapter(new TopRatedModel());
    }

    @Override
    protected void getListData(Boolean isLoadMore) {
            if (!isLoadMore)
                getBannerData();
            getNewsData(isLoadMore);
    }


    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    /**
     * 获取新闻列表
     */
    private void getNewsData(final boolean isLoadMore) {
       Subscription subscription =  RetrofitHelper.getInstance().getService().getTopRatedNews(start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopRatedModel>() {
                    @Override
                    public void OnSuccess(TopRatedModel topRatedModel) {
                        if (topRatedModel != null)
                        {
                            if (!isLoadMore)
                            {
                                mAdapter.setTopRatedModel(topRatedModel);
                                topRatedModel.getHomeNews().add(0,new HomeNewsBean());
                            }
                            mAdapter.dealLoadData(TopNewsFragment.this,isLoadMore,topRatedModel.getHomeNews());
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(TopNewsFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    /**
     * 获取banner数据
     */
    private void getBannerData() {
       Subscription subscription =  RetrofitHelper.getInstance().getService().getBannerNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<BannerModel>() {
                    @Override
                    public void OnSuccess(BannerModel bannerModel) {
                        mAdapter.setBannerModel(bannerModel);
                    }

                    @Override
                    public void OnFailue(String msg) {

                    }
                });
        addSubscribe(subscription);
    }


    public static TopNewsFragment newInstance() {
        return new TopNewsFragment();
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
