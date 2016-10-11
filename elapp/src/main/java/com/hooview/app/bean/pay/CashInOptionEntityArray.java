/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.pay;

import java.util.List;

import com.hooview.app.bean.BaseEntityArray;

public class CashInOptionEntityArray extends BaseEntityArray {

    private List<CashInOptionEntity> optionlist;

    public List<CashInOptionEntity> getOptionlist() {
        return optionlist;
    }

    public void setOptionlist(List<CashInOptionEntity> optionlist) {
        this.optionlist = optionlist;
    }
}
