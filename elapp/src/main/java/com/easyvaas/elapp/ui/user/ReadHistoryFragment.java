package com.easyvaas.elapp.ui.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.easyvaas.elapp.adapter.recycler.WatchHistoryAdapter;
import com.easyvaas.elapp.bean.user.ReadRecord;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.event.HistoryDeleteEvent;
import com.easyvaas.elapp.event.HistoryReadDeleteEvent;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;

/**
 * Created by guoliuya on 2017/2/27.
 */

public class ReadHistoryFragment extends BaseListRcvFragment {
    private static final String TAG = "BaseListRcvFragment";
    private List<Object> mRecordLists;
    private WatchHistoryAdapter mMyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void iniView(View view) {
        mRecordLists = new ArrayList<Object>();
        mRecyclerView.setAdapter(mMyAdapter = new WatchHistoryAdapter(mRecordLists, getActivity(),true));
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        loadHistoryVideoList(isLoadMore);
    }

    public void loadHistoryVideoList(final boolean isLoadMore) {
        List<ReadRecord> recordList = RealmHelper.getInstance().getReadRecordList();
        if (recordList != null && recordList.size() > 0) {
            if (!isLoadMore && mRecordLists != null) {
                mRecordLists.clear();
            }
            mRecordLists.addAll(recordList);
            mMyAdapter.notifyDataSetChanged();
            onRefreshComplete(recordList != null && recordList.size() == 0 ? 0 : recordList.size());
        } else {
            if (mRecordLists != null) {
                mRecordLists.clear();
                mMyAdapter.notifyDataSetChanged();
            }
            onRefreshComplete(0);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadHistoryVideoList(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HistoryReadDeleteEvent messageEvent) {
        RealmHelper.getInstance().deleteReadAllRecord();
        loadHistoryVideoList(false);
    }

}


