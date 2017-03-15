package com.easyvaas.elapp.ui.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.recycler.CollectionRcvAdapter;
import com.easyvaas.elapp.adapter.recycler.WatchHistoryAdapter;
import com.easyvaas.elapp.bean.user.Collection;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.ui.base.BaseListRcvActivity;


public class MyCollectListActivity extends BaseListRcvActivity {
    private List<Object> mCollectionLists;
    private CollectionRcvAdapter mMyAdapter;
    public static void start(Context context) {
        Intent starter = new Intent(context, MyCollectListActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvTitle.setText(R.string.my_collect);
        initView();
    }

    private void initView() {
        mCollectionLists = new ArrayList<Object>();
        mRecyclerView.setAdapter(mMyAdapter = new CollectionRcvAdapter(mCollectionLists,this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData(false);

    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        List<Collection> recordList = RealmHelper.getInstance().getCollectionList();
        if (recordList != null && recordList.size() > 0) {
            if (!isLoadMore && mCollectionLists != null) {
                mCollectionLists.clear();
            }
            mCollectionLists.addAll(recordList);
            mMyAdapter.notifyDataSetChanged();
            onRefreshComplete(recordList != null && recordList.size() == 0 ? 0 : recordList.size());
        } else {
            if (mCollectionLists != null) {
                mCollectionLists.clear();
                mMyAdapter.notifyDataSetChanged();
            }
            onRefreshComplete(0);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
    }
}
