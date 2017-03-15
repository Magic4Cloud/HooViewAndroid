package com.easyvaas.elapp.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.recycler.PullToLoadView;
import com.easyvaas.elapp.adapter.recycler.MyStockNewsListAdapter;
import com.easyvaas.elapp.base.BaseRvcFragment;
import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.bean.news.MyStockNewsModel;
import com.easyvaas.elapp.bean.user.CollectListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.RefreshStockEvent;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.search.SearchStockActivity;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MyStockNewsListFragment extends BaseRvcFragment {
    private static final String TAG = "MyStockNewsListFragment";
    protected MyStockNewsListAdapter mMyStockNewsListAdapter;
    List<MyStockNewsModel.NewsEntity> mNewsEntityList = new ArrayList();
    protected RecyclerView mRecyclerView;
    private RelativeLayout mRlEmptyView;
    private TextView mTvPrompt;
    private TextView mTvAddBtn;
    private ImageView mIvIcon;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private boolean isHaveHeader;
    private String mMyStockCode;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            MyStockNewsListFragment.this.onRefresh();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_my_stock, null);
        mPullToLoadRcvView = (PullToLoadView) view.findViewById(R.id.pull_load_view);
        mRecyclerView = mPullToLoadRcvView.getRecyclerView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRlEmptyView = (RelativeLayout) view.findViewById(R.id.rl_empty);
        mTvAddBtn = (TextView) view.findViewById(R.id.tv_add);
        mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        mTvAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GlobalContentEditActivity.start(getContext(),getActivity().getResources().getString(R.string.navigation_title_news));
                SearchStockActivity.start(getActivity());
            }
        });
        mMyStockCode = Preferences.getInstance(getActivity()).getString(Preferences.KEY_HOOOVIEW_SELECT_STOCK);
        iniView(view);
        return view;
    }

    public void iniView(View view) {
        mRecyclerView.setAdapter(mMyStockNewsListAdapter = new MyStockNewsListAdapter(getContext(), mNewsEntityList));
        mNewsEntityList.add(new MyStockNewsModel.NewsEntity(true));
        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        if (TextUtils.isEmpty(mMyStockCode)) {
            getMyStockList();
        } else {
            getNewsList(isLoadMore);
        }
    }

    private void getMyStockList() {
        ApiHelper.getInstance().getCollectList(ApiConstant.COLLECT_TYPE_STOCK, new MyRequestCallBack<CollectListModel>() {
            @Override
            public void onSuccess(CollectListModel result) {
                if (result != null && !TextUtils.isEmpty(result.getCollectlist())) {
                    mMyStockCode = result.getCollectlist();
                    getNewsList(false);
                } else {
                    showMyStockEmptyView();
                }
            }

            @Override
            public void onFailure(String msg) {
                showMyStockEmptyView();

            }

            @Override
            public void onError(String errorInfo) {
                showMyStockEmptyView();
            }
        });
    }

    private void getNewsList(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getMyStockNewsList(mMyStockCode, start + "", count + "", new MyRequestCallBack<MyStockNewsModel>() {
            @Override
            public void onSuccess(MyStockNewsModel result) {
                if (result != null&&result.getNews().size()>0) {
                    if (!isLoadMore) {
                        isHaveHeader = true;
                        mNewsEntityList.clear();
                        mNewsEntityList.add(new MyStockNewsModel.NewsEntity(true));
                    }
                    mNextPageIndex = result.getNext();
                    mNewsEntityList.addAll(result.getNews());
                    mMyStockNewsListAdapter.notifyDataSetChanged();
                    onRefreshComplete(result == null ? 0 : result.getCount());
                }else{
                    showNewsEmptyView();
                }
            }

            @Override
            public void onFailure(String msg) {
                if (!isLoadMore) {
                    showNewsEmptyView();
                }
            }

            @Override
            public void onError(String errorInfo) {
                if (!isLoadMore) {
                    showNewsEmptyView();
                }
            }
        });
    }

    public void onRefresh() {
        getNewsList(false);
    }

    public void showMyStockEmptyView() {
        mRlEmptyView.setVisibility(View.VISIBLE);
        mTvAddBtn.setVisibility(View.VISIBLE);
        mTvPrompt.setText(R.string.my_selected_stock_empty_prompt);
        mIvIcon.setImageResource(R.drawable.ic_smile);
    }

    public void showNewsEmptyView() {
        mRlEmptyView.setVisibility(View.VISIBLE);
        mTvAddBtn.setVisibility(View.GONE);
        mTvPrompt.setText(R.string.my_news_empty);
        mIvIcon.setImageResource(R.drawable.ic_smile);
    }

    public void hideEmptyView() {
        mRlEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshStockEvent event) {
        if (TextUtils.isEmpty(mMyStockCode)) {
            getMyStockList();
        } else {
            getNewsList(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<ExponentModel> event) {
        for(ExponentModel ex:event){
            if(!TextUtils.isEmpty(mMyStockCode)){
                mMyStockCode = mMyStockCode+","+ex.getSymbol();
            }else{
                mMyStockCode = ex.getSymbol();
            }
            Preferences.getInstance(getActivity()).putString(ex.getName(),ex.getSymbol());
        }
        Preferences.getInstance(getActivity()).putString(Preferences.KEY_HOOOVIEW_SELECT_STOCK,mMyStockCode);
        getNewsList(false);
    }

}
