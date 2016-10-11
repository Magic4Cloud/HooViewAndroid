/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.hooview.app.bean.pay.PayRecordListEntity;
import com.hooview.app.activity.pay.PayRecordListActivity;

public class PayRecordRcvAdapterItem implements AdapterItem<PayRecordListEntity> {
    private Context mContext;

    private TextView mDescriptionTv;
    private TextView mTimeTv;
    private TextView mBarleyTv;
    private String mRecordType;

    public PayRecordRcvAdapterItem(Context context, String recordType) {
        mContext = context;
        this.mRecordType = recordType;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_pay_record;
    }

    @Override
    public void onBindViews(View root) {
        mDescriptionTv = (TextView) root.findViewById(R.id.description_tv);
        mTimeTv = (TextView) root.findViewById(R.id.time_tv);
        mBarleyTv = (TextView) root.findViewById(R.id.barley_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final PayRecordListEntity model, int position) {
        if (PayRecordListActivity.TYPE_CASH_OUT.equals(mRecordType)) {
            mDescriptionTv.setText(model.getDescription());
            mTimeTv.setText(model.getTime());
            mBarleyTv.setText("");
        } else if (PayRecordListActivity.TYPE_CASH_IN.equals(mRecordType)) {
            mDescriptionTv.setText(model.getDescription());
            mTimeTv.setText(model.getTime());
            String eCoin = model.getEcoin()+"";
            SpannableString info = new SpannableString("+" + mContext.getString(R.string.e_coin_count_rear, model.getEcoin()));
            info.setSpan(new AbsoluteSizeSpan(66), 0, eCoin.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBarleyTv.setText(info);
        }
    }
}
