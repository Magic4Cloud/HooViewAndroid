package com.easyvaas.elapp.view.gift;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.easyvaas.common.gift.bean.GiftEntity;
import com.hooview.app.R;
import com.easyvaas.elapp.utils.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GiftViewContainer extends FrameLayout {
    private static final String TAG = "GiftViewContainer";
    private List<GiftView> mGiftViewList;
    private LinkedList<GiftEntity> mGiftEntityList;

    public GiftViewContainer(Context context) {
        this(context, null);
    }

    public GiftViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_container, this, true);
        initView();
    }

    private void initView() {
        mGiftEntityList = new LinkedList<>();
        mGiftViewList = new ArrayList<>();
        mGiftViewList.add((GiftView) findViewById(R.id.giftView1));
        mGiftViewList.add((GiftView) findViewById(R.id.giftView2));
        mGiftViewList.add((GiftView) findViewById(R.id.giftView3));
        mGiftViewList.add((GiftView) findViewById(R.id.giftView4));
    }

    public GiftView getAvailable() {
        for (int i = 0; i < mGiftViewList.size(); i++) {
            GiftView giftView = mGiftViewList.get(i);
            if (!giftView.isSelected()) {
                return giftView;
            }
        }
        return null;
    }

    public void addAndPlayGift(GiftEntity giftEntity) {
        final GiftView giftView = getAvailable();
        if (giftView != null) {
            Logger.d(TAG, "addAndPlayGift: " + giftEntity.getName());
            giftView.startAnimate(giftEntity, new Runnable() {
                @Override
                public void run() {
                    poll();
                }
            });
        } else {
            mGiftEntityList.addFirst(giftEntity);
        }
    }


    public void poll() {
        final GiftView giftView = getAvailable();
        if (giftView != null) {
            if (mGiftEntityList.size() > 0) {
                GiftEntity giftEntity = mGiftEntityList.removeLast();
                if (giftEntity != null) {
                    giftView.startAnimate(giftEntity, new Runnable() {
                        @Override
                        public void run() {
                            poll();
                        }
                    });
                }
            }
        }
    }
}
