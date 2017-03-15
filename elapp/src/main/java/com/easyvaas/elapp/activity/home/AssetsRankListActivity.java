/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.AssetsRankListAdapter;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.user.AssetsRankEntityArray;
import com.easyvaas.elapp.bean.user.RankUserEntity;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.UserUtil;

public class AssetsRankListActivity extends BaseRvcActivity {
    public static final String EXTRA_KEY_ASSETS_RANK_TYPE = "extra_key_assets_rank_type";

    private List<RankUserEntity> mAssetsRankListEntities;
    private AssetsRankListAdapter mAssetsRankListAdapter;
    private String mRankType;
    private ImageView mCloseIv;
    private TextView mCenterContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRankType = getIntent().getStringExtra(EXTRA_KEY_ASSETS_RANK_TYPE);
        setContentView(R.layout.activity_personal_recycler);

        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        if (RankUserEntity.ASSETS_RANK_TYPE_SEND.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND.equals(mRankType)) {
            mCenterContentTv.setText(getString(R.string.rich_man));
        } else if (RankUserEntity.ASSETS_RANK_TYPE_RECEIVE.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_RECEIVE.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_RECEIVE.equals(mRankType)) {
            mCenterContentTv.setText(getString(R.string.rising_star));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mAssetsRankListEntities = new ArrayList<RankUserEntity>();
        mAssetsRankListAdapter = new AssetsRankListAdapter(this, mAssetsRankListEntities);
        mAssetsRankListAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RankUserEntity entity = mAssetsRankListEntities.get(position);
                if (entity != null && !TextUtils.isEmpty(entity.getName())) {
                    UserUtil.showUserInfo(AssetsRankListActivity.this, entity.getName());
                }
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAssetsRankListAdapter);
        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getAssetsRankList(mRankType, mNextPageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<AssetsRankEntityArray>() {
                    @Override
                    public void onSuccess(AssetsRankEntityArray result) {
                        if (result != null) {
                            if (!isLoadMore) {
                                mAssetsRankListEntities.clear();
                            }
                            setData(result);
                            mAssetsRankListAdapter.notifyDataSetChanged();
                            mNextPageIndex = result.getNext();
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
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
        int olderNumber = mAssetsRankListEntities.size();
        List<RankUserEntity> assetsRankListEntities = null;
        if (RankUserEntity.ASSETS_RANK_TYPE_SEND.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND.equals(mRankType)) {
            assetsRankListEntities = result.getSend_rank_list();
        } else if (RankUserEntity.ASSETS_RANK_TYPE_RECEIVE.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_RECEIVE.equals(mRankType)
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_RECEIVE.equals(mRankType)) {
            assetsRankListEntities = result.getReceive_rank_list();
        }
        if (assetsRankListEntities != null && assetsRankListEntities.size() > 0) {
            for (int i = 0; i < assetsRankListEntities.size(); i++) {
                RankUserEntity assetsRankUserEntity = assetsRankListEntities.get(i);
                assetsRankUserEntity.setType(mRankType);
                assetsRankUserEntity.setRank(olderNumber + i + 1);
                mAssetsRankListEntities.add(assetsRankUserEntity);
            }
        }
    }
}
