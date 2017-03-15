/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.RankUserEntity;

public class AssetsRankAdapterPinnedItem implements AdapterItem<RankUserEntity> {
    private OnPinnedViewClick mOnPinnedViewClick;
    private TextView mNowLeftBtn;
    private TextView mNowMiddleBtn;
    private TextView mNowRightBtn;
    private TextView mLastSelectedText;
    private ImageView mTabLeftLineIv;
    private ImageView mTabMiddleLineIv;
    private ImageView mTabRightLineIv;
    private Context mContext;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.now_left_btn:
                    mOnPinnedViewClick.onWeeklyRankClick();
                    mTabLeftLineIv.setSelected(true);
                    mTabMiddleLineIv.setSelected(false);
                    mTabRightLineIv.setSelected(false);
                    break;
                case R.id.now_middle_btn:
                    mOnPinnedViewClick.onMonthRankClick();
                    mTabLeftLineIv.setSelected(false);
                    mTabMiddleLineIv.setSelected(true);
                    mTabRightLineIv.setSelected(false);
                    break;
                case R.id.now_right_btn:
                    mOnPinnedViewClick.onTotalRankClick();
                    mTabLeftLineIv.setSelected(false);
                    mTabMiddleLineIv.setSelected(false);
                    mTabRightLineIv.setSelected(true);
                    break;
            }
            updateTabButton((TextView) view);
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.asset_rank_header_pinned;
    }

    @Override
    public void onBindViews(View rootView) {
        mContext = rootView.getContext();
        mNowLeftBtn = (TextView) rootView.findViewById(R.id.now_left_btn);
        mNowLeftBtn.setOnClickListener(mOnClickListener);
        mNowMiddleBtn = (TextView) rootView.findViewById(R.id.now_middle_btn);
        mNowMiddleBtn.setOnClickListener(mOnClickListener);
        mNowRightBtn = (TextView) rootView.findViewById(R.id.now_right_btn);
        mNowRightBtn.setOnClickListener(mOnClickListener);
        mTabLeftLineIv = (ImageView) rootView.findViewById(R.id.tab_left_line);
        mTabMiddleLineIv = (ImageView) rootView.findViewById(R.id.tab_middle_line);
        mTabRightLineIv = (ImageView) rootView.findViewById(R.id.tab_right_line);

    }

    @Override
    public void onSetViews() {
        mNowLeftBtn.setText(mContext.getString(R.string.asset_rank_weekly));
        mNowMiddleBtn.setText(mContext.getString(R.string.asset_rank_month));
        mNowRightBtn.setText(mContext.getString(R.string.asset_rank_total));
        mLastSelectedText = mNowLeftBtn;
        mLastSelectedText.setSelected(true);
        mTabLeftLineIv.setSelected(true);
    }

    @Override
    public void onUpdateViews(RankUserEntity model, int position) {

    }

    private void updateTabButton(TextView textView) {
        mLastSelectedText.setSelected(false);
        textView.setSelected(true);
        mLastSelectedText = textView;
    }

    public void setOnPinnedViewClick(OnPinnedViewClick onPinnedViewClick) {
        mOnPinnedViewClick = onPinnedViewClick;
    }

    public interface OnPinnedViewClick {
        void onMonthRankClick();

        void onWeeklyRankClick();

        void onTotalRankClick();
    }
}
