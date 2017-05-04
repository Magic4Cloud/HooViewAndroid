package com.easyvaas.elapp.ui.news;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/04
 * Editor  Misuzu
 * 股市新闻列表
 */

public class StockMarketNewsFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }


    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserReadHistory(EVApplication.getUser().getName(),EVApplication.getUser().getSessionid(),start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                        mAdapter.dealLoadData(StockMarketNewsFragment.this,isLoadMore,normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(StockMarketNewsFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static StockMarketNewsFragment newInstance() {
        return new StockMarketNewsFragment();
    }

}
