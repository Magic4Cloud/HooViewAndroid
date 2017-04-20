package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
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
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户历史阅读记录
 */

public class UserHistoryRedNewFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(5);
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
                        mAdapter.dealLoadData(UserHistoryRedNewFragment.this,isLoadMore,normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserHistoryRedNewFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static UserHistoryRedNewFragment newInstance() {
        return new UserHistoryRedNewFragment();
    }
}
