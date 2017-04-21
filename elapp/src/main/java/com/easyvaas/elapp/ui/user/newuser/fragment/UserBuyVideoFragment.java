package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.usernew.UserVideoAdapter;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的购买---精品视频
 */

public class UserBuyVideoFragment extends MyBaseListFragment<UserVideoAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserVideoAdapter initAdapter() {
        UserVideoAdapter adapter = new UserVideoAdapter(new ArrayList<VideoEntity>());
        return adapter;
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService().getUserBuyVideoTest("http://192.168.8.125:8888/user/purchases?type=1")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<UserPublishVideoModel>() {
                            @Override
                            public void OnSuccess(UserPublishVideoModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(UserBuyVideoFragment.this, isLoadMore, result.getVideolive());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserBuyVideoFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    public static UserBuyVideoFragment newInstance() {
        return new UserBuyVideoFragment();
    }
}
