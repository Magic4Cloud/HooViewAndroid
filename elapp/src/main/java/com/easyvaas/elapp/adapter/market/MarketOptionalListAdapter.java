package com.easyvaas.elapp.adapter.market;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.elapp.adapter.StockItemViewHolder;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.hooview.app.R;

import java.util.List;

import lib.adapter.BaseAdapter;
import lib.adapter.expand.StickyRecyclerHeadersAdapter;

/**
 * Date    2017/4/10
 * Author  xiaomao
 * Adapter: 行情---自选
 */
public class MarketOptionalListAdapter extends BaseAdapter implements StickyRecyclerHeadersAdapter<MarketOptionalListAdapter.TitleViewHolder> {

    public MarketOptionalListAdapter() {
    }

    public void setData(List<StockListModel.StockModel> list) {
        addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StockItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_optional_stock, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StockListModel.StockModel model = (StockListModel.StockModel) getItem(position);
        ((StockItemViewHolder) holder).setStockModel(model, position == getItemCount() - 1);
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
        Object object = getItem(position);
        if (object instanceof StockListModel.StockModel) {
            return ((StockListModel.StockModel)object).getHeaderId();
        }
        return 0;
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
        return new TitleViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market_select_title, parent, false));
    }

    /**
     * Binds an existing ViewHolder to the specified adapter position.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindHeaderViewHolder(TitleViewHolder holder, int position) {

    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        TitleViewHolder(View itemView) {
            super(itemView);
        }

    }

}


