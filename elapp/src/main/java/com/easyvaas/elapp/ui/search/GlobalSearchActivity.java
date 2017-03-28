package com.easyvaas.elapp.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hooview.app.R;
import com.easyvaas.elapp.event.SearchEvent;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.utils.SingleToast;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


public class GlobalSearchActivity extends BaseActivity implements View.OnClickListener {
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    private String mKeyWord;
    private TextView mTvOperation;
    private EditText mEtSearch;

    public static void start(Context context) {
        Intent starter = new Intent(context, GlobalSearchActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                EventBus.getDefault().post(new SearchEvent(mKeyWord, position));
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
        mTvOperation = (TextView) findViewById(R.id.tv_operation);
        mTvOperation.setOnClickListener(this);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mTvOperation.setSelected(false);
                mTvOperation.setText(R.string.cancel);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                mKeyWord = String.valueOf(s);
//                if(!TextUtils.isEmpty(mKeyWord)&&mKeyWord.length()>0){
//                    mTvOperation.setSelected(true);
//                    mTvOperation.setText(R.string.complete);
//                }
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mKeyWord = mEtSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(mKeyWord)) {
                        SingleToast.show(getApplicationContext(), getString(R.string.search_empty_prompt));
                    } else {
                        mTvOperation.setSelected(true);
                        EventBus.getDefault().post(new SearchEvent(mKeyWord, mViewPager.getCurrentItem()));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_operation:
                if (!mTvOperation.isSelected()&&mTvOperation.getText().equals(getString(R.string.cancel))) {
                    finish();
                } else {
                    mKeyWord = mEtSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(mKeyWord)) {
                        SingleToast.show(getApplicationContext(), getString(R.string.search_empty_prompt));
                    } else {
                        mTvOperation.setSelected(true);
                        EventBus.getDefault().post(new SearchEvent(mKeyWord, mViewPager.getCurrentItem()));
                    }

                }
                break;
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] mTabsTitle;
        private Map<Integer, Fragment> fragments = new HashMap<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabsTitle = getResources().getStringArray(R.array.search_tab);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            if (position == 0) {
                fragments.put(position, fragment = GlobalSearchListFragment.newInstance(GlobalSearchListFragment.TYPE_NEWS, mKeyWord, position));
            } else if (position == 1) {
                fragments.put(position, fragment = GlobalSearchListFragment.newInstance(GlobalSearchListFragment.TYPE_LIVE, mKeyWord, position));
            } else {
                fragments.put(position, fragment = GlobalSearchListFragment.newInstance(GlobalSearchListFragment.TYPE_STOCk, mKeyWord, position));
            }
            return fragment;
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
