/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.recycler;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.adapter.CommonRcvAdapter;

import com.hooview.app.adapter.item.PayRecordRcvAdapterItem;
import com.hooview.app.bean.pay.PayRecordListEntity;

public class PayCommonRcvAdapter extends CommonRcvAdapter<PayRecordListEntity> {
    private Context mContext;
    private String mRecordType;

    public PayCommonRcvAdapter(Context context, List<PayRecordListEntity> data, String recordType) {
        super(data);
        mContext = context;
        this.mRecordType = recordType;

    }

    @NonNull @Override
    public AdapterItem<PayRecordListEntity> getItemView(Object type) {
        return new PayRecordRcvAdapterItem(mContext, mRecordType);
    }
}
