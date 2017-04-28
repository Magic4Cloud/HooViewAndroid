package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.usernew.UserCheatsAdapter;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的购买---已买秘籍
 */

public class UserBuyCheatsFragment extends MyBaseListFragment<UserCheatsAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserCheatsAdapter initAdapter() {
        UserCheatsAdapter adapter = new UserCheatsAdapter(new ArrayList<CheatsListModel.CheatsModel>());
        adapter.setBuy(true);
        return adapter;
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        /*Subscription subscription =
                RetrofitHelper.getInstance().getService().getUserBuyCheats(Preferences.getInstance(EVApplication.getApp()).getUserNumber(),
                        Preferences.getInstance(EVApplication.getApp()).getSessionId(), start)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<CheatsListModel>() {
                            @Override
                            public void OnSuccess(CheatsListModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(UserBuyCheatsFragment.this, isLoadMore, result.getCheats());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserBuyCheatsFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);*/
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_cheats_now));
        showEmpty();
    }

    public static UserBuyCheatsFragment newInstance() {
        return new UserBuyCheatsFragment();
    }
}
