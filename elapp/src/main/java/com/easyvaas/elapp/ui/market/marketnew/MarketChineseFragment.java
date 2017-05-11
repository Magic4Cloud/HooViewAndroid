package com.easyvaas.elapp.ui.market.marketnew;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.easyvaas.elapp.adapter.market.MarketChineseListAdapter;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.market.StockModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.BaseListLazyFragment;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lib.adapter.expand.StickyRecyclerHeadersDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/12
 * Author  xiaomao
 * 行情---沪深
 */
public class MarketChineseFragment extends BaseListLazyFragment {

    private MarketChineseListAdapter mAdapter;
    private StickyRecyclerHeadersDecoration mHeadersDecoration;

    /**
     * 初始化adapter
     */
    @Override
    protected void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new MarketChineseListAdapter(getContext());
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

    /**
     * 加载数据
     */
    public void loadData() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        // 市场指数
        Subscription subscription = RetrofitHelper.getInstance().getService().getMarketExponent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<MarketExponentModel>() {
                    @Override
                    public void OnSuccess(MarketExponentModel model) {
                        if (model != null) {
                            hideWithData();
                            mAdapter.setDataHeader(model);
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
                    public void OnFailue(String msg) {
                        if (mAdapter.getItemCount() == 0) {
                            showEmptyView(R.string.has_no_data);
                        }
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
        addSubscribe(subscription);
        // 大盘涨跌
        HooviewApiHelper.getInstance().getUpAndDownList(new MyRequestCallBack<UpsAndDownsDataModel>() {
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
                if (i >= 0 && i < 3) {
                    stockModel.setRank(i);
                }
                data.add(stockModel);
            }
        }
        return data;
    }

    private void hideWithData() {
        hideEmptyView();
//        showOperationView(R.drawable.market_refresh_new);
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
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static MarketChineseFragment newInstance() {
        return new MarketChineseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshEvent(MainRefreshEvent event) {
        if (event != null && MainRefreshEvent.TYPE_MARKET.equals(event.type)) {
            if (!mAutoRefreshing && mSwipeRefreshLayout != null && mAdapter != null) {
                mAutoRefreshing = true;
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onRefresh();
                        mAutoRefreshing = false;
                    }
                }, 800);
            }
        }
    }
}
