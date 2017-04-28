package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.usernew.UserVLivingAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的购买---视频直播
 */

public class UserBuyLivingFragment extends MyBaseListFragment<UserVLivingAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserVLivingAdapter initAdapter() {
        UserVLivingAdapter adapter = new UserVLivingAdapter(new ArrayList<VideoEntity>());
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
                RetrofitHelper.getInstance().getService()
                        .getUserBuyLiving(Preferences.getInstance(EVApplication.getApp()).getUserNumber(),
                                Preferences.getInstance(EVApplication.getApp()).getSessionId(), start)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<UserPublishVideoModel>() {
                            @Override
                            public void OnSuccess(UserPublishVideoModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(UserBuyLivingFragment.this, isLoadMore, result.getVideolive());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserBuyLivingFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_buy_living));
    }

    public static UserBuyLivingFragment newInstance() {
        return new UserBuyLivingFragment();
    }
}
