package com.easyvaas.elapp.ui.live.livenew.fragment;

import android.os.Bundle;
import android.view.Gravity;

import com.easyvaas.elapp.adapter.usernew.NormalCommentAdapter;
import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.User;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.bean.video.VideoCommentModel;
import com.easyvaas.elapp.event.LiveCommentEvent;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.AppConstants;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/5/03
 * Editor  Misuzu
 * 精品视频 评论列表
 */

public class VideoCommentFragment extends MyBaseListFragment<NormalCommentAdapter>  {

    private String userId = "";
    private String vid;

    @Override
    protected NormalCommentAdapter initAdapter() {
        return new NormalCommentAdapter(new ArrayList<PostsBean>(),NormalCommentAdapter.VIDEO_TYPE);
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void initSomeData() {
        if (EVApplication.isLogin())
            userId = EVApplication.getUser().getUserid();
        vid = getArguments().getString(AppConstants.VID);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void changeEmptyView() {
        mEmptyView.setEmptyTxt(getString(R.string.empty_video_no_comment));
        mEmptyView.getEmptyLayout().setGravity(Gravity.CENTER_HORIZONTAL);
        mEmptyView.getEmptyLayout().setPadding(0, (int) ViewUtil.dp2Px(mContext,80),0,0);
        mEmptyView.hideImage();
    }

    @Override
    protected void getListData(final Boolean isLoadMore) {

        Subscription subscription = RetrofitHelper.getInstance().getService()
               .getCommentListByType(vid,userId,1,start,AppConstants.DATELINE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<VideoCommentModel>() {
                    @Override
                    public void OnSuccess(VideoCommentModel userPageCommentModel) {
                        if (userPageCommentModel != null)
                            mAdapter.dealLoadData(VideoCommentFragment.this,isLoadMore,userPageCommentModel.getPosts());
                    }

                    @Override
                    public void OnFailue(String msg) {
                        mAdapter.dealLoadError(VideoCommentFragment.this,isLoadMore);
                    }
                });
        addSubscribe(subscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public static VideoCommentFragment newInstance(String vid) {
        Bundle args = new Bundle();
        args.putString(AppConstants.VID,vid);
        VideoCommentFragment fragment = new VideoCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LiveCommentEvent event) {
        sendComment(event.content);
    }

    public void sendComment(String content) {
        User user = EVApplication.getUser();
        if (user != null) {
            HooviewApiHelper.getInstance().sendVideoComment(vid, user.getName(),
                    user.getNickname(), user.getLogourl(), content, new MyRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    SingleToast.show(getContext(), getString(R.string.send_success));
                    onRefresh();
                }

                @Override
                public void onFailure(String msg) {

                }

                @Override
                public void onError(String errorInfo) {
                    super.onError(errorInfo);
                }
            });
        }
    }

}
