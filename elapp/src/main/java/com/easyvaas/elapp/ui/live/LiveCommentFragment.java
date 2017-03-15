package com.easyvaas.elapp.ui.live;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.recycler.LiveCommentAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.video.LiveCommentModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.LiveCommentEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LiveCommentFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LiveCommentFragment";
    private LiveCommentAdapter mAdapter;
    private List<LiveCommentModel.PostsEntity> data;
    private String mVid;
    protected RecyclerView mRecyclerView;
    protected TextView mTvPrompt;
    protected LinearLayout mLLEmpty;
    protected int start = 0;
    protected int count = 20;
    protected int next;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static LiveCommentFragment newInstance(String vid) {
        Bundle args = new Bundle();
        LiveCommentFragment fragment = new LiveCommentFragment();
        fragment.setArguments(args);
        args.putString("vid", vid);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVid = getArguments().getString("vid");
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_live_comment, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCommentList();
            }
        });
        mTvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        mLLEmpty = (LinearLayout) view.findViewById(R.id.ll_empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        view.findViewById(R.id.ll_input_area).setOnClickListener(this);
        mRecyclerView.setAdapter(mAdapter = new LiveCommentAdapter(getContext(), data = new ArrayList<LiveCommentModel.PostsEntity>()));
        loadCommentList();
        return view;
    }


    public void showEmptyView() {
        mLLEmpty.setVisibility(View.VISIBLE);
        mTvPrompt.setText(R.string.empty_data);
    }

    public void hideEmptyView() {
        mLLEmpty.setVisibility(View.GONE);
    }


    public void loadCommentList() {
        HooviewApiHelper.getInstance().getGoodVideoCommentList(mVid, "dateline", count + "", start + "", new MyRequestCallBack<LiveCommentModel>() {
            @Override
            public void onSuccess(LiveCommentModel result) {
                if (result != null && result.getPosts() != null) {
                    hideEmptyView();
                    data.clear();
                    data.addAll(result.getPosts());
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String msg) {
                showEmptyView();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                showEmptyView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (Preferences.getInstance(getActivity()).isLogin() && EVApplication.isLogin()) {
            switch (v.getId()) {
                case R.id.ll_input_area:
                    if (getActivity() instanceof PlayerActivity) {
                        ((PlayerActivity) getActivity()).showCommentTextBox(false);
                    }
                    break;
            }
        } else {
            LoginActivity.start(getActivity());
        }

    }

    public void sendComment(String content) {
        User user = EVApplication.getUser();
        if (user != null) {
            HooviewApiHelper.getInstance().sendVideoComment(mVid, user.getName(), user.getNickname(), user.getLogourl(), content, new MyRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Logger.d(TAG, "onSuccess: ");
                    SingleToast.show(getContext(), getString(R.string.send_success));
                    loadCommentList();
                }

                @Override
                public void onFailure(String msg) {
                    Logger.d(TAG, "onFailure: ");

                }

                @Override
                public void onError(String errorInfo) {
                    Logger.d(TAG, "onError: ");
                    super.onError(errorInfo);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LiveCommentEvent event) {
        Logger.d(TAG, "LiveCommentEvent: " + event.content);
        sendComment(event.content);
    }
}
