/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.bean.pay.CashInOptionEntity;
import com.hooview.app.adapter.CashInAmountAdapter;

public class CashInCustomEndAdapter implements AdapterItem<CashInOptionEntity> {
    private Button mConfirmBt;
    private TextView mAssetsTipTv;
    private CashInAmountAdapter.OnConfirmClickListener mOnclickViewListener;

    public CashInCustomEndAdapter(CashInAmountAdapter.OnConfirmClickListener listener) {
        this.mOnclickViewListener = listener;
    }
   private View.OnClickListener myOnClickListener = new View.OnClickListener() {
       @Override public void onClick(View v) {
           switch (v.getId()){
               case com.hooview.app.R.id.cash_in_confirm_btn:
                   mOnclickViewListener.confirm();
                   break;
               case com.hooview.app.R.id.cash_in_tip_tv:
                   mOnclickViewListener.explain();
                   break;
           }
       }
   };
    @Override
    public int getLayoutResId() {
        return com.hooview.app.R.layout.item_cash_in_coins_confirm;
    }

    @Override
    public void onBindViews(View root) {
        mConfirmBt = (Button) root.findViewById(com.hooview.app.R.id.cash_in_confirm_btn);
        mAssetsTipTv = (TextView) root.findViewById(com.hooview.app.R.id.cash_in_tip_tv);
    }

    @Override
    public void onSetViews() {
        mConfirmBt.setOnClickListener(myOnClickListener);
        mAssetsTipTv.setOnClickListener(myOnClickListener);
    }

    @Override
    public void onUpdateViews(final CashInOptionEntity model, final int position) {

    }
}


