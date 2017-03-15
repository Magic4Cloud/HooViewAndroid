package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.Utils;


public class SearchVideoAdapterItem implements AdapterItem<VideoEntity> {
    private Context mContext;
    private FrameLayout mFlCover;
    private ImageView mIvCover;
    private ImageView mIvPlayback;
    private TextView mTvTagLiving;
    private TextView mTvTitle;
    private TextView mTvName;
    private TextView mTvWatchCount;
    private VideoEntity videoEntity;


    public SearchVideoAdapterItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_live_video;
    }

    @Override
    public void onBindViews(View root) {
        mFlCover = (FrameLayout) root.findViewById(R.id.fl_cover);
        mIvCover = (ImageView) root.findViewById(R.id.iv_cover);
        mIvPlayback = (ImageView) root.findViewById(R.id.iv_tag_payback);
        mTvTagLiving = (TextView) root.findViewById(R.id.tv_tag_living);
        mTvTitle = (TextView) root.findViewById(R.id.tv_title);
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTvWatchCount = (TextView) root.findViewById(R.id.tv_watch_count);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoEntity != null) {
                    PlayerActivity.start(mContext, videoEntity.getVid(), videoEntity.getLiving(),videoEntity.getMode());
                }
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final VideoEntity model, int position) {
        if (model != null) {
            videoEntity = model;
            mTvTitle.setText(model.getTitle());
            mTvName.setText(model.getNickname());
            mTvWatchCount.setText(mContext.getString(R.string.watch_count, model.getWatch_count() + ""));
            Utils.showImage(model.getThumb(), R.drawable.account_bitmap_list, mIvCover);
            if (videoEntity.getLiving() == 1) {
                mTvTagLiving.setVisibility(View.VISIBLE);
                mIvPlayback.setVisibility(View.GONE);
            } else {
                mTvTagLiving.setVisibility(View.GONE);
                mIvPlayback.setVisibility(View.VISIBLE);
            }
        }
    }
}
