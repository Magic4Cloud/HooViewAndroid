package com.easyvaas.elapp.ui.market;

import android.view.View;

import com.easyvaas.elapp.adapter.recycler.SelectStockListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.ui.search.SearchStockActivity;
import com.hooview.app.R;

import butterknife.OnClick;

/**
 * Date    2017/4/10
 * Author  xiaomao
 * 行情--自选
 */
public class MarketOptionalFragment extends BaseListFragment {

    private SelectStockListAdapter mAdapter;
    private StockListModel mStockListModel;

    @Override
    public void iniView(View view) {
        mAdapter = new SelectStockListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        loadData();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @OnClick(R.id.tv_add_select)
    public void onAddOptionalStock() {
        SearchStockActivity.start(getContext());
    }

    @Override
    public void onOperation() {
        EditMySelectedStockActivity.start(getContext(), mStockListModel);
    }

    /**
     * 从网络上得到自选股的列表（会获取到全部的自选股， 没有分类）.没有加载更多
     */
    private void loadData() {
        HooviewApiHelper.getInstance().getUserStockList(EVApplication.getUser().getName(), new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                mSwipeRefreshLayout.setRefreshing(false);
                mStockListModel = result;
                if (result != null && result.getData() != null && result.getData().size() > 0) {
                    mAdapter.setStockListModel(result.getData());
                    hideWithData();
                } else {
                    showWithoutData();
                }
            }

            @Override
            public void onFailure(String msg) {
                mSwipeRefreshLayout.setRefreshing(false);
                showWithoutData();
            }

            @Override
            public void onError(String errorInfo) {
                mSwipeRefreshLayout.setRefreshing(false);
                showWithoutData();
            }
        });
    }

    private void showWithoutData() {
        showEmptyView("您还没有添加自选噢");
        mTvAddSelect.setVisibility(View.VISIBLE);
    }

    private void hideWithData() {
        hideEmptyView();
        showOperationView(R.drawable.logo);
    }

    public static MarketOptionalFragment newInstance() {
        return new MarketOptionalFragment();
    }

}
