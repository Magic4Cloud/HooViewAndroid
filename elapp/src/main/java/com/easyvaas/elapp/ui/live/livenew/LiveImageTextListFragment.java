package com.easyvaas.elapp.ui.live.livenew;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyvaas.elapp.adapter.live.LiveImageTextListAdapter;
import com.easyvaas.elapp.bean.user.UserInfoArrayModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.event.MainRefreshEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date    2017/4/25
 * Author  xiaomao
 * 图文直播列表
 */

public class LiveImageTextListFragment extends MyBaseListFragment<LiveImageTextListAdapter> {

    /**
     * 初始化Adapter
     */
    @Override
    protected LiveImageTextListAdapter initAdapter() {
        return new LiveImageTextListAdapter(new ArrayList<TextLiveListModel.StreamsEntity>());
    }

    /**
     * 获取列表数据
     *
     * @param isLoadMore
     */
    @Override
    protected void getListData(final Boolean isLoadMore) {
        Subscription subscription =
                RetrofitHelper.getInstance().getService().getLiveImageText(start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<TextLiveListModel>() {
                    @Override
                    public void OnSuccess(TextLiveListModel result) {
                        if (result != null) {
                            getUserInfos(result, isLoadMore);
                        }
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(LiveImageTextListFragment.this, isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    private void getUserInfos(final TextLiveListModel textLiveListModel, final boolean isLoadMore) {
        List<String> names = new ArrayList<>();
        if (textLiveListModel.getHotstreams() != null) {
            for (int i = 0; i < textLiveListModel.getHotstreams().size(); i++) {
                names.add(textLiveListModel.getHotstreams().get(i).getOwnerid());
            }
        }
        if (textLiveListModel.getStreams() != null) {
            for (int i = 0; i < textLiveListModel.getStreams().size(); i++) {
                names.add(textLiveListModel.getStreams().get(i).getOwnerid());
            }
        }
        ApiHelper.getInstance().getUserInfosNew(names, new MyRequestCallBack<UserInfoArrayModel>() {
            @Override
            public void onSuccess(UserInfoArrayModel result) {
                if (result != null && result.getUsers() != null) {
                    for (int i = 0; i < result.getUsers().size(); i++) {
                        if (textLiveListModel.getHotstreams() != null) {
                            if (i < textLiveListModel.getHotstreams().size()) {
                                textLiveListModel.getHotstreams().get(i).setUserEntity(result.getUsers().get(i));
                            } else {
                                textLiveListModel.getStreams().get(i - textLiveListModel.getHotstreams().size()).setUserEntity(result.getUsers().get(i));
                            }
                        } else {
                            textLiveListModel.getStreams().get(i).setUserEntity(result.getUsers().get(i));
                        }
                    }
                    if (!isLoadMore) {
                        mAdapter.setHotList(getActivity(), textLiveListModel.getHotstreams());
                    }
                    mAdapter.dealLoadData(LiveImageTextListFragment.this, isLoadMore, textLiveListModel.getStreams());
                }
            }

            @Override
            public void onFailure(String msg) {
                mAdapter.dealLoadError(LiveImageTextListFragment.this, isLoadMore);
            }

            @Override
            public void onError(String errorInfo) {
                mAdapter.dealLoadError(LiveImageTextListFragment.this, isLoadMore);
            }
        });
    }

    public static LiveImageTextListFragment newInstance() {
        return new LiveImageTextListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainRefreshEvent(MainRefreshEvent event) {
        if (event != null && MainRefreshEvent.TYPE_LIVE.equals(event.type)) {
            autoRefresh();
        }
    }
}
