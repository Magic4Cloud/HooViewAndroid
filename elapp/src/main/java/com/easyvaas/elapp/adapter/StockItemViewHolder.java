package com.easyvaas.elapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;

public class StockItemViewHolder extends RecyclerView.ViewHolder {
    private TextView mTvTag;
    private TextView mTvStockName;
    private TextView mTvStockNumber;
    private TextView mTvPrice;
    private TextView mTvPercent;
    private View viewDivider;

    public StockItemViewHolder(View itemView) {
        super(itemView);
        mTvTag = (TextView) itemView.findViewById(R.id.tv_tag);
        mTvStockName = (TextView) itemView.findViewById(R.id.tv_stock_name);
        mTvStockNumber = (TextView) itemView.findViewById(R.id.tv_stock_number);
        mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        mTvPercent = (TextView) itemView.findViewById(R.id.tv_percent);
        viewDivider = itemView.findViewById(R.id.view_divider);
    }

    public void setStockModel(final StockModel stockModel) {
        if (stockModel != null) {
            mTvStockName.setText(stockModel.getName());
            mTvStockNumber.setText(stockModel.getSymbol());
            mTvPrice.setText(stockModel.getClose() + "");
            mTvPercent.setText(StringUtil.getStockPercent(stockModel.getChangepercent()));
            if (stockModel.getRank() >= 0) {
                mTvTag.setVisibility(View.VISIBLE);
                mTvTag.setText("" + (stockModel.getRank() + 1));
            } else {
                mTvTag.setVisibility(View.INVISIBLE);
            }
            //通view的select属性来控制颜色
            if (stockModel.getChangepercent() >= 0) {
                this.mTvPrice.setSelected(true);
                this.mTvPercent.setSelected(true);
                this.mTvTag.setSelected(true);
            } else {
                this.mTvPercent.setSelected(false);
                this.mTvPrice.setSelected(false);
                this.mTvTag.setSelected(false);
            }
        }
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showStockDetail(v.getContext(), stockModel.getName(), stockModel.getSymbol() + "", true);
            }
        });
    }

    public void setStockModel(final StockListModel.StockModel stockModel, boolean isEnd) {
        if (stockModel != null) {
            mTvStockName.setText(stockModel.getName());
            if(TextUtils.isEmpty(stockModel.getSymbol())){
                mTvStockNumber.setText(stockModel.getSymbol());
            }else{
                mTvStockNumber.setText(stockModel.getSymbol());
            }
            mTvPrice.setText(stockModel.getOpen()+ "");
            mTvPercent.setText(StringUtil.getStockPercent(stockModel.getHigh()-stockModel.getLow()));
            mTvTag.setVisibility(View.INVISIBLE);
            //通view的select属性来控制颜色
            mTvPercent.setTextColor(mTvPercent.getContext().getResources().getColor(R.color.white));
            if (stockModel.getPercent() >= 0) {
                this.mTvPercent.setBackgroundResource(R.drawable.bg_stock_up_shape);
                this.mTvPrice.setSelected(true);
                this.mTvPercent.setSelected(true);
                this.mTvTag.setSelected(true);
            } else {
                this.mTvPercent.setBackgroundResource(R.drawable.bg_stock_down_shape);
                this.mTvPrice.setSelected(false);
                this.mTvTag.setSelected(false);
            }
            viewDivider.setVisibility(isEnd ? View.GONE : View.VISIBLE);
        }
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showStockDetail(v.getContext(), stockModel.getName(), TextUtils.isEmpty(stockModel.getSymbol())?stockModel.getSymbol():stockModel.getSymbol(), true);
            }
        });
    }
}
