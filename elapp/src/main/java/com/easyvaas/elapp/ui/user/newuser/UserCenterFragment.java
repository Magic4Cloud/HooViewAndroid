package com.easyvaas.elapp.ui.user.newuser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.activity.user.FansListActivity;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.base.mybase.MyBaseFragment;
import com.easyvaas.elapp.ui.pay.PayRecordListActivity;
import com.easyvaas.elapp.ui.user.FollowersListActivity;
import com.easyvaas.elapp.ui.user.MessageUnReadListActivity;
import com.easyvaas.elapp.ui.user.MyPublishActivity;
import com.easyvaas.elapp.ui.user.SettingActivity;
import com.easyvaas.elapp.ui.user.UserHistoryActivity;
import com.easyvaas.elapp.ui.user.UserInfoModifyActivity;
import com.easyvaas.elapp.ui.user.newuser.activity.UserHistoryNewActivity;
import com.easyvaas.elapp.utils.Constants;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.OnClick;

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

    @Override
    protected int getLayout() {
        return R.layout.fragment_user_center_layout;
    }

    @Override
    protected void initViewAndData() {
        mUserCenterAvator.setIsVip(1);
    }

    public static UserCenterFragment newInstance() {

        return new UserCenterFragment();
    }



    @OnClick({R.id.user_settting, R.id.user_center_focuscounts, R.id.user_center_focustxt, R.id.user_center_fanscounts, R.id.user_center_fanstxt, R.id.user_center_message, R.id.user_center_balance, R.id.user_center_publish, R.id.user_center_buy, R.id.user_center_collect, R.id.user_center_history,R.id.user_edit_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_edit_layout:  // 编辑资料
                UserInfoModifyActivity.start(getActivity(), new Bundle());
                break;
            case R.id.user_settting: // 设置界面
                SettingActivity.start(getActivity());
                break;
            case R.id.user_center_focuscounts:
            case R.id.user_center_focustxt: //关注列表
                startActivity(new Intent(getActivity(), FollowersListActivity.class));
                break;
            case R.id.user_center_fanscounts:
            case R.id.user_center_fanstxt: // 粉丝列表
                Intent fansIntent = new Intent(getActivity(), FansListActivity.class);
                fansIntent.putExtra(Constants.EXTRA_KEY_USER_ID,
                        Preferences.getInstance(getContext()).getUserNumber());
                startActivity(fansIntent);
                break;
            case R.id.user_center_message: // 消息列表
                MessageUnReadListActivity.start(getActivity());
                break;
            case R.id.user_center_balance: // 余额列表
                Intent intent = new Intent(getActivity(), PayRecordListActivity.class);
                intent.putExtra(PayRecordListActivity.EXTRA_ACTIVITY_TYPE,
                        PayRecordListActivity.TYPE_CASH_IN);
                startActivity(intent);
                break;
            case R.id.user_center_publish:
                startActivity(new Intent(getActivity(), MyPublishActivity.class));
                break;
            case R.id.user_center_buy:
                break;
            case R.id.user_center_collect: //收藏列表
                startActivity(new Intent(getActivity(),UserHistoryActivity.class));
                break;
            case R.id.user_center_history: //历史记录
                startActivity(new Intent(getActivity(),UserHistoryNewActivity.class));
                break;
        }
    }
}
