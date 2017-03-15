/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.pay;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class PayCommonRecordEntityArray extends BaseEntityArray {

    private List<PayRecordListEntity> recordlist;

    public void setRecordlist(List<PayRecordListEntity> recordlist) {
        this.recordlist = recordlist;
    }

    public List<PayRecordListEntity> getRecordlist() {
        return recordlist;
    }
}

