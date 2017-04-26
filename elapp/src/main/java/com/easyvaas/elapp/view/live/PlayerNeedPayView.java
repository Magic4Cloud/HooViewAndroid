package com.easyvaas.elapp.view.live;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date   2017/4/25
 * Editor  Misuzu
 * 付费视频 直播 蒙层
 */

public class PlayerNeedPayView extends LinearLayout {

    @BindView(R.id.player_pay_counts)
    TextView mPlayerPayCounts;
    @BindView(R.id.playey_pay_buy_button)
    TextView mPlayeyPayBuyButton;
    @BindView(R.id.player_pay_back)
    ImageView mPlayerPayBack;
    @BindView(R.id.player_pay_share)
    ImageView mPlayerPayShare;

    PayMaskOnclickListener mPayMaskOnclickListener;

    public PlayerNeedPayView(Context context) {
        super(context);
        initView();
    }

    public PlayerNeedPayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.player_need_pay_layout, this);
        ButterKnife.bind(this,this);
    }

    /**
     * 设置付费金额
     */
    public void setPlayerPayCounts(int payCounts)
    {
        mPlayerPayCounts.setText(String.valueOf(payCounts));
    }

    /**
     * 设置mask 所有点击监听
     */
    public void setPayMaskOnclickListener(PayMaskOnclickListener payMaskOnclickListener) {
        mPayMaskOnclickListener = payMaskOnclickListener;
    }

    @OnClick({R.id.playey_pay_buy_button, R.id.player_pay_back, R.id.player_pay_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playey_pay_buy_button:
                mPayMaskOnclickListener.goBuy();
                break;
            case R.id.player_pay_back:
                mPayMaskOnclickListener.goBack();
                break;
            case R.id.player_pay_share:
                mPayMaskOnclickListener.goShare();
                break;
        }
    }

    public interface PayMaskOnclickListener
    {
        public void goShare();
        public void goBack();
        public void goBuy();
    }
}
