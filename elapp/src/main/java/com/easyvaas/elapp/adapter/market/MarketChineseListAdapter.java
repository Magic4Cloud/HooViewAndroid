package com.easyvaas.elapp.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.elapp.adapter.StockItemViewHolder;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.ExponentCellView;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lib.adapter.BaseAdapter;
import lib.adapter.expand.StickyRecyclerHeadersAdapter;

/**
 * Adapter:行情---沪深
 */
public class MarketChineseListAdapter extends BaseAdapter implements StickyRecyclerHeadersAdapter<MarketChineseListAdapter.TitleViewHolder> {
    private static final int ITEM_TYPE_EXPONENT = 1;
    private static final int ITEM_TYPE_STOCK = 2;
    private Context mContext;
    private ExponentListNewModel mListNewModel = new ExponentListNewModel();
    private List<StockModel> mList = new ArrayList<StockModel>();

    public MarketChineseListAdapter(Context context) {
        mContext = context;
    }

    public void setDataHeader(ExponentListNewModel listModel) {
        if (listModel != null) {
            mListNewModel = listModel;
        }
        addAll(mListNewModel, mList);
    }

    public void setDataItem(List<StockModel> list) {
        if (list != null && list.size() > 0) {
            mList = list;
        }
        addAll(mListNewModel, mList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_TYPE_EXPONENT:
                return new MarketChineseListAdapter.ExponentViewHolder(
                        layoutInflater.inflate(R.layout.item_market_list_exponent, parent, false));
            case ITEM_TYPE_STOCK:
                return new StockItemViewHolder(
                        layoutInflater.inflate(R.layout.item_market_list_stock, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = getItem(position);
        if (position == 0 && holder instanceof MarketChineseListAdapter.ExponentViewHolder && object instanceof ExponentListNewModel) {
            ExponentListNewModel model = (ExponentListNewModel) object;
            ((MarketChineseListAdapter.ExponentViewHolder) holder).setExponentModel(model);
        } else if (holder instanceof StockItemViewHolder && object instanceof StockModel) {
            StockModel model = (StockModel) object;
            ((StockItemViewHolder) holder).setStockModel(model, model.isLastInCategory());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object object = getItem(position);
        if (position == 0 && object instanceof ExponentListNewModel) {
            return ITEM_TYPE_EXPONENT;
        } else if (object instanceof StockModel){
            return ITEM_TYPE_STOCK;
        }
        return 0;
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
        if (position == 0 && object instanceof ExponentListNewModel) {
            return -1;
        } else if (object instanceof StockModel){
            return ((StockModel)object).getHeaderId();
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
        holder.setTitle((((StockModel)getItem(position)).isUp()));
    }

    private class ExponentViewHolder extends RecyclerView.ViewHolder {
        private List<ExponentCellView> exponentCellViews = new ArrayList<>(3);

        ExponentViewHolder(View itemView) {
            super(itemView);
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent1));
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent2));
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent3));
        }

        void setExponentModel(ExponentListNewModel listModel) {
            if (listModel == null || listModel.getData() == null) {
                return;
            }
            for (int i = 0; i < 3; i++) {
                final ExponentListNewModel.DataEntity.CnEntity model = listModel.getData().getCn().get(i);
                ExponentCellView cellView = exponentCellViews.get(i);
                String percentStr;
                if (model.getChangepercent() >= 0) {
                    percentStr = cellView.getContext().getString(R.string.exponent_percent_up);
                    percentStr = String.format(percentStr,
                            (model.getClose() - model.getPreclose()), model.getChangepercent());
                } else {
                    percentStr = cellView.getContext().getString(R.string.exponent_percent);
                    percentStr = String.format(percentStr,
                            (model.getClose() - model.getPreclose()), model.getChangepercent());
                }
                String number = new DecimalFormat(".##").format(model.getClose());
                cellView.updateData2(model.getName(), number, percentStr);
                cellView.setCardViewBackground(model.getChangepercent() > 0);
                cellView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.showStockDetail(v.getContext(), model.getName(), model.getSymbol(), false);
                    }
                });
            }
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private View mViewExponent;

        TitleViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mViewExponent = itemView.findViewById(R.id.view_exponent);
        }

        public void setTitle(boolean isUP) {
            if (isUP) {
                tvTitle.setText(R.string.up_list_title);
                mViewExponent.setBackgroundColor(mContext.getResources().getColor(R.color.view_exponent_up));
            } else {
                tvTitle.setText(R.string.down_list_title);
                mViewExponent.setBackgroundColor(mContext.getResources().getColor(R.color.view_exponent_down));
            }
        }
    }

    private class TitleModel {
        boolean isUP;

        TitleModel(boolean isUP) {
            this.isUP = isUP;
        }
    }
}
