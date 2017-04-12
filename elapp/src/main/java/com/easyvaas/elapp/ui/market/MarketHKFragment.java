package com.easyvaas.elapp.ui.market;

import com.easyvaas.elapp.adapter.recycler.HkMarketListAdapter;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.hooview.app.R;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 行情---港股
 */
public class MarketHKFragment extends BaseListLazyFragment {

    private HkMarketListAdapter mAdapter;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        mAdapter = new HkMarketListAdapter(getContext());
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

    public void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);
        HooviewApiHelper.getInstance().getExponentListNew(new MyRequestCallBack<ExponentListNewModel>() {
            @Override
            public void onSuccess(ExponentListNewModel result) {
                if (result != null) {
                    hideWithData();
                    mAdapter.setExponentListModel(result);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        showEmptyView(R.string.has_no_data);
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String msg) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }
        });
        HooviewApiHelper.getInstance().getUpAndDownListHK(new MyRequestCallBack<UpsAndDownsDataModel>() {
            @Override
            public void onSuccess(UpsAndDownsDataModel result) {
                if (result != null) {
                    hideWithData();
                    mAdapter.setUpsAndDownsListModel(result);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        showEmptyView(R.string.has_no_data);
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(String msg) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onOperation() {
        loadData();
    }

    private void hideWithData() {
        hideEmptyView();
        showOperationView(R.drawable.logo);
    }

    public static MarketHKFragment newInstance() {
        return new MarketHKFragment();
    }
}
