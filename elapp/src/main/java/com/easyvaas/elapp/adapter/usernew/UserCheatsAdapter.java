package com.easyvaas.elapp.adapter.usernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.NumberUtil;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date    2017/4/21
 * Author  xiaomao
 * 秘籍
 */
public class UserCheatsAdapter extends MyBaseAdapter<CheatsListModel.CheatsModel> {

    private boolean mBuy = false;

    public UserCheatsAdapter(List<CheatsListModel.CheatsModel> data) {
        super(data);
    }

    public void setBuy(boolean buy) {
        mBuy = buy;
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new CheatsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cheats, null));
    }

    @Override
    protected void initOnItemClickListener() {

    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, CheatsListModel.CheatsModel item) {
        int position = helper.getLayoutPosition();
        if (helper instanceof CheatsViewHolder) {
            ((CheatsViewHolder) helper).setModel(item, position);
        }
    }

    class CheatsViewHolder extends BaseViewHolder {

        @BindView(R.id.item_cheats)
        View mRootView;
        @BindView(R.id.cheats_time)
        TextView mTimeTv;
        @BindView(R.id.cheats_title)
        TextView mTitleTv;
        @BindView(R.id.cheats_introduce)
        TextView mIntroduceTv;
        @BindView(R.id.cheats_price)
        TextView mPriceTv;
        @BindView(R.id.cheats_unit)
        TextView mUnitTv;
        @BindView(R.id.cheats_sale)
        TextView mSaleTv;

        public CheatsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setModel(CheatsListModel.CheatsModel model, int position) {
            if (model != null) {
                // time
                mTimeTv.setText(DateTimeUtil.getCheatsTime(mContext, model.getDate()));
                // title
                mTitleTv.setText(model.getTitle());
                // introduce
                mIntroduceTv.setText(model.getIntroduce());
                // price
                mPriceTv.setText(String.valueOf(model.getPrice()));
                // sale
                if (mBuy) {
                    mSaleTv.setVisibility(View.GONE);
                } else {
                    mSaleTv.setVisibility(View.VISIBLE);
                    mSaleTv.setText(mContext.getString(R.string.sales_count, NumberUtil.format(model.getSalesCount())));
                }
                // click
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2017/4/21
                    }
                });
            }
        }
    }
}
