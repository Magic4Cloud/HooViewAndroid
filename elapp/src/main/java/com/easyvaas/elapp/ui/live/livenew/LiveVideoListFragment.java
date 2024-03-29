package com.easyvaas.elapp.ui.live.livenew;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyvaas.elapp.adapter.live.LiveVideoListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.video.RecommendVideoListModel;
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
 * 视频直播列表
 */

public class LiveVideoListFragment extends MyBaseListFragment<LiveVideoListAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected LiveVideoListAdapter initAdapter() {
        return new LiveVideoListAdapter(new ArrayList<VideoEntity>());
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
                        .getLiveVideo(start, 20, EVApplication.getUser() == null ? "" : EVApplication.getUser().getSessionid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<RecommendVideoListModel>() {
                            @Override
                            public void OnSuccess(RecommendVideoListModel result) {
                                if (result != null) {
                                    if (!isLoadMore) {
                                        mAdapter.setHotList(getActivity(), result.getHotrecommend());
                                    }
                                    mAdapter.dealLoadData(LiveVideoListFragment.this, isLoadMore, result.getRecommend());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(LiveVideoListFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static LiveVideoListFragment newInstance() {
        return new LiveVideoListFragment();
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
