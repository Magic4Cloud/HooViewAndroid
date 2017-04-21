package com.easyvaas.elapp.ui.market;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easyvaas.elapp.adapter.market.MarketGlobalListAdapter;
import com.easyvaas.elapp.bean.market.MarketGlobalModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import lib.adapter.expand.StickyRecyclerHeadersDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/11
 * Author  xiaomao
 * 行情---全球
 */

public class MarketGlobalFragment extends BaseListLazyFragment {

    private MarketGlobalListAdapter mAdapter;
    private StickyRecyclerHeadersDecoration mHeadersDecoration;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new MarketGlobalListAdapter(getContext());
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                View view = mLayoutManager.findViewByPosition(position);
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

    private void loadData() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        Subscription subscription = RetrofitHelper.getInstance().getService().getMarketGlobalStock()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<List<MarketGlobalModel>>() {
                    @Override
                    public void OnSuccess(List<MarketGlobalModel> list) {
                        if (list != null && list.size() > 0) {
                            List<StockModel> datas = new ArrayList<StockModel>();
                            for (int i = 0; i < list.size(); i++) {
                                MarketGlobalModel globalModel = list.get(i);
                                if (globalModel != null) {
                                    String name = globalModel.getName();
                                    List<StockModel> index = globalModel.getIndex();
                                    for (int j = 0; j < index.size(); j++) {
                                        StockModel stockModel = index.get(j);
                                        if (stockModel != null) {
                                            stockModel.setCategory(name);
                                            stockModel.setHeaderId(i + 1);
                                            if (j == index.size() - 1) {
                                                stockModel.setLastInCategory(true);
                                            } else {
                                                stockModel.setLastInCategory(false);
                                            }
                                            datas.add(stockModel);
                                        }
                                    }
                                }
                            }
                            if (mAdapter != null) {
                                mAdapter.setData(datas);
                            }
                            showOperationView(R.drawable.market_refresh_xm);
                        } else {
                            showWithoutData();
                        }
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        Logger.e(MarketGlobalFragment.class.getSimpleName(), msg);
                        showWithoutData();
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
        addSubscribe(subscription);
    }

    public static MarketGlobalFragment newInstance() {
        return new MarketGlobalFragment();
    }

    private void showWithoutData() {
        showEmptyView();
        hideOperationView();
    }

}
