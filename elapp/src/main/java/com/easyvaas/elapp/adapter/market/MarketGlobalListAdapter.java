package com.easyvaas.elapp.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lib.adapter.BaseAdapter;
import lib.adapter.expand.StickyRecyclerHeadersAdapter;

/**
 * 适配器：行情----全球
 */
public class MarketGlobalListAdapter extends BaseAdapter<StockModel, MarketGlobalListAdapter.StockViewHolder>
        implements StickyRecyclerHeadersAdapter<MarketGlobalListAdapter.TitleViewHolder> {

    private Context mContext;

    public MarketGlobalListAdapter(Context context) {
        mContext = context;
    }

    /**
     * 设置数据
     */
    public void setData(List<StockModel> list) {
        this.addAll(list);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StockViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_global_stock, parent, false));
    }


    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        StockModel model = getItem(position);
        holder.setStockModel(model, model.isLastInCategory());
    }

    /**
     * Get the ID of the header associated with this item.  For example, if your headers group
     * items by their first letter, you could return the character representation of the first letter.
     * Return a value < 0 if the view should not have a header (like, a header view or footer view)
     *
     * @param position
     * @return
     */
    @Override
    public long getHeaderId(int position) {
        return getItem(position).getHeaderId();
    }

    /**
     * Creates a new ViewHolder for a header.  This works the same way onCreateViewHolder in
     * Recycler.Adapter, ViewHolders can be reused for different views.  This is usually a good place
     * to inflate the layout for the header.
     *
     * @param parent
     * @return
     */
    @Override
    public TitleViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_list_title, parent, false));
    }

    /**
     * Binds an existing ViewHolder to the specified adapter position.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindHeaderViewHolder(TitleViewHolder holder, int position) {
        holder.setTitle(getItem(position).getCategory());
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.view_bottom_blank)
        View viewBottomBlank;

        public StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setStockModel(final StockModel stockModel, boolean hide) {
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
                if (hide) {
                    viewDivider.setVisibility(View.GONE);
                    viewBottomBlank.setVisibility(View.VISIBLE);
                } else {
                    viewDivider.setVisibility(View.VISIBLE);
                    viewBottomBlank.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.showStockDetail(v.getContext(), stockModel.getName(), stockModel.getSymbol() + "", true);
                    }
                });
            }
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.view_exponent)
        View mViewExponent;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTitle(String title) {
            tvTitle.setText(title);
            mViewExponent.setBackgroundColor(mContext.getResources().getColor(R.color.view_exponent_up));
        }
    }

}
