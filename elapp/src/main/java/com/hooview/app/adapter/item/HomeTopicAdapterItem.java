/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.bean.TopicEntity;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ViewUtil;

public class HomeTopicAdapterItem implements AdapterItem<TopicEntity> {
    private TextView mTopicNameTv;
    private TextView mTopicVideoNumberTv;
    private ImageView mTopicSelectIv;
    private ImageView mTopicIconNewIv;
    private RelativeLayout mRl;

    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_home_topic;
    }

    @Override
    public void onBindViews(View root) {
        mRl = (RelativeLayout) root.findViewById(com.hooview.app.R.id.item_home_topic_rl);
        mTopicNameTv = (TextView) root.findViewById(com.hooview.app.R.id.topic_name_tv);
        mTopicVideoNumberTv = (TextView) root.findViewById(com.hooview.app.R.id.topic_video_number_tv);
        mTopicSelectIv = (ImageView) root.findViewById(com.hooview.app.R.id.topic_select_iv);
        mTopicIconNewIv = (ImageView) root.findViewById(com.hooview.app.R.id.topic_icon_new_iv);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) mRl.getLayoutParams();
        layoutParams.width = (int) ViewUtil.getScreenWidth(root.getContext())/3;
        layoutParams.height = (int) ViewUtil.getScreenHeight(root.getContext())/5;
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(TopicEntity model, int position) {
        mTopicNameTv.setText(model.getTitle());
        mTopicVideoNumberTv.setText("");
        Utils.showImage(model.getIcon(), com.hooview.app.R.drawable.guestavatar, mTopicIconNewIv);
        if (model.isSelect()) {
            mTopicNameTv.setSelected(true);
            mTopicVideoNumberTv.setSelected(true);
            mTopicSelectIv.setVisibility(View.GONE);
        } else {
            mTopicNameTv.setSelected(false);
            mTopicVideoNumberTv.setSelected(false);
            mTopicSelectIv.setVisibility(View.GONE);
        }
    }
}
