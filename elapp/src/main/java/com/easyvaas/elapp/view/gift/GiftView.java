package com.easyvaas.elapp.view.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.hooview.app.R;
import com.easyvaas.elapp.utils.Logger;


public class GiftView extends FrameLayout {
    private static final String TAG = "GiftView";
    private GiftEntity giftEntity;
    private TextView mTvName;
    private TextView mTvCount;
    private String unit;

    public GiftView(Context context) {
        this(context, null);
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift, this, true);
        initView();
    }

    private void initView() {
        mTvCount = (TextView) findViewById(R.id.tv_gift_count);
        mTvName = (TextView) findViewById(R.id.tv_name);
        setAlpha(0);
        unit = getContext().getString(R.string.unit_gift);
    }

    public void startAnimate(GiftEntity giftEntity, final Runnable runnable) {
        Logger.d(TAG, "startAnimate: " + giftEntity.getGiftName());
        this.setAlpha(1);
        mTvCount.setText(giftEntity.getGiftName() + giftEntity.getGiftCount() + unit);
        mTvName.setText(giftEntity.getNickname());
        animate().alpha(0).setDuration(5000).withStartAction(new Runnable() {
            @Override
            public void run() {
                setSelected(true);
            }
        }).withEndAction(new Runnable() {
            @Override
            public void run() {
                setSelected(false);
                runnable.run();
            }
        });
    }
}
