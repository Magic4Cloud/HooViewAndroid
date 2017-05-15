package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.chat.ChatComment;
import com.easyvaas.elapp.bean.user.UserPageInfo;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.HeaderDescriptionEvent;
import com.easyvaas.elapp.event.LiveCommentEvent;
import com.easyvaas.elapp.event.LiveSearchStockEvent;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.live.livenew.fragment.VideoCommentFragment;
import com.easyvaas.elapp.ui.live.livenew.fragment.VideoRecommendFragment;
import com.easyvaas.elapp.ui.pay.CashInActivity;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.PlayerStateView;
import com.easyvaas.elapp.view.base.BottomSendView;
import com.easyvaas.elapp.view.live.PlayerNeedPayView;
import com.easyvaas.elapp.view.live.PlayerPayDialogFragment;
import com.easyvaas.elapp.view.live.PlayerPayDialogFragment.PayOrRechargeListener;
import com.easyvaas.sdk.player.EVPlayer;
import com.easyvaas.sdk.player.PlayerConstants;
import com.easyvaas.sdk.player.base.EVPlayerBase;
import com.easyvaas.sdk.player.base.EVPlayerParameter;
import com.easyvaas.sdk.player.base.EVVideoView;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PlayerActivity extends BasePlayerActivity implements View.OnClickListener, PayOrRechargeListener, BottomSendView.OnBottomInputListener {
    private static final String TAG = "PlayerActivity";

    @BindView(R.id.player_video_title)
    TextView mPlayerVideoTitle;
    @BindView(R.id.player_user_header)
    RoundImageView mPlayerUserHeader;
    @BindView(R.id.player_user_name)
    TextView mPlayerUserName;
    @BindView(R.id.player_watch_counts)
    TextView mPlayerWatchCounts;
    @BindView(R.id.player_user_follow_button)
    TextView mPlayerUserFollowButton;
    @BindView(R.id.player_user)
    RelativeLayout mPlayerUser;
    @BindView(R.id.player_tablayout)
    SlidingTabLayout mPlayerTablayout;
    @BindView(R.id.player_black_view)
    View mPlayerBlackView;
    @BindView(R.id.player_viewpager)
    ViewPager mPlayerViewpager;
    @BindView(R.id.player_bottom_send)
    BottomSendView mPlayerBottomSend;
    @BindView(R.id.player_appbar_layout)
    AppBarLayout mAppBarLayout;
    private String[] mTitles;
    private Fragment[] mFragments;

    private TextView mTvEndTime;
    private TextView mTvCurTime;
    private ImageView mIvPlayState;
    private ImageView mIv_all_screen;
    private SeekBar mSeekBar;
    private FrameLayout mFlPlayer;
    private PlayerStateView mPlayerStateView;
    private EVPlayer mEVPlayer;
    private EVVideoView mVideoView;
    private ImageView ivShare;
    private ImageView ivBack;
    private PlayerNeedPayView mPlayerNeedPayView;
    private boolean isLandscape = false;
    private boolean mIsPlayLive;
    private int payCounts;// 付费的金额
    private boolean isNeedPayVideo; // 是否是付费视频
    private CompositeSubscription mCompositeSubscription;
    private Unbinder mUnbinder;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable runnableHideMediaController = new Runnable() {
        @Override
        public void run() {
            showMediaController(false);
        }
    };

    public static void start(Context context, String videoId, int liveType, int mode) {
        Intent starter = new Intent(context, PlayerActivity.class);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_ID, videoId);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, liveType == VideoEntity.IS_LIVING);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, mode == VideoEntity.MODE_GOOD_VIDEO);
        context.startActivity(starter);
    }

    public static void start(Context context, String videoId, int liveType, int mode, int permisson) {
        Intent starter = new Intent(context, PlayerActivity.class);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_ID, videoId);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, liveType == VideoEntity.IS_LIVING);
        starter.putExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, mode == VideoEntity.MODE_GOOD_VIDEO);
        starter.putExtra(AppConstants.PARAMS, permisson == 7); //是否付费
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mUnbinder = ButterKnife.bind(this);
        mVideoId = getIntent().getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        mIsPlayLive = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, false);
        mIsGoodVideo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, false);
        isNeedPayVideo = getIntent().getBooleanExtra(AppConstants.PARAMS, false);
        if (TextUtils.isEmpty(mVideoId)) {
            finish();
            return;
        }
        mCurrentVideo = new VideoEntity();
        mCurrentVideo.setVid(mVideoId);
        initView();
        initPlayer();
        loadVideoInfo();
        addVideoToHistory();
    }

    private void initView() {
        mCompositeSubscription = new CompositeSubscription();
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mTvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo((long) (mVideoView.getDuration() * ((float) progress / (float) 1000)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mIvPlayState = (ImageView) findViewById(R.id.iv_play_state);
        mIvPlayState.setOnClickListener(this);
        mPlayerStateView = (PlayerStateView) findViewById(R.id.playerStateView);
        findViewById(R.id.iv_back).setOnClickListener(this);
        mIv_all_screen = (ImageView) findViewById(R.id.iv_all_screen);
        mIv_all_screen.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        mVideoView = (EVVideoView) findViewById(R.id.video_view);
        mFlPlayer = (FrameLayout) findViewById(R.id.fl_player);
        mFlPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayLive) return;
                if (!getMediaControllerIsShow()) {
                    showMediaController(true);
                    hideMediaControllerDelay();
                } else {
                    showMediaController(false);
                    mHandler.removeCallbacks(runnableHideMediaController);
                }
            }
        });
        //init viewPager
        if (mIsPlayLive) {
            showMediaController(false);
        } else {
            showMediaController(true);
        }
        mPlayerNeedPayView = (PlayerNeedPayView) findViewById(R.id.player_pay_mask);
        mPlayerNeedPayView.setPayMaskOnclickListener(new PlayerNeedPayView.PayMaskOnclickListener() {
            @Override
            public void goShare() {
                shareVideo();
            }

            @Override
            public void goBack() {
                finish();
            }

            @Override
            public void goBuy() {
                if (EVApplication.isLogin())
                    PlayerPayDialogFragment.newInstance(payCounts).show(getSupportFragmentManager(), "");
                else
                    LoginActivity.start(PlayerActivity.this);
            }
        });
        if (isNeedPayVideo)
            mPlayerNeedPayView.setVisibility(View.VISIBLE);

        initTabAndPager(mIsGoodVideo);
        mPlayerBottomSend.setOnBottomInputListener(this);
        if (null != mEVPlayer) {
            mEVPlayer.onStart();
        }
    }

    /**
     * 初始化Tab 根据是否是精品类型
     */
    private void initTabAndPager(boolean isGoodVideo) {
        if (isGoodVideo) {
            mTitles = getResources().getStringArray(R.array.play_tab_good_video);
            mFragments = new Fragment[]{
                    VideoRecommendFragment.newInstance(mVideoId),
                    VideoCommentFragment.newInstance(mVideoId),
                    DataFragment.newInstance(),
                    BookPlayFragment.newInstance()
            };
            mPlayerBottomSend.setType(BottomSendView.TYPE_NONE);
        } else {
            mTitles = getResources().getStringArray(R.array.play_tab);
            mFragments = new Fragment[]{
                    LiveChatFragment.newInstance(),
                    DataFragment.newInstance(),
                    BookPlayFragment.newInstance()
            };
            mPlayerBlackView.setVisibility(View.VISIBLE);
            mPlayerBottomSend.setType(BottomSendView.TYPE_CHAT);

        }
        mPlayerViewpager.setOffscreenPageLimit(4);
        mPlayerViewpager.setAdapter(new PlayerPageAdapter(getSupportFragmentManager()));
        mPlayerTablayout.setViewPager(mPlayerViewpager);
        mPlayerViewpager.addOnPageChangeListener(mOnPageChangeListener);
    }


    private void initPlayer() {
        mEVPlayer = new EVPlayer(this);
        EVPlayerParameter.Builder builder = new EVPlayerParameter.Builder();
        builder.setLive(mIsPlayLive);
        mEVPlayer.setParameter(builder.build());
        mEVPlayer.setVideoView(mVideoView);
        mEVPlayer.setOnPreparedListener(mOnPreparedListener);
        mEVPlayer.setOnCompletionListener(mOnCompletionListener);
        mEVPlayer.setOnInfoListener(mOnInfoListener);
        mEVPlayer.setOnErrorListener(mOnErrorListener);
        mEVPlayer.onCreate();
    }


    /**
     * 添加观看记录
     */
    private void addVideoToHistory() {
        if (EVApplication.isLogin()) {
            RetrofitHelper.getInstance().getService()
                    .addWatchVideoInfo(EVApplication.getUser().getName(), EVApplication.getUser().getSessionid(), mVideoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel noResponeBackModel) {

                        }

                        @Override
                        public void OnFailue(String msg) {

                        }
                    });
        }
    }


    /**
     * 余额足够 直接购买
     */
    @Override
    public void payForVideo() {

        RetrofitHelper.getInstance().getService()
                .payForVideo(Preferences.getInstance(this).getSessionId(), mVideoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        ApiUtil.getAssetInfo(PlayerActivity.this);
                        SingleToast.show(PlayerActivity.this, R.string.video_pay_success);
                        startWatchLive();
                        mPlayerNeedPayView.setVisibility(View.GONE);
                        mIv_all_screen.setEnabled(true);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(PlayerActivity.this, R.string.hooview_coin_not_enough);
                    }
                });
    }

    /**
     * 余额不足 需要跳转充值
     */
    @Override
    public void skipToReCharge() {
        CashInActivity.start(this);
    }


    /**
     * 获取视频信息
     */
    private void loadVideoInfo() {
        ApiHelper.getInstance().getWatchVideo(mVideoId, "",
                new MyRequestCallBack<VideoEntity>() {
                    @Override
                    public void onSuccess(VideoEntity result) {
                        if (result != null) {
                            mCurrentVideo = result;
                            if (result.getPermission() == 7 && result.getPaid() == 0) {
                                mPlayerNeedPayView.setVisibility(View.VISIBLE); // 显示 付费蒙层
                                mPlayerNeedPayView.setPlayerPayCounts(result.getPrice());
                                payCounts = result.getPrice();
                                mIv_all_screen.setEnabled(false);
                            } else {
                                mIv_all_screen.setEnabled(true);
                                mPlayerNeedPayView.setVisibility(View.GONE);
                                startWatchLive();
                            }
                            if (result.getPermission() == 7)
                                isNeedPayVideo = true;
                            updateVideoInfo(result);
                            // chatServerInit(true);
                            mCurrentVideo.setVip(1); // 暂时默认都是大V直播
                            getUserInfo(result.getName()); // 获取用户信息
                        }
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        //                        loadVideoInfoError(errorInfo);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        //                        loadVideoInfoError(msg);
                    }
                });
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(String personid) {
        if (EVApplication.isLogin()) {
            Subscription subscription = RetrofitHelper.getInstance().getService()
                    .getUserPageInfo(EVApplication.getUser().getName(), EVApplication.getUser().getSessionid(), personid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<UserPageInfo>() {
                        @Override
                        public void OnSuccess(UserPageInfo userPageInfo) {
                            if (userPageInfo.getFollowed() == 1) {
                                mPlayerUserFollowButton.setText(R.string.user_followed);
                                mPlayerUserFollowButton.setSelected(true);
                            } else {
                                mPlayerUserFollowButton.setText(R.string.user_follow);
                                mPlayerUserFollowButton.setSelected(false);
                            }
                            if (userPageInfo.getVip() == 1)
                                mCurrentVideo.setVip(1);
                            else
                                mCurrentVideo.setVip(0);
                        }

                        @Override
                        public void OnFailue(String msg) {
                        }
                    });
            mCompositeSubscription.add(subscription);
        }
    }

    //TODO 更新头像，用户名等
    private void updateVideoInfo(final VideoEntity result) {
        if (mCurrentVideo.getLiving() == VideoEntity.IS_LIVING) {
            showMediaController(false);
            mIv_all_screen.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
        } else {
            showMediaController(true);
        }
        Utils.showImage(result.getLogourl(), R.drawable.account_bitmap_user, mPlayerUserHeader);
        mPlayerUserName.setText(result.getNickname());
        mPlayerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toUserPager(PlayerActivity.this, result.getName(), result.getVip());
            }
        });
        mPlayerVideoTitle.setText(result.getTitle());
        if (isNeedPayVideo)  // 付费视频暂时隐藏分享
            ivShare.setVisibility(View.GONE);
        if (result.getWatch_count() >= 10000) {
            mPlayerWatchCounts.setText(String.format(getString(R.string.video_watch_count2), StringUtil.formatTenThousand(result.getWatch_count())));
        } else {
            mPlayerWatchCounts.setText(String.format(getString(R.string.video_watch_count), result.getWatch_count() + ""));
        }
        mPlayerUserFollowButton.setOnClickListener(this);
        if (mIsGoodVideo)
            EventBus.getDefault().post(new HeaderDescriptionEvent(result.getDescription())); //发送简介信息
    }

    /**
     * 开始播放
     */
    private void startWatchLive() {
        if (TextUtils.isEmpty(mVideoId)) {
            SingleToast.show(this, R.string.msg_video_url_null);
            return;
        }
        mPlayerStateView.showLoadingView();
        mEVPlayer.watchStart(mCurrentVideo.getUri());
    }


    private EVPlayerBase.OnPreparedListener mOnPreparedListener = new EVPlayerBase.OnPreparedListener() {
        @Override
        public boolean onPrepared() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long duration = mVideoView.getDuration();
                    mTvCurTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), curPosition));
                    mTvEndTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), duration));
                    mSeekBar.setProgress((int) (1000 * ((float) curPosition / (float) duration)));
                    mVideoView.seekTo((long) (mVideoView.getDuration() * ((float) mSeekBar.getProgress() / (float) 1000)));
                    mPlayerStateView.hideLoadingView();
                }
            });
            return true;
        }
    };

    private EVPlayerBase.OnCompletionListener mOnCompletionListener = new EVPlayerBase.OnCompletionListener() {
        @Override
        public boolean onCompletion() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopPlayerAndShowEndView();
                }
            });
            return true;
        }
    };

    private EVPlayerBase.OnInfoListener mOnInfoListener = new EVPlayerBase.OnInfoListener() {
        @Override
        public boolean onInfo(int what, int extra) {
            switch (what) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFinishing()) {
                                mPlayerStateView.showLoadingView();
                            }
                        }
                    });
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPlayerStateView.hideLoadingView();
                        }
                    });
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mEVPlayer) {
            mEVPlayer.onResume();
        }
    }

    long curPosition;  // Aya : 2017/5/8 暂时这样写

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mEVPlayer) {
            mVideoView.pause();
            mIvPlayState.setSelected(true);
            curPosition = mVideoView.getCurrentPosition();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mEVPlayer) {
            mEVPlayer.onStop();
            mVideoView.pause();
            mIvPlayState.setSelected(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != mEVPlayer) {
                mEVPlayer.onDestroy();
            }
        } catch (Exception e) {

        }
        ;
        mCompositeSubscription.unsubscribe();
        mUnbinder.unbind();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //        // TODO Not work, need to improve.
        //        String vid = intent.getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        //        if (mVideoId.equals(vid)) {
        //        } else if (!TextUtils.isEmpty(vid)) {
        ////            resetChatData();
        //            startWatchLive();
        //        }
    }

    private void stopPlayerAndShowEndView() {
        //        mVideoView.stopPlayback();
        mIvPlayState.setSelected(true);
        if (mIsPlayLive) {
            mPlayerStateView.showEndView(getString(R.string.play_live_end_prompt));
        } else {
            mPlayerStateView.showEndView(getString(R.string.play_live_video_end_prompt));
        }
    }

    private EVPlayerBase.OnErrorListener mOnErrorListener = new EVPlayerBase.OnErrorListener() {
        @Override
        public boolean onError(int what, int extra) {
            switch (what) {
                case PlayerConstants.EV_PLAYER_ERROR_SDK_INIT:
                    break;
                default:
                    mPlayerStateView.hideLoadingView();
                    break;
            }

            return true;
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.d(TAG, "onConfigurationChanged: ");
        changeVideoSize(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (isLandscape) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    finish();
                }
                break;
            case R.id.iv_all_screen:
                changeScreenOrientation();
                break;
            case R.id.player_user_follow_button:  //关注与取消关注
                if (Preferences.getInstance(this).isLogin() && EVApplication.isLogin()) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        mPlayerUserFollowButton.setText(R.string.user_follow);
                        SingleToast.show(getApplicationContext(), getString(R.string.cancel_follow_sccuess));
                    } else {
                        v.setSelected(true);
                        mPlayerUserFollowButton.setText(R.string.user_followed);
                        SingleToast.show(getApplicationContext(), getString(R.string.follow_sccuess));
                    }
                    ApiUtil.userFollow(PlayerActivity.this, mCurrentVideo.getName(), v.isSelected(), v);
                } else {
                    LoginActivity.start(this);
                }

                break;
            case R.id.iv_play_state:
                if (mIvPlayState.isSelected()) {
                    mIvPlayState.setSelected(false);
                    mVideoView.resume();
                } else {
                    mIvPlayState.setSelected(true);
                    mVideoView.pause();
                    curPosition = mVideoView.getCurrentPosition();
                }
                break;
            case R.id.iv_share:
                shareVideo();
                break;
        }
    }

    //全屏切换
    private void changeScreenOrientation() {
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 横竖屏切换动态改变大小
     */
    private void changeVideoSize(Configuration newConfig) {
        if (newConfig.screenHeightDp < newConfig.screenWidthDp) {
            mPlayerBottomSend.setVisibility(View.GONE);
            Logger.d(TAG, "changeVideoSize:screenHeightDp " + newConfig.screenHeightDp);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mFlPlayer.getLayoutParams();
            layoutParams.height = (int) ViewUtil.dp2Px(getApplicationContext(), newConfig.screenHeightDp) + getStatusHeight();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            mFlPlayer.setLayoutParams(layoutParams);
            isLandscape = true;
            mIv_all_screen.setImageResource(R.drawable.btn_half_screen_n);
        } else {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mFlPlayer.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) ViewUtil.dp2Px(getApplicationContext(), 200);
            mFlPlayer.setLayoutParams(layoutParams);
            mIv_all_screen.setImageResource(R.drawable.btn_full_screen_n);
            isLandscape = false;
            if (mIsGoodVideo) {  // 精品视频 如果是第一个简介 不显示评论
                if (mPlayerViewpager.getCurrentItem() == 0)
                    mPlayerBottomSend.setVisibility(View.GONE);
            } else
                mPlayerBottomSend.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示视频控制器的状态
     */
    public void showMediaController(boolean isShow) {
        if (isShow) {
            long curPosition = mVideoView.getCurrentPosition();
            long duration = mVideoView.getDuration();
            mTvCurTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), curPosition));
            mTvEndTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), duration));
            mSeekBar.setProgress((int) (1000 * ((float) curPosition / (float) duration)));
            mSeekBar.setVisibility(View.VISIBLE);
            mTvCurTime.setVisibility(View.VISIBLE);
            mTvEndTime.setVisibility(View.VISIBLE);
            mIvPlayState.setVisibility(View.VISIBLE);
            mIv_all_screen.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
            hideMediaControllerDelay();
        } else {
            mSeekBar.setVisibility(View.INVISIBLE);
            mTvCurTime.setVisibility(View.INVISIBLE);
            mTvEndTime.setVisibility(View.INVISIBLE);
            mIvPlayState.setVisibility(View.INVISIBLE);
            mIv_all_screen.setVisibility(View.INVISIBLE);
            ivShare.setVisibility(View.INVISIBLE);
            ivBack.setVisibility(View.INVISIBLE);
        }
        if (isNeedPayVideo)
            ivShare.setVisibility(View.GONE);
    }

    private boolean getMediaControllerIsShow() {
        if (ivBack.getVisibility() == View.VISIBLE) return true;
        else return false;
    }

    public void hideMediaControllerDelay() {
        mHandler.removeCallbacks(runnableHideMediaController);
        mHandler.postDelayed(runnableHideMediaController, 5000);
    }


    private class PlayerPageAdapter extends FragmentPagerAdapter {

        private PlayerPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    public int getStatusHeight() {
        Rect rectangle = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    @Override
    public void onStatusUpdate(int status) {
        super.onStatusUpdate(status);
        if (isFinishing()) {
            return;
        }
        stopPlayerAndShowEndView();
    }
    // fragment 切换 改变底部输入框

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mIsGoodVideo) {
                if (position == 1) {
                    mPlayerBottomSend.setType(BottomSendView.TYPE_COMMENT);
                } else if (position == 2) {
                    mPlayerBottomSend.setType(BottomSendView.TYPE_SEARCH);
                } else
                    mPlayerBottomSend.setType(BottomSendView.TYPE_NONE);
            } else {
                if (position == 0) {
                    mPlayerBottomSend.setType(BottomSendView.TYPE_CHAT);
                } else if (position == 1) {
                    mPlayerBottomSend.setType(BottomSendView.TYPE_SEARCH);
                } else
                    mPlayerBottomSend.setType(BottomSendView.TYPE_NONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void sendText(String inputString, int type) {
        switch (type) {
            case BottomSendView.TYPE_CHAT:
                if (!TextUtils.isEmpty(inputString)) {
                    ChatComment comment = new ChatComment();
                    comment.setVid(mVideoId);
                    comment.setId(-1);
                    comment.setReply_name("");
                    comment.setReply_nickname("");
                    comment.setContent(inputString);
                    comment.setMsgType(ChatComment.MSG_TYPE_NORMAL);
                    if (EVApplication.getUser() != null) {
                        comment.setName(EVApplication.getUser().getName());
                        comment.setNickname(EVApplication.getUser().getNickname());
                        comment.setLogourl(EVApplication.getUser().getLogourl());
                        comment.setVip(EVApplication.getUser().getVip());
                    }
                    mChatHelper.chatSendComment(comment);
                }
                break;
            case BottomSendView.TYPE_COMMENT:
                EventBus.getDefault().post(new LiveCommentEvent(inputString));
                break;
            case BottomSendView.TYPE_SEARCH:
                EventBus.getDefault().post(new LiveSearchStockEvent(inputString));
                break;
        }
    }

    /**
     * 加入聊天室
     */
    @Override
    public void onJoinOK() {
        if (isFinishing() || !EVApplication.isLogin() || !Preferences.getInstance(this).isLogin()) {
            return;
        }
        ChatComment comment = new ChatComment();
        comment.setVid(mVideoId);
        comment.setId(-1);
        comment.setReply_name("");
        comment.setReply_nickname("");
        comment.setContent("join...");
        comment.setMsgType(ChatComment.MSG_TYPE_JOIN);
        if (EVApplication.getUser() != null) {
            comment.setName(EVApplication.getUser().getName());
            comment.setNickname(EVApplication.getUser().getNickname());
            comment.setLogourl(EVApplication.getUser().getLogourl());
            comment.setVip(EVApplication.getUser().getVip());
        }
        mChatHelper.chatSendComment(comment);
    }

    /**
     * 打开礼物选择窗口
     */
    @Override
    public void openGiftWindow() {
        showGiftToolsBar();
    }

}
