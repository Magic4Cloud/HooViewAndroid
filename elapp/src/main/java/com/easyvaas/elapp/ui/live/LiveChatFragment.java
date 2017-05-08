package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.recycler.WatcherCommentRcvAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.ChatLiveInputEvent;
import com.easyvaas.elapp.event.NewCommentEvent;
import com.easyvaas.elapp.event.NewGiftEvent;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.view.gift.GiftViewContainer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class LiveChatFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LiveChatFragment";
    private RecyclerView mCommentListView;
    private WatcherCommentRcvAdapter mAnchorCommentRcvAdapter;
    private List<ChatComment> mCommentList;
    private RelativeLayout mRlInput;
    private ImageView mIvGift;
    private LinearLayout mLlInputArea;
    private GiftViewContainer mGiftViewContainer;

    public static LiveChatFragment newInstance() {
        Bundle args = new Bundle();
        LiveChatFragment fragment = new LiveChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommentList = new ArrayList<>();
        mCommentList.add(new ChatComment(ChatComment.TYPE_INTO_TIPS, ""));
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_chat, container, false);
        mCommentListView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRlInput = (RelativeLayout) view.findViewById(R.id.rl_input);
        mIvGift = (ImageView) view.findViewById(R.id.iv_gift);
        mIvGift.setOnClickListener(this);
        view.findViewById(R.id.rl_input_text).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentListView.setLayoutManager(linearLayoutManager);
        mCommentListView.setItemAnimator(new DefaultItemAnimator());
        mCommentListView.setHasFixedSize(true);
        mCommentListView.setLayoutManager(linearLayoutManager);
        mCommentListView.setAdapter(mAnchorCommentRcvAdapter = new WatcherCommentRcvAdapter(getContext(), mCommentList));
        mLlInputArea = (LinearLayout) view.findViewById(R.id.ll_input_area);
        mGiftViewContainer = (GiftViewContainer) view.findViewById(R.id.GiftViewContainer);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewGiftEvent event) {
        Logger.d(TAG, "NewGiftEvent: " + event.giftEntity.getName());
        mGiftViewContainer.addAndPlayGift(event.giftEntity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewCommentEvent event) {
        Logger.d(TAG, "NewCommentEvent: " + event.chatComment.getContent());
        mCommentList.add(0, event.chatComment);
        LinearLayoutManager layoutManager = (LinearLayoutManager) mCommentListView.getLayoutManager();
        if (mCommentList.size() < 7) {
            layoutManager.setStackFromEnd(true);
        } else {
            layoutManager.setStackFromEnd(false);
        }
        mAnchorCommentRcvAdapter.notifyDataSetChanged();
        mCommentListView.scrollToPosition(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChatLiveInputEvent event) {
        if (event.action == ChatLiveInputEvent.ACTION_SHOW_INPUT) {
            mLlInputArea.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (Preferences.getInstance(getActivity()).isLogin() && EVApplication.isLogin()) {
            switch (v.getId()) {
                case R.id.rl_input_text:
                    if (getActivity() instanceof PlayerActivity) {
                        ((PlayerActivity) getActivity()).showCommentTextBox(false);
                    }
                    break;
                case R.id.iv_gift:
                    if (getActivity() instanceof PlayerActivity) {
                        ((PlayerActivity) getActivity()).showGiftToolsBar();
//                    mLlInputArea.setVisibility(View.GONE);
                    }
                    break;
            }
        } else {
            LoginActivity.start(getActivity());
        }

    }
}
