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

import com.easyvaas.elapp.event.SearchEvent;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.utils.SingleToast;
import com.hooview.app.R;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * 股票搜索
 * Created by guojun on 2016/12/28 18:10.
 */
public class SearchStockActivity extends BaseActivity implements View.OnClickListener {
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    private String mKeyWord;
    private TextView mTvOperation;
    private EditText mEtSearch;

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchStockActivity.class);
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
                mKeyWord = String.valueOf(s);
                if (!TextUtils.isEmpty(mKeyWord) && mKeyWord.length() > 0) {
                    mTvOperation.setSelected(true);
                    mTvOperation.setText(R.string.complete);
                }
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
                if (!mTvOperation.isSelected() && mTvOperation.getText().equals(getString(R.string.cancel))) {
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
        private Map<Integer, Fragment> fragments = new HashMap<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
            fragments.put(position, fragment = GlobalSearchListFragment.newInstance(GlobalSearchListFragment.TYPE_STOCk, mKeyWord, true));
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.all);
        }

    }
}
