package com.easyvaas.elapp.ui.user;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.elapp.adapter.oldRecycler.UserRcvAdapter;
import com.easyvaas.elapp.bean.user.UserEntity;
import com.easyvaas.elapp.bean.user.UserEntityArray;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.Constants;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;


public class FansListFragment extends BaseListRcvFragment {
    public static FansListFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_KEY_USER_ID, name);
        FansListFragment fragment = new FansListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<UserEntity> mUsers = new ArrayList<UserEntity>();
    private UserRcvAdapter mAdapter;
    private String mName;


    @Override
    public void iniView(View view) {
        mName = getArguments().getString(Constants.EXTRA_KEY_USER_ID);
        if (TextUtils.isEmpty(mName)) {
            return;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
        mAdapter = new UserRcvAdapter(mUsers, UserRcvAdapter.TYPE_FANS);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                UserUtil.showUserInfo(getApplicationContext(), mUsers.get(position).getName());
            }
        });
        TextView tvPromt = (TextView) view.findViewById(R.id.tv_prompt);
        tvPromt.setText(R.string.no_fans_tips);
        mEmptyView=mPullToLoadRcvView.getEmptyView();
        mEmptyView.setTitle(getString(R.string.no_fans_tips));
        mEmptyView.getSubTitleTextView().setVisibility(View.GONE);
        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        ApiHelper.getInstance().getFansList(mName, mNextPageIndex, ApiConstant.DEFAULT_PAGE_SIZE,
                new MyRequestCallBack<UserEntityArray>() {
                    @Override
                    public void onSuccess(UserEntityArray user) {
                        if (!isLoadMore) {
                            mUsers.clear();
                        }
                        if (user != null && user.getUsers().size() > 0) {
                            mNextPageIndex = user.getNext();
                            mUsers.addAll(user.getUsers());
                        }
                        mAdapter.notifyDataSetChanged();
                        onRefreshComplete(user == null ? 0 : user.getCount());
                    }

                    @Override
                    public void onFailure(String msg) {
//                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        onRefreshComplete(0);
                    }
                });
    }
}
