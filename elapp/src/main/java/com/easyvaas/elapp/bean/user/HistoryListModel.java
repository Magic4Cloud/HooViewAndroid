package com.easyvaas.elapp.bean.user;


import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class HistoryListModel {
    private String historylist;

    public String getHistorylist() {
        return historylist;
    }

    public void setHistorylist(String historylist) {
        this.historylist = historylist;
    }

    public List<String> getCodeList() {
        if (TextUtils.isEmpty(historylist)) {
            return null;
        }
        return Arrays.asList(historylist.split(","));
    }
}
