/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.live.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

//import com.easyvaas.common.chat.ChatManager;
import com.easyvaas.common.widget.MyUserPhoto;

import com.hooview.app.R;
import com.easyvaas.elapp.activity.home.HomeTabActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.BaseUserEntity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.video.VideoEntity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.DialogUtil;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.Utils;

public class LiveRoomManager {
    private Activity mActivity;
    private VideoEntity mCurrentVideo;
    private Dialog mUserInfoDialog;
    private Preferences mPref;
    private RoomManagerListener mListener;
    private ShutUpHelper mShutUpHelper;

    public LiveRoomManager(Activity activity, VideoEntity videoEntity, RoomManagerListener listener) {
        mActivity = activity;
        mCurrentVideo = videoEntity;
        mPref = Preferences.getInstance(activity);
        mListener = listener;
        mShutUpHelper = ShutUpHelper.getInstance();
    }

    public void updateRoomInfo(int commentCount, int likeCount, int watchingCount) {
        mCurrentVideo.setComment_count(commentCount);
        mCurrentVideo.setLike_count(likeCount);
        mCurrentVideo.setWatching_count(watchingCount);
    }

    public void showUserInfoPopView(final String userId, View anchorInfoView) {
        final boolean isOwner = userId.equals(mCurrentVideo.getName());
        final boolean isSelf = mPref.getUserNumber().equals(userId);
        /*if (mPref.getUserNumber().equals(userId)) {
            SingleToast.show(mActivity, R.string.this_is_self);
            return;
        }*/
        final boolean isLiving = mCurrentVideo.getLiving() == VideoEntity.IS_LIVING;
        final boolean isHaveShutUp = mShutUpHelper.checkIsShutUp(userId);
        final boolean isLiveManager = mShutUpHelper.checkIsManager(mPref.getUserNumber())
                || mCurrentVideo.getName().equals(mPref.getUserNumber());

        View userInfoView = null;

        if (isOwner) {
            anchorInfoView.setVisibility(View.VISIBLE);
        } else {
            userInfoView = LayoutInflater.from(mActivity)
                    .inflate(R.layout.live_bottom_user_info, null);
            showUserInfoDialog(userInfoView);
        }

        final View popupView = isOwner ? anchorInfoView : userInfoView;
        final TextView shutUpTv = (TextView) popupView.findViewById(R.id.shut_up_tv);
        final TextView reportTv = (TextView) popupView.findViewById(R.id.report_tv);
        final TextView kickUserTv = (TextView) popupView.findViewById(R.id.kick_user_tv);
        final TextView setManagerTv = (TextView) popupView.findViewById(R.id.set_manager_tv);
        final MyUserPhoto userLogo = (MyUserPhoto) popupView.findViewById(R.id.live_user_portrait);
        final TextView userNumber = (TextView) popupView.findViewById(R.id.user_id_tv);
        final TextView userNameTv = (TextView) popupView.findViewById(R.id.user_name_tv);
        final TextView videoTitleTv = (TextView) popupView.findViewById(R.id.video_title_tv);
        final View closeIv = popupView.findViewById(R.id.user_info_close_iv);
        final View homeBtn = popupView.findViewById(R.id.home_page_tv);
        final View chatBtn = popupView.findViewById(R.id.user_private_chat_tv);
        final View commentBtn = popupView.findViewById(R.id.user_post_tv);

        if (closeIv != null) {
            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOwner) {
                        popupView.setVisibility(View.GONE);
                    } else {
                        hideUserInfoDialog();
                    }
                }
            });
        }

        ApiHelper.getInstance().getUserInfo(userId, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(final User user) {
                if (userLogo != null) {
                    UserUtil.showUserPhoto(mActivity, user.getLogourl(), userLogo);
                    userLogo.setIsVip(user.getVip());
                }
                userNameTv.setText(user.getNickname());
                if (TextUtils.isEmpty(videoTitleTv.getText())) {
                    videoTitleTv.setText(TextUtils.isEmpty(user.getSignature()) ?
                            mActivity.getString(R.string.hint_signature) : user.getSignature());
                }
                userNumber.setText("ID:" + user.getName());
                setSpecialText(((TextView) popupView.findViewById(R.id.user_video_tv)),
                        mActivity.getString(R.string.user_e_coin_count, user.getCostecoin()),
                        user.getCostecoin() + "");
                setSpecialText(((TextView) popupView.findViewById(R.id.user_fans_tv)),
                        mActivity.getString(R.string.user_love_count, user.getFans_count()),
                        user.getFans_count() + "");
                setSpecialText(((TextView) popupView.findViewById(R.id.user_follower_tv)),
                        mActivity.getString(R.string.user_follow_count, user.getFollow_count()),
                        user.getFollow_count() + "");
                if (isSelf) {
                    popupView.findViewById(R.id.player_user_operation_ll).setVisibility(View.GONE);
                    reportTv.setVisibility(View.GONE);
                } else {
                    reportTv.setVisibility(View.VISIBLE);
                    reportTv.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogUtil.showReportUserDialog(mActivity, user.getName());
                                }
                            });
                }
                homeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserUtil.showUserInfo(mActivity, user.getName());
                    }
                });
                chatBtn.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (TextUtils.isEmpty(user.getImuser())) {
                                    return;
                                }
