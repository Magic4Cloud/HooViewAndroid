package com.easyvaas.common.chat.adapter.item;

import android.content.Context;
import android.view.View;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.widget.MyUserPhoto;

public class SelectFriendsAdapterItem implements AdapterItem<String> {
    private Context mContext;
    private MyUserPhoto mGroupMemberPhoto;
    @Override
    public int getLayoutResId() {
        return R.layout.item_select_friends;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        mGroupMemberPhoto = (MyUserPhoto) root.findViewById(R.id.group_member_photo);

    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(String model, int position) {
        ChatUserUtil.setUserAvatar(mContext, model, mGroupMemberPhoto);
    }
}
