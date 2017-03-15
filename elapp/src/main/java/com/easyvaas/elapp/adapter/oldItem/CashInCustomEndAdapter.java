/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.pay.CashInOptionEntity;
import com.easyvaas.elapp.adapter.CashInAmountAdapter;

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
               case R.id.cash_in_confirm_btn:
                   mOnclickViewListener.confirm();
                   break;
               case R.id.cash_in_tip_tv:
                   mOnclickViewListener.explain();
                   break;
           }
       }
   };
    @Override
    public int getLayoutResId() {
        return R.layout.item_cash_in_coins_confirm;
    }

    @Override
    public void onBindViews(View root) {
        mConfirmBt = (Button) root.findViewById(R.id.cash_in_confirm_btn);
        mAssetsTipTv = (TextView) root.findViewById(R.id.cash_in_tip_tv);
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


