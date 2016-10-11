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

import com.hooview.app.R;
import com.hooview.app.bean.user.RankUserEntity;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.utils.Utils;

public class AdapterHeadItem implements AdapterItem{
    private TextView mRankingTitleTv;
    private Context mContext;
    public AdapterHeadItem(Context context) {
        this.mContext = context;
    }
    @Override
    public int getLayoutResId() {
        return R.layout.item_assets_rank_head;
    }

    @Override
    public void onBindViews(View root) {
        mRankingTitleTv = (TextView) root.findViewById(R.id.ranking_title_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        if (model instanceof RankUserEntity) {
            RankUserEntity rankUserEntity = (RankUserEntity) model;
            if (RankUserEntity.ASSETS_RANK_TYPE_RECEIVE.equals(rankUserEntity.getType())
                    || RankUserEntity.ASSETS_RANK_TYPE_MONTH_RECEIVE.equals(rankUserEntity.getType())
                    || RankUserEntity.ASSETS_RANK_TYPE_WEEK_RECEIVE.equals(rankUserEntity.getType())) {
                mRankingTitleTv.setText(mContext.getString(R.string.rising_star));
                Utils.setTextLeftDrawable(mContext, mRankingTitleTv, R.drawable.home_person_icon_ranking_crown);
            } else {
                mRankingTitleTv.setText(mContext.getString(R.string.rich_man));
                Utils.setTextLeftDrawable(mContext, mRankingTitleTv, R.drawable.home_person_icon_ranking_gold);
            }
        } else if (model instanceof UserEntity) {
            UserEntity userEntity = (UserEntity) model;
            if (userEntity.getPinned() == UserEntity.IS_PINNED_LIST_HEADER) {
                mRankingTitleTv.setText(mContext.getString(R.string.user));
            }
        } else if (model instanceof VideoEntity) {
            VideoEntity videoEntity = (VideoEntity) model;
            if (videoEntity.getLiving() == VideoEntity.IS_LIVING) {
                mRankingTitleTv.setText(mContext.getString(R.string.live));
            } else {
                mRankingTitleTv.setText(mContext.getString(R.string.video));
            }
        }
    }
}
