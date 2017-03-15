package com.easyvaas.common.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.easemob.EMValueCallBack;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.SingleToast;

public class GroupNameChangedActivity extends BaseActivity {
    private final int MSG_CHANGE_GROUP_NAME_COMPLETE = 10;
    private EditText mChangeGroupName;
    private String mGroupId;
    private MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name_change);
        setTitle(getString(R.string.modify_group_name));
        mGroupId = getIntent().getStringExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID);
        initView();
    }

    private void initView() {
        mChangeGroupName = (EditText) findViewById(R.id.change_group_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            String groupName = mChangeGroupName.getText().toString();
            if (TextUtils.isEmpty(groupName)) {
                SingleToast.show(this, getString(R.string.group_name_not_null));
            } else {
                ChatHXSDKHelper.getInstance().changeGroupName(mGroupId, groupName, new EMValueCallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        myHandler.sendEmptyMessage(MSG_CHANGE_GROUP_NAME_COMPLETE);
                    }

                    @Override
                    public void onError(int i, String s) {
                    }
                });

            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_CHANGE_GROUP_NAME_COMPLETE:
                Intent groupNameIntent = new Intent();
                groupNameIntent.putExtra(GroupInfoActivity.EXTRA_GROUP_NAME_CHANGED, mChangeGroupName.getText().toString());
                setResult(RESULT_OK, groupNameIntent);
                finish();
                break;

        }
    }
}
