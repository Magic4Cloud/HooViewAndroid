package com.hooview.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.hooview.app.R;
import com.hooview.app.activity.WebViewActivity;
import com.hooview.app.adapter.HomeTabListAdapter;
import com.hooview.app.adapter.holder.HomeHeaderSliderHolder;
import com.hooview.app.base.BaseFragment;
import com.hooview.app.bean.CarouselInfoEntity;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.bean.video.VideoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomeMainTabFragment extends BaseFragment {

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ConvenientBanner convenientBanner;
    private LinearLayout ll_container;
    private ImageView iv_selected;

    private int mPointDis;

    private int bannerCount;

    private ScrollView homeContentScrollView;
    private List<VideoEntity> mVideoLists;
    private HomeTabListAdapter mListAdapter;

    private int currentPageIndex = 0;

    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_KEY = "extra_key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_main_tab, container, false);
        ButterKnife.bind(this, view);
        init(view);
        initBanner();
        loadHotVideoList(false);
        initSwipeRefresh();
        return view;
    }

    private void init(View view) {
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.banner);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        ll_container = (LinearLayout) view.findViewById(R.id.ll_container);
        iv_selected = (ImageView) view.findViewById(R.id.iv_selected_point);
        homeContentScrollView = (ScrollView) view.findViewById(R.id.home_content_scroll_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(manager);

        mVideoLists = new ArrayList<>();
        mListAdapter = new HomeTabListAdapter(getActivity(), mVideoLists);
        mRecyclerView.setAdapter(mListAdapter);
    }

    //下拉刷新的功能
    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.hv662d80, R.color.hv662d80, R.color.hv662d80);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageIndex = 0;
                loadHotVideoList(false);
            }
        });

        //解决滑动冲突
        homeContentScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (homeContentScrollView != null) {
                    homeContentScrollView.setEnabled(homeContentScrollView.getScrollY() == 0);
                }
            }
        });
    }

    //请求服务
    private void loadHotVideoList(final boolean isLoadMore) {
        // Live mark: Request live video first but need to request playback when last video is playback.

        //isLoadMore && mNextPageIndex > 0 ? mNextPageIndex : ApiConstant.DEFAULT_FIRST_PAGE_INDEX;
        ApiHelper.getInstance().getHotVideoList(currentPageIndex++,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
                    @Override
                    public void onSuccess(VideoEntityArray result) {


                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (currentPageIndex == 1) {
                            mVideoLists.clear();
                        }

                        mVideoLists.addAll(result.getVideos());
                        mListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

    }

    //初始化Banner
    private void initBanner() {
        //从缓存获取Banner的数据，如果为空，则再次请求获取
        String json = Preferences.getInstance(getActivity())
                .getString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON);
        if (TextUtils.isEmpty(json)) {
            loadCarouseInfo();
        } else {
            CarouselInfoEntityArray result = new Gson().fromJson(json, CarouselInfoEntityArray.class);
            refreshBanner(result);
        }
    }


    //从服务器再次加载
    private void loadCarouseInfo() {
        ApiHelper.getInstance().getCarouseInfo(new MyRequestCallBack<CarouselInfoEntityArray>() {
            @Override
            public void onSuccess(CarouselInfoEntityArray result) {
                if (result != null && result.getCount() > 0) {
                    refreshBanner(result);
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

    //已经获取到真实的数据，更新Banner
    private void refreshBanner(final CarouselInfoEntityArray array) {

        addPoint(array.getCount());
        bannerCount = array.getCount();
        convenientBanner.setPages(new CBViewHolderCreator<HomeHeaderSliderHolder>() {
            @Override
            public HomeHeaderSliderHolder createHolder() {
                return new HomeHeaderSliderHolder();
            }
        }, array.getObjects())    //设置需要切换的View
                .setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Bundle bundle = new Bundle();
                //int type = bundle.getInt(EXTRA_TYPE, -1);
                String keyValue = bundle.getString(EXTRA_KEY);
                Intent intent = null;
                int type = array.getObjects().get(position).getContent().getType();
                String title = array.getObjects().get(position).getContent().getData().getTitle();
                String url = array.getObjects().get(position).getContent().getData().getWeb_url();
                if (type == CarouselInfoEntity.TYPE_WEB) {
                    intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.EXTRA_KEY_TYPE, WebViewActivity.TYPE_ACTIVITY);
                    intent.putExtra(WebViewActivity.EXTRA_KEY_URL, url);
                    intent.putExtra(Constants.EXTRA_KEY_TITLE, title);
                }
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }
            }
        });
        if(array.getCount() < 2) {
            convenientBanner.setCanLoop(false);
            convenientBanner.setPointViewVisible(false);
        } else {
            convenientBanner.setPointViewVisible(true)    //设置指示器是否可见
                    .startTurning(5000);
        }

        convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                int currentPosition = position % bannerCount;

                int leftMargin = currentPosition
                        * mPointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_selected
                        .getLayoutParams();
                params.leftMargin = leftMargin;// 修改左边距

                // 重新设置布局参数
                iv_selected.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //添加圆点指示器
    private void addPoint(int count) {
        // 初始化小圆点
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(getActivity());
            point.setImageResource(R.drawable.shape_main_point_default);// 设置图片(shape形状)

            // 初始化布局参数, 宽高包裹内容,父控件是谁,就是谁声明的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                // 从第二个点开始设置左边距
                params.leftMargin = (int) ViewUtil.dp2Px(getActivity(), 10);
            }

            point.setLayoutParams(params);// 设置布局参数

            ll_container.addView(point);// 给容器添加圆点
        }
        iv_selected.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 移除监听,避免重复回调
                        iv_selected.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        // ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // layout方法执行结束的回调
                        mPointDis = ll_container.getChildAt(1).getLeft()
                                - ll_container.getChildAt(0).getLeft() - (int) ViewUtil.dp2Px(getActivity(), 2);
                    }
                });
    }
}
