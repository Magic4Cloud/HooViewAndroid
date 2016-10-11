/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.hooview.app.bean.pay.CashInOptionEntity;
import com.hooview.app.adapter.CashInAmountAdapter;

public class CashInAmountAdapterItem implements AdapterItem<CashInOptionEntity> {
    private CashInAmountAdapter.OnclickViewListener mOnclickViewListener;

    private TextView mBuyCoinsTv;
    private TextView mFreeCoinsTv;
    private CheckBox mCashInMoneyCb;

    public CashInAmountAdapterItem(CashInAmountAdapter.OnclickViewListener onClickViewListener) {
        mOnclickViewListener = onClickViewListener;
    }



    @Override
    public int getLayoutResId() {
        return R.layout.item_cash_in_coins;
    }

    @Override
    public void onBindViews(View root) {
        mBuyCoinsTv = (TextView) root.findViewById(R.id.exchange_coins_tv);
        mFreeCoinsTv = (TextView) root.findViewById(R.id.exchange_coins_give_tv);
        mCashInMoneyCb = (CheckBox) root.findViewById(R.id.exchange_money_tv);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCashInMoneyCb.setChecked(!mCashInMoneyCb.isChecked());
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final CashInOptionEntity model, final int position) {
        Context context = mBuyCoinsTv.getContext();
        mBuyCoinsTv.setText(context.getString(R.string.e_coin_count_rear, model.getEcoin()));
        mFreeCoinsTv.setText(context.getString(R.string.e_coin_give_count_rear, model.getFree()));
        mCashInMoneyCb.setText(context.getString(R.string.cash_count_simple, model.getRmb() / 100));
        if (model.isChecked()) {
            mCashInMoneyCb.setSelected(true);
        } else {
            mCashInMoneyCb.setSelected(false);
        }
        mCashInMoneyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mOnclickViewListener.onCheckedChange(position);
            }
        });
    }

}