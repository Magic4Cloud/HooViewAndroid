package com.easyvaas.elapp.ui.user.usernew.fragment;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户收藏fragment
 */

public class UserCollectionFragment extends MyBaseListFragment<NormalNewsAdapter> {

    private String userId;

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeEmptyView() {
        super.changeEmptyView();
        if (userId.equals(EVApplication.getUser().getName()))
            mEmptyView.setEmptyTxt(getString(R.string.empty_no_collect));
        else
            mEmptyView.setEmptyTxt(getString(R.string.empty_no_user_colloct));
    }

    @Override
    protected void initSomeData() {
        userId = getArguments().getString(AppConstants.USER_ID);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserCollection(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                            mAdapter.dealLoadData(UserCollectionFragment.this, isLoadMore, normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserCollectionFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserCollectionFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        UserCollectionFragment fragment = new UserCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
