package com.easyvaas.elapp.ui.user.newuser.fragment;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.usernew.UserVLivingAdapter;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
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
 * 大V 直播界面
 */

public class VipUserLivingFragment extends MyBaseListFragment<UserVLivingAdapter> {

    String userId;

    @Override
    protected UserVLivingAdapter initAdapter() {
        UserVLivingAdapter adapter = new UserVLivingAdapter(new ArrayList<VideoEntity>());
        adapter.showHeader(true);
        return adapter;
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        userId = getArguments().getString(AppConstants.USER_ID);
    }

    @Override
    protected void initViewAndData() {
        super.initViewAndData();

    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService().getHisteryTest("http://demo2821846.mockable.io/user/historylist?type=0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserHistoryTestModel>() {
                    @Override
                    public void OnSuccess(UserHistoryTestModel result) {
                        mAdapter.dealLoadData(VipUserLivingFragment.this, isLoadMore, result.getVideolive());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(VipUserLivingFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static VipUserLivingFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        VipUserLivingFragment fragment = new VipUserLivingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
