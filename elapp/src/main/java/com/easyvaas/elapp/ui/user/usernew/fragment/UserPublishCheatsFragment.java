package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.usernew.UserCheatsAdapter;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        super.changeEmptyView();
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_cheats));
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService().getUserPublishCheatsTest(ApiConstant.MOCK_HOST + "/user/works?type=1")
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
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPublishCheatsFragment newInstance() {
        return new UserPublishCheatsFragment();
    }
}
