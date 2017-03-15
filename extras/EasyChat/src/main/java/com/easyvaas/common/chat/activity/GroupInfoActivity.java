package com.easyvaas.common.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.adapter.SelectFriendsAdapter;
import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.net.MyRequestCallBack;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.utils.SingleToast;
import com.easyvaas.common.widget.MyUserPhoto;

public class GroupInfoActivity extends BaseActivity {
    public static final String EXTRA_GROUP_NAME_CHANGED = "extra_group_name_changed";

    private final int MSG_GET_GROUP_INFO_COMPLETE = 11;
    private final int MSG_DISSOLUTION_GROUP_COMPLETE = 12;
    private final int MSG_QUIT_GROUP_COMPLETE = 13;
    private final int REQUEST_CODE_CHANGE_GROUP_NAME = 10;
    private final int REQUEST_CODE_ADD_MEMBER = 11;
    private final int REQUEST_CODE_REMOVE_MEMBER = 12;
    private final int REQUEST_CODE_VIEW_MEMBER = 15;

    private TextView mGroupIdTv;
    private TextView mGroupNameTv;
    private MyUserPhoto mGroupOwnerPhoto;
    private TextView mGroupMemberNumberTv;
    private ImageView mSubMemberIv;
    private CheckBox mMsgNotBotherCb;
    private TextView mDissolutionGroupTv;
    private String mGroupId;
    private boolean isOwner;
    private EMGroup mCurrentEmGroup;
    private TextView mGroupNameChangeTv;
    private TextView mGroupAnnouncementTv;
    private List<String> mSelectPhotos = new ArrayList<>();
    private List<String> mTotalMembers = new ArrayList<>();
    private SelectFriendsAdapter mSelectFriendsAdapter;
    private MyHandler mMyHandler;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.group_name_ll) {
                Intent groupNameIntent = new Intent(GroupInfoActivity.this,
                        GroupNameChangedActivity.class);
                groupNameIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, mGroupId);
                startActivityForResult(groupNameIntent, REQUEST_CODE_CHANGE_GROUP_NAME);

            } else if (i == R.id.report_group_ll) {

            } else if (i == R.id.dissolution_group_tv) {
                if (isOwner) {
                    ChatHXSDKHelper.getInstance().dissolutionGroup(mGroupId, new EMValueCallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            mMyHandler.sendEmptyMessage(MSG_DISSOLUTION_GROUP_COMPLETE);
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                } else {
                    ChatHXSDKHelper.getInstance().quitGroup(mGroupId, new EMValueCallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            mMyHandler.sendEmptyMessage(MSG_QUIT_GROUP_COMPLETE);
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }

            } else if (i == R.id.msg_not_bother_cb) {
                if (mCurrentEmGroup == null) {
                    return;
                }
                if (!mCurrentEmGroup.isMsgBlocked()) {
                    ChatHXSDKHelper.getInstance().blockGroupMessage(mGroupId, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCurrentEmGroup.setMsgBlocked(true);
                                    mMsgNotBotherCb.setChecked(mCurrentEmGroup.isMsgBlocked());
                                }
                            });

                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                } else {
                    ChatHXSDKHelper.getInstance().unblockGroupMessage(mGroupId, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCurrentEmGroup.setMsgBlocked(false);
                                    mMsgNotBotherCb.setChecked(mCurrentEmGroup.isMsgBlocked());
                                }
                            });

                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }

            } else if (i == R.id.add_member_iv) {
                Intent addMemberIntent = new Intent(GroupInfoActivity.this,
                        FriendsSelectorListActivity.class);
                addMemberIntent.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                        FriendsSelectorListActivity.SELECT_CONTACT_TYPE_ADD_MEMBER);
                addMemberIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, mGroupId);
                addMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_SELECT_GROUP_MEMBER,
                        (ArrayList<String>) mTotalMembers);
                startActivityForResult(addMemberIntent, REQUEST_CODE_ADD_MEMBER);
            } else if (i == R.id.sub_member_iv) {
                Intent removeMemberIntent = new Intent(GroupInfoActivity.this,
                        FriendsSelectorListActivity.class);
                removeMemberIntent.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                        FriendsSelectorListActivity.SELECT_CONTACT_TYPE_DELETE_MEMBER);
                removeMemberIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, mGroupId);
                removeMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_SELECT_GROUP_MEMBER,
                        (ArrayList<String>) mTotalMembers);
                startActivityForResult(removeMemberIntent, REQUEST_CODE_REMOVE_MEMBER);
            } else if (i == R.id.group_number_ll) {
                Intent viewMemberIntent = new Intent(GroupInfoActivity.this,
                        FriendsSelectorListActivity.class);
                viewMemberIntent.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                        FriendsSelectorListActivity.SELECT_CONTACT_TYPE_VIEW_MEMBER);
                viewMemberIntent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID, mGroupId);
                viewMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_SELECT_GROUP_MEMBER,
                        (ArrayList<String>) mTotalMembers);
                startActivityForResult(viewMemberIntent, REQUEST_CODE_VIEW_MEMBER);
            } else if (i == R.id.group_clean_history_ll) {
                EMChatManager.getInstance().clearConversation(mGroupId);
                SingleToast.show(getApplicationContext(), R.string.msg_clean_chat_history_success);
            } else if (i == R.id.group_owner_photo) {
                ChatUserUtil.showUserInfoByIM(getApplicationContext(), mCurrentEmGroup.getName());
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
         if (ChatConstants.ACTION_GO_UPDATE_GROUP_INFO.equals(intent.getAction())) {
             EMMessage message = intent.getParcelableExtra(ChatConstants.EXTRA_KEY_HX_CMD_MESSAGE_UPDATE_GROUP);
             CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
             String action = cmdMsgBody.action;
             if (!TextUtils.isEmpty(action) && action.equals(ChatConstants.ACTION_HX_CMD_MESSAGE_UPDATE_GROUP)) {
                 loadData();
             }
         }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyHandler = new MyHandler<>(this);
        mGroupId = getIntent().getStringExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID);

        setContentView(R.layout.activity_chat_group_info);
        setTitle(R.string.message_my_group);

        initView();

        loadData();
    }

    private void initView() {
        mGroupIdTv = (TextView) findViewById(R.id.group_id_tv);
        mGroupNameTv = (TextView) findViewById(R.id.group_name_tv);
        mGroupNameChangeTv = (TextView) findViewById(R.id.group_name_change_tv);

        findViewById(R.id.group_name_ll).setOnClickListener(mOnClickListener);
        findViewById(R.id.group_number_ll).setOnClickListener(mOnClickListener);
        findViewById(R.id.group_clean_history_ll).setOnClickListener(mOnClickListener);
        mGroupOwnerPhoto = (MyUserPhoto) findViewById(R.id.group_owner_photo);
        mGroupOwnerPhoto.setOnClickListener(mOnClickListener);
        mGroupMemberNumberTv = (TextView) findViewById(R.id.group_member_number_tv);
        RecyclerView groupMemberPhotoRl = (RecyclerView) findViewById(R.id.group_member_photo_rl);
        groupMemberPhotoRl.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setStackFromEnd(false);
        groupMemberPhotoRl.setLayoutManager(linearLayoutManager);
        mSelectFriendsAdapter = new SelectFriendsAdapter(mSelectPhotos);
        groupMemberPhotoRl.setAdapter(mSelectFriendsAdapter);

        findViewById(R.id.add_member_iv).setOnClickListener(mOnClickListener);
        mSubMemberIv = (ImageView) findViewById(R.id.sub_member_iv);
        mSubMemberIv.setOnClickListener(mOnClickListener);
        mMsgNotBotherCb = (CheckBox) findViewById(R.id.msg_not_bother_cb);
        mMsgNotBotherCb.setOnClickListener(mOnClickListener);

        findViewById(R.id.report_group_ll).setOnClickListener(mOnClickListener);
        mDissolutionGroupTv = (TextView) findViewById(R.id.dissolution_group_tv);
        mDissolutionGroupTv.setOnClickListener(mOnClickListener);
        mGroupAnnouncementTv = (TextView) findViewById(R.id.group_announcement_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter groupIntentFilter = new IntentFilter();
        groupIntentFilter.addAction(ChatConstants.ACTION_GO_UPDATE_GROUP_INFO);
        registerReceiver(mBroadcastReceiver, groupIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

    }

    private void loadData() {
        ChatHXSDKHelper.getInstance().getGroupInfo(mGroupId, new EMValueCallBack() {
            @Override
            public void onSuccess(Object o) {
                mMyHandler.sendEmptyMessage(MSG_GET_GROUP_INFO_COMPLETE);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_GET_GROUP_INFO_COMPLETE:
                mCurrentEmGroup = ChatHXSDKHelper.getInstance().getLocalGroupInfo(mGroupId);
                updateGroupInfo();
                break;
            case MSG_DISSOLUTION_GROUP_COMPLETE:
                SingleToast.show(GroupInfoActivity.this, getString(R.string.dissolute_group_complete));
                setResult(RESULT_OK);
                finish();
                break;
            case MSG_QUIT_GROUP_COMPLETE:
                setResult(RESULT_OK);
                SingleToast.show(GroupInfoActivity.this, getString(R.string.quit_group_complete));
                finish();
                break;

        }

    }

    private void updateGroupInfo() {
        mTotalMembers.clear();
        mSelectPhotos.clear();
        if (mCurrentEmGroup == null) {
            return;
        }
        setTitle(mCurrentEmGroup.getGroupName());
        String desc = mCurrentEmGroup.getDescription();
        if (!TextUtils.isEmpty(desc)) {
            mGroupAnnouncementTv.setVisibility(View.VISIBLE);
            mGroupAnnouncementTv.setText(desc);
        } else {
            mGroupAnnouncementTv.setVisibility(View.GONE);
        }

        String ownerImUser = mCurrentEmGroup.getOwner();

        if (ChatHXSDKHelper.getInstance().getHXId().equals(ownerImUser)) {
            isOwner = true;
            mDissolutionGroupTv.setText(getResources().getString(R.string.dissolution_group));
            mSubMemberIv.setVisibility(View.VISIBLE);
            findViewById(R.id.group_set_notify_rl).setVisibility(View.GONE);
        } else {
            mDissolutionGroupTv.setText(getResources().getString(R.string.quit_group));
            mSubMemberIv.setVisibility(View.GONE);
            mGroupNameChangeTv.setVisibility(View.INVISIBLE);
            findViewById(R.id.group_name_ll).setEnabled(false);
        }
        mGroupIdTv.setText(mCurrentEmGroup.getGroupId());
        mMsgNotBotherCb.setChecked(mCurrentEmGroup.isMsgBlocked());
        ChatUserUtil.setUserAvatar(this, mCurrentEmGroup.getOwner(), mGroupOwnerPhoto);
        String groupName = mCurrentEmGroup.getGroupName();
        mGroupNameTv.setText(groupName);
        if (TextUtils.isEmpty(groupName)) {
            mGroupNameTv.setText(R.string.not_setting_group_name);
        }
        List<String> members = mCurrentEmGroup.getMembers();
        if (members != null) {
            for (int i = 0, j = members.size(); i < j; i++) {
                String imUserName = members.get(i);
                mTotalMembers.add(imUserName);
                if (imUserName.equals(ownerImUser)) {
                    continue;
                }

                if (i < 7) {
                    mSelectPhotos.add(imUserName);
                }
            }
            ChatUserUtil.getUserBasicInfoList(this, mTotalMembers, new MyRequestCallBack<UserArray>() {
                @Override
                public void onSuccess(UserArray result) {
                    if (isFinishing()) {
                        return;
                    }
                    mSelectFriendsAdapter.notifyDataSetChanged();
                    ChatUserUtil.setUserAvatar(GroupInfoActivity.this, mCurrentEmGroup.getOwner(),
                            mGroupOwnerPhoto);
                }

                @Override
                public void onFailure(String msg) {

                }
            });
            if (mSelectPhotos.size() == 0) {
                mSubMemberIv.setVisibility(View.GONE);
            }
            mGroupMemberNumberTv.setText(getString(R.string.unit_person, "" + (members.size() - 1)));
        }
        mSelectFriendsAdapter.notifyDataSetChanged();
        mGroupIdTv.setText(mCurrentEmGroup.getGroupId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        List<String> addMemberList = null;
        List<String> deleteMemberList  = null;
        switch (requestCode) {
            case REQUEST_CODE_CHANGE_GROUP_NAME:
                String groupName = data.getStringExtra(EXTRA_GROUP_NAME_CHANGED);
                if (!TextUtils.isEmpty(groupName)) {
                    mGroupNameTv.setText(groupName);
                }
                break;
            case REQUEST_CODE_VIEW_MEMBER:
                addMemberList = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER);
                deleteMemberList = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_DELETE_MEMBER);
                break;
            case REQUEST_CODE_ADD_MEMBER:
                addMemberList = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER);
                break;
            case REQUEST_CODE_REMOVE_MEMBER:
                deleteMemberList = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_DELETE_MEMBER);
                break;
        }
        if (addMemberList != null && addMemberList.size() > 0) {
            if (isOwner) {
                ChatHXSDKHelper.getInstance().addUsersToGroup(mGroupId,
                        addMemberList.toArray(new String[addMemberList.size()]), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                ChatHXSDKHelper.getInstance().sendGroupUpdateNotice(mGroupId);
                                loadData();
                            }

                            @Override
                            public void onError(int i, String s) {

                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
            } else {
                ChatHXSDKHelper.getInstance().inviteUser(mGroupId,
                        addMemberList.toArray(new String[addMemberList.size()]), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                ChatHXSDKHelper.getInstance().sendGroupUpdateNotice(mGroupId);
                                loadData();
                            }

                            @Override
                            public void onError(int i, String s) {

                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
            }
        }
        if (deleteMemberList != null && deleteMemberList.size() > 0) {
            ChatHXSDKHelper.getInstance().removeUsersFromGroup(mGroupId,
                    deleteMemberList.toArray(new String[deleteMemberList.size()]), new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            loadData();
                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
        }
    }
}
