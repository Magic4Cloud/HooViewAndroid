package com.easyvaas.elapp.adapter.item;


import android.view.View;
import android.widget.ImageView;

import com.easyvaas.common.adapter.AdapterItem;
import com.hooview.app.R;

public class NewsHeaderAdapterItem implements AdapterItem {
    private int headerRes;
    private ImageView headerView;

    public NewsHeaderAdapterItem(int headerRes) {
        this.headerRes = headerRes;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_new_common_header;
    }

    @Override
    public void onBindViews(View root) {
        headerView = (ImageView) root.findViewById(R.id.iv_header);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        headerView.setImageResource(headerRes);
    }
}
