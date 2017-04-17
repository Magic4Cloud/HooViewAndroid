package com.easyvaas.elapp.ui.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.easyvaas.elapp.adapter.ImportNewsAdapter;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.market.MarketExponentModel;
import com.easyvaas.elapp.bean.news.ImportantNewsModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.NewsListScrollEvent;
import com.easyvaas.elapp.event.RefreshExponentEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.MainActivity;
import com.easyvaas.elapp.ui.base.BaseListRcvFragment;
import com.easyvaas.elapp.utils.ViewUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ImportantNewsListFragment extends BaseListRcvFragment {
    private static final String TAG = "ImportantNewsListFragme";
    private ImportNewsAdapter mAdapter;
    private ImportantNewsModel mImportantNewsModel = new ImportantNewsModel();
    private boolean isPageNotTop = false;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        int y = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            y = y + dy;
            if (y < ViewUtil.dp2Px(getContext(), 50) && dy < 0) {
                isPageNotTop = false;
                updateTabLayoutView();
            } else if (y > ViewUtil.dp2Px(getContext(), 50) && dy > 0) {
                isPageNotTop = true;
                updateTabLayoutView();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ImportantNewsListFragment newInstance() {
        return new ImportantNewsListFragment();
    }
    @Override
    public void iniView(View view) {
        mRecyclerView.setAdapter(mAdapter = new ImportNewsAdapter(getContext()));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        updateTabLayoutView();
        loadData(false);


        initListener();
    }

    //
    private void initListener() {
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //
                ViewGroup mViewGroup = (ViewGroup) (mRecyclerView.getChildAt(0));
                if (mViewGroup == null) {
                    return;
                }
                View mBottomView = mViewGroup.getChildAt(mViewGroup.getChildCount() - 1);
                int mBottom = mBottomView.getBottom();

                MainActivity mMainActivity = (MainActivity) getActivity();
                mMainActivity.startShowMask(mBottom - (int) ViewUtil.dp2Px(getContext(), 78));

            }
        });
    }

    @Override
    protected void loadData(boolean isLoadMore) {
        super.loadData(isLoadMore);
        getNewsList(isLoadMore);
        loadCarouseInfo(isLoadMore);
        getExponent(isLoadMore);
    }

    public void getNewsList(final boolean isLoadMore) {
        HooviewApiHelper.getInstance().getImportantNewsList(mNextPageIndex + "", 20 + "", new MyRequestCallBack<ImportantNewsModel>() {
            @Override
            public void onSuccess(ImportantNewsModel result) {
                if (result != null) {
                    if (result.getHuoyan() != null && result.getHuoyan().getChannels() != null) {
//                        Preferences.getInstance(getContext().getApplicationContext()).putJsonObject(Preferences.KEY_HOOOVIEW_EYE_TABS, result.getHuoyan().getChannels());
                    }
                    mNextPageIndex = result.getNext();
                    if (isLoadMore) {
                        mAdapter.appendNews(result);
                    } else {
                        mAdapter.setImportantNewsModel(result);
                    }
                    onRefreshComplete(result == null ? 0 : result.getCount());
                }
            }

            @Override
            public void onFailure(String msg) {
                onRefreshComplete(0);
            }
        });
    }

    public void getExponent(boolean isLoadMore) {
        HooviewApiHelper.getInstance().getExponentListNew(new MyRequestCallBack<MarketExponentModel>() {
            @Override
            public void onSuccess(MarketExponentModel result) {
                mAdapter.setExponentListModel(result);
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshExponentEvent event) {
        getExponent(false);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        getNewsList(false);
        loadCarouseInfo(false);
        getExponent(false);
    }

    /**
     * 轮播图
     */
    private void loadCarouseInfo(boolean isLoadMore) {
        String json = Preferences.getInstance(getContext().getApplicationContext())
                .getString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON);
        if (!TextUtils.isEmpty(json)) {
            BannerModel result = new Gson().fromJson(json, BannerModel.class);
            mAdapter.setBannerModel(result);
        } else {
            HooviewApiHelper.getInstance().getBannerInfo(new MyRequestCallBack<BannerModel>() {
                @Override
                public void onSuccess(BannerModel result) {
                    if (result != null && result.getData() != null && result.getData().size() > 0) {
                        mAdapter.setBannerModel(result);
                    }
                }

                @Override
                public void onError(String errorInfo) {
                    super.onError(errorInfo);
//                mSliderLayout.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(String msg) {
                    RequestUtil.handleRequestFailed(msg);
                }
            });
        }
    }

    private void updateTabLayoutView() {
        EventBus.getDefault().post(new NewsListScrollEvent(isPageNotTop));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mRecyclerView != null) {
            updateTabLayoutView();
        }
    }

}
