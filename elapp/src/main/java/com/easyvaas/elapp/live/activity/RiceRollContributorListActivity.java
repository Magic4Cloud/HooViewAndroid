/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.RiceRollContributorAdapter;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.user.RankUserEntity;
import com.easyvaas.elapp.bean.user.RiceRollContributorEntityArray;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.UserUtil;

public class RiceRollContributorListActivity extends BaseRvcActivity {
    private List<RankUserEntity> mEntities;
    private RiceRollContributorAdapter mRiceRollContributeAdapter;
    public static final String RICE_ROLL_USER_NAME = "rice_roll_user_name";
    private String mCurrentUserName;

    private ImageView mCloseIv;
    private TextView mCenterContentTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recycler);

        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(R.string.rice_roll_contribute));
        mCurrentUserName = getIntent().getStringExtra(RICE_ROLL_USER_NAME);
        mEntities = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mRiceRollContributeAdapter = new RiceRollContributorAdapter(this, mEntities);
        mRiceRollContributeAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RankUserEntity entity = mEntities.get(position);
                if (entity != null && !TextUtils.isEmpty(entity.getName())) {
                    UserUtil.showUserInfo(getApplicationContext(), entity.getName());
                }
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mRiceRollContributeAdapter);
        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getContributor(mCurrentUserName, mNextPageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<RiceRollContributorEntityArray>() {
                    @Override
                    public void onSuccess(RiceRollContributorEntityArray result) {
                        if (result != null) {
                            if (!isLoadMore) {
                                mEntities.clear();
                                mEntities.addAll(result.getUsers());
                                createHeaderDataList();
                            } else {
                                mEntities.addAll(result.getUsers());
                            }
                            mRiceRollContributeAdapter.notifyDataSetChanged();
                            mNextPageIndex = result.getNext();
                            onRefreshComplete(result.getCount());
                        } else {
                            onRefreshComplete(0);
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRefreshComplete(0);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }
                });
    }

    private void createHeaderDataList() {
        if (mEntities.size() > 0) {
            int size = mEntities.size();
            if (size > 3) {
                size = 3;
            }
            for (int i = 0; i < size; i++) {
                mEntities.get(i).setRank(i + 1);
            }
        }
    }
}
