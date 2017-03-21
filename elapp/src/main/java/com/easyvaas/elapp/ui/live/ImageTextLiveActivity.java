package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.AppBarLayoutOffsetChangeEvent;
import com.easyvaas.elapp.event.HideGiftViewEvent;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.VIPUserInfoDetailActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;


public class ImageTextLiveActivity extends BaseImageTextLiveActivity implements View.OnClickListener {
    private static final String TAG = "ImageTextLiveActivity";
    public static final String EXTRA_TEXT_LIVE_MODEL = "extra_streams_model";
    private TextLiveListModel.StreamsEntity mStreamsEntity;
    private AppBarLayout mAppBarLayout;
    private TextView mTvTitle;
    private ImageView mIvBack;
    private ImageView mIvShare;
    private TextView mTvFollow;
    private boolean isGiftShown;

    public static void start(Context context, TextLiveListModel.StreamsEntity streamsEntity) {
        Intent starter = new Intent(context, ImageTextLiveActivity.class);
        starter.putExtra(EXTRA_TEXT_LIVE_MODEL, streamsEntity);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStreamsEntity = (TextLiveListModel.StreamsEntity) getIntent().getSerializableExtra(EXTRA_TEXT_LIVE_MODEL);
        Logger.d(TAG, "onCreate: "+mStreamsEntity.getId());
        if (mStreamsEntity == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_image_text_live);
        setupView();
    }

    private void setupView() {
        initCollapsingToolbarLayout();
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mIvShare.setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.GONE);
        LinearLayout tagsViewContainer = (LinearLayout) findViewById(R.id.ll_tag_container);
        if (mStreamsEntity.getUserEntity().getTags() != null) {
            for (int i = 0; i < mStreamsEntity.getUserEntity().getTags().size(); i++) {
                UserInfoModel.TagsEntity tagsEntity = mStreamsEntity.getUserEntity().getTags().get(i);
                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_use_tag, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.rightMargin = (int) ViewUtil.dp2Px(this, 10);
                tagsViewContainer.addView(textView, layoutParams);
                textView.setText(tagsEntity.getName());
            }
        }
        TextView tvName = (TextView) findViewById(R.id.tv_name);
        tvName.setText(mStreamsEntity.getUserEntity().getNickname());
        ImageView headerBg = (ImageView) findViewById(R.id.iv_header_bg);
        ImageView avater = (RoundImageView) findViewById(R.id.riv_avater);
        Utils.showImage(mStreamsEntity.getUserEntity().getLogourl(), R.drawable.account_bitmap_user, avater);
        Utils.showImageBlur(this, mStreamsEntity.getUserEntity().getLogourl(), R.drawable.account_bitmap_user, headerBg);
        mTvFollow = (TextView) findViewById(R.id.tv_follow);
        mTvFollow.setOnClickListener(this);
        avater.setOnClickListener(this);
        initFollowStatus();
    }

    private void initCollapsingToolbarLayout() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.AppBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int start = (int) ViewUtil.dp2Px(getApplicationContext(), 50);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Logger.d(TAG, "onOffsetChanged: verticalOffset==" + verticalOffset);
                EventBus.getDefault().post(new AppBarLayoutOffsetChangeEvent(verticalOffset));
                if (verticalOffset < -start) {
                    mTvTitle.setVisibility(View.VISIBLE);
                    mIvBack.setImageResource(R.drawable.icon_back);
                    mIvShare.setImageResource(R.drawable.icon_share);
                } else {
                    mIvBack.setImageResource(R.drawable.live_icon_more_back);
                    mIvShare.setImageResource(R.drawable.btn_share_w_n);
                    mTvTitle.setVisibility(View.GONE);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isGiftShown) {
            EventBus.getDefault().post(new HideGiftViewEvent());
            return;
        }
        super.onBackPressed();
    }

    public void setGiftShown(boolean isGiftShown) {
        this.isGiftShown = isGiftShown;
    }

    @Override
    public String setRoomId() {
        return mStreamsEntity.getId();
    }

    @Override
    public boolean isAnchor() {
        return false;
    }

    @Override
    public int watchCount() {
        return mStreamsEntity.getViewcount();
    }

    @Override
    public TextLiveListModel.StreamsEntity streamEntity() {
        return mStreamsEntity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                share();
                break;
            case R.id.tv_follow:
                if (Preferences.getInstance(this).isLogin() && EVApplication.isLogin()) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        SingleToast.show(getApplicationContext(), getString(R.string.cancel_follow_sccuess));
                        Preferences.getInstance(this).putBoolean(mStreamsEntity.getUserEntity().getName(), false);
                    } else {
                        v.setSelected(true);
                        SingleToast.show(getApplicationContext(), getString(R.string.follow_sccuess));
                        Preferences.getInstance(this).putBoolean(mStreamsEntity.getUserEntity().getName(), true);
                    }
                    ApiUtil.userFollow(ImageTextLiveActivity.this, mStreamsEntity.getUserEntity().getName(), v.isSelected(), v);
                } else {
                    LoginActivity.start(this);
                }
                break;
            case R.id.riv_avater:
                if (!TextUtils.isEmpty(mStreamsEntity.getUserEntity().getNickname()))
                    VIPUserInfoDetailActivity.start(this, mStreamsEntity.getUserEntity().getName());
                break;
        }
    }


    private void initFollowStatus() {
        if (Preferences.getInstance(this).getBoolean(mStreamsEntity.getUserEntity().getName(), false)) {
            mTvFollow.setSelected(true);
        } else {
            mTvFollow.setSelected(false);
        }

    }
}
