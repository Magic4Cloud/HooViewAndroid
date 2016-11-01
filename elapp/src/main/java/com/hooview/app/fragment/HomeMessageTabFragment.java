package com.hooview.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.ChatManager;
import com.easyvaas.common.chat.activity.FriendsSelectorListActivity;
import com.easyvaas.common.chat.activity.MyGroupListActivity;
import com.easyvaas.common.chat.bean.BaseUser;
import com.easyvaas.common.chat.db.InviteMessageDao;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.recycler.PullToLoadView;
import com.google.gson.Gson;
import com.hooview.app.R;
import com.hooview.app.activity.account.AccountActivity;
import com.hooview.app.activity.home.MessageListActivity;
import com.hooview.app.adapter.recycler.MessageGroupAdapter;
import com.hooview.app.base.BaseRvcFragment;
import com.hooview.app.bean.message.MessageGroupEntity;
import com.hooview.app.bean.message.MessageGroupEntityArray;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.user.UserEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.PicassoUtil;
import com.hooview.app.utils.SingleToast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomeMessageTabFragment extends BaseRvcFragment {
    private static final String TAG = "TabMessageFragment";

    private static final int MSG_REFRESH_GROUP_LIST = 1;
    private static final int MSG_REFRESH_CREATE_GROUP_SUCCESS = 2;
    private static final int MSG_REFRESH_CREATE_GROUP_FAILED = 3;
    private static final int REQUEST_CODE_CREATE_GROUP = 100;

    private MessageGroupAdapter mAdapter;
    private List mAllData;
    private List<EMConversation> mEmConversationList;
    private ChatMessageReceiver mChatMessageReceiver;
    private MyHandler mHandler;
    private int mGroupUnReadMsgNumber;

    private Bundle bundle = new Bundle();

    private class ChatMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_CHAT_MESSAGE)) {
                abortBroadcast();
                loadData(false);
            } else if (ChatConstants.EXTERNAL_ACTION_SHOW_GROUP_MESSAGE_CHANGED.equals(intent.getAction())) {
                loadGroupList();
            }
        }
    }

    private static class MyHandler extends android.os.Handler {
        private SoftReference<HomeMessageTabFragment> softReference;

        public MyHandler(HomeMessageTabFragment fragment) {
            softReference = new SoftReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomeMessageTabFragment fragment = softReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_GROUP_LIST:
                    for (int i = 0, j = fragment.mAllData.size(); i < j; i++) {
                        Object item = fragment.mAllData.get(i);
                        if (item != null && item instanceof MessageGroupEntity
                                && ((MessageGroupEntity) item).getType()
                                == MessageGroupEntity.TYPE_CHAT_GROUP) {
                            ((MessageGroupEntity) item).setUnread(fragment.mGroupUnReadMsgNumber);
                            break;
                        }
                    }
                    fragment.mAdapter.notifyDataSetChanged();
                    break;
                case MSG_REFRESH_CREATE_GROUP_SUCCESS:
                    final EMGroup emGroup = (EMGroup) msg.obj;
                    fragment.dismissLoadingDialog();
                    ChatManager.getInstance().chatToGroup(emGroup.getGroupId(), emGroup.getGroupName());
                    break;
                case MSG_REFRESH_CREATE_GROUP_FAILED:
                    fragment.dismissLoadingDialog();
                    SingleToast.show(fragment.getActivity(),
                            fragment.getString(com.hooview.app.R.string.create_group_failed));
                    break;
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(com.hooview.app.R.layout.fragment_home_tab_message, container, false);

        mHandler = new MyHandler(this);
        mAllData = new ArrayList();

        mPullToLoadRcvView = (PullToLoadView) view.findViewById(com.hooview.app.R.id.pull_load_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new MessageGroupAdapter(getActivity(), mAllData);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mAllData.get(position);
                if (object instanceof MessageGroupEntity) {
                    MessageGroupEntity message
                            = (MessageGroupEntity) object;
                    message.setUnread(0);
                    mAdapter.notifyDataSetChanged();
                    if (message.getType() == MessageGroupEntity.TYPE_CHAT_GROUP) {
                        gotoChatGroupList(false);
                    } else {
                        Intent intent = new Intent(getActivity(), MessageListActivity.class);
                        intent.putExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_ID, message.getGroupid());
                        intent.putExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_NAME, message.getTitle());
                        intent.putExtra(Constants.EXTRA_KEY_MESSAGE_GROUP_ICON, message.getIcon());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                } else {
                    EMConversation conversation = (EMConversation) object;
                    conversation.resetUnreadMsgCount();
                    mAdapter.notifyDataSetChanged();
                    ChatManager.getInstance().chatToUser(conversation.getUserName(), true);
                }
            }
        });
        mAdapter.setOnCreateContextMenuListener(this);
        mChatMessageReceiver = new ChatMessageReceiver();


        //发起群聊
        view.findViewById(com.hooview.app.R.id.create_group_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gotoChatGroupList(true);

                //逻辑
                enterSelectedFriend();
            }
        });


