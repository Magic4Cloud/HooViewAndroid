package com.easyvaas.elapp.ui.user.usernew.fragment;

import android.os.Bundle;
import android.view.Gravity;

import com.easyvaas.elapp.adapter.usernew.UserCheatsAdapter;
import com.easyvaas.elapp.bean.user.CheatsListModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 大V 秘籍
 */

public class VipUserCheatsFragment extends MyBaseListFragment<UserCheatsAdapter> {

    private String userId;

    @Override
    protected UserCheatsAdapter initAdapter() {
        return new UserCheatsAdapter(new ArrayList<CheatsListModel.CheatsModel>());
    }

    @Override
    protected void initSomeData() {
        userId = getArguments().getString(AppConstants.USER_ID);
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_vipcheats));
        mEmptyView.getEmptyLayout().setGravity(Gravity.CENTER_HORIZONTAL);
        mEmptyView.getEmptyLayout().setPadding(0, (int) ViewUtil.dp2Px(mContext,70),0,0);
        mEmptyView.hideImage();
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService().getUserBuyCheatsTest("http://192.168.8.125:8888/user/purchases?type=2")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<CheatsListModel>() {
                            @Override
                            public void OnSuccess(CheatsListModel result) {
                                if (result != null) {
                                    mAdapter.dealLoadData(VipUserCheatsFragment.this, isLoadMore, result.getCheats());
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(VipUserCheatsFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static VipUserCheatsFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        VipUserCheatsFragment fragment = new VipUserCheatsFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
