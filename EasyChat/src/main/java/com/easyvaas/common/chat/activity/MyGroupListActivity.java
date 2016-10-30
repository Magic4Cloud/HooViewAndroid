package com.easyvaas.common.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMGroup;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.adapter.MyGroupAdapter;
import com.easyvaas.common.chat.base.BaseRvcActivity;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.net.MyRequestCallBack;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.GroupUtil;
import com.easyvaas.common.chat.utils.SingleToast;

import java.util.ArrayList;
import java.util.List;

public class MyGroupListActivity extends BaseRvcActivity {
    private static final String TAG = "MyGroupListActivity";

    private static final int MSG_LOAD_GROUP_LIST_COMPLETE = 10;
    private static final int MSG_LOAD_GROUP_LIST_ERROR = 11;
    private static final int MSG_CREATE_GROUP_SUCCESS = 12;
    private static final int MSG_CREATE_GROUP_FAILED = 13;
    private static final int REQUEST_CODE_CREATE_GROUP = 12;

    private MyGroupAdapter mMyGroupAdapter;
    private List<EMGroup> mMyGroupList;
    private MyHandler mMyHandler;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                loadData(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyHandler =  new MyHandler<>(this);
        setContentView(R.layout.activcity_list_group);
        setTitle(R.string.message_my_group);

        //ImageView iv  = (ImageView) findViewById(R.id.ivqwe);
        SingleToast.show(this,"hehehe");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mMyGroupList = new ArrayList<EMGroup>();
        mMyGroupAdapter = new MyGroupAdapter(mMyGroupList);
        mMyGroupAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMGroup emGroup = mMyGroupList.get(position);
                Intent chatIntent = new Intent(MyGroupListActivity.this, ChatActivity.class);
                chatIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_GROUP);
                chatIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, emGroup.getGroupId());
                chatIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME, emGroup.getGroupName());
                startActivity(chatIntent);
            }
        });
        mPullToLoadRcvView.getRecyclerView().setAdapter(mMyGroupAdapter);

        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);
        TextView tv_right = (TextView) findViewById(R.id.add_option_iv);

        tv_right.setText(R.string.create_group);
        tv_right.setVisibility(View.VISIBLE);

        tv.setText(R.string.message_my_group);

        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter groupIntentFilter = new IntentFilter();
        groupIntentFilter.addAction(ChatConstants.EXTERNAL_ACTION_SHOW_GROUP_MESSAGE_CHANGED);
        registerReceiver(mBroadcastReceiver, groupIntentFilter);

        mPullToLoadRcvView.initLoad();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_group) {
            Intent createPGroup = new Intent(this, FriendsSelectorListActivity.class);
            createPGroup.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                    FriendsSelectorListActivity.SELECT_CONTACT_TYPE_CREATE_GROUP);
            startActivityForResult(createPGroup, REQUEST_CODE_CREATE_GROUP);
        }
        return super.onOptionsItemSelected(item);
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
                        .loadGroupWithRecentChat(MyGroupListActivity.this, groups,
                                new MyRequestCallBack<UserArray>() {
                    @Override
                    public void onSuccess(UserArray result) {
                        if (isFinishing()) {
                            return;
                        }
                        mMyGroupAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
                mMyGroupList.clear();
                for (Pair<Long, EMGroup> sortItem : sortList) {
                    mMyGroupList.add(sortItem.second);
                }
                mMyGroupAdapter.notifyDataSetChanged();
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
            case MSG_CREATE_GROUP_SUCCESS:
                if (MyGroupListActivity.this.isFinishing()) {
                    return;
                }
                dismissLoadingDialog();
                EMGroup emGroup = (EMGroup) msg.obj;
                Intent intent = new Intent(MyGroupListActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_GROUP);
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, emGroup.getGroupId());
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME, emGroup.getGroupName());
                startActivity(intent);
                break;
            case MSG_CREATE_GROUP_FAILED:
                if (MyGroupListActivity.this.isFinishing()) {
                    return;
                }
                dismissLoadingDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_CREATE_GROUP) {
            showLoadingDialog(R.string.create_group_loading, false, true);
            List<String> selectMember = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER);
            String groupName = data.getStringExtra(ChatConstants.EXTRA_KEY_SELECT_GROUP_NAME);
            ChatHXSDKHelper.getInstance().createPublicGroup(groupName, "",
                    selectMember.toArray(new String[0]), true, 200, new EMValueCallBack() {
                        @Override
                        public void onSuccess(final Object o) {
                            Message msg = mMyHandler.obtainMessage(MSG_CREATE_GROUP_SUCCESS, o);
                            mMyHandler.sendMessage(msg);
                        }

                        @Override
                        public void onError(int i, String s) {
                            mMyHandler.sendEmptyMessage(MSG_CREATE_GROUP_FAILED);
                        }
                    });
        }
    }
}
