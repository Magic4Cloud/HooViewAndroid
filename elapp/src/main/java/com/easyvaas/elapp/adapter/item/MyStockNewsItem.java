package com.easyvaas.elapp.adapter.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.elapp.bean.news.MyStockNewsModel;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.nex3z.flowlayout.FlowLayout;

/**
 * Created by guojun on 2016/12/29 17:46.
 */

public class MyStockNewsItem implements AdapterItem<MyStockNewsModel.NewsEntity> {
    private static final String TAG = "MyStockNewsItem";
    private Context mContext;
    private TextView mTvTime;
    private TextView mTvTitle;
    private FlowLayout mLlStocksContainer;
    private MyStockNewsModel.NewsEntity model;

    public MyStockNewsItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_news_my_stock;
    }

    @Override
    public void onBindViews(View root) {
        mTvTime = (TextView) root.findViewById(R.id.tv_time);
        mTvTitle = (TextView) root.findViewById(R.id.tv_title);
        mLlStocksContainer = (FlowLayout) root.findViewById(R.id.ll_stocks);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showNewsDetail(mContext, model.getTitle(), model.getId() + "");
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(MyStockNewsModel.NewsEntity model, int position) {
        if (model != null) {
            this.model = model;
            mTvTitle.setText(model.getTitle());
            mTvTime.setText(DateTimeUtil.getSimpleTime(mContext, model.getTime()));
            setupStockView(model);
        }
    }

    public void setupStockView(MyStockNewsModel.NewsEntity model) {
        mLlStocksContainer.removeAllViews();
        if (model.getStocks() != null) {
            MyStockNewsModel.StocksEntity stocksEntity;
            for (int i = 0; i < model.getStocks().size(); i++) {
                stocksEntity = model.getStocks().get(i);
                if (stocksEntity != null)
                {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.view_my_stock_news_stock, null);
                    TextView tvStock = (TextView) view.findViewById(R.id.tv_name);
                    TextView tvPercent = (TextView) view.findViewById(R.id.tv_percent);
                    tvStock.setText(stocksEntity.getName());
                    double percent = Double.parseDouble(stocksEntity.getPercent());
                    tvPercent.setText(StringUtil.getStockPercent(percent));
                    tvPercent.setSelected(percent > 0);
                    tvStock.setSelected(percent > 0);
                    mLlStocksContainer.addView(view);
                }

            }
        }
    }
}
