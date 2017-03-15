/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.recycler.PullToLoadView;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.VideoRcvAdapter;
import com.easyvaas.elapp.base.BaseRvcFragment;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.bean.video.VideoEntityArray;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Utils;

public class TabTimelineCategoryList extends BaseRvcFragment {
    protected VideoRcvAdapter mAdapter;
    protected List<VideoEntity> mVideoList;
    protected int mPinnedItemCount;
    protected String mTopicId;
    private boolean isActionBarShow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mVideoList = new ArrayList<>();
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(),
                R.style.Theme_PageIndicatorDefaults);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View rootView = localInflater.inflate(R.layout.fragment_category, container, false);
        mPullToLoadRcvView = (PullToLoadView) rootView.findViewById(R.id.pull_load_view);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position < mPinnedItemCount ? 2 : 1;
            }
        });
        mAdapter = new VideoRcvAdapter(getActivity(), mVideoList);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mPullToLoadRcvView.getSwipeRefreshLayout().setProgressViewOffset(false,
                getResources().getDimensionPixelSize(R.dimen.action_bar_height),
                getResources().getDimensionPixelSize(R.dimen.action_bar_height) + 200);
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

        mPullToLoadRcvView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastOffset;
            int scrollDownDistance = 0;
            int scrollUpDistance = 0;
            long lastUpTimeOffset;
            long lastDownTimeOffset;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int homeTitleTotalHeight = getResources()
                        .getDimensionPixelSize(R.dimen.action_bar_height);
                int currentOffset = mPullToLoadRcvView.getRecyclerView().computeVerticalScrollOffset();
                if (currentOffset < lastOffset) {
                    //scroll down
                    scrollDownDistance += lastOffset - currentOffset;
                    if (scrollDownDistance > 50) {
                        scrollDownDistance = 0;
                        if (!isActionBarShow) {
                            isActionBarShow = true;
                            lastUpTimeOffset = System.currentTimeMillis();
                            getContext()
                                    .sendOrderedBroadcast(
                                            new Intent(Constants.ACTION_SHOW_HOME_TITLE_BAR),
                                            null);
                        }
                    }
                } else {
                    //scroll up
                    if (currentOffset > homeTitleTotalHeight) {
                        scrollUpDistance += currentOffset - lastOffset;
                        lastDownTimeOffset = System.currentTimeMillis();
                        if (scrollUpDistance > 50 && lastDownTimeOffset - lastUpTimeOffset > 1000) {
                            scrollUpDistance = 0;
                            if (isActionBarShow) {
                                isActionBarShow = false;
                                getContext()
                                        .sendOrderedBroadcast(
                                                new Intent(Constants.ACTION_HIDE_HOME_TITLE_BAR),
                                                null);
                            }
                        }
                    }
                }

                lastOffset = currentOffset;

            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isActionBarShow = true;
        }
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        if (!isLoadMore) {
            mPullToLoadRcvView.showRefreshingLoadingIcon();
        }
        updateVideoList(isLoadMore);
    }

    protected void updateVideoList(boolean isLoadMore) {
        loadTopicVideoList(isLoadMore, mTopicId, false);
    }

    protected void loadTopicVideoList(final boolean isLoadMore, String topicId, final boolean containLive) {
        int position =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getTopicVideoList(topicId, containLive, position,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {
                        if (result != null) {
                            updateListView(isLoadMore, true, containLive, result);
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRequestFailed(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        onRequestFailed(msg);
                    }
                });
    }

    protected void updateListView(boolean isLoadMore, boolean isTopicField, boolean containLive,
            VideoEntityArray result) {
        if (!isLoadMore) {
            mVideoList.clear();
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.setPinned(VideoEntity.IS_PINNED_LIST_TITLE_BAR);
            mVideoList.add(videoEntity);
            VideoEntity sliderEntity = new VideoEntity();
            sliderEntity.setPinned(VideoEntity.IS_PINNED_LIST_SLIDER_BAR);
            mVideoList.add(sliderEntity);
            mPinnedItemCount = 2;
        }

        boolean bUrlCached = false;
        for (VideoEntity videoEntity : result.getVideos()) {
            if (isTopicField) {
                videoEntity.setPinned(VideoEntity.IS_PINNED_LIST_GIRL);
                mPinnedItemCount++;
            } else if (videoEntity.getLiving() == VideoEntity.IS_LIVING
                    || videoEntity.getRecommend() == VideoEntity.IS_RECOMMEND) {
                mPinnedItemCount++;
            }
            if (containLive && !bUrlCached && videoEntity.getLiving() == VideoEntity.IS_LIVING
                    && !TextUtils.isEmpty(videoEntity.getPlay_url())) {
                bUrlCached = true;
            }
        }

        if (containLive) {
            removeDuplicateData(mVideoList);
        }
        mVideoList.addAll(result.getVideos());
        mNextPageIndex = result.getNext();
        mAdapter.notifyDataSetChanged();
    }
}
