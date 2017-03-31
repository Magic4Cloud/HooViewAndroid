package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.Record;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.db.RealmHelper;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DateTimeUtil;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.PlayerStateView;
import com.easyvaas.sdk.player.EVPlayer;
import com.easyvaas.sdk.player.PlayerConstants;
import com.easyvaas.sdk.player.base.EVPlayerBase;
import com.easyvaas.sdk.player.base.EVPlayerParameter;
import com.easyvaas.sdk.player.base.EVVideoView;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class PlayerActivity extends BasePlayerActivity implements View.OnClickListener {
    private static final String TAG = "PlayerActivity";
    private RoundImageView mRivHeader;
    private TextView mTvName;
    private TextView mTvGroup;
    private TextView mTvEndTime;
    private TextView mTvCurTime;
    private TextView mFollowTv;
    private ImageView mIvPlayState;
    private ImageView mIv_all_screen;
    private SeekBar mSeekBar;
    private RelativeLayout mFlPlayer;
    private ViewPager mViewPager;
    private PlayerStateView mPlayerStateView;
    private EVPlayer mEVPlayer;
    private EVVideoView mVideoView;
    private ImageView ivShare;
    private ImageView ivBack;
    private boolean isLandscape = false;
    private boolean mIsPlayLive;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mVideoId = getIntent().getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        mIsPlayLive = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_IS_LIVE, false);
        mIsGoodVideo = getIntent().getBooleanExtra(Constants.EXTRA_KEY_VIDEO_GOOD_VIDEO, false);
        if (TextUtils.isEmpty(mVideoId)) {
            finish();
            return;
        }
        mCurrentVideo = new VideoEntity();
        mCurrentVideo.setVid(mVideoId);
        initView();
        initPlayer();
        loadVideoInfo();

    }

    private void insertHistoryRecord() {
        if (!TextUtils.isEmpty(mVideoId) && !RealmHelper.getInstance().queryRecordId(mVideoId)) {
            Record bean = new Record();
            bean.setLiving(mCurrentVideo.getLiving());
            bean.setId(String.valueOf(mVideoId));
            bean.setMode(mCurrentVideo.getMode());
            bean.setPic(mCurrentVideo.getLogourl());
            bean.setTitle(mCurrentVideo.getTitle());
            bean.setTime(DateTimeUtil.getSimpleTime(PlayerActivity.this, mCurrentVideo.getLive_start_time_span()));
            RealmHelper.getInstance().insertRecord(bean, 30);
        }
    }

    private void initView() {
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
        mFollowTv = (TextView) findViewById(R.id.tv_follow);
        mFollowTv.setOnClickListener(this);
        mRivHeader = (RoundImageView) findViewById(R.id.riv_header);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvGroup = (TextView) findViewById(R.id.tv_group);
        mVideoView = (EVVideoView) findViewById(R.id.video_view);
        mFlPlayer = (RelativeLayout) findViewById(R.id.fl_player);
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(mViewPager);
        if (mIsPlayLive) {
            showMediaController(false);
        } else {
            showMediaController(true);
        }
        if (mIsGoodVideo) {
            List<Fragment> list = new ArrayList<>();
            list.add(LiveCommentFragment.newInstance(mVideoId));
            list.add(DataFragment.newInstance());
            list.add(BookPlayFragment.newInstance());
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setPageMargin((int) ViewUtil.dp2Px(getApplicationContext(), 10));
            mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), list, getResources().getStringArray(R.array.play_tab_good_video)));
        } else {
            List<Fragment> list = new ArrayList<>();
            list.add(LiveChatFragment.newInstance());
            list.add(DataFragment.newInstance());
            list.add(BookPlayFragment.newInstance());
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setPageMargin((int) ViewUtil.dp2Px(getApplicationContext(), 10));
            mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), list, getResources().getStringArray(R.array.play_tab)));
        }
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

    private void loadVideoInfo() {
        ApiHelper.getInstance().getWatchVideo(mVideoId, "",
                new MyRequestCallBack<VideoEntity>() {
                    @Override
                    public void onSuccess(VideoEntity result) {
                        if (result != null) {
                            mCurrentVideo = result;
                            updateVideoInfo(result);
//                            chatServerInit(true);
                            startWatchLive();
                            insertHistoryRecord();
                            initFollowStatus();
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

    private void initFollowStatus() {
        if (Preferences.getInstance(this).getBoolean(mCurrentVideo.getName(), false)) {
            mFollowTv.setSelected(true);
        } else {
            mFollowTv.setSelected(false);
        }

    }

    //TODO 更新头像，用户名等
    private void updateVideoInfo(VideoEntity result) {
        if (mCurrentVideo.getLiving() == VideoEntity.IS_LIVING) {
            showMediaController(false);
            mIv_all_screen.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
        } else {
            showMediaController(true);
        }
        Utils.showImage(result.getLogourl(), R.drawable.account_bitmap_user, mRivHeader);
        mTvName.setText(result.getNickname());
        mTvGroup.setText(result.getSignature());
    }

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
                    long curPosition = mVideoView.getCurrentPosition();
                    long duration = mVideoView.getDuration();
                    mTvCurTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), curPosition));
                    mTvEndTime.setText(DateTimeUtil.getDurationTime(getApplicationContext(), duration));
                    mSeekBar.setProgress((int) (1000 * ((float) curPosition / (float) duration)));
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
        if (null != mEVPlayer) {
            mEVPlayer.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mEVPlayer) {
            mEVPlayer.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mEVPlayer) {
            mEVPlayer.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mEVPlayer) {
            mEVPlayer.onDestroy();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TODO Not work, need to improve.
        String vid = intent.getStringExtra(Constants.EXTRA_KEY_VIDEO_ID);
        if (mVideoId.equals(vid)) {
        } else if (!TextUtils.isEmpty(vid)) {
//            resetChatData();
            startWatchLive();
        }
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
            case R.id.tv_follow:
                if (Preferences.getInstance(this).isLogin() && EVApplication.isLogin()) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        SingleToast.show(getApplicationContext(), getString(R.string.cancel_follow_sccuess));
                    } else {
                        v.setSelected(true);
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
                    mVideoView.start();
                } else {
                    mIvPlayState.setSelected(true);
                    mVideoView.pause();
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

    private void changeVideoSize(Configuration newConfig) {
        if (newConfig.screenHeightDp < newConfig.screenWidthDp) {
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
        }
    }

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
    }

    private boolean getMediaControllerIsShow(){
        if (ivBack.getVisibility() == View.VISIBLE) return true;
        else return false;
    }

    public void hideMediaControllerDelay() {
        mHandler.removeCallbacks(runnableHideMediaController);
        mHandler.postDelayed(runnableHideMediaController, 5000);
    }


    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm, List<Fragment> list, String[] tabTitles) {
            super(fm);
//            mTabsTitle = getResources().getStringArray(R.array.play_tab);
            this.mTabsTitle = tabTitles;
            this.fragments = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
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
}
