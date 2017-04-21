package com.easyvaas.elapp.ui.user.newuser.fragment;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 大V 文章界面
 */

public class VipUserArticleFragment extends MyBaseListFragment<NormalNewsAdapter>{

    private String userId;

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(5);
    }

    @Override
    protected void initViewAndData() {
        super.initViewAndData();
        userId = getArguments().getString(AppConstants.USER_ID);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserReadHistoryTest("http://demo2821846.mockable.io/user/historylist?type=1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                            mAdapter.dealLoadData(VipUserArticleFragment.this,isLoadMore,normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(VipUserArticleFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }


    public static VipUserArticleFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        VipUserArticleFragment fragment = new VipUserArticleFragment();
        fragment.setArguments(args);
        return fragment;

    }

}
