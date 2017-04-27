package com.easyvaas.elapp.ui.user.usernew.fragment;

import com.easyvaas.elapp.adapter.usernew.UserVLivingNewAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserPublishVideoModel;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/20
 * Author  xiaomao
 * 个人中心---我的发布---直播
 */

public class UserPublishLivingFragment extends MyBaseListFragment<UserVLivingNewAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected UserVLivingNewAdapter initAdapter() {
        UserVLivingNewAdapter adapter = new UserVLivingNewAdapter(new ArrayList<VideoEntity>());
        return adapter;
    }

    @Override
    protected void changeEmptyView() {
        super.changeEmptyView();
        mEmptyView.setEmptyTxt(getString(R.string.empty_no_living));
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService()
                        .getUserPublishLiving(Preferences.getInstance(EVApplication.getApp()).getUserNumber(),
                                Preferences.getInstance(EVApplication.getApp()).getSessionId(), start)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSubscribe<UserPublishVideoModel>() {
                            @Override
                            public void OnSuccess(UserPublishVideoModel result) {
                                if (result != null) {
                                    if (!isLoadMore) {
                                        mAdapter.setHeaderModel(result.getTextlive());
                                    }
                                    List<VideoEntity> list = result.getVideolive();
                                    if (list != null && list.size() > 0) {
                                        list.add(list.get(list.size() - 1));
                                    }
                                    mAdapter.dealLoadData(UserPublishLivingFragment.this, isLoadMore, list);
                                }
                            }

                            @Override
                            public void OnFailue(String msg) {
                                mAdapter.dealLoadError(UserPublishLivingFragment.this, isLoadMore);
                            }
                        });
        addSubscribe(subscription);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    public static UserPublishLivingFragment newInstance() {
        return new UserPublishLivingFragment();
    }
}
