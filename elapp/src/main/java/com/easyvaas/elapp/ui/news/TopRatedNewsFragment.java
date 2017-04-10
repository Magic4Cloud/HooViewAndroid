package com.easyvaas.elapp.ui.news;

import android.view.View;

import com.easyvaas.elapp.adapter.TopRatedNewsAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.news.TopRatedModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/10
 * Editor  Misuzu
 * 要闻列表
 */

public class TopRatedNewsFragment extends BaseListRcvFragment{

    private TopRatedNewsAdapter mTopRatedNewsAdapter;
    private TopRatedModel mTopRatedModel;

    @Override
    public void iniView(View view) {
        mTopRatedModel = new TopRatedModel();
        mRecyclerView.setAdapter(mTopRatedNewsAdapter = new TopRatedNewsAdapter(getActivity(),mTopRatedModel));
        getBannerData();
    }

    private void getNewsData()
    {
        RetrofitHelper.getInstance().getService().getTopRatedNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TopRatedModel>() {
                    @Override
                    public void OnSuccess(TopRatedModel topRatedModel) {
                        // Aya : 2017/4/11 后台接口有问题 延后调试
                    }

                    @Override
                    public void OnFailue(String msg) {
                    }
                });
    }

    private void  getBannerData()
    {
        RetrofitHelper.getInstance().getService().getBannerNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<BannerModel>() {
                    @Override
                    public void OnSuccess(BannerModel bannerModel) {
                        mTopRatedNewsAdapter.setBannerModel(bannerModel);
                    }

                    @Override
                    public void OnFailue(String msg) {

                    }
                });
    }
    public static TopRatedNewsFragment newInstance() {
        return new TopRatedNewsFragment();
    }

}
