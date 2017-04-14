package com.easyvaas.elapp.ui.market;

import android.support.v7.widget.RecyclerView;

import com.easyvaas.elapp.adapter.market.MarketHKListAdapter;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import lib.adapter.expand.StickyRecyclerHeadersDecoration;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 行情---港股
 */
public class MarketHKFragment extends BaseListLazyFragment {

    private MarketHKListAdapter mAdapter;
    private StickyRecyclerHeadersDecoration mHeadersDecoration;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new MarketHKListAdapter(getContext());
        }
        if (mHeadersDecoration == null) {
            mHeadersDecoration = new StickyRecyclerHeadersDecoration(mAdapter);
            mRecyclerView.addItemDecoration(mHeadersDecoration);
        }
        // setTouchHelper();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mHeadersDecoration.invalidateHeaders();
            }
        });
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
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        HooviewApiHelper.getInstance().getExponentListNew(new MyRequestCallBack<ExponentListNewModel>() {
            @Override
            public void onSuccess(ExponentListNewModel result) {
                if (result != null) {
                    hideWithData();
                    mAdapter.setDataHeader(result);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        showEmptyView(R.string.has_no_data);
                    }
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        HooviewApiHelper.getInstance().getUpAndDownListHK(new MyRequestCallBack<UpsAndDownsDataModel>() {
            @Override
            public void onSuccess(UpsAndDownsDataModel result) {
                if (result != null) {
                    hideWithData();
                    List<StockModel> datas = dealData(result);
                    mAdapter.setDataItem(datas);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        showEmptyView(R.string.has_no_data);
                    }
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 处理数据
     */
    private List<StockModel> dealData(UpsAndDownsDataModel result) {
        List<StockModel> data = new ArrayList<StockModel>();
        UpsAndDownsDataModel.DataModel dataModel = result.getData();
        if (dataModel != null) {
            data.addAll(dealDataList(dataModel.getHead(), true));
            data.addAll(dealDataList(dataModel.getTail(), false));
        }
        return data;
    }

    /**
     * 处理数据，给StockModel设置标识值
     */
    private List<StockModel> dealDataList(List<StockModel> list, boolean head) {
        List<StockModel> data = new ArrayList<StockModel>();
        for (int i = 0; i < list.size(); i++) {
            StockModel stockModel = list.get(i);
            if (stockModel != null) {
                stockModel.setHeaderId(head ? 1: 2);
                stockModel.setUp(head);
                if (i == list.size() - 1) {
                    stockModel.setLastInCategory(true);
                } else {
                    stockModel.setLastInCategory(false);
                }
                data.add(stockModel);
            }
        }
        return data;
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
        showOperationView(R.drawable.market_refresh_xm);
    }

    public static MarketHKFragment newInstance() {
        return new MarketHKFragment();
    }
}
