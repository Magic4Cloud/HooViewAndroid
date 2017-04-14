package com.easyvaas.elapp.ui.base.mybase;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * Date   2017/4/12
 * Editor  Misuzu
 * 列表界面简单基类
 */

public abstract class MyBaseListFragment<T extends MyBaseAdapter> extends MyBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerview)
    protected RecyclerView mRecyclerview;
    @BindView(R.id.swiprefreshlayout)
    protected SwipeRefreshLayout mSwiprefreshlayout;
    protected T mAdapter;
    protected int start;

    @Override
    protected int getLayout() {
        return R.layout.swiperefresh_layout;
    }

    @Override
    protected void initViewAndData() {
        mSwiprefreshlayout.setOnRefreshListener(this);
        mSwiprefreshlayout.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.base_purplish));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerview.setLayoutManager(linearLayoutManager);
        changeRecyclerView(); // 重写改变列表设置
        mAdapter = initAdapter();
        setLoadMoreCallBack(mAdapter);
        mRecyclerview.setAdapter(mAdapter);
        mSwiprefreshlayout.post(new Runnable() {
            @Override
            public void run() {
                mSwiprefreshlayout.setRefreshing(true);
            }
        });
        onRefresh();
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
     * 加载更多回调
     */
    protected void setLoadMoreCallBack(T mAdapter) {
        if (mAdapter != null)
        {
            mAdapter.initOnItemClickListener();
            mAdapter.setAutoLoadMoreSize(4);
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    start += 20 ;
                    getListData(true);
                }
            }, mRecyclerview);
        }

    }

    /**
     * 配置RecyclerView
     */
    protected void changeRecyclerView() {

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
