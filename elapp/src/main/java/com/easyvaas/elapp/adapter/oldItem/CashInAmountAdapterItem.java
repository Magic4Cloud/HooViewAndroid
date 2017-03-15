/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.CashInAmountAdapter;
import com.easyvaas.elapp.bean.pay.CashInOptionEntity;

import java.text.DecimalFormat;

public class CashInAmountAdapterItem implements AdapterItem<CashInOptionEntity> {
    private static final String TAG = "CashInAmountAdapterItem";
    private CashInAmountAdapter.OnclickViewListener mOnclickViewListener;

    private TextView mBuyCoinsTv;
    private TextView mPriceTv;
    //    private CheckBox mCashInMoneyCb;
    private RelativeLayout mRoot;

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
        mPriceTv = (TextView) root.findViewById(R.id.exchange_money_tv);
//        mCashInMoneyCb = (CheckBox) root.findViewById(R.id.cb_exchange_money_tv);
        mRoot = (RelativeLayout) root.findViewById(R.id.rl_root);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final CashInOptionEntity model, final int position) {
        Context context = mBuyCoinsTv.getContext();
        mBuyCoinsTv.setText(context.getString(R.string.e_coin_count_rear, model.getEcoin()));
        DecimalFormat df=new DecimalFormat("##.##");
        mPriceTv.setText(context.getString(R.string.price, df.format((float)model.getRmb() / (float)100)));
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                if (mOnclickViewListener != null) {
                    mOnclickViewListener.onCheckedChange(position);
                }
            }
        });
        mRoot.setSelected(model.isChecked());
//        mCashInMoneyCb.setText(context.getString(R.string.cash_count_simple, model.getRmb() / 100));
//        if (model.isChecked()) {
//            mCashInMoneyCb.setChecked(true);
//        } else {
//            mCashInMoneyCb.setChecked(false);
//        }
//        mCashInMoneyCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                mOnclickViewListener.onCheckedChange(position);
//                Logger.d(TAG, "onCheckedChanged: "+position+"   ischecked="+isChecked);
//            }
//        });
    }

}
