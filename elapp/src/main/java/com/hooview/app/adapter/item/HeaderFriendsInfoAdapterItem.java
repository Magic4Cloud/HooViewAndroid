/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.adapter.recycler.FriendsUserInfoAdapter;
import com.hooview.app.bean.user.User;
import com.hooview.app.utils.UserUtil;

public class HeaderFriendsInfoAdapterItem implements AdapterItem<User> {
    private TextView mVideoCountTv;
    private TextView mFansCountTv;
    private TextView mFollowCountTv;
    private MyUserPhoto myUserPhoto;
    private TextView mNicknameTv;
    private TextView mGenderTv;
    private TextView mConstellationTv;
    private TextView mUserIdTv;
    private TextView mSignatureTv;
    private TextView mLocationTv;
    private ImageView mOperationIconCb;
    private Context mContext;

    private FriendsUserInfoAdapter.OnClickUserItemViewListener mOnClickUserItemViewListener;

    public HeaderFriendsInfoAdapterItem(Context context) {
        mContext = context;
    }

    public void setOnItemClickViewListener(
            FriendsUserInfoAdapter.OnClickUserItemViewListener onClickUserItemViewListener) {
        mOnClickUserItemViewListener = onClickUserItemViewListener;
    }

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_header_user_info;
    }

    @Override
    public void onBindViews(View root) {
        mOperationIconCb = (ImageView) root.findViewById(com.hooview.app.R.id.operation_action_iv);
        mOperationIconCb.setImageResource(com.hooview.app.R.drawable.personal_icon_share);
        root.findViewById(com.hooview.app.R.id.user_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickUserItemViewListener.onUserInfoBackClick();
            }
        });

        //隐藏分享功能
        mOperationIconCb.setVisibility(View.GONE);

        mVideoCountTv = (TextView) root.findViewById(com.hooview.app.R.id.video_count_tv);
        mVideoCountTv.setTextColor(mContext.getResources().getColor(com.hooview.app.R.color.btn_color_main));
        mFansCountTv = (TextView) root.findViewById(com.hooview.app.R.id.fans_count_tv);
        mFollowCountTv = (TextView) root.findViewById(com.hooview.app.R.id.follow_count_tv);
        mNicknameTv = (TextView) root.findViewById(com.hooview.app.R.id.mine_user_name_tv);
        mGenderTv = (TextView) root.findViewById(com.hooview.app.R.id.user_gender_tv);
        mConstellationTv = (TextView) root.findViewById(com.hooview.app.R.id.user_constellation_tv);
        mUserIdTv = (TextView) root.findViewById(com.hooview.app.R.id.mine_id_tv);
        mSignatureTv = (TextView) root.findViewById(com.hooview.app.R.id.mine_signature_tv);
        mLocationTv = (TextView) root.findViewById(com.hooview.app.R.id.mine_location_tv);
        root.findViewById(com.hooview.app.R.id.fans_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickUserItemViewListener.onFansItemClick();
            }
        });

        root.findViewById(com.hooview.app.R.id.follower_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickUserItemViewListener.onFollowItemClick();
            }
        });
        myUserPhoto = (MyUserPhoto) root.findViewById(com.hooview.app.R.id.my_user_photo);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(User user, int position) {
        UserUtil.showUserPhoto(mContext, user.getLogourl(), myUserPhoto);
        myUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickUserItemViewListener.onUserPhotoClick();
            }
        });
        mOperationIconCb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mOnClickUserItemViewListener.onShareItemClick();
            }
        });
        mNicknameTv.setText(user.getNickname());
        mUserIdTv.setText("ID:" + user.getName());
        UserUtil.setGender(mGenderTv, user.getGender(), user.getBirthday());
        UserUtil.setConstellation(mConstellationTv, user.getBirthday());
        mLocationTv.setText(user.getLocation());
        if (TextUtils.isEmpty(user.getSignature())) {
            mSignatureTv.setText(com.hooview.app.R.string.hint_signature);
        } else {
            mSignatureTv.setText(user.getSignature());
        }
        mVideoCountTv.setText(user.getVideo_count() + "");
        mFansCountTv.setText(user.getFans_count() + "");
        mFollowCountTv.setText(user.getFollow_count() + "");
    }
}
