/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.user;

import com.easyvaas.elapp.base.BaseRvcActivity;

public class FriendsUserInfoActivity extends BaseRvcActivity {
//    private FriendsUserInfoAdapter mFriendsUserInfoAdapter;
//    private List mUserInfoVideos;
//    private TextView mFollowTv;
//    private String mUserId;
//    private User mCurrentUser;
//    private View mChatBar;
//    private TextView mPullBlackTv;
//    private int mNextPage;
//
//    private FriendsUserInfoAdapter.OnClickUserItemViewListener mOnClickUserItemViewListener =
//            new FriendsUserInfoAdapter.OnClickUserItemViewListener() {
//                @Override
//                public void onUserInfoBackClick() {
//                    onBackPressed();
//                }
//
//                @Override
//                public void onFansItemClick() {
//                    Intent fansIntent = new Intent(getApplicationContext(), FansListActivity.class);
//                    fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID, mCurrentUser.getName());
//                    startActivity(fansIntent);
//                }
//
//                @Override
//                public void onFollowItemClick() {
//                    Intent followIntent = new Intent(getApplicationContext(), FollowersListActivity.class);
//                    followIntent.putExtra(Constants.EXTRA_KEY_USER_ID, mCurrentUser.getName());
//                    startActivity(followIntent);
//                }
//
//                @Override
//                public void onUserPhotoClick() {
//                    Intent showImage = new Intent(FriendsUserInfoActivity.this,
//                            ShowBigImageActivity.class);
//                    showImage.putExtra(ShowBigImageActivity.REMOTE_IMAGE_URL, mCurrentUser.getLogourl());
//                    startActivity(showImage);
//                }
//
//                @Override
//                public void onShareItemClick() {
//                    showShareUserInfoPanel();
//                }
//            };
//
//    private OnBlackListChangeListener mBlackListChangeListener = new OnBlackListChangeListener() {
//        @Override
//        public void onAddUserSuccess() {
//            updateViewByBlackState(true);
//        }
//
//        @Override
//        public void onAddFailed() {
//
//        }
//
//        @Override
//        public void onRemoveUserSuccess() {
//            updateViewByBlackState(false);
//        }
//
//        @Override
//        public void onRemoveUserFailed() {
//
//        }
//    };
//
//    private void showShareUserInfoPanel() {
//        String title = String.format(getString(R.string.share_user_title), mCurrentUser.getNickname());
//        String content = String.format(getString(R.string.share_user_content), mCurrentUser.getNickname());
//        String logoUrl = mCurrentUser.getLogourl();
//        String shareUrl = mCurrentUser.getShare_url();
//        ShareContent shareContent = new ShareContentWebpage(title, content, shareUrl, logoUrl);
//        ShareHelper.getInstance(this).showShareBottomPanel(shareContent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends_user_info);
//        Intent intent = getIntent();
//        mUserId = getIntent().getStringExtra(Constants.EXTRA_KEY_USER_ID);
//        String imUsername = intent.getStringExtra(Constants.EXTRA_KEY_USER_IM_ID);
//        initView();
//        if (!TextUtils.isEmpty(mUserId)) {
//            getUserInfo(mUserId, false);
//        } else if (!TextUtils.isEmpty(imUsername)) {
//            getUserInfo(imUsername, true);
//        } else {
//            finish();
//        }
//        mPullToLoadRcvView.setSwipeRefreshDisable();
//        mPullToLoadRcvView.setHeaderCount(1);
//    }
//
//    @Override
//    protected void loadData(boolean isLoadMore) {
//        super.loadData(isLoadMore);
//        loadData(mNextPage);
//    }
//
//    private View.OnClickListener mViewOnclick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.follow_ll:
//                    String textFollowTv = mFollowTv.getText().toString();
//                    final boolean isFollowed = !textFollowTv.equals(getString(R.string.follow));
//                    ApiUtil.userFollow(FriendsUserInfoActivity.this, mUserId, !isFollowed, mFollowTv,
//                            new MyRequestCallBack<String>() {
//                                @Override
//                                public void onSuccess(String result) {
//                                    updateViewByFollowState(!isFollowed);
//                                }
//
//                                @Override
//                                public void onFailure(String msg) {
//
//                                }
//                            });
//                    break;
//                case R.id.chat_ll:
//                    if (mCurrentUser == null || TextUtils.isEmpty(mCurrentUser.getImuser())) {
//                        return;
//                    }
//                    ChatManager.getInstance().chatToUser(mCurrentUser.getImuser(),
//                            mCurrentUser.getFollowed() == User.FOLLOWED);
//                    break;
//                case R.id.block_ll:
//                    if (getString(R.string.pull_black).equals((mPullBlackTv.getText().toString()))) {
//                        ChatManager.getInstance().addUserToBlackList(FriendsUserInfoActivity.this,
//                                mCurrentUser.getImuser(), mBlackListChangeListener);
//                    } else {
//                        ChatManager.getInstance().removeUserFromBlackList(FriendsUserInfoActivity.this,
//                                mCurrentUser.getImuser(), mBlackListChangeListener);
//                    }
//                    break;
//            }
//        }
//    };
//
//    public void initView() {
//        findViewById(R.id.chat_ll).setOnClickListener(mViewOnclick);
//        findViewById(R.id.block_ll).setOnClickListener(mViewOnclick);
//        findViewById(R.id.follow_ll).setOnClickListener(mViewOnclick);
//        mFollowTv = (TextView) findViewById(R.id.follow_tv);
//        mPullBlackTv = (TextView) findViewById(R.id.block_tv);
//        mChatBar = findViewById(R.id.chat_bottom_bar_ll);
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
//        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        mPullToLoadRcvView.getRecyclerView().setLayoutManager(layoutManager);
//        mUserInfoVideos = new ArrayList();
//        mFriendsUserInfoAdapter = new FriendsUserInfoAdapter(mUserInfoVideos,
//                FriendsUserInfoActivity.this);
//        mFriendsUserInfoAdapter.setOnClickUserItemViewListener(mOnClickUserItemViewListener);
//        mPullToLoadRcvView.getRecyclerView().setAdapter(mFriendsUserInfoAdapter);
//        mFriendsUserInfoAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Object obj = mUserInfoVideos.get(position);
//                if (obj == null) {
//                    return;
//                }
//                if (obj instanceof VideoEntity) {
//                    Utils.watchVideo(getApplicationContext(), (VideoEntity) obj);
//                }
//            }
//        });
//    }
//
//    private void getUserInfo(String userId, final boolean isImUser) {
//        mEmptyView.showLoadingView();
//        MyRequestCallBack<User> callBack = new MyRequestCallBack<User>() {
//            @Override
//            public void onSuccess(User result) {
//                mEmptyView.hide();
//                if (result != null && !isFinishing()) {
//                    if (isImUser) {
//                        mUserId = result.getName();
//                    }
//                    if (!result.getName().equals(Preferences.getInstance(getApplicationContext())
//                            .getUserNumber())) {
//                        mChatBar.setVisibility(View.VISIBLE);
//                    }
//                    mCurrentUser = result;
//                    mUserInfoVideos.add(0, mCurrentUser);
//                    updateViewByFollowState(mCurrentUser.getFollowed() == UserEntity.FOLLOWED);
//                    updateViewByBlackState(ChatManager.getInstance().isInBlackList(result.getImuser()));
//                    mFriendsUserInfoAdapter.notifyDataSetChanged();
//                    ChatManager.getInstance().saveContact(result.getName(), result.getNickname(),
//                            result.getLogourl(), result.getImuser());
//                    loadData(false);
//                }
//            }
//
//            @Override
//            public void onError(String errorInfo) {
//                super.onError(errorInfo);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                RequestUtil.handleRequestFailed(msg);
//            }
//        };
//
//        if (isImUser) {
//            ApiHelper.getInstance().getUserInfoByImuser(userId, callBack);
//        } else {
//            ApiHelper.getInstance().getUserInfo(userId, callBack);
//        }
//    }
//
//    public void loadData(final int pageIndex) {
//        if (pageIndex == ApiConstant.DEFAULT_FIRST_PAGE_INDEX) {
//            mEmptyView.showLoadingView();
//        }
//        ApiHelper.getInstance().getUserVideoList(mUserId, pageIndex,
//                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<VideoEntityArray>() {
//                    @Override
//                    public void onSuccess(VideoEntityArray result) {
//                        if (isFinishing()) {
//                            return;
//                        }
//                        if (result != null) {
//                            mNextPage = result.getNext();
//                            mUserInfoVideos.addAll(result.getVideos());
//                            mFriendsUserInfoAdapter.notifyDataSetChanged();
//                        }
//                        if (pageIndex == ApiConstant.DEFAULT_FIRST_PAGE_INDEX
//                                && mUserInfoVideos.size() == 1) {
//                            mEmptyView.showEmptyView();
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mEmptyView.getLayoutParams();
//                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
//                            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.user_center_empty_view_margin_top);
//                            mEmptyView.setLayoutParams(layoutParams);
//                        } else {
//                            onRefreshComplete(result == null ? 0 : result.getCount());
//                        }
//                    }
//
//                    @Override
//                    public void onError(String errorInfo) {
//                        super.onError(errorInfo);
//                        mEmptyView.showErrorView();
//                        onRefreshComplete(0);
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        RequestUtil.handleRequestFailed(msg);
//                        mEmptyView.showErrorView();
//                        onRefreshComplete(0);
//                    }
//                });
//    }
//
//    private void updateViewByFollowState(boolean isFollowed) {
//        if (isFollowed) {
//            mFollowTv.setText(R.string.follow_cancel);
//            mFollowTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
//                    R.drawable.personal_icon_added), null, null, null);
//        } else {
//            mFollowTv.setText(R.string.follow);
//            mFollowTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
//                    R.drawable.personal_icon_add), null, null, null);
//        }
//    }
//
//    private void updateViewByBlackState(boolean isInBlackList) {
//        if (isInBlackList) {
//            mPullBlackTv.setText(R.string.remove_black);
//            mPullBlackTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
//                    R.drawable.personal_icon_added), null, null, null);
//        } else {
//            mPullBlackTv.setText(R.string.pull_black);
//            mPullBlackTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
//                    R.drawable.personal_icon_add), null, null, null);
//        }
//    }
}
