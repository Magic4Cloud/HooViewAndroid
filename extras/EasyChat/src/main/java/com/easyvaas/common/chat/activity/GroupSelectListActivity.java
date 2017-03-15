package com.easyvaas.common.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;

import com.easemob.EMCallBack;
import com.easemob.chat.EMGroup;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.adapter.GroupSelectAdapter;
import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.base.BaseRvcActivity;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.net.MyRequestCallBack;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.GroupUtil;

public class GroupSelectListActivity extends BaseRvcActivity {
    private static final String TAG = "GroupSelectListActivity";

    private static final int MSG_LOAD_GROUP_LIST_COMPLETE = 10;
    private static final int MSG_LOAD_GROUP_LIST_ERROR = 11;

    private GroupSelectAdapter mGroupSelectAdapter;
    private List<EMGroup> mGroupList;
    private BaseActivity.MyHandler mMyHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyHandler =  new MyHandler<>(this);
        setContentView(R.layout.activity_common_recycler);
        setTitle(R.string.select_the_group);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mGroupList = new ArrayList<>();
        mGroupSelectAdapter = new GroupSelectAdapter(mGroupList);
        mGroupSelectAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMGroup emGroup = mGroupList.get(position);
                Intent intent = new Intent();
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, emGroup.getGroupId());
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME, emGroup.getGroupName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mGroupSelectAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadAllGroupList();
    }

    private void loadAllGroupList() {
        ChatHXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {
            @Override
            public void onSuccess() {
                mMyHandler.sendEmptyMessageDelayed(MSG_LOAD_GROUP_LIST_COMPLETE, 100);
            }

            @Override
            public void onError(int i, String s) {
                mMyHandler.sendEmptyMessageDelayed(MSG_LOAD_GROUP_LIST_COMPLETE, 100);
                mMyHandler.sendEmptyMessageDelayed(MSG_LOAD_GROUP_LIST_ERROR, 100);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_LOAD_GROUP_LIST_COMPLETE:
                List<EMGroup> groups = ChatHXSDKHelper.getInstance().getLocalAllGroupList();
                if (groups == null) {
                    return;
                }
                ArrayList<Pair<Long, EMGroup>> sortList = GroupUtil
                        .loadGroupWithRecentChat(GroupSelectListActivity.this, groups,
                                new MyRequestCallBack<UserArray>() {
                                    @Override
                                    public void onSuccess(UserArray result) {
                                        if (isFinishing()) {
                                            return;
                                        }
                                        mGroupSelectAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(String msg) {

                                    }
                                });
                mGroupList.clear();
                for (Pair<Long, EMGroup> sortItem : sortList) {
                    mGroupList.add(sortItem.second);
                }
                mGroupSelectAdapter.notifyDataSetChanged();
                onRefreshComplete(0);
                mPullToLoadRcvView.setSwipeRefreshDisable();
                mPullToLoadRcvView.isLoadMoreEnabled(false);
                mPullToLoadRcvView.hideFootView();
                break;
            case MSG_LOAD_GROUP_LIST_ERROR:
                onRefreshComplete(0);
                mPullToLoadRcvView.setSwipeRefreshDisable();
                mPullToLoadRcvView.isLoadMoreEnabled(false);
                mPullToLoadRcvView.hideFootView();
                break;
        }
    }
}
