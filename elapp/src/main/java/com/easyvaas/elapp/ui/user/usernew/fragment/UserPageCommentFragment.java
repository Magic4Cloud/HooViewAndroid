package com.easyvaas.elapp.ui.user.usernew.fragment;

import android.os.Bundle;
import android.view.Gravity;

import com.easyvaas.elapp.adapter.usernew.UserPageCommentAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserPageCommentModel;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
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
 * Date   2017/4/24
 * Editor  Misuzu
 * 普通用户主页 评论列表
 */

public class UserPageCommentFragment extends MyBaseListFragment<UserPageCommentAdapter> {

    private String userId;
    private String personId;

    @Override
    protected UserPageCommentAdapter initAdapter() {
        return new UserPageCommentAdapter(new ArrayList<PostsBean>());
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        userId = getArguments().getString(AppConstants.USER_ID);
        personId = getArguments().getString(AppConstants.PERSON_ID);
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_user_comment));
        if (EVApplication.isLogin())
            if (personId.equals(EVApplication.getUser().getName()))
                mEmptyView.setEmptyTxt(getString(R.string.empty_user_comment));
        mEmptyView.getEmptyLayout().setGravity(Gravity.CENTER_HORIZONTAL);
        mEmptyView.getEmptyLayout().setPadding(0, (int) ViewUtil.dp2Px(mContext,85),0,0);
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {

        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserPostCommentList(userId,personId,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<UserPageCommentModel>() {
                    @Override
                    public void OnSuccess(UserPageCommentModel userPageCommentModel) {
                        if (userPageCommentModel != null)
                            mAdapter.dealLoadData(UserPageCommentFragment.this,isLoadMore,userPageCommentModel.getPosts());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(UserPageCommentFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    public static UserPageCommentFragment newInstance(String userId,String personid) {
        
        Bundle args = new Bundle();
        args.putString(AppConstants.USER_ID,userId);
        args.putString(AppConstants.PERSON_ID,personid);
        UserPageCommentFragment fragment = new UserPageCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
