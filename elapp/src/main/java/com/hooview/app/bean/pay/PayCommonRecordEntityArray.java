/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.pay;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class PayCommonRecordEntityArray extends BaseEntityArray {

    private List<PayRecordListEntity> recordlist;

    public void setRecordlist(List<PayRecordListEntity> recordlist) {
        this.recordlist = recordlist;
    }

    public List<PayRecordListEntity> getRecordlist() {
        return recordlist;
    }
}

