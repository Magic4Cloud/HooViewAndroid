/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.easyvaas.common.widget.LetterSideBar;
import com.easyvaas.common.widget.stickylistview.StickyListHeadersListView;
import com.hooview.app.R;
import com.hooview.app.adapter.CountryCodeAdapter;
import com.hooview.app.base.BaseActivity;
import com.hooview.app.bean.CountryCodeEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.utils.CharacterParser;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.PinyinComparatorCountry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CountryCodeListActivity extends BaseActivity implements
        StickyListHeadersListView.OnHeaderClickListener, AdapterView.OnItemClickListener
        , StickyListHeadersListView.OnLoadingMoreListener {
    private static final String TAG = CountryCodeListActivity.class.getSimpleName();
    private CountryCodeAdapter mAdapter;
    private List<CountryCodeEntity> mCountryCodeList;
    private CharacterParser mCharacterParser;
    private StickyListHeadersListView mStickyLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(com.hooview.app.R.string.country_code);
        setTitleColor(getResources().getColor(android.R.color.white));

        setContentView(com.hooview.app.R.layout.activity_city_select);
        TextView tv = (TextView) findViewById(R.id.common_custom_title_tv);
        tv.setText(R.string.country_code);
        findViewById(R.id.close_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        mCharacterParser = CharacterParser.getInstance();
        mCountryCodeList = initData(getResources().getStringArray(com.hooview.app.R.array.country_code_data));
        mAdapter = new CountryCodeAdapter(this, mCountryCodeList);
        mStickyLv = (StickyListHeadersListView) this.findViewById(com.hooview.app.R.id.stickyList);
        mStickyLv.setAdapter(mAdapter);
        mStickyLv.setOnItemClickListener(this);
        mStickyLv.setOnHeaderClickListener(this);
        mStickyLv.setLoadingMoreListener(this);
        mStickyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String countryName = mCountryCodeList.get(position).getName();
                String countryCode = mCountryCodeList.get(position).getCode();
                Preferences.getInstance(CountryCodeListActivity.this)
                        .putString(Preferences.KEY_COUNTRY_NAME, countryName);
                Preferences.getInstance(CountryCodeListActivity.this)
                        .putString(Preferences.KEY_COUNTRY_CODE, countryCode);
                finish();
            }
        });

        LetterSideBar letterSideBar = (LetterSideBar) findViewById(com.hooview.app.R.id.cs_letter_sb);
        letterSideBar.setOnTouchingLetterChangedListener(
                new LetterSideBar.OnTouchingLetterChangedListener() {
                    @Override
                    public void onTouchingLetterChanged(String letter) {
                        Logger.d(TAG, "onTouchingLetterChanged  letter: " + letter);
                        int jumpPos = mAdapter.getPositionForSection(letter.charAt(0));
                        mStickyLv.setSelection(jumpPos);
                    }
                });
        letterSideBar.setTextView((TextView) findViewById(com.hooview.app.R.id.cs_selected_letter_tv));
    }

    private List<CountryCodeEntity> initData(String[] sData) {
        List<CountryCodeEntity> sortList = new ArrayList<CountryCodeEntity>();
        for (int i = 0; i < sData.length; i++) {
            CountryCodeEntity countryCodeEntity = new CountryCodeEntity();
            countryCodeEntity.setName(sData[i].split("#")[0]);
            countryCodeEntity.setCode(sData[i].split("#")[1]);
            String pinyin = mCharacterParser.getSelling(sData[i].split("#")[0]);
            countryCodeEntity.setPinyin(pinyin);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                countryCodeEntity.setSortLetter(sortString.toUpperCase());
            } else {
                countryCodeEntity.setSortLetter("#");
            }
            sortList.add(countryCodeEntity);
        }
        PinyinComparatorCountry pyComparatorCountry = new PinyinComparatorCountry();
        Collections.sort(sortList, pyComparatorCountry);
        return sortList;
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition,
                              long headerId, boolean currentlySticky) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onLoadingMore() {

    }
}
