package com.easyvaas.common.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyvaas.common.widget.EmptyView;
import com.easyvaas.common.widget.R;
import com.easyvaas.common.widget.SwipRefreshLayout.SwipeRefreshLayoutEx;

/**
 * @author bian.xd
 */
public class PullToLoadView extends FrameLayout {

    private SwipeRefreshLayoutEx mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private ProgressBar mProgressBar;
    private TextView mNoMoreTv;
    private LinearLayout mBottomTipLl;
    private PullCallback mPullCallback;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    protected ScrollDirection mCurScrollingDirection;
    protected int mPrevFirstVisibleItem = 0;
    private int mLoadMoreOffset = 5;
    private boolean mIsLoadMoreEnabled = false;
    private int mHeaderCount;

    public PullToLoadView(Context context) {
        this(context, null);
    }

    public PullToLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.view_rcv_pull_load, this, true);
        mSwipeRefreshLayout = (SwipeRefreshLayoutEx) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNoMoreTv = (TextView) findViewById(R.id.tipNoMoreTv);
        mBottomTipLl = (LinearLayout) findViewById(R.id.bottomTipLl);

        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(mRecyclerView);
    }

    private void init() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.base_purplish);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayoutEx.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPullCallback) {
                    mPullCallback.onRefresh();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mCurScrollingDirection = null;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mCurScrollingDirection == null) { //User has just started a scrolling motion
                    mCurScrollingDirection = ScrollDirection.SAME;
                    mPrevFirstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                } else {
                    final int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                    if (firstVisibleItem > mPrevFirstVisibleItem) {
                        //User is scrolling up
                        mCurScrollingDirection = ScrollDirection.UP;
                    } else if (firstVisibleItem < mPrevFirstVisibleItem) {
//                        if (firstVisibleItem == 0) {
//                            if (mPrevFirstVisibleItem == 1) {
//                                getContext().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TOPIC_BAR));
//                                getContext().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TITLE_BAR));
//                            }
//                        } else if (firstVisibleItem == 1) {
//                            if (mPrevFirstVisibleItem == 2) {
//                                getContext().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TOPIC_BAR));
//                                getContext().sendBroadcast(new Intent(Constants.ACTION_SHOW_HOME_TITLE_BAR));
//                            }
//                        }
                        //User is scrolling down
                        mCurScrollingDirection = ScrollDirection.DOWN;
                        hideFootView();
                    } else {
                        mCurScrollingDirection = ScrollDirection.SAME;
                    }
                    mPrevFirstVisibleItem = firstVisibleItem;
                }

                if (mIsLoadMoreEnabled && (mCurScrollingDirection == ScrollDirection.UP)) {
                    //We only need to paginate if user scrolling near the end of the list
                    if (!mPullCallback.isLoading() && !mPullCallback.hasLoadedAllItems()) {
                        //Only trigger a load more if a load operation is NOT happening AND all the items have not been loaded
                        final int totalItemCount = mRecyclerViewHelper.getItemCount();
                        final int firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
                        final int visibleItemCount = Math
                                .abs(mRecyclerViewHelper.findLastVisibleItemPosition() - firstVisibleItem);
                        final int lastAdapterPosition = totalItemCount - 1;
                        final int lastVisiblePosition = (firstVisibleItem + visibleItemCount) - 1;
                        if (lastVisiblePosition >= (lastAdapterPosition - mLoadMoreOffset)) {
                            if (null != mPullCallback) {
                                showLoadingDataView();
                                mPullCallback.onLoadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setComplete() {
        hideFootView();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showNoMoreDataView() {
        mProgressBar.setVisibility(GONE);
        mBottomTipLl.setVisibility(GONE);
        mNoMoreTv.setText(getResources().getString(R.string.no_more_data));
        mNoMoreTv.setVisibility(VISIBLE);
    }

    public void hideFootView() {
        mBottomTipLl.setVisibility(GONE);
    }

    public void showLoadingDataView() {
        mBottomTipLl.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
        mNoMoreTv.setText(getResources().getString(R.string.loading_data));
        mNoMoreTv.setVisibility(VISIBLE);
    }

    public void initLoad() {
        if (null != mPullCallback) {
            showRefreshingLoadingIcon();
            mPullCallback.onRefresh();
        }
    }

    public void showRefreshingLoadingIcon() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public void setColorSchemeResources(int... colorResIds) {
        mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
    }

    public void setSwipeRefreshDisable() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    public void setPullCallback(PullCallback mPullCallback) {
        this.mPullCallback = mPullCallback;
    }

    public void setLoadMoreOffset(int mLoadMoreOffset) {
        this.mLoadMoreOffset = mLoadMoreOffset;
    }

    public void isLoadMoreEnabled(boolean mIsLoadMoreEnabled) {
        this.mIsLoadMoreEnabled = mIsLoadMoreEnabled;
    }

    public int getHeaderCount() {
        return mHeaderCount;
    }

    public void setHeaderCount(int mHeaderCount) {
        this.mHeaderCount = mHeaderCount;
    }

    public EmptyView getEmptyView() {
        return mEmptyView;
    }

    public SwipeRefreshLayoutEx getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }
}
