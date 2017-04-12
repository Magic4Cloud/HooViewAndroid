package com.easyvaas.elapp.ui.market;

import android.util.Log;

import com.easyvaas.elapp.adapter.recycler.ChineseMarketListAdapter;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.hooview.app.R;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 行情---沪深
 */
public class MarketChineseFragment extends BaseListLazyFragment {

    private ChineseMarketListAdapter mAdapter;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        mAdapter = new ChineseMarketListAdapter(getContext());
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

    /**
     * 加载数据
     */
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
        HooviewApiHelper.getInstance().getUpAndDownList(new MyRequestCallBack<UpsAndDownsDataModel>() {
            @Override
            public void onSuccess(UpsAndDownsDataModel result) {
                Log.d("Misuzu", "-------------success");
                if (result != null) {
                    hideWithData();
                    mAdapter.setUpsAndDownsListModel(result);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    if (mAdapter.getItemCount() == 0) {
                        showEmptyView(R.string.has_no_data);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                Log.d("Misuzu", "-------------onFailure-----msg-> " + msg);
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                Log.d("Misuzu", "-------------onError  " + errorInfo);
                mSwipeRefreshLayout.setRefreshing(false);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView(R.string.has_no_data);
                }
            }
        });
    }

    private void hideWithData() {
        hideEmptyView();
        showOperationView(R.drawable.logo);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onOperation() {
        loadData();
    }

    public static MarketChineseFragment newInstance() {
        return new MarketChineseFragment();
    }

}
