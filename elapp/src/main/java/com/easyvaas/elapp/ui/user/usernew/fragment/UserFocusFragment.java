package com.easyvaas.elapp.ui.user.usernew.fragment;

import android.os.Bundle;

import com.easyvaas.elapp.adapter.usernew.UserFollowAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserFollow;
import com.easyvaas.elapp.bean.user.UserFollowModel;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 用户粉丝列表
 */

public class UserFocusFragment extends MyBaseListFragment<UserFollowAdapter> {

    private String userId;
    private String sessionId;

    @Override
    protected UserFollowAdapter initAdapter() {
        return new UserFollowAdapter(new ArrayList<UserFollow>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        super.initSomeData();
        userId = getArguments().getString(AppConstants.USER_ID);
        sessionId = getArguments().getString(AppConstants.SESSION_ID);
    }

    @Override
    protected void changeEmptyView() {

        mEmptyView.setEmptyTxt(getString(R.string.empty_no_userfocus));
        if (EVApplication.isLogin()) {
            if (userId.equals(EVApplication.getUser().getName())) {
                mEmptyView.setEmptyTxt(getString(R.string.empty_no_focus));
            }
        }

    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService().getUserFocus(userId,sessionId,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserFollowModel>() {
                    @Override
                    public void OnSuccess(UserFollowModel userFollowModel) {
                        if (userFollowModel !=null)
                            mAdapter.dealLoadData(UserFocusFragment.this,isLoadMore,userFollowModel.getUsers());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserFocusFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static UserFocusFragment newInstance(String userId,String sessionId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        args.putString(AppConstants.SESSION_ID,sessionId);
        UserFocusFragment fragment = new UserFocusFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
