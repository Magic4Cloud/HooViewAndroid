package com.easyvaas.elapp.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 适配器：行情----全球
 */
public class MarketGlobalListAdapter extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_STOCK = 1;
    private static final int ITEM_TYPE_TITLE = 2;
    private Context mContext;
    private LinkedList mData;

    public MarketGlobalListAdapter(Context context) {
        mContext = context;
        mData = new LinkedList();
    }

    /**
     * 设置数据
     */
    public void setData(List<MarketGlobalModel> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mData.clear();
        for (int i = 0; i < list.size(); i++) {
            MarketGlobalModel marketGlobalModel = list.get(i);
            if (hasData(marketGlobalModel)) {
                String title = marketGlobalModel.getName();
                if (TextUtils.isEmpty(title)) {
                    if (i == 0) {
                        title = "常用";
                    } else if (i == 1) {
                        title = "欧美指数";
                    } else if (i == 2) {
                        title = "亚太指数";
                    }
                }
                mData.add(new TitleModel(title));
                mData.addAll(marketGlobalModel.getIndex());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM_TYPE_STOCK:
                viewHolder = new StockViewHolder(layoutInflater.inflate(R.layout.item_market_global_stock, parent, false));
                break;
            case ITEM_TYPE_TITLE:
                viewHolder = new TitleViewHolder(layoutInflater.inflate(R.layout.item_market_list_title, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = mData.get(position);
        if (holder instanceof StockViewHolder && object instanceof StockModel) {
            ((StockViewHolder) holder).setStockModel((StockModel) object,
                    (position + 1 < mData.size() - 1) && mData.get(position + 1) instanceof TitleModel ? View.GONE : View.VISIBLE);
        } else if (holder instanceof TitleViewHolder && object instanceof TitleModel) {
            ((TitleViewHolder) holder).setTitle(((TitleModel) object).title, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = mData.get(position);
        if (obj instanceof TitleModel) {
            return ITEM_TYPE_TITLE;
        } else {
            return ITEM_TYPE_STOCK;
        }
    }

    /**
     * 判断是否有数据
     *
     * @param marketGlobalModel
     * @return
     */
    private boolean hasData(MarketGlobalModel marketGlobalModel) {
        return marketGlobalModel != null
                && marketGlobalModel.getIndex() != null
                && marketGlobalModel.getIndex().size() > 0;
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    class StockViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_national_flag)
        ImageView mIvNationalFlag;
        @BindView(R.id.tv_stock_name)
        TextView mTvStockName;
        @BindView(R.id.tv_price)
        TextView mTvPrice;
        @BindView(R.id.tv_percent)
        TextView mTvPercent;
        @BindView(R.id.view_divider)
        View viewDivider;

        public StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setStockModel(final StockModel stockModel, int status) {
            if (stockModel != null) {
                Picasso.with(mContext).load(stockModel.getNationalFlag()).placeholder(R.drawable.account_bitmap_list).into(mIvNationalFlag);
                mTvStockName.setText(stockModel.getName());
                mTvPrice.setText(stockModel.getClose() + "");
                mTvPercent.setText(StringUtil.getStockPercent(stockModel.getChangepercent()));
                //通view的select属性来控制颜色
                if (stockModel.getChangepercent() >= 0) {
                    this.mTvPrice.setSelected(true);
                    this.mTvPercent.setSelected(true);
                } else {
                    this.mTvPercent.setSelected(false);
                    this.mTvPrice.setSelected(false);
                }
                viewDivider.setVisibility(status);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.showStockDetail(v.getContext(), stockModel.getName(), stockModel.getSymbol() + "", true);
                    }
                });
            }
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_top_blank)
        View mViewBlank;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.view_exponent)
        View mViewExponent;
        @BindView(R.id.market_list_title_divider)
        View mViewDivider;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTitle(String title, int position) {
            if (position == 0) {
                mViewBlank.setVisibility(View.GONE);
            } else {
                mViewBlank.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(title);
            mViewExponent.setBackgroundColor(mContext.getResources().getColor(R.color.view_exponent_up));
            mViewDivider.setVisibility(View.GONE);
        }
    }

    private class TitleModel {
        String title;

        TitleModel(String title) {
            this.title = title;
        }
    }
}
