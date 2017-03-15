package com.easyvaas.elapp.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hooview.app.R;


public class ExponentCellView extends FrameLayout {
    private TextView mTvName;
    private TextView mTvNumber;
    private TextView mTvPercent;
    private CardView mCardView;

    public ExponentCellView(Context context) {
        this(context, null);
    }

    public ExponentCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_exponent_cell, this);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvNumber = (TextView) findViewById(R.id.tv_number);
        mTvPercent = (TextView) findViewById(R.id.tv_percent);
        mCardView = (CardView) findViewById(R.id.cardView);
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
        if (up) {
            mCardView.setCardBackgroundColor(mCardView.getContext().getResources()
                    .getColor(R.color.exponent_up));
        } else {
            mCardView.setCardBackgroundColor(mCardView.getContext().getResources()
                    .getColor(R.color.exponent_down));
        }
    }
}
