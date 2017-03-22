package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.HkMarketListAdapter;
import com.easyvaas.elapp.bean.market.ExponentListNewModel;
import com.easyvaas.elapp.bean.market.UpsAndDownsDataModel;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.event.RefreshExponentEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by guojun on 2016/12/29 10:11.
 */

public class HKMarketFragment extends BaseListFragment {
    private HkMarketListAdapter mAdapter;

    public static HKMarketFragment newInstance() {
        Bundle args = new Bundle();
        HKMarketFragment fragment = new HKMarketFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mAdapter = new HkMarketListAdapter());
        loadData();
    }

    public void loadData() {
        HooviewApiHelper.getInstance().getExponentListNew(new MyRequestCallBack<ExponentListNewModel>() {
            @Override
            public void onSuccess(ExponentListNewModel result) {
                if (result != null) {
                    hideEmptyView();
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
                    hideEmptyView();
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
        super.onRefresh();
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MarketRefreshEvent event) {
        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshExponentEvent event) {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}

