/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.widget.MyUserPhoto;
import com.hooview.app.R;
import com.easyvaas.elapp.activity.pay.CashInActivity;
import com.easyvaas.elapp.activity.pay.MyProfitActivity;
import com.easyvaas.elapp.activity.user.FansListActivity;
import com.easyvaas.elapp.ui.user.FollowersListActivity;
import com.easyvaas.elapp.activity.user.SettingActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.base.BaseFragment;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.ui.user.UserInfoActivity;
import com.easyvaas.elapp.ui.user.VideoListActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.ShareHelper;
import com.easyvaas.elapp.utils.UserUtil;

public class TabMyFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = TabMyFragment.class.getSimpleName();

    private MyUserPhoto mMyUserPhoto;
    private TextView mUserId;
    private TextView mUserNameTv;
    private TextView mGenderTv;
    private TextView mConstellationTv;
    private TextView mLocationTv;
    private TextView mSignatureTv;

    private TextView mVideoCountTv;
    private TextView mFansCountTv;
    private TextView mFollowerCountTv;

    private View mView;
    private Bundle bundle = new Bundle();
    protected BottomSheet mSharePanel;
    protected int mShareType;
    protected static final int SHARE_TYPE_COPY = R.id.menu_share_copy_url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        updateUserInfo();
        return mView;
    }

    private void initView() {
        ImageView shareIcon = (ImageView) mView.findViewById(R.id.operation_action_iv);
        shareIcon.setImageResource(R.drawable.personal_icon_share);
        shareIcon.setOnClickListener(this);
        mUserId = (TextView) mView.findViewById(R.id.mine_id_tv);
        mUserNameTv = (TextView) mView.findViewById(R.id.mine_user_name_tv);
        mGenderTv = (TextView) mView.findViewById(R.id.user_gender_tv);
        mConstellationTv = (TextView) mView.findViewById(R.id.user_constellation_tv);
        mLocationTv = (TextView) mView.findViewById(R.id.mine_location_tv);
        mSignatureTv = (TextView) mView.findViewById(R.id.mine_signature_tv);

        ImageView backIv = (ImageView) mView.findViewById(R.id.user_info_back);
        backIv.setVisibility(View.VISIBLE);
        backIv.setOnClickListener(this);
        mMyUserPhoto = (MyUserPhoto) mView.findViewById(R.id.my_user_photo);
        initProfileContent();
    }

    private void showShareUserInfoPanel() {
        String title = String.format(getString(R.string.share_user_title),
                mUserNameTv.getText().toString());
        String content = String.format(getString(R.string.share_user_content),
                mUserNameTv.getText().toString());
        String logoUrl = EVApplication.getUser().getLogourl();
        String shareUrl = EVApplication.getUser().getShare_url();
        ShareContent shareContent = new ShareContentWebpage(title, content, shareUrl, logoUrl);
        ShareHelper.getInstance(getActivity()).showShareBottomPanel(shareContent);
    }

    private void initProfileContent() {
        View summaryInfo = mView.findViewById(R.id.user_summary_info);
        summaryInfo.findViewById(R.id.video_ll).setOnClickListener(this);
        summaryInfo.findViewById(R.id.fans_ll).setOnClickListener(this);
        summaryInfo.findViewById(R.id.follower_ll).setOnClickListener(this);
        mVideoCountTv = (TextView) summaryInfo.findViewById(R.id.video_count_tv);
        mFansCountTv = (TextView) summaryInfo.findViewById(R.id.fans_count_tv);
        mFollowerCountTv = (TextView) summaryInfo.findViewById(R.id.follow_count_tv);

        mView.findViewById(R.id.item_cash_in_rl).setOnClickListener(this);
        mView.findViewById(R.id.item_message_notice_rl).setOnClickListener(this);
        mView.findViewById(R.id.item_my_profit_rl).setOnClickListener(this);

    }

    private void initUserInfo(final User user) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (user != null) {
            UserUtil.showUserPhoto(getActivity(), user.getLogourl(), mMyUserPhoto);
            mUserId.setText("ID:" + user.getName());
            mUserNameTv.setText(user.getNickname());
            UserUtil.setGender(mGenderTv, user.getGender(), user.getBirthday());
            UserUtil.setConstellation(mConstellationTv, user.getBirthday());
            mMyUserPhoto.setIsVip(user.getVip());
            mLocationTv.setText(user.getLocation());
            if (TextUtils.isEmpty(user.getSignature())) {
                mSignatureTv.setText(getString(R.string.hint_signature));
            } else {
                mSignatureTv.setText(user.getSignature());
            }

            bundle.putString(ShareConstants.PARAMS_NICK_NAME, user.getNickname());
            bundle.putString(ShareConstants.PARAMS_IMAGEURL, user.getLogourl());
            bundle.putString(ShareConstants.PARAMS_SEX, user.getGender());
            bundle.putString(ShareConstants.USER_CITY, user.getLocation());
            bundle.putString(ShareConstants.DESCRIPTION, user.getSignature());
            bundle.putString(ShareConstants.BIRTHDAY, user.getBirthday());
            bundle.putBoolean(Constants.EXTRA_KEY_IS_REGISTER, false);
            mVideoCountTv.setText(user.getVideo_count() + "");
            mFansCountTv.setText(user.getFans_count() + "");
            mFollowerCountTv.setText(user.getFollow_count() + "");
            Button editUserProfile= (Button) mView.findViewById(R.id.mine_set_remarks_tv);
            editUserProfile.setText(getString(R.string.edit_user_profile));
            editUserProfile.setVisibility(View.VISIBLE);
            editUserProfile.setOnClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initUserInfo(EVApplication.getUser());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_back:
                getActivity().onBackPressed();
                break;
            case R.id.item_location_set_rl:
                break;
            case R.id.item_my_profit_rl:
                startActivity(new Intent(getActivity(), MyProfitActivity.class));
                break;
            case R.id.item_message_notice_rl:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.video_ll:
                Intent in = new Intent(getActivity(), VideoListActivity.class);
                in.putExtra(Constants.EXTRA_KEY_TYPE_VIDEO_LIST, VideoListActivity.TYPE_MY_VIDEO_LIST);
                startActivity(in);
                break;
            case R.id.fans_ll:
                Intent fansIntent = new Intent(getActivity(), FansListActivity.class);
                fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID,
                        Preferences.getInstance(getContext()).getUserNumber());
                startActivity(fansIntent);
                break;
            case R.id.follower_ll:
                startActivity(new Intent(getActivity(), FollowersListActivity.class));
                break;
            case R.id.operation_action_iv:
                showShareUserInfoPanel();
                break;
            case R.id.item_cash_in_rl:
                startActivity(new Intent(getActivity(), CashInActivity.class));
                break;
            case R.id.mine_set_remarks_tv:
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_IS_REGISTER, false);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUserInfo() {
        String sessionId = Preferences.getInstance(EVApplication.getApp()).getSessionId();
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
        ApiHelper.getInstance().getUserInfo("", new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                EVApplication.setUser(result);
                initUserInfo(result);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                Logger.e(TAG, "Update user info error, " + errorInfo);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }
}
