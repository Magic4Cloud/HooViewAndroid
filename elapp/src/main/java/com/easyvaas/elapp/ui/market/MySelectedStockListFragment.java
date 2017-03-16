package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.elapp.adapter.recycler.SelectStockListAdapter;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.bean.user.CollectListModel;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.event.RefreshExponentEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListFragment;
import com.easyvaas.elapp.utils.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guojun on 2016/12/29 10:11.
 */

public class MySelectedStockListFragment extends BaseListFragment {
    private static final String TAG = "MySelectedStockListFragment";
    private SelectStockListAdapter mAdapter;
    private String mStockIdStr = "";
    private String mType = "1";
    private List<StockListModel.StockModel> headerLists = new ArrayList<StockListModel.StockModel>();
    private List<StockListModel.StockModel> allLists = new ArrayList<StockListModel.StockModel>();
    private List<StockListModel.StockModel> luLists = new ArrayList<StockListModel.StockModel>();
    private List<StockListModel.StockModel> hkLists = new ArrayList<StockListModel.StockModel>();
    private List<StockListModel.StockModel> usLists = new ArrayList<StockListModel.StockModel>();

    public static MySelectedStockListFragment newInstance(String type) {
        Bundle args = new Bundle();
        MySelectedStockListFragment fragment = new MySelectedStockListFragment();
        fragment.mType = type;
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
        mRecyclerView.setAdapter(mAdapter = new SelectStockListAdapter());
        loadData();
    }

    public void loadData() {
        ApiHelper.getInstance().getCollectList("4", new MyRequestCallBack<CollectListModel>() {
            @Override
            public void onSuccess(CollectListModel result) {
                mStockIdStr = result.getCollectlist();
                List<String> codeList = result.getCodeList();
                getStockListInfo(mStockIdStr);
            }

            @Override
            public void onFailure(String msg) {
                Logger.e(TAG, msg);
                showEmptyView();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                Logger.e(TAG, errorInfo);
                showEmptyView();
            }
        });
    }

    private void getNewStockInfo(String mStockIdStr) {
        HooviewApiHelper.getInstance().getSelectStockList(mStockIdStr,
                new MyRequestCallBack<StockListModel>() {
                    @Override
                    public void onSuccess(StockListModel result) {
                        if (result != null) {
                            result.getData().addAll(headerLists);
                            List<StockListModel.StockModel> data = result.getData();
                            if (!(luLists.size()>0||hkLists.size()>0||usLists.size()>0))
                            filterStock(data);
                            if (mType.equals("2")) {
                                result.getData().clear();
                                result.getData().addAll(luLists);

                            } else if (mType.equals("3")) {
                                result.getData().clear();
                                result.getData().addAll(hkLists);

                            } else if (mType.equals("4")) {
                                result.getData().clear();
                                result.getData().addAll(usLists);

                            }
                            if (result.getData() != null && result.getData().size() > 0) {
                                mAdapter.setStockListModel(result);
                                mSwipeRefreshLayout.setRefreshing(false);
                                EventBus.getDefault().post(result);
                            } else {
                                mSwipeRefreshLayout.setRefreshing(false);
                                showEmptyView();
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void filterStock(List<StockListModel.StockModel> data) {

        for (StockListModel.StockModel model : data) {
            if (!TextUtils.isEmpty(model.getSymbol()) && (model.getSymbol().startsWith("SZ") || model
                    .getSymbol().startsWith("SH"))) {
                luLists.add(model);
            } else if (!TextUtils.isEmpty(model.getSymbol()) && model.getSymbol().startsWith("HK")) {
                hkLists.add(model);
            } else if (!TextUtils.isEmpty(model.getSymbol()) && model.getSymbol().startsWith("IXIX")) {
                usLists.add(model);
            }
        }
    }

    private void judgeHasCase(List<String> codeList) {
        for (String code : codeList) {
            for (int i = 0; i < code.length(); i++) {
                char c = code.charAt(i);
                if (Character.isUpperCase(c)) {

                }
            }
        }

    }

    private void getStockListInfo(String listStr) {
        HooviewApiHelper.getInstance().getSelectStockList(listStr, new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                if (result != null) {
                    headerLists = result.getData();
                    getNewStockInfo(mStockIdStr);
                }
            }

            @Override
            public void onFailure(String msg) {
                mSwipeRefreshLayout.setRefreshing(false);
                showEmptyView();
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                showEmptyView();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<StockListModel.StockModel> event) {
        mAdapter.setStockListModel(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}


