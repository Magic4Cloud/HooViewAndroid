/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter;

import java.util.List;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.CashInAmountAdapterItem;
import com.hooview.app.adapter.item.CashInCustomEndAdapter;
import com.hooview.app.bean.pay.CashInOptionEntity;

public class CashInAmountAdapter extends CommonRcvAdapter<CashInOptionEntity> {
    private static final Object ITEM_TYPE_END = 2;

    private OnConfirmClickListener mOnEndClickViewListener;
    private OnclickViewListener mOnclickViewListener;

    public CashInAmountAdapter(List<CashInOptionEntity> data) {
        super(data);
    }

    @NonNull
    @Override
    public AdapterItem<CashInOptionEntity> getItemView(Object type) {
        if(type == ITEM_TYPE_END){
            return new CashInCustomEndAdapter(mOnEndClickViewListener);
        }
        return new CashInAmountAdapterItem(mOnclickViewListener);
    }

    @Override
    public Object getItemViewType(CashInOptionEntity cashInOptionEntity) {
        if(cashInOptionEntity.getPinned() == CashInOptionEntity.PINNED_END){
            return ITEM_TYPE_END;
        }
        return null;
    }

    public void setOnEndClickViewListener(OnConfirmClickListener onclickViewListener) {
        this.mOnEndClickViewListener = onclickViewListener;
    }

    public void setOnclickViewListener(OnclickViewListener onclickViewListener) {
        this.mOnclickViewListener = onclickViewListener;
    }

    public interface OnclickViewListener {
        void onCheckedChange(int position);
    }

    public interface OnConfirmClickListener {
        void confirm();
        void explain();
    }
}
