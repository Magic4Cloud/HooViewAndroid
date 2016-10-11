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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.recycler.PullToLoadView;

import com.hooview.app.activity.home.AssetsRankListActivity;
import com.hooview.app.adapter.recycler.AssetsRankListAdapter;
import com.hooview.app.base.BaseRvcFragment;
import com.hooview.app.bean.user.AssetsRankEntityArray;
import com.hooview.app.bean.user.RankUserEntity;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.UserUtil;

public class TabAssetsRankFragment extends BaseRvcFragment {
    private List<RankUserEntity> mAssetsRankList;
    private AssetsRankListAdapter mAssetsRankListAdapter;
    private String mCurrentRankType;
    private boolean isActionBarShow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.hooview.app.R.layout.activity_common_recycler, container, false);
        mAssetsRankList = new ArrayList<>();
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

        mAssetsRankListAdapter = new AssetsRankListAdapter(getActivity(), mAssetsRankList);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAssetsRankListAdapter);
        mCurrentRankType = RankUserEntity.ASSETS_RANK_TYPE_ALL;
        mAssetsRankListAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RankUserEntity entity = mAssetsRankList.get(position);
                if (entity == null) {
                    return;
                }
                if (entity.getPinned() == RankUserEntity.IS_HEADER) {
                    // DO NOTHING
                } else if (entity.getPinned() == RankUserEntity.IS_FOOTER) {
                    Intent rankIntent = new Intent(getActivity(), AssetsRankListActivity.class);
                    rankIntent.putExtra(AssetsRankListActivity.EXTRA_KEY_ASSETS_RANK_TYPE, entity.getType());
                    startActivity(rankIntent);
                } else {
                    if (!TextUtils.isEmpty(entity.getName())) {
                        UserUtil.showUserInfo(getActivity(), entity.getName());
                    }
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
        ApiHelper.getInstance().getAssetsRankList(mCurrentRankType, mNextPageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<AssetsRankEntityArray>() {
                    @Override
                    public void onSuccess(AssetsRankEntityArray result) {
                        if (result != null) {
                            if (!isLoadMore) {
                                mAssetsRankList.clear();
                                RankUserEntity titleAssetsRankUserEntity = new RankUserEntity();
                                titleAssetsRankUserEntity
                                        .setPinned(RankUserEntity.IS_TITLE_PINNED);
                                mAssetsRankList.add(titleAssetsRankUserEntity);
                            }
                            setData(result);
                            mAssetsRankListAdapter.notifyDataSetChanged();
                            mNextPageIndex = result.getNext();
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

    private void setData(AssetsRankEntityArray result) {
        int maxCount;
        String sendRankType;
        String receiveRankType;
        if (RankUserEntity.ASSETS_RANK_TYPE_ALL.equals(mCurrentRankType)) {
            sendRankType = RankUserEntity.ASSETS_RANK_TYPE_SEND;
            receiveRankType = RankUserEntity.ASSETS_RANK_TYPE_RECEIVE;
        } else if (RankUserEntity.ASSETS_RANK_TYPE_MONTH_ALL.equals(mCurrentRankType)) {
            sendRankType = RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND;
            receiveRankType = RankUserEntity.ASSETS_RANK_TYPE_MONTH_RECEIVE;
        } else if (RankUserEntity.ASSETS_RANK_TYPE_WEEK_ALL.equals(mCurrentRankType)) {
            sendRankType = RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND;
            receiveRankType = RankUserEntity.ASSETS_RANK_TYPE_WEEK_RECEIVE;
        } else {
            sendRankType = RankUserEntity.ASSETS_RANK_TYPE_SEND;
            receiveRankType = RankUserEntity.ASSETS_RANK_TYPE_RECEIVE;
        }
        List<RankUserEntity> sendRankList = result.getSend_rank_list();
        List<RankUserEntity> receiveRankList = result.getReceive_rank_list();
        if (sendRankList != null) {
            maxCount = sendRankList.size();
            if (maxCount > 5) {
                maxCount = 5;
            }
            RankUserEntity sendHeadEntity = new RankUserEntity();
            sendHeadEntity.setPinned(RankUserEntity.IS_HEADER);
            sendHeadEntity.setType(sendRankType);
            mAssetsRankList.add(sendHeadEntity);
            for (int i = 0; i < maxCount; i++) {
                RankUserEntity assetsRankUserEntity = sendRankList.get(i);
                assetsRankUserEntity.setType(sendRankType);
                assetsRankUserEntity.setRank(i + 1);
                mAssetsRankList.add(assetsRankUserEntity);
            }
            RankUserEntity sendFootEntity = new RankUserEntity();
            sendFootEntity.setPinned(RankUserEntity.IS_FOOTER);
            sendFootEntity.setType(sendRankType);
            mAssetsRankList.add(sendFootEntity);
        }
        if (receiveRankList != null) {
            RankUserEntity receiveHeadEntity = new RankUserEntity();
            receiveHeadEntity.setPinned(RankUserEntity.IS_HEADER);
            receiveHeadEntity.setType(receiveRankType);
            mAssetsRankList.add(receiveHeadEntity);
            maxCount = receiveRankList.size();
            if (maxCount > 5) {
                maxCount = 5;
            }
            for (int i = 0; i < maxCount; i++) {
                RankUserEntity assetsRankUserEntity = receiveRankList.get(i);
                assetsRankUserEntity.setType(receiveRankType);
                assetsRankUserEntity.setRank(i + 1);
                mAssetsRankList.add(assetsRankUserEntity);
            }
            RankUserEntity receiveFootEntity = new RankUserEntity();
            receiveFootEntity.setPinned(RankUserEntity.IS_FOOTER);
            receiveFootEntity.setType(receiveRankType);
            mAssetsRankList.add(receiveFootEntity);
        }

    }
}
