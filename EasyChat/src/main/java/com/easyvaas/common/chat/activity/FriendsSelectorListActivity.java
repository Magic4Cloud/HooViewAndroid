package com.easyvaas.common.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.adapter.FriendLetterSortAdapter;
import com.easyvaas.common.chat.base.BaseRvcActivity;
import com.easyvaas.common.chat.bean.BaseUser;
import com.easyvaas.common.chat.bean.UserArray;
import com.easyvaas.common.chat.bean.UserEntity;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.net.ApiConstant;
import com.easyvaas.common.chat.net.MyRequestCallBack;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.widget.LetterSideBar;

import java.util.ArrayList;
import java.util.List;

public class FriendsSelectorListActivity extends BaseRvcActivity {
    private static final String TAG = "FriendsSelectorListActivity";

    public static final String EXTRA_MESSAGE_SELECT_CONTACT_TYPE = "extra_message_select_contact_type";

    public static final int SELECT_CONTACT_TYPE_CREATE_GROUP = 8;
    public static final int SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT = 9;
    public static final int SELECT_CONTACT_TYPE_ADD_MEMBER = 10;

    public static final int SELECT_CONTACT_TYPE_DELETE_MEMBER = 11;
    public static final int SELECT_CONTACT_TYPE_VIEW_MEMBER = 12;
    public static final int SELECT_CONTACT_TYPE_ONLY_AT_MEMBER = 13;

    public static final int ACTIVITY_RESULT_SELECT_GROUP_REQUEST_CODE = 1;

    private int mSelectType;
    private FriendLetterSortAdapter mListAdapter;
    private List<String> mSelectUserList = new ArrayList<String>();
    private List<String> mAtSelectUserList = new ArrayList<String>();
    private String mGroupName = "";
    private ArrayList<String> mGroupNames = new ArrayList<String>();
    private ArrayList<String> mGroupImNames = new ArrayList<String>();
    private ArrayList<String> mGroupMembers;
    private String mOwnerImUser;

    private List<UserEntity> mUserList = new ArrayList<>();

