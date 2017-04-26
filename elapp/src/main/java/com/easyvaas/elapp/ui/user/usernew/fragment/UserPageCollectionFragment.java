package com.easyvaas.elapp.ui.user.usernew.fragment;

import android.os.Bundle;
import android.view.Gravity;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.news.NormalNewsModel;
import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
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
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户收藏fragment
 */

public class UserPageCollectionFragment extends MyBaseListFragment<NormalNewsAdapter> {

    private String userId;

    @Override
    protected NormalNewsAdapter initAdapter() {
        return new NormalNewsAdapter(new ArrayList<HomeNewsBean>());
    }

    @Override
    protected void changeEmptyView() {

        mEmptyView.setEmptyTxt(getString(R.string.empty_no_user_colloct));
        if (EVApplication.isLogin())
            if (userId.equals(EVApplication.getUser().getName()))
                mEmptyView.setEmptyTxt(getString(R.string.empty_no_collect));

        mEmptyView.getEmptyLayout().setGravity(Gravity.CENTER_HORIZONTAL);
        mEmptyView.getEmptyLayout().setPadding(0, (int) ViewUtil.dp2Px(mContext,85),0,0);
    }

    @Override
    protected void initSomeData() {
        userId = getArguments().getString(AppConstants.USER_ID);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserCollection(userId,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NormalNewsModel>() {
                    @Override
                    public void OnSuccess(NormalNewsModel normalNewsModel) {
                        if (normalNewsModel != null)
                            mAdapter.dealLoadData(UserPageCollectionFragment.this, isLoadMore, normalNewsModel.getNews());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserPageCollectionFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPageCollectionFragment newInstance(String userId) {

        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID, userId);
        UserPageCollectionFragment fragment = new UserPageCollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
