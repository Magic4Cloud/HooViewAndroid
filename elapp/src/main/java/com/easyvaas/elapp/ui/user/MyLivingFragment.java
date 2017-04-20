package com.easyvaas.elapp.ui.user;

import com.easyvaas.elapp.adapter.UserVLivingAdapter;
import com.easyvaas.elapp.bean.user.UserHistoryTestModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的发布---直播
 */

public class MyLivingFragment extends MyBaseListFragment {


    /**
     * 初始化Adapter
     */
    @Override
    protected MyBaseAdapter initAdapter() {
        UserVLivingAdapter adapter = new UserVLivingAdapter(new ArrayList());
        adapter.showHeader(true);
        return adapter;
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        RetrofitHelper.getInstance().getService().getHisteryTest("http://demo2821846.mockable.io/user/historylist?type=0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserHistoryTestModel>() {
                    @Override
                    public void OnSuccess(UserHistoryTestModel result) {
                        mAdapter.dealLoadData(MyLivingFragment.this, isLoadMore, result.getVideolive());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(MyLivingFragment.this, isLoadMore);
                    }
                });
    }

    public static MyLivingFragment newInstance() {
        return new MyLivingFragment();
    }
}
