package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.search.SearchStockModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;

import static com.easyvaas.elapp.app.EVApplication.isLogin;

public class SearchStockItem implements AdapterItem<SearchStockModel.DataEntity> {
    private Context mContext;
    private TextView mTvName;
    private TextView mTcCode;
    private ImageButton mIvAdd;
    private SearchStockModel.DataEntity mStockModel;
    private boolean hasAddBtn;
    private TextView mtvCom;

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
        mtvCom = (TextView) root.findViewById(R.id.tv_add_complete);
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
            if (model.getCollected() == 1)
            {
                mIvAdd.setVisibility(View.GONE);
                mtvCom.setVisibility(View.VISIBLE);
            }else
            {
                mIvAdd.setVisibility(View.VISIBLE);
                mtvCom.setVisibility(View.GONE);
            }

            mStockModel = model;
            mTvName.setText(model.getName());
            mTcCode.setText(model.getSymbol());
//            mIvAdd.setVisibility(hasAddBtn ? View.VISIBLE : View.GONE);
            mIvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Preferences.getInstance(mContext).isLogin() || !isLogin()) {
                        LoginActivity.start(mContext);
                        return;
                    }
                    collectStock(mContext, model.getSymbol(),model.getCollected());
                    model.setCollected(0);

                }
            });
            mtvCom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collectStock(mContext, model.getSymbol(),model.getCollected());
                    model.setCollected(1);

                }
            });
        }
    }

    private void collectStock(final Context context, String code, final int type) {
        if (TextUtils.isEmpty(code)) {
            return;
        }
        HooviewApiHelper.getInstance().updateStocks(EVApplication.getUser().getName(), code, type == 1?"2":"1", new MyRequestCallBack() {

            @Override
            public void onSuccess(Object result) {
                if (type == 0)
                {
                    SingleToast.show(context, context.getString(R.string.add_stock_success));
                    mIvAdd.setVisibility(View.GONE);
                    mtvCom.setVisibility(View.VISIBLE);
                }else
                {
                    SingleToast.show(context,context.getString(R.string.delect_stock_sucess));
                    mIvAdd.setVisibility(View.VISIBLE);
                    mtvCom.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(String msg) {

            }
        });

//        CollectHelper.collectStock(context, code,
//                new MyRequestCallBack<String>() {
//
//                    @Override
//                    public void onSuccess(String result) {
//                        SingleToast.show(context, "添加自选成功");
//                        EventBus.getDefault().post(new MarketRefreshEvent());
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//
//                    }
//
//                    @Override
//                    public void onError(String errorInfo) {
//                        super.onError(errorInfo);
//                    }
//                });

    }

}
