/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.home.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.recycler.PullToLoadView;

import com.hooview.app.adapter.recycler.VideoRcvAdapter;
import com.hooview.app.base.BaseRvcFragment;
import com.hooview.app.bean.user.RankUserEntity;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Utils;

public class TabTimeLineFriendFragment extends BaseRvcFragment {
    private List<VideoEntity> mVideoList;
    private VideoRcvAdapter mAdapter;
    private String mCurrentRankType;
    private boolean isActionBarShow = true;
    protected int mPinnedItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.hooview.app.R.layout.activity_common_recycler, container, false);
        mVideoList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView = (PullToLoadView) view.findViewById(com.hooview.app.R.id.pull_load_view);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mPullToLoadRcvView.getSwipeRefreshLayout().setProgressViewOffset(false,
                getResources().getDimensionPixelSize(com.hooview.app.R.dimen.action_bar_height),
                getResources().getDimensionPixelSize(com.hooview.app.R.dimen.action_bar_height) + 200);
        mPullToLoadRcvView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastOffset;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int homeTitleTotalHeight = getResources()
                        .getDimensionPixelSize(com.hooview.app.R.dimen.action_bar_height);
                int currentOffset = mPullToLoadRcvView.getRecyclerView().computeVerticalScrollOffset();

                if (currentOffset < lastOffset) {
                    //scroll down
                    if (!isActionBarShow) {
                        isActionBarShow = true;
                        getContext()
                                .sendOrderedBroadcast(
                                        new Intent(Constants.ACTION_SHOW_HOME_TITLE_BAR),
                                        null);
                    }
                } else {
                    //scroll up
                    if (currentOffset > homeTitleTotalHeight) {
                        if (isActionBarShow) {
                            isActionBarShow = false;
                            getContext()
                                    .sendOrderedBroadcast(new Intent(Constants.ACTION_HIDE_HOME_TITLE_BAR),
                                            null);
                        }
                    }
                }

                lastOffset = currentOffset;

            }
        });

        mAdapter = new VideoRcvAdapter(getActivity(), mVideoList);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mCurrentRankType = RankUserEntity.ASSETS_RANK_TYPE_WEEK_ALL;
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > 0 && position < mVideoList.size())  {
                    Utils.watchVideo(getActivity(), mVideoList.get(position));
                } else {
                    loadData(false);
                }
            }
        });

        loadData(false);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isActionBarShow = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEmptyView.setEmptyIcon(com.hooview.app.R.drawable.home_rank_empty);
        mEmptyView.setTitle(getString(com.hooview.app.R.string.home_rank_empty_tip));
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getFriendVideoList(mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {
                        if (result != null) {
                            updateListView(isLoadMore, result);
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
                        mPullToLoadRcvView.isLoadMoreEnabled(false);
                        mPullToLoadRcvView.hideFootView();
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRefreshComplete(0);
                    }
                });
    }

    protected void updateListView(boolean isLoadMore, VideoEntityArray result) {
        /*if (!isLoadMore) {
            mVideoList.clear();
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setPinned(VideoEntity.IS_PINNED_LIST_TITLE_BAR);
            mVideoList.add(videoEntity);
            VideoEntity sliderEntity = new VideoEntity();
            sliderEntity.setPinned(VideoEntity.IS_PINNED_LIST_SLIDER_BAR);
            mVideoList.add(sliderEntity);
            mPinnedItemCount = 2;
        }

        for (VideoEntity videoEntity : result.getVideos()) {
            if (videoEntity.getLiving() == VideoEntity.IS_LIVING
                    || videoEntity.getRecommend() == VideoEntity.IS_RECOMMEND) {
                mPinnedItemCount++;
            }
        }*/

        if (!isLoadMore) {
            mVideoList.clear();
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setPinned(VideoEntity.IS_PINNED_LIST_TITLE_BAR);
            mVideoList.add(videoEntity);
        }
        mVideoList.addAll(result.getVideos());
        mNextPageIndex = result.getNext();
        mAdapter.notifyDataSetChanged();
    }
}
