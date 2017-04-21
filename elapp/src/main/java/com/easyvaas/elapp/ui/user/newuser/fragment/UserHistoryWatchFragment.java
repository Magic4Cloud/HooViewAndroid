package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.usernew.UserVLivingAdapter;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
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
 * 用户历史观看
 */

public class UserHistoryWatchFragment  extends MyBaseListFragment<UserVLivingAdapter>{


    @Override
    protected UserVLivingAdapter initAdapter() {
        return new UserVLivingAdapter(new ArrayList<VideoEntity>());
    }


    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService().getHisteryTest("http://demo2821846.mockable.io/user/historylist?type=0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserHistoryTestModel>() {
                    @Override
                    public void OnSuccess(UserHistoryTestModel result) {
                        mAdapter.dealLoadData(UserHistoryWatchFragment.this, isLoadMore, result.getVideolive());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserHistoryWatchFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static UserHistoryWatchFragment newInstance() {
        return new UserHistoryWatchFragment();
    }
}
