package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.helper.CollectHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

public class SearchStockItem implements AdapterItem<SearchStockModel.DataEntity> {
    private Context mContext;
    private TextView mTvName;
    private TextView mTcCode;
    private ImageButton mIvAdd;
    private SearchStockModel.DataEntity mStockModel;
    private boolean hasAddBtn;

    public SearchStockItem(Context mContext, boolean hasAddBtn) {
        this.mContext = mContext;
        this.hasAddBtn = hasAddBtn;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_seach_stock;
    }

    @Override
    public void onBindViews(View root) {
        mTvName = (TextView) root.findViewById(R.id.tv_name);
        mTcCode = (TextView) root.findViewById(R.id.tc_code);
        mIvAdd = (ImageButton) root.findViewById(R.id.iv_add);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStockModel != null) {
                    //// TODO: 2017/1/1  无法区分指数和股票
                    Utils.showStockDetail(mContext, mStockModel.getName(), mStockModel.getSymbol(), true);
                }
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final SearchStockModel.DataEntity model, int position) {
        if (model != null) {
            mStockModel = model;
            mTvName.setText(model.getName());
            mTcCode.setText(model.getSymbol());
            mIvAdd.setVisibility(hasAddBtn ? View.VISIBLE : View.GONE);
            mIvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collectStock(mContext, model.getSymbol());
                }
            });
        }
    }

    private void collectStock(final Context context, String code) {
        if (TextUtils.isEmpty(code)) {
            return;
        }
        CollectHelper.collectStock(context, code,
                new MyRequestCallBack<String>() {

                    @Override
                    public void onSuccess(String result) {
                        SingleToast.show(context, "添加自选成功");
                        EventBus.getDefault().post(new MarketRefreshEvent());
                    }

                    @Override
                    public void onFailure(String msg) {

                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                    }
                });

    }

}
