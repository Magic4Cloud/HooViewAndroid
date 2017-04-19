package com.easyvaas.elapp.ui.base.mybase;

/**
 * Date   2017/4/19
 * Editor  Misuzu
 * 加载数据操作接口
 */

public interface LoadOperator {

    public void showEmpty(); // 显示空布局

    public void showError(); // 显示错误布局

    public void hideEmpty(); // 隐藏空布局

    public void setLoading(boolean isLoading); // 显示加载状态

}
