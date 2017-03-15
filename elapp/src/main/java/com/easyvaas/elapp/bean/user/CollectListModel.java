package com.easyvaas.elapp.bean.user;

import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

/**
 * Created by guoliuya on 2017/2/28.
 */

public class CollectListModel {
    private int count;
    private String collectlist;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCollectlist() {
        return collectlist;
    }

    public void setCollectlist(String collectlist) {
        this.collectlist = collectlist;
    }

    public List<String> getCodeList() {
        if (TextUtils.isEmpty(collectlist)) {
            return null;
        }
        return Arrays.asList(collectlist.split(","));
    }
}
