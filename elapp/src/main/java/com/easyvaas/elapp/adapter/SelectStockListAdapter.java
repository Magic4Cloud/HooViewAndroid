package com.easyvaas.elapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.ExponentCellView;
import com.hooview.app.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guoliuya on 2017/2/28.
 */

public class SelectStockListAdapter extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_EXPONENT = 1;
    private static final int ITEM_TYPE_STOCK = 2;
    private static final int ITEM_TYPE_TITLE = 3;
    private StockListModel upsAndDownsListModel;
    private MarketExponentModel exponentListModel;
    private LinkedList datas;

    public SelectStockListAdapter() {
        datas = new LinkedList();
        exponentListModel = new MarketExponentModel();
    }

    public void setStockListModel(StockListModel upsAndDownsListModel) {
        this.upsAndDownsListModel = upsAndDownsListModel;
        datas.clear();
        datas.add(new SelectStockListAdapter.TitleModel(false));
        for (int i = 0; i < upsAndDownsListModel.getData().size(); i++) {
            if (i >= 0 && i < 3) {
                upsAndDownsListModel.getData().get(i).setRank(i);
            }
        }
        datas.addAll(upsAndDownsListModel.getData());
        notifyDataSetChanged();
    }

    public void setStockListModel(List<StockListModel.StockModel> upsAndDownsListModel) {
        datas.clear();
        datas.add(new SelectStockListAdapter.TitleModel(false));
        for (int i = 0; i < upsAndDownsListModel.size(); i++) {
            if (i >= 0 && i < 3) {
                upsAndDownsListModel.get(i).setRank(i);
            }
        }
        datas.addAll(upsAndDownsListModel);
        notifyDataSetChanged();
    }

    public void setExponentListModel(MarketExponentModel listModel) {
        this.exponentListModel = listModel;
        if (datas.size() > 0 && (datas.get(0) instanceof MarketExponentModel)) {
            datas.remove(0);
        }
        datas.addFirst(listModel);
        notifyItemChanged(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM_TYPE_EXPONENT:
                viewHolder = new SelectStockListAdapter.ExponentViewHolder(
                        layoutInflater.inflate(R.layout.item_market_list_exponent, parent, false));
                break;
            case ITEM_TYPE_STOCK:
                viewHolder = new StockItemViewHolder(
                        layoutInflater.inflate(R.layout.item_market_optional_stock, parent, false));
                break;
            case ITEM_TYPE_TITLE:
                viewHolder = new SelectStockListAdapter.TitleViewHolder(
                        layoutInflater.inflate(R.layout.item_market_select_title, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = datas.get(position);
        if (holder instanceof StockItemViewHolder && object instanceof StockListModel.StockModel) {
            ((StockItemViewHolder) holder).setStockModel((StockListModel.StockModel) object, position == datas.size() - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = datas.get(position);
        if (obj instanceof MarketExponentModel) {
            return ITEM_TYPE_EXPONENT;
        } else if (obj instanceof SelectStockListAdapter.TitleModel) {
            return ITEM_TYPE_TITLE;
        } else {
            return ITEM_TYPE_STOCK;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class ExponentViewHolder extends RecyclerView.ViewHolder {
        private List<ExponentCellView> exponentCellViews = new ArrayList<>(3);

        ExponentViewHolder(View itemView) {
            super(itemView);
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent1));
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent2));
            exponentCellViews.add((ExponentCellView) itemView.findViewById(R.id.exponent3));
        }

        void setExponentModel(MarketExponentModel listModel) {
            if (listModel == null || listModel.getData() == null) {
                return;
            }
            for (int i = 0; i < 3; i++) {
                final MarketExponentModel.ExponentEntity model = listModel.getData().getHk().get(i);
                ExponentCellView cellView = exponentCellViews.get(i);
                String percentStr = cellView.getContext().getString(R.string.exponent_percent);
                percentStr = String.format(percentStr,
                        (model.getClose() - model.getPreclose()), model.getChangepercent());
                String number = new DecimalFormat(".##").format(model.getClose());
                cellView.updateData(model.getName(), number, percentStr);
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

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        TitleViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }

        public void setTitle(boolean isUP) {
            if (isUP) {
                tvTitle.setText(R.string.up_list_title);
            } else {
                tvTitle.setText(R.string.down_list_title);
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


