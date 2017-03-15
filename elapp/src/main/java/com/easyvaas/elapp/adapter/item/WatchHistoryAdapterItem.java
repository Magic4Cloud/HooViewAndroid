package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.ui.live.PlayerActivity;
import com.easyvaas.elapp.utils.Utils;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class WatchHistoryAdapterItem implements AdapterItem<Record> {
    private Context mContext;
    private ImageView mIvCover;
    private ImageView mIvPlayback;
    private TextView mTvTagLiving;
    private TextView mTvTitle;
    private TextView mTvName;
    private TextView mTvWatchCount;

    public WatchHistoryAdapterItem(Context context) {
        this.mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_video_live_video;
    }

    @Override
    public void onBindViews(View root) {
        mIvCover = (ImageView) root.findViewById(R.id.iv_cover);
        mIvPlayback = (ImageView) root.findViewById(R.id.iv_tag_payback);
        mTvTagLiving = (TextView) root.findViewById(R.id.tv_tag_living);
        mTvTitle = (TextView) root.findViewById(R.id.tv_title);
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTvWatchCount = (TextView) root.findViewById(R.id.tv_watch_count);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final Record model, int position) {
        mTvTitle.setText(model.getTitle());
        mTvName.setText(model.getTitle());
        mTvWatchCount.setText(mContext.getString(R.string.watch_count, model.getCount()));
        Utils.showImage(model.getPic(), R.drawable.account_bitmap_list, mIvCover);
        mIvCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerActivity.start(mContext,model.getId(),model.getLiving(),model.getMode());
            }
        });
        if (model.getLiving() == 1) {
            mTvTagLiving.setVisibility(View.VISIBLE);
            mIvPlayback.setVisibility(View.GONE);
        } else {
            mTvTagLiving.setVisibility(View.GONE);
            mIvPlayback.setVisibility(View.VISIBLE);
        }
    }
}
