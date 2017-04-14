package com.easyvaas.elapp.ui.market;

import android.view.View;

import com.easyvaas.elapp.adapter.market.SelectStockListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.easyvaas.elapp.ui.search.SearchStockActivity;
import com.hooview.app.R;

import butterknife.OnClick;

/**
 * Date    2017/4/10
 * Author  xiaomao
 * 行情---自选
 */
public class MarketOptionalFragment extends BaseListLazyFragment {

    private SelectStockListAdapter mAdapter;
    private StockListModel mStockListModel;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        mAdapter = new SelectStockListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onVisibleChanged(boolean isVisible) {
        if (isVisible && !mIsLoad) {
            loadData();
            mIsLoad = true;
        }
        if (!isVisible) {
            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
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
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        HooviewApiHelper.getInstance().getUserStockList(EVApplication.getUser().getName(), new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                mStockListModel = result;
                if (result != null && result.getData() != null && result.getData().size() > 0) {
                    mAdapter.setStockListModel(result.getData());
                    hideWithData();
                } else {
                    showWithoutData();
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                showWithoutData();
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(String errorInfo) {
                showWithoutData();
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void showWithoutData() {
        showEmptyView("您还没有添加自选噢");
        if (mTvAddSelect != null) {
            mTvAddSelect.setVisibility(View.VISIBLE);
        }
    }

    private void hideWithData() {
        hideEmptyView();
        showOperationView(R.drawable.market_edit_xm);
    }

    public static MarketOptionalFragment newInstance() {
        return new MarketOptionalFragment();
    }

}
