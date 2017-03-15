/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.user;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.oldRecycler.UserRcvAdapter;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.user.UserEntity;
import com.easyvaas.elapp.bean.user.UserEntityArray;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class FansListActivity extends BaseRvcActivity {
    private static final String TAG = FansListActivity.class.getSimpleName();

    private List<UserEntity> mUsers = new ArrayList<UserEntity>();
    private UserRcvAdapter mAdapter;
    private String mName;
    private String mSelfName;

    private ImageView mCloseIv;
    private TextView mCenterContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_recycler);
        mCloseIv = (ImageView) findViewById(R.id.close_iv);
        mCenterContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(R.string.fans));

        mSelfName = Preferences.getInstance(this).getUserNumber();
        mName = getIntent().getStringExtra(Constants.EXTRA_KEY_USER_ID);
        if (TextUtils.isEmpty(mName)) {
            Logger.w(TAG, "ERROR ! Name is empty ! name: " + mName);
            finish();
            return;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new UserRcvAdapter(mUsers, UserRcvAdapter.TYPE_FANS);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                UserUtil.showUserInfo(getApplicationContext(), mUsers.get(position).getName());
            }
        });
        mEmptyView.setTitle(getString(R.string.no_fans_tips));
        mEmptyView.getSubTitleTextView().setVisibility(View.GONE);
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
        ApiHelper.getInstance().getFansList(mName, mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
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
//                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        onRefreshComplete(0);
                    }
                });
    }
}
