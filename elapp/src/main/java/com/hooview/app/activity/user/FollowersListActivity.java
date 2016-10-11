/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.recycler.UserRcvAdapter;
import com.hooview.app.base.BaseRvcActivity;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.user.UserEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UserUtil;

public class FollowersListActivity extends BaseRvcActivity {

    private List<UserEntity> mUsers = new ArrayList<UserEntity>();
    private UserRcvAdapter mAdapter;
    private String mMame;
    private ImageView mCloseIv;
    private TextView mCenterContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_personal_recycler);
        mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(com.hooview.app.R.string.follow));
        mMame = getIntent().getStringExtra(Constants.EXTRA_KEY_USER_ID);
        if (TextUtils.isEmpty(mMame)) {
            mMame = Preferences.getInstance(this).getUserNumber();
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new UserRcvAdapter(mUsers, UserRcvAdapter.TYPE_FOLLOWER);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UserUtil.showUserInfo(getApplicationContext(), mUsers.get(position).getName());
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getFollowerList(mMame, mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                new MyRequestCallBack<UserEntityArray>() {
                            @Override
                            public void onSuccess(UserEntityArray user) {
                                if (isFinishing()) {
                                    return;
                                }
                                if (!isLoadMore) {
                                    mUsers.clear();
                                }
                                if (user != null && user.getUsers().size() > 0) {
                                    mNextPageIndex = user.getNext();
                                    mUsers.addAll(user.getUsers());
                                }
                                mAdapter.notifyDataSetChanged();
                                onRefreshComplete(user == null ? 0 : user.getCount());
                            }

                            @Override
                            public void onFailure(String msg) {
                                RequestUtil.handleRequestFailed(msg);
                                onRequestFailed(msg);
                            }

                            @Override
                            public void onError(String msg) {
                                SingleToast.show(FollowersListActivity.this, com.hooview.app.R.string.no_friend);
                                onRefreshComplete(0);
                            }
                        });
    }
}
