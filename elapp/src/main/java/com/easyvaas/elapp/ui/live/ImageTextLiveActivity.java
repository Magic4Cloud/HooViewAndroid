package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.RoundImageView;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.BaseUserEntity;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.HideGiftViewEvent;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.NumberUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.view.base.ToolBarTitleView;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 图文聊天界面
 */
public class ImageTextLiveActivity extends BaseImageTextLiveActivity {

    private static final String TAG = "ImageTextLiveActivity";
    public static final String EXTRA_TEXT_LIVE_MODEL = "extra_streams_model";
    @BindView(R.id.text_live_toolbar)
    ToolBarTitleView mTextLiveToolbar;
    @BindView(R.id.text_live_user_header)
    RoundImageView mTextLiveUserHeader;
    @BindView(R.id.text_live_user_name)
    TextView mTextLiveUserName;
    @BindView(R.id.text_live_user_info)
    TextView mTextLiveUserInfo;
    @BindView(R.id.text_live_user_follow_button)
    TextView mTextLiveUserFollowButton;
    @BindView(R.id.text_live_user_follow_counts)
    TextView mTextLiveUserFollowCounts;
    @BindView(R.id.text_live_user_tags)
    TextView mTextLiveUserTags;
    @BindView(R.id.text_live_user)
    RelativeLayout mTextLiveUser;
    @BindView(R.id.text_live_tablayout)
    SlidingTabLayout mTextLiveTablayout;
    @BindView(R.id.text_live_viewpager)
    ViewPager mTextLiveViewpager;
    private TextLiveListModel.StreamsEntity mStreamsEntity;
    private boolean isGiftShown;
    private int onLineCounts;

    public static void start(Context context, TextLiveListModel.StreamsEntity streamsEntity) {
        Intent starter = new Intent(context, ImageTextLiveActivity.class);
        starter.putExtra(EXTRA_TEXT_LIVE_MODEL, streamsEntity);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStreamsEntity = (TextLiveListModel.StreamsEntity) getIntent().getSerializableExtra(EXTRA_TEXT_LIVE_MODEL);
        Logger.d(TAG, "onCreate: " + mStreamsEntity.getId());
        if (mStreamsEntity == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_image_text_live);
        setupView(mStreamsEntity.getUserEntity());
    }

    /**
     * 初始化头部数据
     */
    private void setupView(UserInfoModel data) {
        mTextLiveUserName.setText(data.getNickname());
        mTextLiveUserInfo.setText(data.getSignature());
        mTextLiveUserTags.setText(getJoinString(data.getTags()));
        Utils.showImage(data.getLogourl(), R.drawable.account_bitmap_user, mTextLiveUserHeader);
        if (data.getFans_count() >= 10000) {
            mTextLiveUserFollowCounts.setText(StringUtil.formatTenThousand(data.getFans_count())+"万");
        } else {
            mTextLiveUserFollowCounts.setText(String.valueOf(data.getFans_count()));
        }

        if (data.getFollowed() == 1) {
            mTextLiveUserFollowButton.setText(R.string.user_followed);
            mTextLiveUserFollowButton.setSelected(true);
        } else {
            mTextLiveUserFollowButton.setText(R.string.user_follow);
            mTextLiveUserFollowButton.setSelected(false);
        }
        mTextLiveToolbar.setTitleText(NumberUtil.format(mStreamsEntity.getViewcount())+" 人参与");
        mTextLiveToolbar.getCenterTextView().setTextColor(Color.argb(255,254,79,80));
        mTextLiveToolbar.getCenterTextView().setTextSize(14);
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


    @OnClick({R.id.text_live_user, R.id.text_live_user_follow_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_live_user: // 跳转大V主页
                Utils.toUserPager(this,mStreamsEntity.getOwnerid(),1);
                break;
            case R.id.text_live_user_follow_button: // 关注
                if (Preferences.getInstance(this).isLogin() && EVApplication.isLogin()) {
                    int count = mStreamsEntity.getUserEntity().getFans_count();
                    if (view.isSelected()) {
                        view.setSelected(false);
                        mTextLiveUserFollowButton.setText(R.string.user_follow);
                        mStreamsEntity.getUserEntity().setFans_count(count - 1);
                        mTextLiveUserFollowCounts.setText(String.valueOf(count - 1));
                        SingleToast.show(getApplicationContext(), getString(R.string.cancel_follow_sccuess));
                    } else {
                        view.setSelected(true);
                        mTextLiveUserFollowButton.setText(R.string.user_followed);
                        mStreamsEntity.getUserEntity().setFans_count(count + 1);
                        mTextLiveUserFollowCounts.setText(String.valueOf(count + 1));
                        SingleToast.show(getApplicationContext(), getString(R.string.follow_sccuess));
                    }
                    ApiUtil.userFollow(ImageTextLiveActivity.this, mStreamsEntity.getOwnerid(), view.isSelected(), view);
                } else {
                    LoginActivity.start(this);
                }
                break;
        }
    }

    /**
     * 格式化标签
     */
    public static  String getJoinString(List<BaseUserEntity.TagsEntity> datas)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < datas.size(); i++) {
            if (i == 0)
                stringBuilder.append(datas.get(i).getName());
            else
            {
                stringBuilder.append(",").append(datas.get(i).getName());
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 设置在线人数
     */
    public void setOnLineCounts(int counts)
    {
        onLineCounts = counts;
//        mTextLiveToolbar.setTitleText(NumberUtil.format(onLineCounts)+" 人参与");
    }

    /**
     * 有用户离开或者加入
     */
    public void onLineCountsChange(boolean isLeave)
    {
        if (isLeave)
            onLineCounts = onLineCounts - 1;
        else
            onLineCounts = onLineCounts + 1;

//        mTextLiveToolbar.setTitleText(NumberUtil.format(onLineCounts)+" 人参与");
    }

}
