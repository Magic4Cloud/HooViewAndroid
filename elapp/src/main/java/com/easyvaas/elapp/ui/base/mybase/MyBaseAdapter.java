package com.easyvaas.elapp.ui.base.mybase;

import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Date   2017/4/11
 * Editor  Misuzu
 * 列表adapter基类
 */

public abstract class MyBaseAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {


    public MyBaseAdapter(List<T> data) {
        super(data);
    }

    public MyBaseAdapter(int layoutResId, List<T> data) {
        super(layoutResId, data);
    }

    @Override
    protected int getDefItemViewType(int position) {  //这里的position 是真实数据的
        return getItemViewByType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return OnCreateViewByHolder(parent, viewType);
    }

    protected abstract int getItemViewByType(int position);  //设置不同类型
    protected abstract BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType); //设置ViewHolder

    /**
     * 处理上拉 下拉的数据
     */
    public void dealLoadData(boolean isLoadMore,List<T> data)
    {
        if (!isLoadMore) //下拉刷新
        {
            if (data != null && data.size() > 0)
            {
                setNewData(data);
                setEnableLoadMore(true);
            }
        }else  // 上拉加载
        {
            if (data != null && data.size() > 0)
            {
                addData(data);
                loadMoreComplete();
            }else
                loadMoreEnd();
        }
    }
}