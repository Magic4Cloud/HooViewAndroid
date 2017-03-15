package com.easyvaas.elapp.adapter.oldItem;

import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.bean.user.BaseUserEntity;
import com.easyvaas.elapp.utils.UserUtil;

public class VCallUserItem implements AdapterItem<BaseUserEntity> {

    private MyUserPhoto mUserPhoto;
    private TextView mNickname;


    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void onBindViews(View root) {
//        mUserPhoto = (MyUserPhoto) root.findViewById(R.id.v_call_user_logo_iv);
//        mNickname = (TextView) root.findViewById(R.id.v_call_user_nickname_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(BaseUserEntity model, int position) {
        UserUtil.showUserPhoto(mUserPhoto.getContext(), model.getLogourl(), mUserPhoto);
        mNickname.setText(model.getNickname());
    }
}