    private TextView tv_title;
    private TextView tv_right;
    private TextView tv_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selector_list);


        mSelectType = getIntent().getIntExtra(EXTRA_MESSAGE_SELECT_CONTACT_TYPE, 0);
        mGroupMembers = getIntent().getStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_SELECT_GROUP_MEMBER);

        //initTitleBar();

        if (mSelectType == SELECT_CONTACT_TYPE_VIEW_MEMBER) {
            String groupId = getIntent().getStringExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID);
            EMGroup currentEmGroup = ChatHXSDKHelper.getInstance().getLocalGroupInfo(groupId);
            mOwnerImUser = currentEmGroup.getOwner();
        }
        mListAdapter = new FriendLetterSortAdapter(this, mUserList);
        if (mSelectType == SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT) {
            addGroupItemView();
            tv_title.setText(R.string.select_contacts);
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT);
        } else if (mSelectType == SELECT_CONTACT_TYPE_CREATE_GROUP) {
            addGroupItemView();
            tv_title.setText(R.string.select_contacts);
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_CREATE_GROUP);
        } else if (mSelectType == SELECT_CONTACT_TYPE_ADD_MEMBER) {
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_ADD_MEMBER);
            tv_title.setText(R.string.add_group_member);
        } else if (mSelectType == SELECT_CONTACT_TYPE_DELETE_MEMBER) {
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_DELETE_MEMBER);
            tv_title.setText(R.string.remove_group_member);
        } else if (mSelectType == SELECT_CONTACT_TYPE_VIEW_MEMBER) {
            mListAdapter.setChoiceList(false);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_VIEW_MEMBER);
            tv_title.setText(R.string.group_member);
        } else if (mSelectType == SELECT_CONTACT_TYPE_ONLY_AT_MEMBER) {
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_ONLY_AT_MEMBER);
            tv_title.setText(R.string.chat_select_forward_user);
        } else {
            addGroupItemView();
            mListAdapter.setChoiceList(false);
            tv_title.setText(R.string.chat_select_forward_user);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mListAdapter);
        mListAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UserEntity user = mUserList.get(position);
                if (user == null || TextUtils.isEmpty(user.getImuser())) {
                    return;
                }

                if (mSelectType == SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT
                        || mSelectType == SELECT_CONTACT_TYPE_ADD_MEMBER
                        || mSelectType == SELECT_CONTACT_TYPE_DELETE_MEMBER
                        || mSelectType == SELECT_CONTACT_TYPE_ONLY_AT_MEMBER
                        || mSelectType == SELECT_CONTACT_TYPE_CREATE_GROUP) {
                    if (user.isSelected()) {
                        user.setSelected(false);
                        if (mGroupName.contains(user.getNickname())) {
                            mGroupName = mGroupName.replace(user.getNickname() + ",", "");
                        }
                        if (mSelectUserList.contains(user.getImuser())) {
                            mSelectUserList.remove(user.getImuser());
                        }
                        if (mAtSelectUserList.contains(user.getNickname())) {
                            mAtSelectUserList.remove(user.getNickname());
                        }
                    } else {
                        user.setSelected(true);
                        mSelectUserList.add(user.getImuser());
                        mAtSelectUserList.add(user.getNickname());
                        mGroupName = mGroupName + user.getNickname() + ",";
                    }
                    ((CheckBox) view.findViewById(R.id.allow_cb)).setChecked(user.isSelected());
                    mListAdapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                } else if (mSelectType == SELECT_CONTACT_TYPE_VIEW_MEMBER) {
                    String userName = ChatDB.getInstance(getApplicationContext()).getUserNumber();
                    if (TextUtils.isEmpty(userName)) {
                        return;
                    }
                    if (!userName.equals(user.getName())) {
                        ChatUserUtil.showUserInfo(FriendsSelectorListActivity.this, user.getName());
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID, user.getImuser());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        final ImageButton clearKeywordIb = (ImageButton) findViewById(R.id.search_clear_ib);
        final EditText searchEt = (EditText) findViewById(R.id.search_keyword_et);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = mListAdapter.getFilter();
                filter.filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (count == 0) {
                            mEmptyView.showEmptyView();
                        } else {
                            mEmptyView.hide();
                        }
                    }
                });
                if (s.length() > 0) {
                    clearKeywordIb.setVisibility(View.VISIBLE);
                } else {
                    clearKeywordIb.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearKeywordIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.getText().clear();
                mListAdapter.getFilter().filter("");
                v.setVisibility(View.GONE);
            }
        });

        LetterSideBar sideBar = (LetterSideBar) findViewById(R.id.letter_sidebar);
        TextView letterSelectedTv = (TextView) findViewById(R.id.letter_selected_tv);
        sideBar.setTextView(letterSelectedTv);
        sideBar.setOnTouchingLetterChangedListener(new LetterSideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String letter) {
                int jumpPos = mListAdapter.getPositionForSection(letter.charAt(0));
                mPullToLoadRcvView.getRecyclerView().scrollToPosition(jumpPos);
            }
        });


        loadData(false);
    }

    //初始化Titlebar
