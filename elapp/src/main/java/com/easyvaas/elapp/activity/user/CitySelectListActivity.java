/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.activity.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.easyvaas.common.widget.LetterSideBar;
import com.easyvaas.common.widget.stickylistview.StickyListHeadersListView;

import com.hooview.app.R;
import com.easyvaas.elapp.adapter.CityLetterSortAdapter;
import com.easyvaas.elapp.bean.CountryCodeEntity;
import com.easyvaas.elapp.utils.CharacterParser;
import com.easyvaas.elapp.utils.Logger;
import com.easyvaas.elapp.utils.PinyinComparatorCountry;
import com.easyvaas.elapp.utils.SingleToast;

public class CitySelectListActivity extends Activity implements
        StickyListHeadersListView.OnHeaderClickListener, AdapterView.OnItemClickListener
        , StickyListHeadersListView.OnLoadingMoreListener {

    private static final String TAG = CitySelectListActivity.class.getSimpleName();
    public static final String EXTRA_KEY_SELECT_CITY = "extra_key_select_city";

    private CityLetterSortAdapter mAdapter;
    private StickyListHeadersListView stickyLV;

    private CharacterParser mCharacterParser;
    private List<CountryCodeEntity> sourceDateFilterList = new ArrayList<CountryCodeEntity>();
    private EditText searchEt;
    private List<CountryCodeEntity> sourceDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        initView();
    }

    public void initView() {
        mCharacterParser = CharacterParser.getInstance();
        PinyinComparatorCountry pinyinComparator = new PinyinComparatorCountry();
        sourceDateList = filledData(getResources().getStringArray(R.array.cities_data));
        Collections.sort(sourceDateList, pinyinComparator);

        mAdapter = new CityLetterSortAdapter(this, sourceDateList);
        stickyLV = (StickyListHeadersListView) this.findViewById(R.id.stickyList);
        stickyLV.setAdapter(mAdapter);
        stickyLV.setOnItemClickListener(this);
        stickyLV.setOnHeaderClickListener(this);
        stickyLV.setLoadingMoreListener(this);
        LetterSideBar letterSideBar = (LetterSideBar) findViewById(R.id.cs_letter_sb);
        letterSideBar.setOnTouchingLetterChangedListener(
                new LetterSideBar.OnTouchingLetterChangedListener() {
                    @Override
                    public void onTouchingLetterChanged(String letter) {
                        Logger.d(TAG, "onTouchingLetterChanged  letter: " + letter);
                        int jumpPos = mAdapter.getPositionForSection(letter.charAt(0));
                        stickyLV.setSelection(jumpPos);
                    }
                });
        letterSideBar.setTextView((TextView) findViewById(R.id.cs_selected_letter_tv));

        searchEt = (EditText) findViewById(R.id.cs_search_et);
        searchEt.setVisibility(View.VISIBLE);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    mAdapter.updateListView(sourceDateList);
                } else {
                    String searchText = searchEt.getText().toString();
                    if (TextUtils.isEmpty(searchText)) {
                        SingleToast.show(getApplicationContext(), R.string.msg_keyword_is_empty);
                    }
                    sourceDateFilterList.clear();
                    for (int h = 0; h < sourceDateList.size(); h++) {
                        if (sourceDateList.get(h).getName().contains(searchText)) {
                            sourceDateFilterList.add(sourceDateList.get(h));
                        }
                    }
                    mAdapter.updateListView(sourceDateFilterList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private List<CountryCodeEntity> filledData(String[] date) {
        List<CountryCodeEntity> sortList = new ArrayList<CountryCodeEntity>();
        for (int i = 0, n = date.length; i < n; i++) {
            CountryCodeEntity sortModel = new CountryCodeEntity();
            sortModel.setName(date[i]);
            String pinyin = mCharacterParser.getSelling(date[i]);
            sortModel.setPinyin(pinyin);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetter(sortString.toUpperCase());
            } else {
                sortModel.setSortLetter("#");
            }

            sortList.add(sortModel);
        }
        return sortList;
    }

    @Override
    public void onLoadingMore() {
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header,
            int itemPosition, long headerId, boolean currentlySticky) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        String city = ((CountryCodeEntity) mAdapter.getItem(position)).getName();
        setResult(RESULT_OK, getIntent().putExtra(CitySelectListActivity.EXTRA_KEY_SELECT_CITY, city));
        finish();
    }
}
