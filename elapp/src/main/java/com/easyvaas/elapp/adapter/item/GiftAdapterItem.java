package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.hooview.app.R;

public class GiftAdapterItem implements AdapterItem<GiftEntity> {
    int[] backgrounds = {R.drawable.shape_gift_bg_1, R.drawable.shape_gift_bg_2, R.drawable.shape_gift_bg_3, R.drawable.shape_gift_bg_4};
    private TextView mTvName;
    private TextView mTvGift;
    private LinearLayout mLlGift;
    private Context mContext;

    public GiftAdapterItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_gift;
    }

    @Override
    public void onBindViews(View root) {
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTvGift = (TextView) root.findViewById(R.id.tv_gift_count);
        mLlGift = (LinearLayout) root.findViewById(R.id.ll_gift);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final GiftEntity model, int position) {
        mLlGift.setBackgroundResource(backgrounds[position % 4]);
        mTvGift.setText(mContext.getResources().getString(R.string.gift_tips, model.getGiftName(), model.getGiftCount() + ""));
        mTvName.setText(model.getNickname());
        if (model.isDisplayed()) {
            mLlGift.setAlpha(0);
        } else {
            mLlGift.setAlpha(1);
            mLlGift.animate().alpha(0).setDuration(10000);
            model.setDisplayed(true);
        }
    }
}
