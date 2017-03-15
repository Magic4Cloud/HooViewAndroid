/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.RankUserEntity;
import com.easyvaas.elapp.utils.UserUtil;

public class RiceRollContributorAdapterItem implements AdapterItem<RankUserEntity> {
    private TextView mRankingTv;
    private TextView mContributorTv;
    private TextView mContributeValue;
    private MyUserPhoto mMyUserPhoto;
    private TextView mUserGenderTv;
    private Context mContext;
    public RiceRollContributorAdapterItem(Context context) {
        this.mContext = context;
    }
    @Override
    public int getLayoutResId() {
        return R.layout.item_rice_roll_contributor;
    }

    @Override
    public void onBindViews(View root) {
        mRankingTv = (TextView) root.findViewById(R.id.ranking_tv);
        mContributorTv = (TextView) root.findViewById(R.id.contributor_tv);
        mUserGenderTv = (TextView) root.findViewById(R.id.user_gender_tv);

        mContributeValue = (TextView) root.findViewById(R.id.contribute_value);
        mMyUserPhoto = (MyUserPhoto) root.findViewById(R.id.contribute_photo);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(RankUserEntity model, int position) {
        UserUtil.showUserPhoto(mContext, model.getLogourl(), mMyUserPhoto);
        mRankingTv.setText("" + (position + 1));
        mContributorTv.setText(model.getNickname());
        mContributeValue.setText("" + model.getRiceroll());
        mMyUserPhoto.setIsVip(model.getVip());
        UserUtil.setGender(mUserGenderTv, model.getGender());
    }
}