//                                ChatManager.getInstance().chatToUser(user.getImuser(),
//                                        user.getFollowed() == User.FOLLOWED);
                            }
                        });
                commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.sendComment(0, user.getName(), user.getNickname());
                        hideUserInfoDialog();
                    }
                });
                setUserInfoPopupViewFollow(user, popupView, mCurrentVideo.getVid(), isSelf);
                // ===== Different the roles show different views. ======================
                if (isOwner && isSelf) {
                    kickUserTv.setVisibility(View.VISIBLE);
                    setManagerTv.setVisibility(View.VISIBLE);
                    shutUpTv.setVisibility(View.VISIBLE);
                } else if (isLiveManager) {
                    shutUpTv.setVisibility(View.VISIBLE);
                    if (isHaveShutUp) {
                        shutUpTv.setText(R.string.shut_up_cancel);
                    } else {
                        shutUpTv.setText(R.string.shut_up);
                    }
                    shutUpTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean shutUp = shutUpTv.getContext()
                                    .getString(R.string.shut_up).equals(shutUpTv.getText());
                            mShutUpHelper.liveShutUp(mCurrentVideo.getVid(), userId,
                                    shutUp, shutUpTv);
                        }
                    });
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

    private void showUserInfoDialog(View userInfoView) {
        mUserInfoDialog = DialogUtil.getCustomDialog(mActivity, userInfoView, true, true, -1);
        mUserInfoDialog.show();
    }

    private void hideUserInfoDialog() {
        if (mUserInfoDialog != null && mUserInfoDialog.isShowing()) {
            mUserInfoDialog.dismiss();
        }
    }

    private void setUserInfoPopupViewFollow(final BaseUserEntity user, final View popupView,
                                            final String vid, boolean isSelf) {
        final TextView followTv = (TextView) popupView.findViewById(R.id.user_follow_status_tv);
        if (user.getFollowed() == User.FOLLOWED) {
            followTv.setText(mActivity.getString(R.string.followed));
        } else {
            followTv.setText(mActivity.getString(R.string.follow_plus));
        }
        if (isSelf) {
            followTv.setVisibility(View.GONE);
            return;
        }
        followTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiHelper.getInstance().userFollow(user.getName(), user.getFollowed() != User.FOLLOWED,
                        vid, new MyRequestCallBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if (user.getFollowed() == User.FOLLOWED) {
                                    followTv.setText(mActivity.getString(R.string.follow_plus));
                                    user.setFollowed(User.UN_FOLLOWED);
                                    mListener.unFollowAnchor();
                                } else {
                                    followTv.setText(mActivity.getString(R.string.followed));
                                    user.setFollowed(User.FOLLOWED);
                                    SingleToast.show(mActivity, R.string.msg_follow_success);
                                    mListener.followAnchor();
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
        });
    }

    public void showPlayEndView(View playEndView) {
        if (mCurrentVideo == null) {
            return;
        }
        final boolean isCurrentUserRecording = mCurrentVideo.getLiving() == VideoEntity.IS_LIVING
                && mCurrentVideo.getName().equals(Preferences.getInstance(mActivity).getUserNumber());
        TextView titleTv = (TextView) playEndView.findViewById(R.id.lep_title_tv);
        TextView liveOverTipTv = (TextView) playEndView.findViewById(R.id.live_over_tip_tv);
        TextView watchCountTv = (TextView) playEndView.findViewById(R.id.ed_view_number_tv);
        TextView likeCountTv = (TextView) playEndView.findViewById(R.id.ed_like_number_tv);
        final TextView leftBtn = (TextView) playEndView.findViewById(R.id.lep_left_tv);
        TextView rightBtn = (TextView) playEndView.findViewById(R.id.lep_right_tv);
        final TextView saveVideoTv = (TextView) playEndView.findViewById(R.id.living_action_tv);
        TextView deleteVideoTv = (TextView) playEndView.findViewById(R.id.delete_video_tv);

        ImageView closeIv = (ImageView) playEndView.findViewById(R.id.lep_close_iv);
        TextView liveOverErrorTipTv = (TextView) playEndView.findViewById(R.id.live_over_error_tip_tv);
        saveVideoTv.setText(R.string.save_video);
        saveVideoTv.setSelected(true);
        if (isCurrentUserRecording) {
            titleTv.setText(mCurrentVideo.getTitle());
            leftBtn.setVisibility(View.INVISIBLE);
            rightBtn.setVisibility(View.INVISIBLE);
            if (mCurrentVideo.getSentTimeLength() < mPref
                    .getLong(Preferences.KEY_PARAM_SWITCH_SAVE_DURATION, 0)) {
                leftBtn.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
                liveOverErrorTipTv.setVisibility(View.VISIBLE);
                saveVideoTv.setText(R.string.live_back_to_home_page);
                removeVideo(mCurrentVideo.getVid());
                deleteVideoTv.setVisibility(View.INVISIBLE);
            } else {
                deleteVideoTv.setVisibility(View.VISIBLE);
            }
            liveOverTipTv.setText(R.string.live_over_tip);
        } else {
            deleteVideoTv.setVisibility(View.INVISIBLE);
            saveVideoTv.setText(R.string.live_back_to_home_page);
            titleTv.setText(R.string.tip_live_end_recommend_to_square);
            leftBtn.setText(R.string.follow_plus);
            Utils.setTextTopDrawable(mActivity, leftBtn, R.drawable.ic_live_follow_anchor);
            rightBtn.setText(R.string.chat_send);
            Utils.setTextTopDrawable(mActivity, rightBtn, R.drawable.ic_live_chat_anchor);
            if (mCurrentVideo.isFollowed() == User.FOLLOWED) {
                leftBtn.setText(R.string.followed);
                Utils.setTextTopDrawable(mActivity, leftBtn, R.drawable.ic_live_follow_anchor);
            }
            if (mCurrentVideo.getLiving() == VideoEntity.IS_LIVING) {
                liveOverTipTv.setText(R.string.live_watching_over_tip);
            } else {
                liveOverTipTv.setText(R.string.taped_watching_over_tip);
                if (mCurrentVideo.getName().equals(mPref.getUserNumber())) {
                    leftBtn.setVisibility(View.INVISIBLE);
                    rightBtn.setVisibility(View.INVISIBLE);
                }
            }
        }

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        deleteVideoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVideo(mCurrentVideo.getVid());
                mActivity.finish();
            }
        });
        saveVideoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrentUserRecording) {
                }
                if (mActivity.getString(R.string.live_back_to_home_page).equals(saveVideoTv.getText())) {
                    Intent intent = new Intent(mActivity, HomeTabActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_TAB_ID, HomeTabActivity.TAB_ID_DISCOVER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mActivity.startActivity(intent);
                }
                mActivity.finish();
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrentUserRecording) {
                    Utils.watchVideo(mActivity, mCurrentVideo);
                } else {
                    ApiHelper.getInstance().userFollow(mCurrentVideo.getName(),
                            mCurrentVideo.isFollowed() != User.FOLLOWED,
                            new MyRequestCallBack<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    if (!mActivity.isFinishing()) {
                                        User user = EVApplication.getUser();
                                        if (mCurrentVideo.isFollowed() != User.FOLLOWED) {
                                            mCurrentVideo.setFollowed(User.FOLLOWED);
                                            leftBtn.setText(R.string.followed);
                                            user.setFollow_count(user.getFollow_count() + 1);
                                        } else {
                                            mCurrentVideo.setFollowed(User.UN_FOLLOWED);
                                            leftBtn.setText(R.string.follow_plus);
                                            user.setFollow_count(user.getFollow_count() - 1);
                                        }
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
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrentUserRecording) {
                    removeVideo(mCurrentVideo.getVid());
                } else {
//                    ChatManager.getInstance().chatToUser(mCurrentVideo.getName(),
//                            mCurrentVideo.isFollowed() == User.FOLLOWED);
                }
                mActivity.finish();
            }
        });
        watchCountTv.setText(mCurrentVideo.getWatch_count() + "");
        likeCountTv.setText(mCurrentVideo.getLike_count() + "");

        playEndView.setVisibility(View.VISIBLE);
    }

    private void removeVideo(String vid) {
        ApiHelper.getInstance().videoRemove(vid, new MyRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                User user = EVApplication.getUser();
                user.setVideo_count(user.getVideo_count() - 1);
                Preferences pref = Preferences.getInstance(mActivity);
                pref.remove(Preferences.KEY_LAST_LIVE_INTERRUPT_VID);
                pref.remove(Preferences.KEY_LAST_LIVE_IS_AUDIO_MODE);
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

    private void setSpecialText(TextView v, String completeContent, String headContent) {
        SpannableString info = new SpannableString(completeContent);
        info.setSpan(new AbsoluteSizeSpan(48), 0, headContent.length() + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        info.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.text_color_main)), 0,
                headContent.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        v.setText(info);
    }

    public boolean checkIsLiveShutUp(String userName) {
        return mShutUpHelper.checkIsShutUp(userName);
    }

    public interface RoomManagerListener {
        void setManager(String name, boolean setManager);

        void setShutUp(String name, boolean setShutUp);

        void reportUser(String name);

        void sendComment(long replyCommentId, String name, String nickname);

        void followAnchor();

        void unFollowAnchor();
    }
}
