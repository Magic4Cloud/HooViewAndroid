/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter;

import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.oldItem.CashInAmountAdapterItem;
import com.easyvaas.elapp.adapter.oldItem.CashInCustomEndAdapter;
import com.easyvaas.elapp.bean.pay.CashInOptionEntity;

import java.util.List;

public class CashInAmountAdapter extends CommonRcvAdapter<CashInOptionEntity> {
    private static final Object ITEM_TYPE_END = 2;

    private OnConfirmClickListener mOnEndClickViewListener;
    private OnclickViewListener mOnclickViewListener = new OnclickViewListener() {
        @Override
        public void onCheckedChange(int position) {
            setSelect(position);
        }
    };
    private List<CashInOptionEntity> data;

    private OnItemSelectListener mOnItemSelectListener;
    private int selectIndex = -1;

    public CashInAmountAdapter(List<CashInOptionEntity> data) {
        super(data);
        this.data = data;
    }

    @NonNull
    @Override
    public AdapterItem<CashInOptionEntity> getItemView(Object type) {
        if (type == ITEM_TYPE_END) {
            return new CashInCustomEndAdapter(mOnEndClickViewListener);
        }
        return new CashInAmountAdapterItem(mOnclickViewListener);
    }

    @Override
    public Object getItemViewType(CashInOptionEntity cashInOptionEntity) {
        if (cashInOptionEntity.getPinned() == CashInOptionEntity.PINNED_END) {
            return ITEM_TYPE_END;
        }
        return null;
    }

    public void setOnEndClickViewListener(OnConfirmClickListener onclickViewListener) {
        this.mOnEndClickViewListener = onclickViewListener;
    }
//
//    public void setOnclickViewListener(OnclickViewListener onclickViewListener) {
//        this.mOnclickViewListener = onclickViewListener;
//    }


    public OnItemSelectListener getOnItemSelectListener() {
        return mOnItemSelectListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void setSelect(int selectIndex) {
        for (int i = 0; i < data.size(); i++) {
            if (i == selectIndex) {
                data.get(i).setIsChecked(true);
            } else {
                data.get(i).setIsChecked(false);
            }
        }
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onItemSelect(selectIndex);
        }
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public interface OnclickViewListener {
        void onCheckedChange(int position);
    }

    public interface OnItemSelectListener {
        void onItemSelect(int position);
    }


    public interface OnConfirmClickListener {
        void confirm();

        void explain();
    }
}
