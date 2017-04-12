package com.easyvaas.elapp.ui.news;

import android.support.v7.widget.StaggeredGridLayoutManager;

import com.easyvaas.elapp.adapter.news.TopRatedNewsMyAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

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
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(manager);
    }

    /**
     * 获取新闻列表
     */
    private void getNewsData(final boolean isLoadMore) {
        RetrofitHelper.getInstance().getService().getTopRatedNewsTest("http://www.mocky.io/v2/58ec8f0c2700000c1048942d")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopRatedModel>() {
                    @Override
                    public void OnSuccess(TopRatedModel topRatedModel) {
                        // Aya : 2017/4/11 后台接口有问题 延后调试
                        if (topRatedModel != null)
                        {
                            if (!isLoadMore)
                                mAdapter.setTopRatedModel(topRatedModel);
                            mAdapter.dealLoadData(isLoadMore,topRatedModel.getHomeNews());
                            mSwiprefreshlayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                    }
                });
    }

    /**
     * 获取banner数据
     */
    private void getBannerData() {
        RetrofitHelper.getInstance().getService().getBannerNews()
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
    }

    public static TopNewsFragment newInstance() {
        return new TopNewsFragment();
    }

}
