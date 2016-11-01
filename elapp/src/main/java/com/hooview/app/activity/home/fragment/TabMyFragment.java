/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.home.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.bottomsheet.BottomSheet;
import com.easyvaas.common.chat.activity.ShowBigImageActivity;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.common.sharelogin.model.ShareContentWebpage;
import com.easyvaas.common.widget.MyUserPhoto;
import com.hooview.app.R;
import com.hooview.app.activity.user.FansListActivity;
import com.hooview.app.activity.user.FollowersListActivity;
import com.hooview.app.activity.user.SettingActivity;
import com.hooview.app.activity.user.UserInfoActivity;
import com.hooview.app.activity.user.VideoListActivity;
import com.hooview.app.app.EVApplication;
import com.hooview.app.base.BaseFragment;
import com.hooview.app.bean.user.User;
import com.hooview.app.db.Preferences;
import com.hooview.app.live.activity.LivePrepareActivity;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.ShareHelper;
import com.hooview.app.utils.UserUtil;

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
    protected static final int SHARE_TYPE_COPY = com.hooview.app.R.id.menu_share_copy_url;

    //控制是否可以进行直播
    private boolean canLive;
    private RelativeLayout rl_live;
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(com.hooview.app.R.layout.fragment_mine, container, false);
        initView();
        updateUserInfo();
        return mView;
    }

    private void initView() {
        ImageView shareIcon = (ImageView) mView.findViewById(com.hooview.app.R.id.operation_action_iv);
        shareIcon.setImageResource(com.hooview.app.R.drawable.personal_icon_share);
        shareIcon.setOnClickListener(this);

        //隐藏分享按钮
        shareIcon.setVisibility(View.GONE);


        mUserId = (TextView) mView.findViewById(com.hooview.app.R.id.mine_id_tv);
        mUserNameTv = (TextView) mView.findViewById(com.hooview.app.R.id.mine_user_name_tv);
        mGenderTv = (TextView) mView.findViewById(com.hooview.app.R.id.user_gender_tv);
        mConstellationTv = (TextView) mView.findViewById(com.hooview.app.R.id.user_constellation_tv);
        mLocationTv = (TextView) mView.findViewById(com.hooview.app.R.id.mine_location_tv);
        mSignatureTv = (TextView) mView.findViewById(com.hooview.app.R.id.mine_signature_tv);

        ImageView backIv = (ImageView) mView.findViewById(com.hooview.app.R.id.user_info_back);
        backIv.setVisibility(View.VISIBLE);
        backIv.setOnClickListener(this);
        mMyUserPhoto = (MyUserPhoto) mView.findViewById(com.hooview.app.R.id.my_user_photo);
        mMyUserPhoto.getRoundImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = bundle.getString(ShareConstants.PARAMS_IMAGEURL);
                Intent showImage = new Intent(getActivity(), ShowBigImageActivity.class);
                showImage.putExtra(ShowBigImageActivity.REMOTE_IMAGE_URL, url);
                startActivity(showImage);
            }
        });
        initProfileContent();
    }

    private void showShareUserInfoPanel() {
        String title = String.format(getString(com.hooview.app.R.string.share_user_title),
                mUserNameTv.getText().toString());
        String content = String.format(getString(com.hooview.app.R.string.share_user_content),
                mUserNameTv.getText().toString());
        String logoUrl = EVApplication.getUser().getLogourl();
        String shareUrl = EVApplication.getUser().getShare_url();
        ShareContent shareContent = new ShareContentWebpage(title, content, shareUrl, logoUrl);
        ShareHelper.getInstance(getActivity()).showShareBottomPanel(shareContent);
    }

    private void initProfileContent() {
        View summaryInfo = mView.findViewById(com.hooview.app.R.id.user_summary_info);
        summaryInfo.findViewById(com.hooview.app.R.id.video_ll).setOnClickListener(this);
        summaryInfo.findViewById(com.hooview.app.R.id.fans_ll).setOnClickListener(this);
        summaryInfo.findViewById(com.hooview.app.R.id.follower_ll).setOnClickListener(this);
        mVideoCountTv = (TextView) summaryInfo.findViewById(com.hooview.app.R.id.video_count_tv);
        mFansCountTv = (TextView) summaryInfo.findViewById(com.hooview.app.R.id.fans_count_tv);
        mFollowerCountTv = (TextView) summaryInfo.findViewById(com.hooview.app.R.id.follow_count_tv);

        //mView.findViewById(com.hooview.app.R.id.item_cash_in_rl).setOnClickListener(this);
        mView.findViewById(com.hooview.app.R.id.item_message_notice_rl).setOnClickListener(this);
        //mView.findViewById(com.hooview.app.R.id.item_my_profit_rl).setOnClickListener(this);
        rl_live = (RelativeLayout) mView.findViewById(R.id.item_live_rl);
        rl_live.setOnClickListener(this);


        //设置直播功能是否可见
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        if (sp.getBoolean("live", false)) {
            rl_live.setVisibility(View.VISIBLE);
        } else {
            rl_live.setVisibility(View.GONE);
        }
    }

    private void initUserInfo(final User user) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (user != null) {
            UserUtil.showUserPhoto(getActivity(), user.getLogourl(), mMyUserPhoto);
            mUserId.setText("ID:" + user.getName());
            mUserNameTv.setText(getContext().getResources().getString(R.string.nickname) + ":" + user.getNickname());
            UserUtil.setGender(mGenderTv, user.getGender(), user.getBirthday());
            UserUtil.setConstellation(mConstellationTv, user.getBirthday());
            mMyUserPhoto.setIsVip(user.getVip());
            mLocationTv.setText(user.getLocation());
            if (TextUtils.isEmpty(user.getSignature())) {
                mSignatureTv.setText(getString(com.hooview.app.R.string.hint_signature));
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
            mVideoCountTv.setTextColor(getResources().getColor(R.color.login_text_color_3));
            mFansCountTv.setText(user.getFans_count() + "");
            mFollowerCountTv.setText(user.getFollow_count() + "");
            Button editUserProfile = (Button) mView.findViewById(com.hooview.app.R.id.mine_set_remarks_tv);
            editUserProfile.setText(getString(com.hooview.app.R.string.edit_user_profile));
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
            case com.hooview.app.R.id.user_info_back:
                getActivity().onBackPressed();
                break;
            case com.hooview.app.R.id.item_location_set_rl:
                break;
//            case com.hooview.app.R.id.item_my_profit_rl:
//                startActivity(new Intent(getActivity(), MyProfitActivity.class));
//                break;
            case com.hooview.app.R.id.item_message_notice_rl:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case com.hooview.app.R.id.video_ll:
                Intent in = new Intent(getActivity(), VideoListActivity.class);
                in.putExtra(Constants.EXTRA_KEY_TYPE_VIDEO_LIST, VideoListActivity.TYPE_MY_VIDEO_LIST);
                startActivity(in);
                break;
            case com.hooview.app.R.id.fans_ll:
                Intent fansIntent = new Intent(getActivity(), FansListActivity.class);
                fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID,
                        Preferences.getInstance(getContext()).getUserNumber());
                startActivity(fansIntent);
                break;
            case com.hooview.app.R.id.follower_ll:
                startActivity(new Intent(getActivity(), FollowersListActivity.class));
                break;
            case com.hooview.app.R.id.operation_action_iv:
                showShareUserInfoPanel();
                break;
            case R.id.item_live_rl:
                showIsLiveDialog();
                break;
//            case com.hooview.app.R.id.item_cash_in_rl:
//                startActivity(new Intent(getActivity(), CashInActivity.class));
//                break;
            case com.hooview.app.R.id.mine_set_remarks_tv:
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

                //是否可以进行直播
                canLive = result.getJurisdiction() == 1 ? true : false;
                if (canLive) {
                    rl_live.setVisibility(View.VISIBLE);
                } else {
                    rl_live.setVisibility(View.GONE);
                }

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

    //弹出是否进行直播的提示框
    private void showIsLiveDialog() {
        final Dialog dialog = new AlertDialog.Builder(getActivity()).create();
        View view = View.inflate(getActivity(), R.layout.dialog_is_start_live, null);
        dialog.show();
        dialog.getWindow().setContentView(view);

        view.findViewById(R.id.dialog_sure_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToLive();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dialog_cancel_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void startToLive() {
        startActivity(new Intent(getActivity().getApplicationContext(), LivePrepareActivity.class));
        //mStartRecordCount++;
    }
}
