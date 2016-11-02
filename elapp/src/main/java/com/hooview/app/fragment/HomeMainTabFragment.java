package com.hooview.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.hooview.app.R;
import com.hooview.app.activity.account.AccountActivity;
import com.hooview.app.activity.user.SearchListActivity;
import com.hooview.app.adapter.HomeTabListAdapter;
import com.hooview.app.base.BaseFragment;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.listener.OnLoadMoreListener;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomeMainTabFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    //首页的数据
    private List mHomeLists;

    private HomeTabListAdapter mListAdapter;

    private OnLoadMoreListener mLoadMoreListener;

    //Toolbar上方的
    private ImageButton iv_search;
    private ImageButton iv_account;

    private int dateCount = 0;

    private CarouselInfoEntityArray bannerEntity;

    private int mPageIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_main_tab, container, false);
        ButterKnife.bind(this, view);

        init(view);
        initSwipeRefresh();
        return view;
    }

    private void init(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mHomeLists = new ArrayList<>();

        mListAdapter = new HomeTabListAdapter(getActivity(), mHomeLists);

        //加载更多的逻辑判断
        mLoadMoreListener = new OnLoadMoreListener(manager, 0) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState, int lastPosition) {

            }

            @Override
            public void onLoadMore(int current_page) {
                mLoadMoreListener.setLoading(true);
                mPageIndex ++;
                loadHotVideoList(false);
            }

            @Override
            public void onItemSwitch(int lastPosition, int scrollType) {

            }
        };
        mRecyclerView.addOnScrollListener(mLoadMoreListener);
        mRecyclerView.setAdapter(mListAdapter);

        initBanner();

        iv_search = (ImageButton) view.findViewById(R.id.iv_search);
        iv_account = (ImageButton) view.findViewById(R.id.iv_account);
        iv_search.setOnClickListener(this);
        iv_account.setOnClickListener(this);
    }

    //下拉刷新的功能
    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.hv662d80, R.color.hv662d80, R.color.hv662d80);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageIndex = 0;
                mRecyclerView.addOnScrollListener(mLoadMoreListener);
                loadCarouseInfo();
            }
        });
    }

    //请求服务
    private void loadHotVideoList(final boolean isLoadMore) {
        // Live mark: Request live video first but need to request playback when last video is playback.

        //isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getHotVideoList(mPageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {


                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
//
                        mLoadMoreListener.resetState();
                        mLoadMoreListener.setLoading(false);


                        //重新加载数据,清除之前的数据
                        if (mPageIndex == 0) {
                            mHomeLists.clear();
                        }

                        List<VideoEntity> mResultlists = result.getVideos();

                        //添加数据
                        if (result != null && mResultlists.size() > 0) {
                            mHomeLists.addAll(mResultlists);
                        }

                        //已经是最后一页了
                        if (mResultlists.size() <= ApiConstant.DEFAULT_PAGE_SIZE) {
                            mHomeLists.add("END");
                            //停止再次加载
                            mRecyclerView.clearOnScrollListeners();
                        }

                        if (mListAdapter != null) {
                            mListAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mLoadMoreListener.setLoading(false);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        mLoadMoreListener.setLoading(false);
                    }
                }

        );

    }

    //初始化Banner

    private void initBanner() {
        //从缓存获取Banner的数据，如果为空，则再次请求获取
        String json = Preferences.getInstance(getActivity())
                .getString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON);
        if (TextUtils.isEmpty(json)) {
            loadCarouseInfo();
        } else {
            if (mListAdapter != null) {
                mHomeLists.add(new Gson().fromJson(json, CarouselInfoEntityArray.class));
                loadHotVideoList(false);
            }
        }
    }


    //从服务器再次加载,获取到数据的话，刷新页面
    private void loadCarouseInfo() {
        ApiHelper.getInstance().getCarouseInfo(new MyRequestCallBack<CarouselInfoEntityArray>() {
            @Override
            public void onSuccess(CarouselInfoEntityArray result) {
                if (result != null && result.getCount() > 0) {
                    if (mListAdapter != null) {
                        mHomeLists.add(result);
                        loadHotVideoList(false);
                    }
                } else {

                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_account) {
            startActivity(new Intent(getActivity(), AccountActivity.class));
        } else if (v.getId() == R.id.iv_search) {
            startActivity(new Intent(getActivity(), SearchListActivity.class));
        }
    }
}
