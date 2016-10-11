/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiUtil;
import com.hooview.app.utils.UserUtil;

public class UserAdapterItem implements AdapterItem<UserEntity> {

    static final int TYPE_FOLLOWER = 0;
    static final int TYPE_FANS = 1;
    static final int TYPE_NEARBY = 2;
    static final int TYPE_SUBJECT = 3;
    static final int TYPE_INTEREST = 4;

    private Context mContext;
    private int mCurrentType;

    private MyUserPhoto my_user_photo;
    private Button followCb;
    private TextView nicknameTv;
    private TextView signatureTv;
    private TextView distanceTv;
    private TextView liveInfoTv;

    private View userExtInfoLl;
    private TextView genderTv;
    private TextView constellationTv;
    private boolean mIsFans;

    private UserAdapterItem() {

    }

    /**
     * @param type {@link #TYPE_FANS}, {@link #TYPE_FOLLOWER}, {@link #TYPE_NEARBY}
     */
    protected UserAdapterItem(int type) {
        mCurrentType = type;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_follower_fans;
    }

    @Override
    public void onBindViews(View root) {
        mContext = root.getContext();
        my_user_photo = (MyUserPhoto) root.findViewById(R.id.my_user_photo);
        followCb = (Button) root.findViewById(R.id.follow_cb);
        nicknameTv = (TextView) root.findViewById(R.id.nickname_tv);
        signatureTv = (TextView) root.findViewById(R.id.signature_tv);
        distanceTv = (TextView) root.findViewById(R.id.distance_tv);
        liveInfoTv = (TextView) root.findViewById(R.id.live_info_tv);

        userExtInfoLl = root.findViewById(R.id.user_ext_info_ll);
        genderTv = (TextView) root.findViewById(R.id.user_gender_tv);
        constellationTv = (TextView) root.findViewById(R.id.user_constellation_tv);
    }

    @Override
    public void onSetViews() {
    }

    @Override
    public void onUpdateViews(final UserEntity model, int position) {
        followCb.setTag(model);
        UserUtil.showUserPhoto(mContext, model.getLogourl(), my_user_photo);
        my_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserUtil.showUserInfo(mContext, model.getName());
            }
        });
        signatureTv.setText(model.getSignature());
        nicknameTv.setText(model.getNickname());
        my_user_photo.setIsVip(model.getVip());

        if (mCurrentType == TYPE_FOLLOWER) {
            if (model.getFaned() != UserEntity.FANED) {
                mIsFans = true;
            } else {
                mIsFans = false;
            }
            setUserExtInfo(false, model);
        } else if (mCurrentType == TYPE_FANS) {
            // TODO UserId is the view users id
            //if (GiftDB.getInstance(mContext).getUserNumber().equals(mUserId)) {
            //    followCb.setBackgroundResource(R.drawable.btn_mutual_fans);
            //} else {
//            followCb.setBackgroundResource(R.drawable.btn_mutual_follow);
            //}
            mIsFans = true;
            setUserExtInfo(false, model);
        }
        if (model.getFollowed() != UserEntity.FOLLOWED) {
                followCb.setText(mContext.getString(R.string.follow));
        } else {
            if(mIsFans){
                followCb.setText(mContext.getString(R.string.followed));
            }else{
                followCb.setText(mContext.getString(R.string.follow_each_other));
            }
        }
        followCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followCb.getText().toString().equals(mContext.getString(R.string.follow))) {
                    model.setFollowed(UserEntity.FOLLOWED);
                    if(mIsFans){
                        followCb.setText(mContext.getString(R.string.followed));
                    }else{
                        followCb.setText(mContext.getString(R.string.follow_each_other));
                    }
                } else {
                    model.setFollowed(UserEntity.UN_FOLLOWED);
                    followCb.setText(mContext.getString(R.string.follow));
                }
                ApiUtil.userFollow(mContext, model.getName(), model.getFollowed() == UserEntity.FOLLOWED, v);
            }
        });
    }

    private void setUserExtInfo(boolean isVisible, UserEntity model) {
        if (isVisible) {
            UserUtil.setGender(genderTv, model.getGender(), model.getBirthday());
//            UserUtil.setConstellation(constellationTv, model.getBirthday());
            userExtInfoLl.setVisibility(View.VISIBLE);
        } else {
            UserUtil.setGender(genderTv, model.getGender());
            userExtInfoLl.setVisibility(View.GONE);
        }
        if (Preferences.getInstance(mContext).getUserNumber().equals(model.getName())) {
            followCb.setVisibility(View.INVISIBLE);
        } else {
            followCb.setVisibility(View.VISIBLE);
        }
    }
}
