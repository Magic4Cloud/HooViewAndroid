package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.easyvaas.elapp.adapter.recycler.MessageNoticeAdapter;
import com.easyvaas.elapp.bean.message.MessageGroupEntity;
import com.easyvaas.elapp.bean.message.MessageGroupEntityArray;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvActivity;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoliuya on 2017/2/25.
 */

public class MessageUnReadListActivity extends BaseListRcvActivity{
    private List<MessageGroupEntity> mGroups;
    private MessageNoticeAdapter mMsgAdapter;
    public static void start(Context context) {
        Intent starter = new Intent(context, MessageUnReadListActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvTitle.setText(R.string.user_msg_notification);
        initView();
        loadData(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getMessageGroupList(new MyRequestCallBack<MessageGroupEntityArray>() {
            @Override
            public void onSuccess(MessageGroupEntityArray result) {
                if(result!=null){
                    mNextPageIndex = result.getNext();
                    if (isLoadMore)
                    {
                        mGroups.addAll(result.getGroups());
                    }else
                    {
                        mGroups.clear();
                        mGroups.addAll(result.getGroups());
                    }
                    if (mGroups != null && mGroups.size() > 0) {
                        mMsgAdapter.notifyDataSetChanged();
                    }
                    onRefreshComplete(result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {
                onRefreshComplete(0);
            }
        });

    }

    private void initView() {
          mGroups = new ArrayList<MessageGroupEntity>();
          mRecyclerView.setAdapter(mMsgAdapter = new MessageNoticeAdapter(mGroups,this));
    }
}
