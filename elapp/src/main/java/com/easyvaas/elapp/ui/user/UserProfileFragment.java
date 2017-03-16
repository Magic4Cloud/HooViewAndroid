package com.easyvaas.elapp.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.feedback.FeedbackHelper;
import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.activity.user.FansListActivity;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.message.UnReadMessageEntity;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.NewMessageEvent;
import com.easyvaas.elapp.event.UserInfoUpdateEvent;
import com.easyvaas.elapp.net.ApiConstant;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseFragment;
import com.easyvaas.elapp.ui.pay.PayRecordListActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.StringUtil;
import com.easyvaas.elapp.utils.UserUtil;
import com.easyvaas.elapp.utils.ViewUtil;
import com.easyvaas.elapp.view.CommonItemButton;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserProfileFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UserProfileFragment";
    private RelativeLayout mLlUserEdit;
    private TextView mTvLoginPrompt;
    private TextView mTvName;
    private TextView mTvSignature;
    private MyUserPhoto mIvUserLogo;
    private ImageView mIvEdit;
    private ImageView mIvGender;
    private TextView mTvMessageCount;
    private ImageView mIvMessageState;
    private TextView mTvFans;
    private TextView mTvFollowCount;
    private TextView mTvCoinCount;
    private TextView mTvCoinTitle;
    private CommonItemButton cibMyLive;
    private View cibMyLiveDivider;
    private Preferences mPreferences;
    private User user;
    LinearLayout tagsViewContainer;

    private Bundle bundle = new Bundle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = Preferences.getInstance(getContext().getApplicationContext());
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, null);
        assignViews(view);
        return view;
    }

    private void assignViews(View view) {
        view.findViewById(R.id.iv_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_user_edit).setOnClickListener(this);
        view.findViewById(R.id.fl_message).setOnClickListener(this);
        mIvGender = (ImageView) view.findViewById(R.id.iv_gender);
        mTvLoginPrompt = (TextView) view.findViewById(R.id.tv_login_prompt);
        mTvLoginPrompt.setOnClickListener(this);
        tagsViewContainer = (LinearLayout) view.findViewById(R.id.ll_tag_container);

        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvSignature = (TextView) view.findViewById(R.id.tv_signature);
        mIvUserLogo = (MyUserPhoto) view.findViewById(R.id.iv_userhead);
        mIvEdit = (ImageView) view.findViewById(R.id.iv_edit);
        mTvMessageCount = (TextView) view.findViewById(R.id.tv_message_count);
        mIvMessageState = (ImageView) view.findViewById(R.id.iv_message_state);
        mTvFans = (TextView) view.findViewById(R.id.tv_fans);
        mTvFollowCount = (TextView) view.findViewById(R.id.tv_follow_count);
        mTvCoinCount = (TextView) view.findViewById(R.id.tv_coin_count);
        mTvCoinCount.setOnClickListener(this);
        mTvCoinTitle = (TextView) view.findViewById(R.id.tv_coin_title);
        mTvCoinTitle.setOnClickListener(this);
        cibMyLive = (CommonItemButton) view.findViewById(R.id.cib_my_live);
        cibMyLiveDivider = view.findViewById(R.id.cib_my_live_divider);

        cibMyLive.setOnClickListener(this);
        view.findViewById(R.id.cib_my_book).setOnClickListener(this);
        view.findViewById(R.id.cib_my_collection).setOnClickListener(this);
        view.findViewById(R.id.cib_my_history).setOnClickListener(this);
        view.findViewById(R.id.cib_my_feedback).setOnClickListener(this);
        view.findViewById(R.id.ll_my_follow).setOnClickListener(this);
        view.findViewById(R.id.ll_my_fans).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUserInfo();
        ApiUtil.checkServerParam(getContext());
        ApiUtil.checkUnreadMessage(getContext());
        initFeedback();
        ApiUtil.checkSession(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUserInfo() {
        String userNumber = Preferences.getInstance(EVApplication.getApp()).getUserNumber();
        String sessionId = Preferences.getInstance(EVApplication.getApp()).getSessionId();
        if (TextUtils.isEmpty(userNumber) || TextUtils.isEmpty(sessionId)) {
            return;
        }
        ApiHelper.getInstance().getUserInfo(userNumber, new MyRequestCallBack<User>() {
            @Override
            public void onSuccess(User result) {
                EVApplication.setUser(result);
                initUserInfo(result);
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    private void initUserInfo(final User user) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (user != null) {
            bundle.putString(ShareConstants.PARAMS_NICK_NAME, user.getNickname());
            bundle.putString(ShareConstants.PARAMS_IMAGEURL, user.getLogourl());
            bundle.putString(ShareConstants.PARAMS_SEX, user.getGender());
            bundle.putString(ShareConstants.USER_CITY, user.getLocation());
            bundle.putString(ShareConstants.DESCRIPTION, user.getSignature());
            bundle.putString(ShareConstants.BIRTHDAY, user.getBirthday());
            bundle.putBoolean(Constants.EXTRA_KEY_IS_REGISTER, false);
            bundle.putBoolean(Constants.EXTRA_KEY_IS_VIP, user.getVip() == 1);
            bundle.putString(UserInfoActivity.EXTRA_KEY_USER_CERTIFICATE, user.getCredentials());
        }
    }

    private void updateUi() {
        if (mPreferences.isLogin() && EVApplication.isLogin()) {
            user = EVApplication.getUser();
            long eCount = mPreferences.getLong(Preferences.KEY_PARAM_ASSET_E_COIN_ACCOUNT,
                    0);
            mTvCoinCount.setText(eCount + "");
            mTvFans.setText(user.getFans_count() + "");
            mTvFollowCount.setText(user.getFollow_count() + "");
            mTvName.setVisibility(View.VISIBLE);
            mTvName.setText(user.getNickname());
            mTvSignature.setText(user.getSignature());
            mTvSignature.setVisibility(View.VISIBLE);
            mTvLoginPrompt.setVisibility(View.GONE);
            mIvGender.setVisibility(View.VISIBLE);
            if (user.getGender().equals("male")) {
                mIvGender.setImageResource(R.drawable.ic_man);
            } else {
                mIvGender.setImageResource(R.drawable.ic_woman);
            }
            UserUtil.showUserPhoto(getContext(), user.getLogourl(), mIvUserLogo);
            mIvUserLogo.setIsVip(user.getVip());
            setUnReadMessage();
            if (user.getTags() != null) {
                tagsViewContainer.removeAllViews();
                for (int i = 0; i < user.getTags().size() && i < 3; i++) {
                    UserInfoModel.TagsEntity tagsEntity = user.getTags().get(i);
                    TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_use_tag, null);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.rightMargin = (int) ViewUtil.dp2Px(getContext(), 10);
                    tagsViewContainer.addView(textView, layoutParams);
                    textView.setText(tagsEntity.getName());
                }
            }

            if (user.getVip() == 1) {
                cibMyLive.setVisibility(View.VISIBLE);
                cibMyLiveDivider.setVisibility(View.VISIBLE);
            } else {
                cibMyLive.setVisibility(View.GONE);
                cibMyLiveDivider.setVisibility(View.GONE);
                cibMyLive.setOnClickListener(null);
            }
        } else {
            mTvName.setVisibility(View.INVISIBLE);
            mTvSignature.setVisibility(View.INVISIBLE);
            mTvLoginPrompt.setVisibility(View.VISIBLE);
            mIvGender.setVisibility(View.INVISIBLE);
            mTvCoinCount.setText(0 + "");
            mTvFans.setText(0 + "");
            mTvFollowCount.setText(0 + "");
            UserUtil.showUserPhoto(getContext(), "", mIvUserLogo);
            mIvUserLogo.setIsVip(0);
        }
    }

    private void setUnReadMessage() {
        HooviewApiHelper.getInstance().getUnReadMessageCount(new MyRequestCallBack<UnReadMessageEntity>() {
            @Override
            public void onSuccess(UnReadMessageEntity result) {
                if (result != null) {
                    mTvMessageCount.setText(result.getUnread());
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!mPreferences.isLogin() || !EVApplication.isLogin()) {
            LoginActivity.start(getActivity());
            return;
        }
        switch (v.getId()) {
            case R.id.cib_my_live:
//                Intent in = new Intent(getActivity(), VideoListActivity.class);
//                in.putExtra(Constants.EXTRA_KEY_TYPE_VIDEO_LIST, VideoListActivity.TYPE_MY_VIDEO_LIST);
//                in.putExtra(Constants.EXTRA_KEY_TYPE_VIP, user.getVip());
//                startActivity(in);
                startActivity(new Intent(getActivity(), VipUserVideoInfoActivity.class));
                break;
            case R.id.cib_my_book:
                SingleToast.show(getContext(), R.string.feature_no_open_prompt);
                break;
            case R.id.cib_my_collection:
                MyCollectListActivity.start(getContext());
                break;
            case R.id.cib_my_history:
//                SingleToast.show(getContext(), R.string.feature_no_open_prompt);
                UserHistoryActivity.start(getActivity());
                break;
            case R.id.cib_my_feedback:
                FeedbackHelper.getInstance(getActivity()).showFeedbackUI();
                break;
            case R.id.ll_user_edit:
                UserInfoActivity.start(getActivity(), bundle);
                break;
            case R.id.iv_setting:
                SettingActivity.start(getActivity());
                break;
            case R.id.tv_coin_count:
            case R.id.tv_coin_title:
//                CashInActivity.start(getContext());
                Intent intent = new Intent(getActivity(), PayRecordListActivity.class);
                intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                        PayRecordListActivity.TYPE_CASH_IN);
                startActivity(intent);
                break;
            case R.id.tv_login_prompt:
                LoginActivity.start(getActivity());
                break;
            case R.id.fl_message:
                MessageUnReadListActivity.start(getActivity());
                break;
            case R.id.ll_my_fans:
                Intent fansIntent = new Intent(getActivity(), FansListActivity.class);
                fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID,
                        Preferences.getInstance(getContext()).getUserNumber());
                startActivity(fansIntent);
                break;
            case R.id.ll_my_follow:
                startActivity(new Intent(getActivity(), FollowersListActivity.class));
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent messageEvent) {
        if (messageEvent.hasNewMessage) {
            mTvMessageCount.setVisibility(View.VISIBLE);
            mTvMessageCount.setText("1");
        } else {
            mTvMessageCount.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserInfoUpdateEvent loginevent) {
        updateUi();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initFeedback() {
        if (mPreferences.isLogin() && EVApplication.isLogin()) {
            JSONObject jsonObject = new JSONObject();
            String phoneNumber = Preferences.getInstance(getContext())
                    .getString(Preferences.KEY_LOGIN_PHONE_NUMBER, "");
            if (phoneNumber.startsWith(ApiConstant.VALUE_COUNTRY_CODE_CHINA)) {
                String[] numbers = StringUtil.parseFullPhoneNumber(Preferences.getInstance(getContext())
                        .getString(Preferences.KEY_LOGIN_PHONE_NUMBER, ""));
                if (numbers.length == 2) {
                    phoneNumber = numbers[1];
                }
            }
            /// TODO: 8/9/16 This need to replace with dynamic url
            String toAvatar = "http://aliimg.yizhibo.tv/test/message/07/fd/Secretary.png";
            if (EVApplication.getUser() != null) {
                FeedbackHelper.getInstance(getContext()).customUI(getString(R.string.feedback), phoneNumber,
                        EVApplication.getUser().getLogourl(), toAvatar);
            } else {
                FeedbackHelper.getInstance(getContext()).customUI(getString(R.string.feedback), phoneNumber,
                        "", toAvatar);
            }

            String qqNumber = Preferences.getInstance(getContext()).getString(Preferences.KEY_LOGIN_QQ_NUMBER, "");
            // Calculate user age .
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(Calendar.YEAR);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            ParsePosition pos = new ParsePosition(0);
            String birthDay = EVApplication.getUser().getBirthday();
            int ageInt = 0;
            if (!TextUtils.isEmpty(birthDay)) {
                Date birthdayDate = formatter.parse(birthDay, pos);
                if (birthdayDate != null) {
                    calendar.setTimeInMillis(birthdayDate.getTime());
                    ageInt = currentYear - calendar.get(Calendar.YEAR);
                }
            }
            try {
                if (!TextUtils.isEmpty(phoneNumber)) {
                    jsonObject.put("phone", phoneNumber);
                }
                if (!TextUtils.isEmpty(qqNumber)) {
                    jsonObject.put("qq", qqNumber);
                }
                if (ageInt > 0) {
                    jsonObject.put("age", ageInt);
                }
                jsonObject.put("name", Preferences.getInstance(getContext()).getUserNumber());
                jsonObject.put("nickname", Preferences.getInstance(getContext()).getUserNickname());
                jsonObject.put("gender", EVApplication.getUser().getGender());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FeedbackHelper.getInstance(getContext()).setAppExtInfo(jsonObject);
        }
    }
}
