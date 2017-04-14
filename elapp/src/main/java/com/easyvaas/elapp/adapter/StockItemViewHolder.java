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

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_tag)
    TextView mTvTag;
    @BindView(R.id.tv_stock_name)
    TextView mTvStockName;
    @BindView(R.id.tv_stock_number)
    TextView mTvStockNumber;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_percent)
    TextView mTvPercent;
    @BindView(R.id.view_divider)
    View viewDivider;

    public StockItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setStockModel(final StockModel stockModel, int status) {
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
            viewDivider.setVisibility(status);
        }
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showStockDetail(v.getContext(), stockModel.getName(), stockModel.getSymbol() + "", true);
            }
        });
    }

    public void setStockModel(final StockModel stockModel, boolean hide) {
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
            if (hide) {
                viewDivider.setVisibility(View.GONE);
            } else {
                viewDivider.setVisibility(View.VISIBLE);
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
            mTvStockNumber.setText(stockModel.getSymbol());
            mTvPrice.setText(stockModel.getClose() + "");
            mTvPercent.setText(StringUtil.getStockPercent(stockModel.getPercent()));
//            mTvPercent.setText(StringUtil.getStockPercent(stockModel.getHigh()-stockModel.getLow()));
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
                Utils.showStockDetail(v.getContext(), stockModel.getName(), TextUtils.isEmpty(stockModel.getSymbol()) ? stockModel.getSymbol() : stockModel.getSymbol(), true);
            }
        });
    }
}
