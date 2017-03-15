package com.easyvaas.common.emoji.view.I;

import com.easyvaas.common.emoji.bean.EmoticonBean;

public interface IView {
    void onItemClick(EmoticonBean bean);

    void onItemDisplay(EmoticonBean bean);

    void onPageChangeTo(int position);
}
