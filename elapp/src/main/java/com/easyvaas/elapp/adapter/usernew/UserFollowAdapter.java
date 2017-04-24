package com.easyvaas.elapp.adapter.usernew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.bean.user.UserFollow;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.easyvaas.elapp.utils.SingleToast;
import com.hooview.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.tencent.open.utils.Global.getContext;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 用户粉丝 关注 adapter
 */

public class UserFollowAdapter extends MyBaseAdapter<UserFollow> {

    public UserFollowAdapter(List<UserFollow> data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return new FollowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_follow_item, parent, false));
    }

    @Override
    protected void initOnItemClickListener() {
        // Aya : 2017/4/21 跳转个人主页
    }

    @Override
    protected void convert(BaseViewHolder helper, UserFollow item) {
        ((FollowViewHolder)helper).setData(item);
    }

    public class FollowViewHolder extends BaseViewHolder {

        @BindView(R.id.user_follow_avator)
        MyUserPhoto mUserFollowAvator;
        @BindView(R.id.user_follow_name)
        TextView mUserFollowName;
        @BindView(R.id.user_follow_info)
        TextView mUserFollowInfo;
        @BindView(R.id.user_follow_button)
        TextView mUserFollowButton;

        public FollowViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(UserFollow data) {
            mUserFollowName.setText(data.getNickname());
            mUserFollowInfo.setText(data.getSignature());
            Picasso.with(getContext()).load(data.getLogourl()).placeholder(R.drawable.user_avtor).into(mUserFollowAvator);
            if (data.getVip() == 1){ // 1 大V 0 普通用户
                mUserFollowAvator.setIsVip(1);
            } else {
                mUserFollowAvator.setIsVip(0);
            }
            if (data.getFollowed() == 1) // 0 关注 1 已关注
            {
                mUserFollowButton.setSelected(true);
                mUserFollowButton.setText(R.string.user_followed);
            }else
            {
                mUserFollowButton.setSelected(false);
                mUserFollowButton.setText(R.string.user_follow);
            }
        }

        @OnClick(R.id.user_follow_button)
        public void onClick()
        {
            final UserFollow data = mData.get(getLayoutPosition());
            final int action = mUserFollowButton.isSelected() ? 0 : 1; // 0 取消关注 1 关注
            RetrofitHelper.getInstance().getService()
                    .followSomeOne(data.getName(),EVApplication.getUser().getSessionid(),action)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSubscribe<NoResponeBackModel>() {
                        @Override
                        public void OnSuccess(NoResponeBackModel s) {
                            if (action == 1) // 0 关注 1 已关注
                            {
                                mUserFollowButton.setSelected(true);
                                mUserFollowButton.setText(R.string.user_followed);
                                SingleToast.show(EVApplication.getApp(),R.string.follow_sccuess);
                            }else
                            {
                                mUserFollowButton.setSelected(false);
                                mUserFollowButton.setText(R.string.user_follow);
                                SingleToast.show(EVApplication.getApp(),R.string.follow_cancel);
                            }
                        }

                        @Override
                        public void OnFailue(String msg) {
                            SingleToast.show(EVApplication.getApp(),R.string.opreat_fail);
                        }
                    });
        }
    }

}