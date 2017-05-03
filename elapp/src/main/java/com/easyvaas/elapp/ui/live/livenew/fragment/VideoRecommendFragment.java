package com.easyvaas.elapp.ui.live.livenew.fragment;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.live.LiveRecommendAdapter;
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
 * Date   2017/5/03
 * Editor  Misuzu
 * 推荐视频列表
 */

public class VideoRecommendFragment extends MyBaseListFragment<LiveRecommendAdapter>{

    private String vid;

    @Override
    protected LiveRecommendAdapter initAdapter() {
        return new LiveRecommendAdapter(new ArrayList<VideoEntity>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        vid = getArguments().getString(AppConstants.VIDEO_ID);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getVideoRecommendList(vid,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<ArrayList<VideoEntity>>() {
                    @Override
                    public void OnSuccess(ArrayList<VideoEntity> result) {
                        mAdapter.dealLoadData(VideoRecommendFragment.this, isLoadMore, result);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(VideoRecommendFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static VideoRecommendFragment newInstance(String vid) {

        Bundle args = new Bundle();
        args.putString(AppConstants.VIDEO_ID, vid);
        VideoRecommendFragment fragment = new VideoRecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
