package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.usernew.UserCheatsAdapter;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import java.util.ArrayList;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的发布---秘籍
 */

public class UserPublishCheatsFragment extends MyBaseListFragment<UserCheatsAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserCheatsAdapter initAdapter() {
        return new UserCheatsAdapter(new ArrayList<CheatsListModel.CheatsModel>());
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt("秘籍暂未开通！");
        showEmpty();
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        /*Subscription subscription =
                RetrofitHelper.getInstance().getService()
                        .getUserPublishCheats(Preferences.getInstance(EVApplication.getApp()).getUserNumber(),
                                Preferences.getInstance(EVApplication.getApp()).getSessionId(), start)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<CheatsListModel>() {
                            @Override
                            public void OnSuccess(CheatsListModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(UserPublishCheatsFragment.this, isLoadMore, result.getCheats());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserPublishCheatsFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);*/
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPublishCheatsFragment newInstance() {
        return new UserPublishCheatsFragment();
    }
}
