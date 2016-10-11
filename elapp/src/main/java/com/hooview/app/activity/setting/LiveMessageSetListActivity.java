/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.recycler.PushMessageRcvAdapter;
import com.hooview.app.base.BaseRvcActivity;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.user.UserEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.SingleToast;

public class LiveMessageSetListActivity extends BaseRvcActivity {
    private List<UserEntity> mUserEntityList = new ArrayList<UserEntity>();
    public static final String EXTRA_KEY_IS_LIVE_PUSH = "extra_key_is_live_push";
    private PushMessageRcvAdapter mAdapter;
    private CheckBox mNoticeLiveCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hooview.app.R.layout.activity_live_push_message_setting);
        setTitle(com.hooview.app.R.string.push_message_notice_live);
        mUserEntityList = new ArrayList<UserEntity>();

        boolean isLiveNotice = getIntent().getBooleanExtra(EXTRA_KEY_IS_LIVE_PUSH, true);
        initView(isLiveNotice);

        loadData(false);
    }

    private void initView(boolean isLiveNotice) {
        mNoticeLiveCb = (CheckBox) findViewById(com.hooview.app.R.id.location_toggle_tb);
        ImageView mCloseIv = (ImageView) findViewById(com.hooview.app.R.id.close_iv);
        TextView mCenterContentTv = (TextView) findViewById(com.hooview.app.R.id.common_custom_title_tv);
        mCenterContentTv.setText(getString(com.hooview.app.R.string.push_message_notice_live));
        mNoticeLiveCb.setChecked(isLiveNotice);
        mNoticeLiveCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do nothing server api will handle
                mAdapter.setPush(isChecked);
                enableListItem(isChecked);
                setResult(RESULT_OK,
                        new Intent().putExtra(EXTRA_KEY_IS_LIVE_PUSH, mNoticeLiveCb.isChecked()));
            }
        });
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mAdapter = new PushMessageRcvAdapter(mUserEntityList, mNoticeLiveCb.isChecked());
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        int pos = isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        String name = Preferences.getInstance(getApplicationContext()).getUserNumber();
        ApiHelper.getInstance().getFollowerList(name, pos, ApiConstant.DEFAULT_PAGE_SIZE,
                new MyRequestCallBack<UserEntityArray>() {
                    @Override
                    public void onSuccess(UserEntityArray result) {
                        if (result != null && !isFinishing()) {
                            if (!isLoadMore) {
                                mUserEntityList.clear();
                            }
                            mUserEntityList.addAll(result.getUsers());
                            mAdapter.notifyDataSetChanged();

                            mNextPageIndex = result.getNext();
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
                        enableListItem(mNoticeLiveCb.isChecked());
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        SingleToast.show(LiveMessageSetListActivity.this, com.hooview.app.R.string.no_friend);
                        onRefreshComplete(0);
                    }
                });
    }

    public void enableListItem(boolean focusable) {
        for (int i = 0; i < mPullToLoadRcvView.getRecyclerView().getChildCount(); i++) {
            View view = mPullToLoadRcvView.getRecyclerView().getChildAt(i);
            if (view != null) {
                CheckBox checkBox = (CheckBox) view.findViewById(com.hooview.app.R.id.location_toggle_tbs);
                if (checkBox != null) {
                    if (focusable) {
                        checkBox.setEnabled(true);
                        checkBox.setFocusable(true);
                        checkBox.setFocusableInTouchMode(true);
                    } else {
                        checkBox.setEnabled(false);
                        checkBox.setFocusable(false);
                        checkBox.setFocusableInTouchMode(false);
                    }
                }
            }
        }
    }
}
