package com.easyvaas.elapp.ui.user.usernew.activity;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.NoResponeBackModel;
import com.easyvaas.elapp.event.UserHistoryCleanEvent;
import com.easyvaas.elapp.net.mynet.NetSubscribe;
import com.easyvaas.elapp.net.mynet.RetrofitHelper;
import com.easyvaas.elapp.ui.base.mybase.MyBaseActivity;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserHistoryReadNewFragment;
import com.easyvaas.elapp.ui.user.usernew.fragment.UserHistoryWatchFragment;
import com.easyvaas.elapp.utils.SingleToast;
import com.flyco.tablayout.SlidingTabLayout;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Date   2017/4/20
 * Editor  Misuzu
 * 用户历史记录
 */

public class UserHistoryNewActivity extends MyBaseActivity {

    @BindView(R.id.user_history_tablayout)
    SlidingTabLayout mUserHistoryTablayout;
    @BindView(R.id.user_history_viewpager)
    ViewPager mUserHistoryViewpager;
    private String[] titles;
    private Fragment[] mFragments;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_history_new_layout;
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.user_history);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        mToobarTitleView.setTitleRightImg(R.drawable.btn_clear_n, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserHistoryNewActivity.this);
                builder.setMessage(mUserHistoryViewpager.getCurrentItem() == 0? R.string.user_clean_watch_sure:R.string.user_clean_news_sure)
                        .setNegativeButton(getString(R.string.user_cancel), null)
                        .setPositiveButton(getString(R.string.user_sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                cleanHistory();

                            }
                        });
                builder.show();
            }
        });

    }

    /**
     * 清空历史记录
     */
    private void cleanHistory()
    {
        final int type = mUserHistoryViewpager.getCurrentItem();
        Subscription subscription = RetrofitHelper.getInstance().getService()
                .cleanHistoryList(EVApplication.getUser().getUserid(),EVApplication.getUser().getSessionid(),type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSubscribe<NoResponeBackModel>() {
                    @Override
                    public void OnSuccess(NoResponeBackModel noResponeBackModel) {
                        EventBus.getDefault().post(new UserHistoryCleanEvent(type)); // 发送清除消息
                        SingleToast.show(UserHistoryNewActivity.this,R.string.user_clean_succuss);
                    }

                    @Override
                    public void OnFailue(String msg) {
                        SingleToast.show(UserHistoryNewActivity.this,R.string.user_clean_fail);
                    }
                });
    }

    @Override
    protected void initViewAndData() {
        titles = new String[]{
                getString(R.string.user_history_watch),
                getString(R.string.user_history_read)};
        mFragments = new Fragment[]{
                UserHistoryWatchFragment.newInstance(),
                UserHistoryReadNewFragment.newInstance()};

        mUserHistoryViewpager.setAdapter(new HistoryPageAdapter(getSupportFragmentManager()));
        mUserHistoryTablayout.setViewPager(mUserHistoryViewpager);
    }



    private class HistoryPageAdapter extends FragmentPagerAdapter {

        private HistoryPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
