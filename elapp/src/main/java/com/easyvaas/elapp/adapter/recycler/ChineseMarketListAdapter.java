package com.easyvaas.elapp.adapter.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyvaas.elapp.bean.market.*;
import com.hooview.app.R;
import com.easyvaas.elapp.adapter.StockItemViewHolder;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.ExponentCellView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 沪深
 */
public class ChineseMarketListAdapter extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_EXPONENT = 1;
    private static final int ITEM_TYPE_STOCK = 2;
    private static final int ITEM_TYPE_TITLE = 3;
    private UpsAndDownsDataModel upsAndDownsListModel;
    private ExponentListNewModel exponentListModel;
    private LinkedList datas;

    public ChineseMarketListAdapter() {
        datas = new LinkedList();
        exponentListModel = new ExponentListNewModel();
    }

    public void setUpsAndDownsListModel(UpsAndDownsDataModel upsAndDownsListModel) {
        this.upsAndDownsListModel = upsAndDownsListModel;
        datas.clear();
        datas.add(exponentListModel);
        if (hasHead()) {
            datas.add(new TitleModel(true));
            for (int i = 0; i < upsAndDownsListModel.getData().getHead().size(); i++) {
                if (i >= 0 && i < 3) {
                    upsAndDownsListModel.getData().getHead().get(i).setRank(i);
                }
            }
            datas.addAll(upsAndDownsListModel.getData().getHead());
        }
        if (hasTail()) {
            datas.add(new TitleModel(false));
            for (int i = 0; i < upsAndDownsListModel.getData().getTail().size(); i++) {
                if (i >= 0 && i < 3) {
                    upsAndDownsListModel.getData().getTail().get(i).setRank(i);
                }
            }
            datas.addAll(upsAndDownsListModel.getData().getTail());
        }
        notifyDataSetChanged();
    }

    public void setExponentListModel(ExponentListNewModel listModel) {
        this.exponentListModel = listModel;
        if (datas.size() > 0 && (datas.get(0) instanceof ExponentListNewModel)) {
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
                viewHolder = new ExponentViewHolder(layoutInflater.inflate(R.layout.item_market_list_exponent, parent, false));
                break;
            case ITEM_TYPE_STOCK:
                viewHolder = new StockItemViewHolder(
                        layoutInflater.inflate(R.layout.item_market_list_stock, parent, false));
                break;
            case ITEM_TYPE_TITLE:
                viewHolder = new TitleViewHolder(
                        layoutInflater.inflate(R.layout.item_market_list_title, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = datas.get(position);
        if (holder instanceof ExponentViewHolder) {
            ((ExponentViewHolder) holder).setExponentModel((ExponentListNewModel) object);
        } else if (holder instanceof StockItemViewHolder && object instanceof StockModel) {
            ((StockItemViewHolder) holder).setStockModel((StockModel) object,
                    (position + 1 < datas.size() - 1) && datas.get(position + 1) instanceof TitleModel ? View.GONE : View.VISIBLE);
        } else if (holder instanceof TitleViewHolder && object instanceof TitleModel) {
            ((TitleViewHolder) holder).setTitle(((TitleModel) object).isUP);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = datas.get(position);
        if (obj instanceof ExponentListNewModel) {
            return ITEM_TYPE_EXPONENT;
        } else if (obj instanceof TitleModel) {
            return ITEM_TYPE_TITLE;
        } else {
            return ITEM_TYPE_STOCK;
        }
    }

    //判断是否有涨幅榜
    private boolean hasHead() {
        return upsAndDownsListModel != null && upsAndDownsListModel.getData().getHead() != null
                && upsAndDownsListModel.getData().getHead().size() != 0;
    }

    //判断是否有跌幅榜
    private boolean hasTail() {
        return upsAndDownsListModel != null && upsAndDownsListModel.getData().getTail() != null
                && upsAndDownsListModel.getData().getTail().size() != 0;
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
