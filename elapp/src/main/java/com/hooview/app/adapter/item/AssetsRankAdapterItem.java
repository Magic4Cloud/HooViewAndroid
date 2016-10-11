/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.bean.user.RankUserEntity;
import com.hooview.app.utils.UserUtil;

public class AssetsRankAdapterItem implements AdapterItem<RankUserEntity> {
    private MyUserPhoto mRankingUserPhoto;
    private TextView mRankingUserName;
    private TextView mRankingUserRankTv;
    private TextView mRankingNumberTv;
    private TextView mRankingEconTv;
    private TextView mRankingGenderTv;
    private TextView mUserConstellationTv;
    private Context mContext;

    public AssetsRankAdapterItem(Context context) {
        this.mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_tab_person_ranking;
    }

    @Override
    public void onBindViews(View root) {
        mRankingNumberTv = (TextView) root.findViewById(com.hooview.app.R.id.ranking_number_tv);
        mRankingUserPhoto = (MyUserPhoto) root.findViewById(com.hooview.app.R.id.ranking_user_photo);
        mRankingUserName = (TextView) root.findViewById(com.hooview.app.R.id.ranking_user_name_tv);
        mRankingUserRankTv = (TextView) root.findViewById(com.hooview.app.R.id.ranking_user_rank_tv);
        mRankingEconTv = (TextView) root.findViewById(com.hooview.app.R.id.ranking_user_rank_ecoin_tv);
        mRankingGenderTv = (TextView) root.findViewById(com.hooview.app.R.id.ranking_user_name_gender_tv);
        mUserConstellationTv = (TextView) root.findViewById(com.hooview.app.R.id.user_constellation_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final RankUserEntity model, int position) {
        mRankingNumberTv.setText("");
        if (RankUserEntity.ASSETS_RANK_TYPE_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND.equals(model.getType())) {
            if (model.getRank() == 1) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_first);
            } else if (model.getRank() == 2) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_second);
            } else if (model.getRank() == 3) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_third);
            } else {
                mRankingNumberTv.setText("   " + model.getRank() + "   ");
                mRankingNumberTv.setBackgroundResource(0);
            }
        } else if (RankUserEntity.ASSETS_RANK_TYPE_RECEIVE.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_RECEIVE.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_RECEIVE.equals(model.getType())) {
            if (model.getRank() == 1) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_first);

            } else if (model.getRank() == 2) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_second);
            } else if (model.getRank() == 3) {
                mRankingNumberTv.setBackgroundResource(com.hooview.app.R.drawable.home_person_icon_ranking_third);
            } else {
                mRankingNumberTv.setText("   " + model.getRank() + "   ");
                mRankingNumberTv.setBackgroundResource(0);
            }
        }
        mRankingUserName.setText(model.getNickname());
        if (RankUserEntity.ASSETS_RANK_TYPE_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND.equals(model.getType())) {
            mRankingUserRankTv.setText(com.hooview.app.R.string.contribute_e_coin_count);
            mRankingEconTv.setText(model.getCostecoin() + "");
        } else {
            mRankingUserRankTv.setText(com.hooview.app.R.string.get_rice_roll_count);
            mRankingEconTv.setText(model.getRiceroll() + "");
        }
        UserUtil.showUserPhoto(mContext, model.getLogourl(), mRankingUserPhoto);
        mRankingUserPhoto.setIsVip(model.getVip());
//        UserUtil.setGender(mRankingGenderTv, model.getGender());
//        UserUtil.setConstellation(mUserConstellationTv,  model.getBirthday());
    }
}
