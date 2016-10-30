package com.easyvaas.common.chat.adapter.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.chat.activity.FriendsSelectorListActivity;
import com.easyvaas.common.chat.bean.UserEntity;
import com.easyvaas.common.chat.db.ChatDB;
import com.easyvaas.common.chat.utils.ChatUserUtil;
import com.easyvaas.common.chat.R;
import com.easyvaas.common.widget.MyUserPhoto;

public class AdapterFriendLetterItem implements AdapterItem<UserEntity> {
    private boolean mIsChoiceList;
    private SectionIndexer mSectionIndexer;
    private Context mContext;
    private int mListSelectType;

    private TextView titleTv;
    private TextView letterTv;
    private MyUserPhoto myUserPhoto;
    private CheckBox selectedCb;

    public void setChoice(boolean isChoice) {
        mIsChoiceList = isChoice;
    }
    
    @Override
    public int getLayoutResId() {
        return R.layout.item_friends_letter_sort;
    }

    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        mSectionIndexer = sectionIndexer;
    }

    public void setListSelectType(int listSelectType) {
        mListSelectType = listSelectType;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        titleTv = (TextView) root.findViewById(R.id.title);
        letterTv = (TextView) root.findViewById(R.id.letter_tv);
        myUserPhoto = (MyUserPhoto) root.findViewById((R.id.my_user_photo));
        selectedCb = (CheckBox) root.findViewById(R.id.allow_cb);
        if (mIsChoiceList) {
            selectedCb.setVisibility(View.VISIBLE);
            selectedCb.setClickable(false);
            selectedCb.setEnabled(false);
        } else {
            selectedCb.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final UserEntity friend, int position) {
        int section = friend.getSortLetter().charAt(0);
        if (position == mSectionIndexer.getPositionForSection(section)) {
            letterTv.setVisibility(View.VISIBLE);
            letterTv.setText(friend.getSortLetter());
        } else {
            letterTv.setVisibility(View.GONE);
        }
        selectedCb.setChecked(friend.isSelected());
        titleTv.setText(friend.getNickname());
        ChatUserUtil.showUserPhoto(mContext, friend.getLogourl(), myUserPhoto);
        myUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = ChatDB.getInstance(mContext).getUserNumber();
                if (TextUtils.isEmpty(userName)) {
                    return;
                }
                if (!userName.equals(friend.getName())) {
                    ChatUserUtil.showUserInfo(mContext, friend.getName());
                }
            }
        });
        titleTv.setCompoundDrawables(null, null, null, null);
    }
}
