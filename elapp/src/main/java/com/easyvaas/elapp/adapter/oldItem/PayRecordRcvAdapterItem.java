/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.pay.PayRecordListEntity;
import com.easyvaas.elapp.activity.pay.PayRecordListActivity;

public class PayRecordRcvAdapterItem implements AdapterItem<PayRecordListEntity> {
    private Context mContext;

    private TextView mDescriptionTv;
    private TextView mTimeTv;
    private TextView mDataTv;
    private TextView mBarleyTv;
    private String mRecordType;

    public PayRecordRcvAdapterItem(Context context, String recordType) {
        mContext = context;
        this.mRecordType = recordType;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_hootview_pay_record;
    }

    @Override
    public void onBindViews(View root) {
        mDescriptionTv = (TextView) root.findViewById(R.id.description_tv);
        mTimeTv = (TextView) root.findViewById(R.id.time_tv);
        mDataTv = (TextView) root.findViewById(R.id.date_tv);
        mBarleyTv = (TextView) root.findViewById(R.id.barley_tv);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final PayRecordListEntity model, int position) {
        mDescriptionTv.setText(model.getDescription());
        mDataTv.setText(TextUtils.isEmpty(model.getTime())?"":model.getTime().substring(0,10));
        mTimeTv.setText(TextUtils.isEmpty(model.getTime())?"":model.getTime().substring(10,model.getTime().length()));
        String eCoin = model.getEcoin() + "";
        SpannableString info = null;
        if (PayRecordListActivity.TYPE_CASH_IN.equals(mRecordType)) {
            info = new SpannableString("" + model.getEcoin());
        } else {
            info = new SpannableString("-" + model.getEcoin());
        }
        info.setSpan(new AbsoluteSizeSpan(66), 0, eCoin.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBarleyTv.setText(info);
    }
}