//        mMyUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = ;
//                Intent showImage = new Intent(getActivity(), ShowBigImageActivity.class);
//                showImage.putExtra(ShowBigImageActivity.REMOTE_IMAGE_URL, url);
//                startActivity(showImage);
//            }
//        });

        //设置头像
        ImageView iv = (ImageView) view.findViewById(R.id.iv_header);
        String json = Preferences.getInstance(getActivity())
                .getString(Preferences.KEY_CACHED_USER_INFO_JSON);
        UserEntity entity = new Gson().fromJson(json, UserEntity.class);
        String url = entity.getLogourl();
        PicassoUtil.loadPlaceholder(getContext(), url, R.drawable.home_icon_person).into(iv);

        mPullToLoadRcvView.getSwipeRefreshLayout().setColorSchemeResources(R.color.hv662d80, R.color.hv662d80, R.color.hv662d80);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPullToLoadRcvView.initLoad();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(com.hooview.app.R.menu.delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean isHandled = false;
        boolean isDeleteMessage = false;
        if (item.getItemId() == com.hooview.app.R.id.delete_conversation) {
            isDeleteMessage = true;
            isHandled = true;
        }

        Object selectObject = mAllData.get(mAdapter.getPosition());
        if (!(selectObject instanceof EMConversation)) {
            return false;
        }
        EMConversation tobeDeleteCons = (EMConversation) selectObject;
        EMChatManager.getInstance()
                .deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), isDeleteMessage);
        InviteMessageDao inviteMessageDao = new InviteMessageDao(getActivity());
        inviteMessageDao.deleteMessage(tobeDeleteCons.getUserName());
        mEmConversationList.remove(tobeDeleteCons);
        mAllData.remove(tobeDeleteCons);
        mAdapter.notifyDataSetChanged();

        return isHandled || super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        showLoadingDialog(com.hooview.app.R.string.create_group_loading, false, true);
        if (requestCode == REQUEST_CODE_CREATE_GROUP) {
            List<String> selectMember = data.getStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER);
            String groupName = data.getStringExtra(ChatConstants.EXTRA_KEY_SELECT_GROUP_NAME);
            ChatHXSDKHelper.getInstance().createPublicGroup(groupName, "",
                    selectMember.toArray(new String[0]), true, 200, new EMValueCallBack() {
                        @Override
                        public void onSuccess(final Object o) {
                            Message msg = mHandler.obtainMessage(MSG_REFRESH_CREATE_GROUP_SUCCESS, o);
                            mHandler.sendMessage(msg);
                        }

                        @Override
                        public void onError(int i, final String s) {
                            mHandler.sendEmptyMessage(MSG_REFRESH_CREATE_GROUP_FAILED);
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.addAction(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_MESSAGE_ICON);
        intentFilter.addAction(ChatConstants.EXTERNAL_ACTION_SHOW_NEW_CHAT_MESSAGE);
        intentFilter.addAction(ChatConstants.EXTERNAL_ACTION_SHOW_GROUP_MESSAGE_CHANGED);
        intentFilter.setPriority(3);
        getActivity().registerReceiver(mChatMessageReceiver, intentFilter);
        ChatHXSDKHelper.getInstance().notifyForRecevingEvents();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mChatMessageReceiver);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getMessageGroupList(
                new MyRequestCallBack<MessageGroupEntityArray>() {
                    @Override
                    public void onSuccess(MessageGroupEntityArray result) {
                        if (result == null || !isAdded()) {
                            onRefreshComplete(0);
                            return;
                        }
                        mAllData.clear();
                        mAllData.addAll(result.getGroups());
                        mEmConversationList = ChatManager.getInstance().loadConversationsWithRecentChat();
                        MessageGroupEntity chatGroupItemEntity = new MessageGroupEntity();
                        chatGroupItemEntity.setTitle(
                                getString(com.hooview.app.R.string.message_my_group));
                        chatGroupItemEntity.setDesc(
                                getString(com.hooview.app.R.string.group_topic));
                        chatGroupItemEntity.setUnread(mGroupUnReadMsgNumber);
                        chatGroupItemEntity.setType(MessageGroupEntity.TYPE_CHAT_GROUP);
                        chatGroupItemEntity.setLastest_content(chatGroupItemEntity.new LatestContentEntity());
                        mAllData.add(chatGroupItemEntity);
                        mAllData.addAll(mEmConversationList);
                        mAdapter.notifyDataSetChanged();
                        onRefreshComplete(result.getCount());
                        boolean isHaveUnreadMessage = false;
                        for (MessageGroupEntity message : result.getGroups()) {
                            if (message.getUnread() > 0) {
                                isHaveUnreadMessage = true;
                                break;
                            }
                        }
                        isHaveUnreadMessage = isHaveUnreadMessage
                                || EMChatManager.getInstance().getUnreadMsgsCount() > 0;
                        ChatManager.getInstance().setHaveUnreadMsg(isHaveUnreadMessage);
                        loadGroupList();
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

    private void loadGroupList() {
        ChatManager.getInstance().loadGroupList(new EMCallBack() {
            @Override
            public void onSuccess() {
                int unReadMsg = 0;
                List<EMGroup> emGroups = ChatHXSDKHelper.getInstance().getLocalAllGroupList();
                for (int i = 0; i < emGroups.size(); i++) {
                    EMConversation conversation = ChatHXSDKHelper.getInstance()
                            .getGroupConversation(emGroups.get(i).getGroupId());
                    unReadMsg += conversation.getUnreadMsgCount();
                }
                mGroupUnReadMsgNumber = unReadMsg;
                mHandler.sendEmptyMessage(MSG_REFRESH_GROUP_LIST);
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private boolean mIsLoadingFriends;

    private void gotoChatGroupList(boolean createGroup) {
        if (mIsLoadingFriends) {
            return;
        }
        mIsLoadingFriends = true;
        ApiHelper.getInstance().getFriends(ApiConstant.DEFAULT_FIRST_PAGE_INDEX,
                ApiConstant.DEFAULT_PAGE_SIZE_ALL, new MyRequestCallBack<UserEntityArray>() {
                    @Override
                    public void onSuccess(UserEntityArray result) {
                        List<BaseUser> friends = new ArrayList<>();
                        for (UserEntity user : result.getUsers()) {
                            BaseUser baseUser = new BaseUser();
                            baseUser.setImuser(user.getImuser());
                            baseUser.setName(user.getName());
                            baseUser.setNickname(user.getNickname());
                            baseUser.setLogourl(user.getLogourl());
                            friends.add(baseUser);
                        }
                        ChatManager.getInstance().updateFriendList(friends);
                        Intent intent = new Intent(getActivity(), MyGroupListActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_CREATE_GROUP);
                        mIsLoadingFriends = false;
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        mIsLoadingFriends = false;
                    }

                    @Override
                    public void onFailure(String msg) {
                        mIsLoadingFriends = false;
                    }
                });
    }

    //群聊
    private void enterSelectedFriend() {
//        Intent intent = new Intent(getActivity(), FriendsSelectorListActivity.class);
//        intent.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
//                FriendsSelectorListActivity.SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT);
//        startActivity(intent);
        Intent createPGroup = new Intent(getActivity(), FriendsSelectorListActivity.class);
        createPGroup.putExtra(FriendsSelectorListActivity.EXTRA_MESSAGE_SELECT_CONTACT_TYPE,
                    FriendsSelectorListActivity.SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT);
        startActivityForResult(createPGroup, REQUEST_CODE_CREATE_GROUP);
    }
}
