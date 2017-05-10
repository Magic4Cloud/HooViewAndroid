package com.easyvaas.elapp.ui.base.mybase;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.base.MyEmptyView;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 列表界面简单基类
 */

public abstract class MyBaseListFragment<T extends MyBaseAdapter> extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadOperator {

    @BindView(R.id.recyclerview)
    protected RecyclerView mRecyclerview;
    @BindView(R.id.swiprefreshlayout)
    protected SwipeRefreshLayout mSwiprefreshlayout;
    @BindView(R.id.empty_view)
    protected MyEmptyView mEmptyView;

    protected T mAdapter;
    protected int start;


    @Override
    protected int getLayout() {
        return R.layout.swiperefresh_layout;
    }

    @Override
    protected void initViewAndData() {
        initSomeData();//初始化一些数据
        mSwiprefreshlayout.setOnRefreshListener(this);
        mSwiprefreshlayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.base_purplish));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(linearLayoutManager);
        changeRecyclerView(); // 重写改变列表设置
        changeEmptyView();// 重写改变empty显示
        mAdapter = initAdapter();
        if (mAdapter != null) {
            setLoadMoreCallBack(mAdapter);
            mRecyclerview.setAdapter(mAdapter);
            mSwiprefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    setLoading(true);
                }
            });
            onRefresh();
        }
    }

    /**
     * 下拉刷新回调
     */
    @Override
    public void onRefresh() {
        start = 0;
        getListData(false);
    }

    /**
     * 自动刷新
     */
    protected void autoRefresh() {
        if (mSwiprefreshlayout != null && mAdapter != null) {
            mSwiprefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    setLoading(true);
                }
            });
            onRefresh();
        }
    }

    /**
     * 加载更多回调
     */
    protected void setLoadMoreCallBack(T mAdapter) {
        if (mAdapter != null) {
            mAdapter.initOnItemClickListener();
            mAdapter.setAutoLoadMoreSize(4);
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    start += 20;
                    getListData(true);
                }
            }, mRecyclerview);
        }
    }

    /**
     * 初始化某些数据
     */
    protected void initSomeData() {

    }

    /**
     * 配置RecyclerView
     */
    protected void changeRecyclerView() {

    }

    /**
     * 配置Empty显示
     */
    protected void changeEmptyView() {

    }

    /**
     * 显示空布局
     */
    @Override
    public void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.showEmptyOnNoData();
        }
    }

    /**
     * 显示错误布局
     */
    @Override
    public void showError() {
        if (mEmptyView != null) {
            mEmptyView.showErrorOnNetError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideEmpty();
                    setLoading(true);
                    onRefresh();
                }
            });
        }
    }

    /**
     * 隐藏空布局
     */
    @Override
    public void hideEmpty() {
        if (mEmptyView != null) {
            mEmptyView.hideEmptyView();
        }
    }

    /**
     * 设置刷新显示状态
     */
    @Override
    public void setLoading(boolean isLoading) {
        if (mSwiprefreshlayout != null) {
            mSwiprefreshlayout.setRefreshing(isLoading);
        }
    }

    /**
     * 设置padding Top
     */
    protected void setPaddingTop(int dp) {
        if (mRecyclerview != null) {
            mRecyclerview.setClipToPadding(false);
            mRecyclerview.setPadding(0, (int) ViewUtil.dp2Px(getContext(), dp), 0, 0);
        }
    }

    /**
     * 初始化Adapter
     */
    protected abstract T initAdapter();

    /**
     * 获取列表数据
     */
    protected abstract void getListData(Boolean isLoadMore);

}