//    private void initTitleBar() {
//        tv_title = (TextView) findViewById(R.id.common_custom_title_tv);
//        tv_right = (TextView) findViewById(R.id.add_option_iv);
//        tv_left = (TextView) findViewById(R.id.add_option_tv_left);
//        tv_title.setText(R.string.message_my_group);
//        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//
//        });
//    }

    private void addGroupItemView() {
       /* View headerView = LayoutInflater.from(this).inflate(R.layout.item_group_contact,
                mPullToRefreshListView.getRefreshableView(), false);
        ListView listView = mPullToRefreshListView.getRefreshableView();
        listView.addHeaderView(headerView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResults(GroupSelectListActivity.class,
                        ACTIVITY_RESULT_SELECT_GROUP_REQUEST_CODE);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSelectType == SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT
                || mSelectType == SELECT_CONTACT_TYPE_ADD_MEMBER
                || mSelectType == SELECT_CONTACT_TYPE_CREATE_GROUP) {
            getMenuInflater().inflate(R.menu.complete, menu);
        } else if (mSelectType == SELECT_CONTACT_TYPE_DELETE_MEMBER) {
            getMenuInflater().inflate(R.menu.delete_member, menu);
        } else if (mSelectType == SELECT_CONTACT_TYPE_ONLY_AT_MEMBER) {
            getMenuInflater().inflate(R.menu.at_select_member, menu);
        } else if (mSelectType == SELECT_CONTACT_TYPE_VIEW_MEMBER) {
            getMenuInflater().inflate(R.menu.chat_member_manager, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.menu_complete) {
            int index = mGroupName.lastIndexOf(",");
            if (index != -1) {
                mGroupName = mGroupName.substring(0, index);
            }
            Intent selectMemberIntent = new Intent();
            selectMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER,
                    (ArrayList<String>) mSelectUserList);
            selectMemberIntent.putExtra(ChatConstants.EXTRA_KEY_SELECT_GROUP_NAME, mGroupName);
            setResult(RESULT_OK, selectMemberIntent);
            finish();

        } else if (i1 == R.id.menu_add) {
            Intent addMemberIntent = new Intent();
            addMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_KEY_SELECT_MEMBER,
                    (ArrayList<String>) mSelectUserList);
            setResult(RESULT_OK, addMemberIntent);
            finish();

        } else if (i1 == R.id.menu_manage_add) {
            setTitle(getResources().getString(R.string.add_group_member));
            mSelectType = SELECT_CONTACT_TYPE_ADD_MEMBER;
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_ADD_MEMBER);
            loadData(false);

        } else if (i1 == R.id.menu_manage_delete) {
            setTitle(getResources().getString(R.string.remove_group_member));
            mSelectType = SELECT_CONTACT_TYPE_DELETE_MEMBER;
            mListAdapter.setChoiceList(true);
            mListAdapter.setListSelectType(SELECT_CONTACT_TYPE_DELETE_MEMBER);
            loadData(false);

        } else if (i1 == R.id.menu_delete) {
            Intent deleteMemberIntent = new Intent();
            deleteMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_KEY_DELETE_MEMBER,
                    (ArrayList<String>) mSelectUserList);
            setResult(RESULT_OK, deleteMemberIntent);
            finish();

        } else if (i1 == R.id.menu_at_select) {
            int mIndex = mGroupName.lastIndexOf(",");
            if (mIndex != -1) {
                mGroupName = mGroupName.substring(0, mIndex);
            }
            for (int i = 0; i < mAtSelectUserList.size(); i++) {
                mGroupNames.add(mAtSelectUserList.get(i));
            }
            for (int i = 0; i < mSelectUserList.size(); i++) {
                mGroupImNames.add(mSelectUserList.get(i));
            }
            Intent atSelectMemberIntent = new Intent();
            atSelectMemberIntent.putExtra(ChatConstants.EXTRA_KEY_AT_IM_USER_NAME, mGroupName);
            atSelectMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_VIEW_AT_GROUP_MEMBERS,
                    mGroupNames);
            atSelectMemberIntent.putStringArrayListExtra(ChatConstants.EXTRA_MESSAGE_VIEW_AT_GROUP_IM_USERS,
                    mGroupImNames);
            setResult(RESULT_OK, atSelectMemberIntent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = null;
        switch (mSelectType) {
            case SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT:
            case SELECT_CONTACT_TYPE_CREATE_GROUP:
            case SELECT_CONTACT_TYPE_ADD_MEMBER:
                menuItem = menu.findItem(R.id.menu_complete);
                if (menuItem != null) {
                    menuItem.setTitle(getString(R.string.select_member_number, mSelectUserList.size()));
                    menuItem.setEnabled(mSelectUserList.size() > 0);
                }
                break;
            case SELECT_CONTACT_TYPE_DELETE_MEMBER:
                menuItem = menu.findItem(R.id.menu_complete);
                if (menuItem != null) {
                    menuItem.setTitle(getString(R.string.remove_group_member_number, mSelectUserList.size()));
                    menuItem.setEnabled(mSelectUserList.size() > 0);
                }
                break;
            case SELECT_CONTACT_TYPE_ONLY_AT_MEMBER:
                menuItem = menu.findItem(R.id.menu_complete);
                if (menuItem != null) {
                    menuItem.setTitle(getString(R.string.select_member_number, mSelectUserList.size()));
                    menuItem.setEnabled(mSelectUserList.size() > 0);
                }
                break;
            case SELECT_CONTACT_TYPE_VIEW_MEMBER:
                if (!ChatHXSDKHelper.getInstance().getHXId().equals(mOwnerImUser)) {
                    menu.findItem(R.id.menu_manage_delete).setVisible(false);
                }
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //        userMultiFollow();
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        int pageIndex =
                isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        if (mSelectType == SELECT_CONTACT_TYPE_VIEW_MEMBER
                || mSelectType == SELECT_CONTACT_TYPE_DELETE_MEMBER
                || mSelectType == SELECT_CONTACT_TYPE_ONLY_AT_MEMBER) {
            ChatUserUtil.getUserBasicInfoList(this, mGroupMembers, new MyRequestCallBack<UserArray>() {
                @Override
                public void onSuccess(UserArray result) {
                    if (result != null) {
                        if (!isLoadMore) {
                            mUserList.clear();
                        }
                        List<BaseUser> users = result.getUsers();
                        String myName = ChatDB.getInstance(getApplicationContext()).getUserNumber();
                        for (BaseUser user : users) {
                            if (mSelectType == SELECT_CONTACT_TYPE_DELETE_MEMBER
                                    && myName.equals(user.getName())) {
                                continue;
                            }
                            UserEntity userEntity = new UserEntity();
                            userEntity.setNickname(user.getNickname());
                            userEntity.setName(user.getName());
                            userEntity.setLogourl(user.getLogourl());
                            userEntity.setImuser(user.getImuser());
                            mUserList.add(userEntity);
                        }
                        mListAdapter.assembleData();
                        mListAdapter.notifyDataSetChanged();
                    }
                    onRefreshComplete(result == null ? 0 : result.getCount());
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
        } else {
            mUserList.clear();
            List<BaseUser> friends = ChatUserUtil.getFriendList();
            for (BaseUser user : friends) {
                UserEntity userEntity = new UserEntity();
                userEntity.setNickname(user.getNickname());
                userEntity.setName(user.getName());
                userEntity.setLogourl(user.getLogourl());
                userEntity.setImuser(user.getImuser());
                mUserList.add(userEntity);
            }
            calculateGroupMember();
            mListAdapter.assembleData();
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void calculateGroupMember() {
        if (mSelectType == SELECT_CONTACT_TYPE_ADD_MEMBER) {
            if (mGroupMembers != null && mGroupMembers.size() > 0) {
                for (int i = 0, j = mUserList.size(); i < j; i++) {
                    for (String username : mGroupMembers) {
                        if (username.equals(mUserList.get(i).getImuser())) {
                            mUserList.remove(i);
                            j--;
                            i--;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_RESULT_SELECT_GROUP_REQUEST_CODE) {
                if (data != null) {
                    if (mSelectType == SELECT_CONTACT_TYPE_GROUP_CHAT_SELECT
                            || mSelectType == SELECT_CONTACT_TYPE_CREATE_GROUP) {
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_TYPE, ChatActivity.CHAT_TYPE_GROUP);
                        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID,
                                data.getStringExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_ID));
                        intent.putExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME,
                                data.getStringExtra(ChatActivity.EXTRA_IM_CHAT_GROUP_NAME));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        setResult(RESULT_OK, data);
                    }
                    finish();
                }
            }
        }

    }
}