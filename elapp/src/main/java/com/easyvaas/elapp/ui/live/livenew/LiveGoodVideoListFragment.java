package com.easyvaas.elapp.ui.live.livenew;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyvaas.elapp.adapter.usernew.UserVideoAdapter;
import com.easyvaas.elapp.bean.video.GoodsVideoListModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/25
 * Author  xiaomao
 * 精品视频列表
 */

public class LiveGoodVideoListFragment extends MyBaseListFragment<UserVideoAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserVideoAdapter initAdapter() {
        return new UserVideoAdapter(new ArrayList<VideoEntity>());
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService().getLiveGoodVideo(start)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<GoodsVideoListModel>() {
                            @Override
                            public void OnSuccess(GoodsVideoListModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(LiveGoodVideoListFragment.this, isLoadMore, result.getVideos());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(LiveGoodVideoListFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    public static LiveGoodVideoListFragment newInstance() {
        return new LiveGoodVideoListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshEvent(MainRefreshEvent event) {
        if (event != null && MainRefreshEvent.TYPE_LIVE.equals(event.type)) {
            autoRefresh();
        }
    }
}
