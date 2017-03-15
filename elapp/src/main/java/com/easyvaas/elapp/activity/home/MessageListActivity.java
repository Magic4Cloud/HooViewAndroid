/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.oldRecycler.MessageQueueAdapter;
import com.easyvaas.elapp.base.BaseRvcActivity;
import com.easyvaas.elapp.bean.message.MessageItemEntity;
import com.easyvaas.elapp.bean.message.MessageItemEntityArray;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageListActivity extends BaseRvcActivity {
    private static final long NEW_FRIEND_GROUP_ID = 1;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;

    private MessageQueueAdapter mAdapter;
    private long mMessageGroupId;
    private List<MessageItemEntity> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_queue);
        ImageView closeIv = (ImageView) findViewById(R.id.close_iv);
        TextView centerContentTv = (TextView) findViewById(R.id.common_custom_title_tv);
        String title = getIntent().getStringExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_NAME);
        centerContentTv.setText(title);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        mMessageGroupId = getIntent().getLongExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_ID, -1);
        String messageGroupIconUrl = getIntent().getStringExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_ICON);
        if (mMessageGroupId < 0) {
            SingleToast.show(this, R.string.msg_error);
            finish();
        }
        mList = new ArrayList<>();
        mAdapter = new MessageQueueAdapter(mList);
        mAdapter.setItemIconUrl(messageGroupIconUrl);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MessageItemEntity entity = mList.get(position);
                if (entity.getContent().getType() == MessageItemEntity.TYPE_FOLLOW) {
                    UserUtil.showUserInfo(getApplicationContext(), entity.getContent().getData().getName()
                    );
                }
            }
        });
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullToLoadRcvView.setSwipeRefreshDisable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        int pageIndex =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getMessageItemList(pageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                mMessageGroupId, new MyRequestCallBack<MessageItemEntityArray>() {
                    @Override
                    public void onSuccess(MessageItemEntityArray result) {
                        if (result != null && !isFinishing()) {
                            List<MessageItemEntity> messages = result.getItems();
                            if (mMessageGroupId != NEW_FRIEND_GROUP_ID) {
                                Collections.reverse(messages);
                            }
                            if (!isLoadMore) {
                                mList.clear();
                            }
                            mList.addAll(messages);
                            mAdapter.notifyDataSetChanged();
                            mNextPageIndex = result.getNext();
                            if (mAdapter.getItemCount() > 0 && mMessageGroupId != NEW_FRIEND_GROUP_ID) {
                                mPullToLoadRcvView.getRecyclerView()
                                        .smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                        onRefreshComplete(result == null ? 0 : result.getCount());
                        sendBroadcast(new Intent(Constants.ACTION_HIDE_NEW_MESSAGE_ICON));
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRefreshComplete(0);
                    }

                    @Override
                    public void onFailure(String msg) {
                        onRequestFailed(msg);
                    }
                });
    }
}
