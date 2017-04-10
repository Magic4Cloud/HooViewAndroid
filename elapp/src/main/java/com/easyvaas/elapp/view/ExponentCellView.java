package com.easyvaas.elapp.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyvaas.elapp.utils.ViewUtil;
import com.hitomi.cslibrary.CrazyShadow;
import com.hitomi.cslibrary.base.CrazyShadowDirection;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExponentCellView extends FrameLayout {

    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_number)
    TextView mTvNumber;
    @BindView(R.id.tv_percent)
    TextView mTvPercent;
    @BindView(R.id.cardView)
    CardView mCardView;

    public ExponentCellView(Context context) {
        this(context, null);
    }

    public ExponentCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(getContext(), R.layout.view_exponent_cell, this);
        ButterKnife.bind(this);
    }

    public void updateData(String name, String number, String percent) {
        mTvName.setText(name);
        mTvNumber.setText(number);
        if (percent.startsWith("-")) {
            mTvPercent.setText(percent);
        } else {
            mTvPercent.setText("+" + percent);
        }
    }

    public void updateData2(String name, String number, String percent) {
        mTvName.setText(name);
        mTvNumber.setText(number);
        mTvPercent.setText(percent);
    }

    public void setCardViewBackground(boolean up) {
        Context context = mCardView.getContext();
        int bg;
        if (up) {
            bg = context.getResources().getColor(R.color.exponent_up);
        } else {
            bg = context.getResources().getColor(R.color.exponent_down);
        }
        mCardView.setCardBackgroundColor(bg);
        new CrazyShadow.Builder()
                .setContext(context)
                .setDirection(CrazyShadowDirection.RIGHT_BOTTOM_LEFT)
                .setShadowRadius(ViewUtil.dp2Px(context, 4))
                .setCorner(ViewUtil.dp2Px(context, 4))
                .setBackground(bg)
                .setImpl(CrazyShadow.IMPL_DRAW)
                .action(this);
    }
}
