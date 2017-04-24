package com.easyvaas.elapp.ui.user.newuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.event.NewMessageEvent;
import com.easyvaas.elapp.event.UserInfoUpdateEvent;
import com.easyvaas.elapp.net.ApiUtil;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.ui.user.MessageUnReadListActivity;
import com.easyvaas.elapp.ui.user.SettingActivity;
import com.easyvaas.elapp.ui.user.UserInfoActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserBalanceActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserBuyActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserCollectionNewActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserFansActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserFocusActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserHistoryNewActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserPublishActivity;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.Utils;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/19
 * Editor  Misuzu
 * 个人中心
 */

public class UserCenterFragment extends MyBaseFragment {

    @BindView(R.id.user_settting)
    ImageView mUserSettting;
    @BindView(R.id.user_center_name)
    TextView mUserCenterName;
    @BindView(R.id.user_center_sex)
    ImageView mUserCenterSex;
    @BindView(R.id.user_center_introduce)
    TextView mUserCenterIntroduce;
    @BindView(R.id.user_center_focuscounts)
    TextView mUserCenterFocuscounts;
    @BindView(R.id.user_center_focustxt)
    TextView mUserCenterFocustxt;
    @BindView(R.id.user_center_fanscounts)
    TextView mUserCenterFanscounts;
    @BindView(R.id.user_center_fanstxt)
    TextView mUserCenterFanstxt;
    @BindView(R.id.user_center_avator)
    MyUserPhoto mUserCenterAvator;
    @BindView(R.id.user_center_msgimg)
    ImageView mUserCenterMsgimg;
    @BindView(R.id.user_center_msgred)
    ImageView mUserCenterMsgred;
    @BindView(R.id.user_center_message)
    LinearLayout mUserCenterMessage;
    @BindView(R.id.user_center_balance)
    LinearLayout mUserCenterBalance;
    @BindView(R.id.user_center_publish)
    LinearLayout mUserCenterPublish;
    @BindView(R.id.user_center_buy)
    LinearLayout mUserCenterBuy;
    @BindView(R.id.user_center_collect)
    LinearLayout mUserCenterCollect;
    @BindView(R.id.user_center_history)
    LinearLayout mUserCenterHistory;
    @BindView(R.id.user_edit_layout)
    RelativeLayout mEidtLayout;
    private Bundle bundle;
    private Preferences mPreferences;

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_center_layout;
    }

    @Override
    protected void initViewAndData() {
        bundle = new Bundle();
        mPreferences = Preferences.getInstance(getContext().getApplicationContext());
        EventBus.getDefault().register(this);
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo() {

        String userId = Preferences.getInstance(EVApplication.getApp()).getUserNumber();
        String sessionId = Preferences.getInstance(EVApplication.getApp()).getSessionId();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(sessionId)) {
            return;
        }
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .getUserInfo(userId,sessionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<User>() {
                    @Override
                    public void OnSuccess(User user) {
                        EVApplication.setUser(user);
                        initUserInfo(user);
                        updateUI();
                    }

                    @Override
                    public void OnFailue(String msg) {

                    }
                });
        addSubscribe(subscription);
    }


    /**
     * 更新UI
     */
    private void updateUI()
    {
        if (mPreferences.isLogin() && EVApplication.isLogin())
        {
            User user = EVApplication.getUser();
            mUserCenterName.setText(user.getNickname());
            mUserCenterFanscounts.setText(String.valueOf(user.getFans_count()));
            mUserCenterFocuscounts.setText(String.valueOf(user.getFollow_count()));
            mUserCenterIntroduce.setText(user.getSignature());
            Picasso.with(getContext()).load(user.getLogourl()).placeholder(R.drawable.user_avtor).into(mUserCenterAvator);
            if (!user.getGender().equals("male"))
                mUserCenterSex.setImageResource(R.drawable.ic_woman);
            else
                mUserCenterSex.setImageResource(R.drawable.ic_man);
            if (user.getVip() == 1) // 1 大V 0 普通用户
            {
                mUserCenterAvator.setIsVip(1);
                mUserCenterPublish.setVisibility(View.VISIBLE);
            }else
            {
                mUserCenterAvator.setIsVip(0);
                mUserCenterPublish.setVisibility(View.GONE);
            }
            mUserCenterSex.setVisibility(View.VISIBLE);
        }else  // 未登录情况
        {
            mUserCenterName.setText(R.string.user_login_text);
            mUserCenterIntroduce.setText(R.string.user_login_info);
            mUserCenterSex.setVisibility(View.GONE);
            mUserCenterFocuscounts.setText("0");
            mUserCenterFanscounts.setText("0");
            mUserCenterAvator.setIsVip(0);
            mUserCenterAvator.setImageResource(R.drawable.user_avtor);
        }

    }

    //哎 暂时用到吧
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
            if (user.getTags() != null && user.getTags().size() > 0){
                List<String> list = new ArrayList<>();
                for (int i= 0; i < user.getTags().size(); i++){
                    list.add(user.getTags().get(i).getName());
                }
                bundle.putStringArrayList(Constants.EXTRA_ADD_LABEL, (ArrayList<String>)list);
            }
        }
    }


    @Override
    public void onResume() {   //暂时和它之前一样处理
        super.onResume();
        updateUI();
        getUserInfo();
        ApiUtil.checkServerParam(getContext());
        ApiUtil.checkUnreadMessage(getContext());
//        ApiUtil.checkSession(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.user_settting, R.id.user_center_focuscounts, R.id.user_center_focustxt, R.id.user_center_fanscounts, R.id.user_center_fanstxt, R.id.user_center_message, R.id.user_center_balance, R.id.user_center_publish, R.id.user_center_buy, R.id.user_center_collect, R.id.user_center_history,R.id.user_edit_layout})
    public void onViewClicked(View view) {
        if (!mPreferences.isLogin() || !EVApplication.isLogin()) {
            LoginActivity.start(getActivity());
            return;
        }
        switch (view.getId()) {
            case R.id.user_edit_layout:  // 编辑资料
//                UserInfoActivity.start(getActivity(),bundle);
                Utils.toUserPager(mContext,EVApplication.getUser().getName(),1);
                break;
            case R.id.user_settting: // 设置界面
                SettingActivity.start(getActivity());
                break;
            case R.id.user_center_focuscounts:
            case R.id.user_center_focustxt: //关注列表
                startActivity(new Intent(getActivity(), UserFocusActivity.class));
                break;
            case R.id.user_center_fanscounts:
            case R.id.user_center_fanstxt: // 粉丝列表
                Intent fansIntent = new Intent(getActivity(), UserFansActivity.class);
                fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID,
                        Preferences.getInstance(getContext()).getUserNumber());
                startActivity(fansIntent);
                break;
            case R.id.user_center_message: // 消息列表
                MessageUnReadListActivity.start(getActivity());
                break;
            case R.id.user_center_balance: // 余额列表
                /*Intent intent = new Intent(getActivity(), PayRecordListActivity.class);
                intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                        PayRecordListActivity.TYPE_CASH_IN);
                startActivity(intent);*/
                UserBalanceActivity.start(getActivity(), bundle);
                break;
            case R.id.user_center_publish:
                startActivity(new Intent(getActivity(), UserPublishActivity.class));
                break;
            case R.id.user_center_buy:
                startActivity(new Intent(getActivity(), UserBuyActivity.class));
                break;
            case R.id.user_center_collect: //收藏列表
                startActivity(new Intent(getActivity(),UserCollectionNewActivity.class));
                break;
            case R.id.user_center_history: //历史记录
                startActivity(new Intent(getActivity(),UserHistoryNewActivity.class));
                break;
        }
    }

    public static UserCenterFragment newInstance() {
        return new UserCenterFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)   //未读消息
    public void onMessageEvent(NewMessageEvent messageEvent) {
        if (messageEvent.hasNewMessage) {
            mUserCenterMsgimg.setImageResource(R.drawable.ic_newmessage);
            mUserCenterMsgred.setVisibility(View.VISIBLE);
        } else {
            mUserCenterMsgimg.setImageResource(R.drawable.ic_message);
            mUserCenterMsgred.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) // 先保持和之前一样
    public void onMessageEvent(UserInfoUpdateEvent loginevent) {
        updateUI();
    }
}
