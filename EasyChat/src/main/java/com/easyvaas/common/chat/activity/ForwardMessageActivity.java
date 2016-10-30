package com.easyvaas.common.chat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.easyvaas.common.chat.ChatHXSDKHelper;
import com.easyvaas.common.chat.adapter.ContactAdapter;
import com.easyvaas.common.chat.applib.controller.HXSDKHelper;
import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.bean.ChatUser;
import com.easyvaas.common.chat.utils.ChatConstants;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.view.Sidebar;
import com.easyvaas.common.chat.R;

public class ForwardMessageActivity extends BaseActivity {
    public static final String EXTRA_MESSAGE_ID = "extra_forward_msg_id";
    public static final String EXTRA_CURRENT_CHAT_USERNAME = "extra_current_chat_username";
    private static final int REQUEST_ALERT_DIALOG = 1;
    private static final int REQUEST_NEW_CHAT_USER = 2;

    private ContactAdapter mContactAdapter;
    private ChatUser mSelectUser;
    private String mForwardMsgId;
    private String mCurrentChatUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForwardMsgId = getIntent().getStringExtra(EXTRA_MESSAGE_ID);
        mCurrentChatUsername = getIntent().getStringExtra(EXTRA_CURRENT_CHAT_USERNAME);

        setContentView(R.layout.activity_forward_message);
        setTitle(R.string.chat_select_forward_user);

        final ListView listView = (ListView) findViewById(R.id.forward_chat_user_list);
        View headerView = LayoutInflater.from(this)
                .inflate(R.layout.view_header_forward_message, listView, false);
        headerView.findViewById(R.id.chat_to_new_user_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendsSelectorListActivity.class);
                startActivityForResult(intent, REQUEST_NEW_CHAT_USER);
            }
        });
        final ImageButton clearKeywordIb = (ImageButton) headerView.findViewById(R.id.search_clear_ib);
        final EditText searchEt = (EditText) headerView.findViewById(R.id.search_keyword_et);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContactAdapter.getFilter().filter(s);
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
                mContactAdapter.getFilter().filter("");
                v.setVisibility(View.GONE);
            }
        });

        listView.addHeaderView(headerView);
        mContactAdapter = new ContactAdapter(this, R.layout.item_chat_contact, getContactList());
        listView.setAdapter(mContactAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListUserItemClick(position - listView.getHeaderViewsCount());
            }
        });
        Sidebar sidebar = (Sidebar) findViewById(R.id.letter_sidebar);
        sidebar.setListView(listView);
    }

    private void onListUserItemClick(int position) {
        mSelectUser = mContactAdapter.getItem(position);
        Intent intent = new Intent(ForwardMessageActivity.this, AlertDialogActivity.class);
        intent.putExtra("cancel", true);
        intent.putExtra("titleIsCancel", true);
        intent.putExtra("msg", getString(R.string.confirm_forward_to,
                ChatUserUtil.getNickName(mSelectUser.getUsername(), this)));
        startActivityForResult(intent, REQUEST_ALERT_DIALOG);
    }

    private List<ChatUser> getContactList() {
        String name = ChatHXSDKHelper.getInstance().getHXId();
        List<ChatUser> contactList = new ArrayList<ChatUser>();
        Map<String, ChatUser> users = ((ChatHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
        for (Map.Entry<String, ChatUser> entry : users.entrySet()) {
            if (!entry.getKey().equals(ChatConstants.NEW_FRIENDS_USERNAME)
                    && !entry.getKey().equals(ChatConstants.GROUP_USERNAME)
                    && !entry.getKey().equals(ChatConstants.CHAT_ROOM)
                    && !entry.getKey().equals(ChatConstants.CHAT_ROBOT)) {
                if (entry.getKey().equals(name) || entry.getKey().equals(mCurrentChatUsername)) {
                    continue;
                }
                contactList.add(entry.getValue());
            }
        }
        Collections.sort(contactList, new Comparator<ChatUser>() {
            @Override
            public int compare(ChatUser lhs, ChatUser rhs) {
                return lhs.getHeader().compareTo(rhs.getHeader());
            }
        });

        return contactList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ChatActivity.class);
            if (requestCode == REQUEST_ALERT_DIALOG) {
                if (mSelectUser == null || TextUtils.isEmpty(mSelectUser.getUsername())) {
                    return;
                }
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID, mSelectUser.getUsername());
            } else if (requestCode == REQUEST_NEW_CHAT_USER) {
                intent.putExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID,
                        data.getStringExtra(ChatActivity.EXTRA_IM_CHAT_USER_ID));
            }
            intent.putExtra(EXTRA_MESSAGE_ID, mForwardMsgId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
