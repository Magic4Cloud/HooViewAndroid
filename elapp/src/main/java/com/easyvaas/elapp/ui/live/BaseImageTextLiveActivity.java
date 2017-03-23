package com.easyvaas.elapp.ui.live;


import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.easyvaas.elapp.event.ImageTextLiveMessageEvent;
import com.easyvaas.elapp.event.JoinRoomSuccessEvent;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.Utils;
import com.easyvaas.elapp.utils.ViewUtil;
import com.hooview.app.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public abstract class BaseImageTextLiveActivity extends BaseActivity implements EMMessageListener {
    private static final String TAG = "BaseImageTextLiveActivity";
    protected MyAdapter mMyAdapter;
    protected ViewPager mViewPager;
    protected String mRoomId;
    private boolean isAnchor;
    protected View mRoot;
    private CompositeSubscription compositeSubscription;
    private boolean forbiddenCmdMessage = true; // 禁止礼物消息的处理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    private void initView() {
        mRoomId = setRoomId();
        isAnchor = isAnchor();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(mViewPager);
        List<Fragment> list = new ArrayList<>();
//        list.add(ImageTextLiveFragment.newInstance(streamEntity()));
                list.add(isAnchor ? ImageTextLiveFragment.newInstance(mRoomId, isAnchor, watchCount()) : ImageTextLiveFragment.newInstance(streamEntity()));
        list.add(ImageTextLiveDataFragment.newInstance(isAnchor));
        list.add(ImageTextLiveChatFragment.newInstance(isAnchor, mRoomId));
        list.add(BookPlayFragment.newInstance());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin((int) ViewUtil.dp2Px(getApplicationContext(), 10));
        mMyAdapter = new MyAdapter(getSupportFragmentManager(), list, getResources().getStringArray(R.array.image_text_live));
        mViewPager.setAdapter(mMyAdapter);
        mRoot = findViewById(R.id.root);
        joinChatRoom();
        mViewPager.setOffscreenPageLimit(3);
    }

    public abstract String setRoomId();

    public abstract boolean isAnchor();

    public abstract int watchCount();

    public abstract TextLiveListModel.StreamsEntity streamEntity();

    private void joinChatRoom() {
        EMClient.getInstance().chatroomManager().joinChatRoom(mRoomId, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(final EMChatRoom value) {
//                if (value != null && !mRoomId.equals(value.getId())) {
                    EventBus.getDefault().post(new JoinRoomSuccessEvent());
//                }
            }

            @Override
            public void onError(final int error, String errorMsg) {
                Logger.d(TAG, "join room onError : " + errorMsg);
//                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription != null) compositeSubscription.unsubscribe();
        EMClient.getInstance().chatroomManager().leaveChatRoom(mRoomId);
    }

    public void goToChat() {
        mViewPager.setCurrentItem(2);
    }

    public void share() {
        mRoot.buildDrawingCache();
        String imageUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.jpg";
        Utils.saveBitmap(mRoot.getDrawingCache(), imageUrl);
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        } else {
            ScreenShotShareActivity.start(this, imageUrl, EVApplication.getUser().getNickname() + getString(R.string.who_room));
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        Logger.d(TAG, "onMessageReceived: " + list.size());
        EventBus.getDefault().post(new ImageTextLiveMessageEvent(list));
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        if (forbiddenCmdMessage) {
            // 延迟 1 秒才对礼物消息做处理
            Subscription noAdSubscribe = Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                            forbiddenCmdMessage = false;
                        }

                        @Override
                        public void onError(Throwable e) {
                            forbiddenCmdMessage = false;

                        }

                        @Override
                        public void onNext(Long aLong) {
                        }
                    });
            addSubscription(noAdSubscribe);
        } else {
            EventBus.getDefault().post(new ImageTextLiveMessageEvent(list, ImageTextLiveMessageEvent.MSG_TYPE_CMD));
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    public void addSubscription(Subscription subscription) {
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm, List<Fragment> list, String[] tabTitles) {
            super(fm);
            this.mTabsTitle = tabTitles;
            this.fragments = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabsTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }
}
