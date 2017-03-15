package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.hooview.app.R;

public class LiveStockAdapterItem implements AdapterItem<SearchStockModel.DataEntity> {
    private TextView mTvName;
    private TextView mTvCode;
    private Context mContext;

    public LiveStockAdapterItem(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_live_stock;
    }

    @Override
    public void onBindViews(View root) {
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTvCode = (TextView) root.findViewById(R.id.tv_code);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(SearchStockModel.DataEntity model, int position) {
        if (model != null) {
            mTvCode.setText(model.getSymbol());
            mTvName.setText(model.getName());
        }
    }
}
