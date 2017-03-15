package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.news.LastestNewsModel;
import com.easyvaas.elapp.utils.DateTimeUtil;

public class LastestNewsItem implements AdapterItem<LastestNewsModel.NewsFlashEntity> {
    private LinearLayout mLlLine;
    private TextView mTvTime;
    private TextView mTvTitle;
    private Context mContext;

    public LastestNewsItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_lastest_news;
    }

    @Override
    public void onBindViews(View root) {
        mLlLine = (LinearLayout) root.findViewById(R.id.ll_line);
        mTvTime = (TextView) root.findViewById(R.id.tv_time);
        mTvTitle = (TextView) root.findViewById(R.id.tv_title);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(LastestNewsModel.NewsFlashEntity model, int position) {
        if (model != null) {
            boolean important = !TextUtils.isEmpty(model.getImportance()) && !model.getImportance().equals("0");
            if (!TextUtils.isEmpty(model.getTime())) {
                mTvTime.setText(DateTimeUtil.getLastestNewsTime(mContext, model.getTime()));
                mTvTime.setSelected(important);
            }
            mTvTitle.setSelected(important);
            mTvTitle.setText(model.getBody());
        }
    }
}
