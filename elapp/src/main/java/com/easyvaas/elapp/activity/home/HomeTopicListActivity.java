/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.google.gson.Gson;

import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.HomeTopicListAdapter;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.TopicEntity;
import com.easyvaas.elapp.bean.TopicEntityArray;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.utils.Constants;

public class HomeTopicListActivity extends BaseRvcActivity {
    private List<TopicEntity> mTopicEntities;
    private HomeTopicListAdapter mHomeTopicListAdapter;
    private String mSelectTopicId;
    private String mSelectTopicName;
    private int mCurrentSelectPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_video_topic);
        setContentView(R.layout.activity_common_recycler);
        mTopicEntities = new ArrayList<>();
        initView();
    }

    private void initView() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        linearLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mHomeTopicListAdapter = new HomeTopicListAdapter(mTopicEntities);
        mHomeTopicListAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mCurrentSelectPosition == position) {
                    return;
                } else {
                    mCurrentSelectPosition = position;
                }
                mTopicEntities.get(position).setSelect(false);
                TopicEntity videoTopicEntity = mTopicEntities.get(position);
                videoTopicEntity.setSelect(true);
                mSelectTopicId = videoTopicEntity.getId();
                mSelectTopicName = videoTopicEntity.getTitle();
                mHomeTopicListAdapter.notifyDataSetChanged();
                Preferences.getInstance(HomeTopicListActivity.this)
                        .putString(Preferences.KEY_HOME_CURRENT_TOPIC_ID, mSelectTopicId);
                Preferences.getInstance(HomeTopicListActivity.this)
                        .putString(Preferences.KEY_HOME_CURRENT_TOPIC_NAME, mSelectTopicName);
                //send broadcast
                Intent backVideoListIntent = new Intent();
                backVideoListIntent.setAction(Constants.ACTION_CHANGE_HOME_TOPIC);
                sendBroadcast(backVideoListIntent);
                finish();
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mHomeTopicListAdapter);
        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);

        if (!isLoadMore) {
            mTopicEntities.clear();
            TopicEntityArray topicEntityArray = new Gson().fromJson(Preferences.getInstance(this)
                    .getString(Preferences.KEY_CACHED_TOPICS_INFO_JSON), TopicEntityArray.class);
            if (topicEntityArray != null) {
                mTopicEntities.addAll(topicEntityArray.getTopics());
            }
        }
        mPullToLoadRcvView.setSwipeRefreshDisable();
        mPullToLoadRcvView.isLoadMoreEnabled(false);
        mPullToLoadRcvView.hideFootView();
        mPullToLoadRcvView.postDelayed(new Runnable() {
            @Override public void run() {
                onRefreshComplete(mTopicEntities.size());
            }
        }, 200);
    }
}
