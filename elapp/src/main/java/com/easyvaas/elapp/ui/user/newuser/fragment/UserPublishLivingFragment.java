package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.usernew.UserVLivingAdapter;
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
 * 个人中心---我的发布---直播
 */

public class UserPublishLivingFragment extends MyBaseListFragment<UserVLivingAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserVLivingAdapter initAdapter() {
        UserVLivingAdapter adapter = new UserVLivingAdapter(new ArrayList<VideoEntity>());
        adapter.showHeader(true);
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
                RetrofitHelper.getInstance().getService().getUserPublishLivingTest("http://192.168.8.191:8888/user/works?type=0")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<UserPublishVideoModel>() {
                            @Override
                            public void OnSuccess(UserPublishVideoModel result) {
                                if (result != null) {
                                    mAdapter.setHeaderModel(result.getTextlive());
                                    mAdapter.dealLoadData(UserPublishLivingFragment.this, isLoadMore, result.getVideolive());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserPublishLivingFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPublishLivingFragment newInstance() {
        return new UserPublishLivingFragment();
    }
}
