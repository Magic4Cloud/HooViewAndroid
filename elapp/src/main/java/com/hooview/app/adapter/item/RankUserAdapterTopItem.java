/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.hooview.app.adapter.recycler.RiceRollContributorAdapter;
import com.hooview.app.bean.user.RankUserEntity;
import com.hooview.app.utils.UserUtil;

public class RankUserAdapterTopItem implements AdapterItem<RankUserEntity> {
    private MyUserPhoto mRankingUserPhoto;
    private TextView mRankingUserName;
    private TextView mRankingUserRankTv;
    private TextView mRankingNumberTv;
    private ImageView mRankingUserPhotoBg;
    private TextView mUserConstellationTv;
    private TextView mUserGenderTv;
    private Object mType;
    private Context mContext;

    public RankUserAdapterTopItem(Context context, Object type) {
        this.mContext = context;
        this.mType = type;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_tab_person_ranking_top;
    }

    @Override
    public void onBindViews(View root) {
        mRankingNumberTv = (TextView) root.findViewById(R.id.ranking_number_tv_top);
        mRankingUserPhoto = (MyUserPhoto) root.findViewById(R.id.ranking_user_photo_top);
        mRankingUserName = (TextView) root.findViewById(R.id.user_name_tv);
        mRankingUserRankTv = (TextView) root.findViewById(R.id.ranking_user_rank_tv_top);
        mRankingUserPhotoBg = (ImageView) root.findViewById(R.id.ranking_user_photo_bg);
        mUserConstellationTv = (TextView) root.findViewById(R.id.user_constellation_tv);
        mUserConstellationTv.setVisibility(View.GONE);
        mUserGenderTv = (TextView) root.findViewById(R.id.user_gender_tv);


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRankingUserPhoto
                .getLayoutParams();
        RelativeLayout.LayoutParams layoutParamsBg = (RelativeLayout.LayoutParams) mRankingUserPhotoBg
                .getLayoutParams();
        if (mType == RiceRollContributorAdapter.ITEM_TYPE_FIRST) {
            layoutParams.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_third_size);
            layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.avatar_big_third_size);
            layoutParamsBg.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_third_bg_size);
            layoutParamsBg.width = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_third_bg_size);
        } else if (mType == RiceRollContributorAdapter.ITEM_TYPE_SECOND) {
            layoutParams.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_second_size);
            layoutParams.width = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_second_size);
            layoutParamsBg.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_second_bg_size);
            layoutParamsBg.width = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_second_bg_size);
        } else if (mType == RiceRollContributorAdapter.ITEM_TYPE_THIRD) {
            layoutParams.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_first_size);
            layoutParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.avatar_big_first_size);
            layoutParamsBg.height = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_first_bg_size);
            layoutParamsBg.width = mContext.getResources()
                    .getDimensionPixelSize(R.dimen.avatar_big_first_bg_size);
        }
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final RankUserEntity model, int position) {
        mRankingNumberTv.setText("");
        if (model.getRank() == 1) {
            mRankingNumberTv.setBackgroundResource(R.drawable.list_icon_medal_one);
            mRankingUserPhotoBg.setImageResource(R.drawable.list_icon_no_one);
        } else if (model.getRank() == 2) {
            mRankingNumberTv.setBackgroundResource(R.drawable.list_icon_medal_two);
            mRankingUserPhotoBg.setImageResource(R.drawable.list_icon_no_two);
        } else if (model.getRank() == 3) {
            mRankingNumberTv.setBackgroundResource(R.drawable.list_icon_medal_three);
            mRankingUserPhotoBg.setImageResource(R.drawable.list_icon_no_three);
        } else {
            mRankingNumberTv.setText("   " + model.getRank() + "   ");
            mRankingNumberTv.setBackgroundResource(0);
            mRankingUserPhotoBg.setVisibility(View.GONE);
        }
        mRankingUserName.setText(model.getNickname());
//        UserUtil.setConstellation(mUserConstellationTv, model.getBirthday());
        if (RankUserEntity.ASSETS_RANK_TYPE_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_MONTH_SEND.equals(model.getType())
                || RankUserEntity.ASSETS_RANK_TYPE_WEEK_SEND.equals(model.getType())) {
        } else {
            mRankingUserRankTv.setText(mContext.getString(R.string.contribute_value, model.getRiceroll()));
        }
        UserUtil.showUserPhoto(mContext, model.getLogourl(), mRankingUserPhoto);
        UserUtil.setGender(mUserGenderTv, model.getGender());
    }
}

