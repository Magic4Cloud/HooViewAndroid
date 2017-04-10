package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.adapter.recycler.SelectStockListAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.market.StockListModel;
import com.easyvaas.elapp.event.MarketRefreshEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.LazyLoadFragment;
import com.easyvaas.elapp.ui.search.SearchStockActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghai on 2017/3/27.
 * 自选的列表页面
 * 根据不同的 type 来区分不同的股票类型
 */

public class MySelectedStockAllFragment extends LazyLoadFragment {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_CN = 1;
    public static final int TYPE_HK = 2;
    private SwipeRefreshLayout stockRefresh;
    private RecyclerView mRecyclerView;
    private SelectStockListAdapter mAdapter;
    protected TextView mTvPrompt;
    protected TextView mTvAddSelect;
    protected ImageView mIvPrompt;
    protected LinearLayout mLLEmpty;
    private int type = 1;
    private List<StockListModel.StockModel> sModels = new ArrayList<>();
    private boolean isFirstLoad = true;

    public static MySelectedStockAllFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRA_TYPE, type);
        MySelectedStockAllFragment fragment = new MySelectedStockAllFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_market_optional, null);
        stockRefresh = (SwipeRefreshLayout) view.findViewById(R.id.stock_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.stock_rv);
        mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        mTvAddSelect = (TextView) view.findViewById(R.id.tv_add_select);
        mIvPrompt = (ImageView) view.findViewById(R.id.iv_prompt);
        mLLEmpty = (LinearLayout) view.findViewById(R.id.ll_empty);
        initView();
        initRecycler();
        mTvPrompt.setText(R.string.empty_data);
        return view;
    }

    @Override
    public void lazyLoadView() {
        getStocksList(); // 第一次界面可见的时候加载网络数据
    }

    @Override
    public void onResume() {
        super.onResume();
        // 要确保 lazyLoadVie() 和 onResume() 都不是第一次执行
        if (!isFirstLoad && isFirstLoad() && mAdapter != null) {
            getStocksList();
        } else {
            isFirstLoad = false;
        }
    }

    private void initView() {
        type = getArguments().getInt(Constants.EXTRA_TYPE, 0);
        ViewUtil.initSwipeRefreshLayout(stockRefresh);
        stockRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStocksList();
            }
        });
        mTvAddSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchStockActivity.start(getContext());
            }
        });
    }

    private void initRecycler(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SelectStockListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    // 从网络上得到自选股的列表（会获取到全部的自选股， 没有分类）.没有加载更多
    private void getStocksList(){
        HooviewApiHelper.getInstance().getUserStockList(EVApplication.getUser().getName(), new MyRequestCallBack<StockListModel>() {
            @Override
            public void onSuccess(StockListModel result) {
                stockRefresh.setRefreshing(false);
                if (result == null) return;
                if (result.getData()!= null && result.getData().size() > 0){
                    sModels.clear();
                    filterData(result.getData());
                    if (sModels.size() > 0){
                        hideEmptyView();
                        mAdapter.setStockListModel(sModels);
                        return;
                    }
                }
                showAddBtn();
            }

            @Override
            public void onFailure(String msg) {
                stockRefresh.setRefreshing(false);
                showAddBtn();
            }

            @Override
            public void onError(String errorInfo) {
                stockRefresh.setRefreshing(false);
                showAddBtn();
            }
        });
    }

    // 过滤数据（得到当前 Tab 相关的数据）
    private  void filterData(List<StockListModel.StockModel> stockModels){
        if (type == TYPE_ALL) {
            sModels.addAll(stockModels);
            return;
        }
        for (int i = 0; i < stockModels.size(); i++){
            StockListModel.StockModel model = stockModels.get(i);
            if (filterDataByName(model.getSymbol())) {
                sModels.add(model);
            }
        }
    }

    private boolean filterDataByName(String filterName){
        if (TextUtils.isEmpty(filterName)) return false;
        if (type == TYPE_HK) {
            return filterName.startsWith("HK");
        } else {
            return filterName.startsWith("SZ") || filterName.startsWith("SH");
        }
    }

    public void showEmptyView() {
        mLLEmpty.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        if (mLLEmpty.getVisibility() != View.GONE) mLLEmpty.setVisibility(View.GONE);
    }

    public void showAddBtn(){
        showEmptyView();
        mTvAddSelect.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MarketRefreshEvent event) {
        if (getUserVisibleHint()){
            stockRefresh.setRefreshing(true);
            getStocksList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
