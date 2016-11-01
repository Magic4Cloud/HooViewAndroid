/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyvaas.common.adapter.CommonRcvAdapter;
import com.easyvaas.common.widget.flowlayout.FlowLayout;
import com.easyvaas.common.widget.flowlayout.TagAdapter;
import com.easyvaas.common.widget.flowlayout.TagFlowLayout;
import com.hooview.app.R;
import com.hooview.app.adapter.recycler.SearchAdapter;
import com.hooview.app.base.BaseRvcActivity;
import com.hooview.app.bean.SearchInfoEntity;
import com.hooview.app.bean.user.UserEntity;
import com.hooview.app.bean.video.VideoEntity;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiConstant;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.SingleToast;
import com.hooview.app.utils.UserUtil;
import com.hooview.app.utils.Utils;
import com.hooview.app.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchListActivity extends BaseRvcActivity implements View.OnClickListener {
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    public static final String EXTRA_KEY_KEYWORD = "extra_key_keyword";

    private EditText mKeywordEt;
    private TextView mOperationTv;
    private String mType;

    private SearchAdapter mAdapter;

    private Preferences mPref;
    private LinearLayout mSearchKeywordsLl;
    private List<String> mHistoryKeywords;
    private List<String> mHotKeywords;
    private ArrayList mSearchResults;
    private TagFlowLayout mHistoryFlowLayout;
    private TextView mHistoryLabelTV;
    private TextView mClearHistoryBtn;
    private TagAdapter<String> mHistoryTagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = Preferences.getInstance(this);

        mType = getIntent().getStringExtra(EXTRA_KEY_TYPE);
        String keyword = getIntent().getStringExtra(EXTRA_KEY_KEYWORD);
        if (TextUtils.isEmpty(mType)) {
            mType = ApiConstant.VALUE_SEARCH_TYPE_ALL;
        }
        if (!TextUtils.isEmpty(keyword)) {
            mKeywordEt.setText(keyword);
        }
        mHistoryKeywords = new ArrayList<>();
        mHotKeywords = new ArrayList<>();
        mIsUserTapTopView = true;
        setContentView(com.hooview.app.R.layout.activity_search_list);
        mSearchResults = new ArrayList();
        final ImageView clearKeywordIv = (ImageView) findViewById(com.hooview.app.R.id.clear_keyword_iv);
        clearKeywordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordEt.setText("");
                v.setVisibility(View.GONE);
            }
        });
        mKeywordEt = (EditText) findViewById(com.hooview.app.R.id.tab_bar_keyword_et);

        mKeywordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    initLoadData(ApiConstant.VALUE_ANCHOR_LIST_TYPE_ALL);
                    hideInputMethod();
                    return true;
                }
                return false;
            }
        });
        mKeywordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSearchResults.clear();
                    mAdapter.notifyDataSetChanged();
                    mOperationTv.setText(com.hooview.app.R.string.cancel);
                    mEmptyView.hide();
                    clearKeywordIv.setVisibility(View.GONE);
                    if (mHistoryKeywords.size() > 0 || mHotKeywords.size() > 0) {
                        setHistoryVisibility();
                    } else {
                        mSearchKeywordsLl.setVisibility(View.GONE);
                    }
                } else {
                    mSearchKeywordsLl.setVisibility(View.GONE);
                    mOperationTv.setText(com.hooview.app.R.string.search);
                    clearKeywordIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mKeywordEt.requestFocus();

        mOperationTv = (TextView) findViewById(com.hooview.app.R.id.tab_bar_cancel_tv);
        mOperationTv.setText(getResources().getString(R.string.cancel));
        mOperationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeywordEt.getText().length() > 0) {
                    startSearch(mKeywordEt.getText().toString());
                } else {
                    finish();
                }
            }
        });
        initSearchKeyword();
        mAdapter = new SearchAdapter(this, mSearchResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPullToLoadRcvView.getRecyclerView().setLayoutManager(linearLayoutManager);
        mPullToLoadRcvView.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonRcvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Object object = mSearchResults.get(position);
                if (object instanceof VideoEntity) {
                    if (TextUtils.isEmpty(((VideoEntity) object).getVid())) {
                        return;
                    }
                    Utils.watchVideo(getApplicationContext(), (VideoEntity) object);
                } else if (object instanceof UserEntity) {
                    if (TextUtils.isEmpty(((UserEntity) object).getName())) {
                        return;
                    }
                    UserEntity userEntity = (UserEntity) object;
                    if (userEntity.getPinned() == UserEntity.IS_PINNED) {
                        Intent intent = new Intent(SearchListActivity.this, SearchListActivity.class);
                        intent.putExtra(SearchListActivity.EXTRA_KEY_TYPE, ApiConstant.VALUE_SEARCH_TYPE_USER);
                        startActivity(intent);
                    } else {
                        UserUtil.showUserInfo(getApplicationContext(), userEntity.getName());
                    }
                }
            }
        });
        mEmptyView.hide();
    }

    public void initSearchKeyword() {
        initSearchHistory();
    }

    private void initSearchHistory() {
        mSearchKeywordsLl = (LinearLayout) findViewById(com.hooview.app.R.id.search_history_ll);
        mHistoryLabelTV = (TextView) findViewById(com.hooview.app.R.id.history_label_tv);
        mClearHistoryBtn = (TextView) findViewById(com.hooview.app.R.id.clear_history_btn);
        mHistoryFlowLayout = (TagFlowLayout) findViewById(com.hooview.app.R.id.history_tfl);
        mClearHistoryBtn.setOnClickListener(this);
        String history = mPref.getString(Preferences.KEY_SEARCH_HISTORY_KEYWORD);
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mSearchKeywordsLl.setVisibility(View.VISIBLE);
        } else {
            mSearchKeywordsLl.setVisibility(View.VISIBLE);
            mHistoryFlowLayout.setVisibility(View.INVISIBLE);
            mClearHistoryBtn.setVisibility(View.INVISIBLE);
        }
        mHistoryTagAdapter = new TagAdapter<String>(mHistoryKeywords) {
            int margin = (int) ViewUtil.dp2Px(getApplicationContext(), 4);

            @Override
            public View getView(FlowLayout parent, int position, String keyWord) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, margin, margin, margin);
                TextView textView = (TextView) View.inflate(SearchListActivity.this,
                        com.hooview.app.R.layout.view_search_keyword_label, null);
                textView.setLayoutParams(params);
                textView.setText(keyWord);
                return textView;
            }
        };

        mHistoryFlowLayout.setMaxSelectCount(1);
        mHistoryFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                startSearch(mHistoryTagAdapter.getItem(position));
                return true;
            }
        });
        mHistoryFlowLayout.setAdapter(mHistoryTagAdapter);
    }

    private void setHistoryVisibility() {
        if (mHistoryKeywords.isEmpty()) {
            mClearHistoryBtn.setVisibility(View.GONE);
            //mHistoryLabelTV.setVisibility(View.GONE);
            mHistoryFlowLayout.setVisibility(View.GONE);
        } else {
            mSearchKeywordsLl.setVisibility(View.VISIBLE);
            mClearHistoryBtn.setVisibility(View.VISIBLE);
            //mHistoryLabelTV.setVisibility(View.VISIBLE);
            mHistoryFlowLayout.setVisibility(View.VISIBLE);
        }
        mHistoryLabelTV.setVisibility(View.VISIBLE);
    }

    private void startSearch(String keyword) {
        startSearch(keyword, ApiConstant.VALUE_SEARCH_TYPE_ALL);
    }

    private void startSearch(String keyword, String type) {
        mKeywordEt.setText(keyword);
        mKeywordEt.setSelection(mKeywordEt.length());
        hideInputMethod();
        save();
        initLoadData(type);
    }

    public void save() {
        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(Preferences.KEY_SEARCH_HISTORY_KEYWORD);
        if (!TextUtils.isEmpty(text) && !oldText.contains(text)) {
            mPref.putString(Preferences.KEY_SEARCH_HISTORY_KEYWORD, text + "," + oldText);
            mHistoryKeywords.add(0, text);
        }
        mHistoryTagAdapter.notifyDataChanged();
    }

    public void cleanHistory() {
        mPref.remove(Preferences.KEY_SEARCH_HISTORY_KEYWORD);
        mHistoryKeywords.clear();
        mHistoryTagAdapter.notifyDataChanged();
        mHistoryLabelTV.setVisibility(View.VISIBLE);
        mHistoryFlowLayout.setVisibility(View.GONE);
        mClearHistoryBtn.setVisibility(View.GONE);
        SingleToast.show(this, getString(com.hooview.app.R.string.clear_history_success), Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.hooview.app.R.id.clear_history_btn:
                cleanHistory();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String type = intent.getStringExtra(EXTRA_KEY_TYPE);
        String keyword = intent.getStringExtra(EXTRA_KEY_KEYWORD);

        initLoadData(type);

        if (!TextUtils.isEmpty(keyword)) {
            mKeywordEt.setText(keyword);
        }
    }

    private void initLoadData(String type) {
        if (TextUtils.isEmpty(mKeywordEt.toString())) {
            SingleToast.show(this, com.hooview.app.R.string.msg_keyword_is_empty);
            return;
        }
        mType = type;
        loadData(false);
    }

    @Override
    protected void loadData(final boolean isLoadMore) {
        super.loadData(isLoadMore);
        if (!isLoadMore) {
            mType = ApiConstant.VALUE_SEARCH_TYPE_ALL;
        }
        ApiHelper.getInstance().searchInfos(mType, mKeywordEt.getText().toString(), mNextPageIndex,
                ApiConstant.DEFAULT_PAGE_SIZE, new MyRequestCallBack<SearchInfoEntity>() {
                    @Override
                    public void onSuccess(SearchInfoEntity result) {
                        if (isLoadMore) {
                            setLoadMoreData(result);
                        } else {
                            assembleData(result);
                            mPullToLoadRcvView.getRecyclerView().smoothScrollToPosition(0);
                        }
                        if (ApiConstant.VALUE_SEARCH_TYPE_USER.equals(mType)) {
                            mNextPageIndex = result.getUser_next();
                        } else if (ApiConstant.VALUE_SEARCH_TYPE_LIVE.equals(mType)) {
                            mNextPageIndex = result.getLive_next();
                        } else if (ApiConstant.VALUE_SEARCH_TYPE_VIDEO.equals(mType)) {
                            mNextPageIndex = result.getVideo_next();
                        } else if (ApiConstant.VALUE_SEARCH_TYPE_ALL.equals(mType)) {
                            mNextPageIndex = result.getVideo_next();
                            if (isLoadMore) {
                                mType = ApiConstant.VALUE_SEARCH_TYPE_VIDEO;
                            }
                        }
                        onRefreshComplete(result.getUser_count() + result.getLive_count()
                                + result.getVideo_count());
                    }

                    @Override
                    public void onError(String errorInfo) {
                        super.onError(errorInfo);
                        onRefreshComplete(0);
                    }

                    @Override
                    public void onFailure(String msg) {
                        RequestUtil.handleRequestFailed(msg);
                        onRequestFailed(msg);
                    }
                });
    }

    private void setLoadMoreData(SearchInfoEntity result) {
        if (mType.equals(ApiConstant.VALUE_SEARCH_TYPE_USER)) {
            mSearchResults.addAll(result.getUsers());
        } else {
            mSearchResults.addAll(result.getVideos());
        }
    }

    private void assembleData(SearchInfoEntity result) {
        mSearchResults.clear();
        if (result.getUsers() != null && result.getUsers().size() > 3) {
            UserEntity userEntityHead = new UserEntity();
            userEntityHead.setPinned(UserEntity.IS_PINNED_LIST_HEADER);
            mSearchResults.add(userEntityHead);
            mSearchResults.addAll(result.getUsers());
            UserEntity userEntityFoot = new UserEntity();
            userEntityFoot.setPinned(UserEntity.IS_PINNED);
            mSearchResults.add(userEntityFoot);
        } else if (result.getUsers() != null && result.getUsers().size() > 0) {
            UserEntity userEntityHead = new UserEntity();
            userEntityHead.setPinned(UserEntity.IS_PINNED_LIST_HEADER);
            mSearchResults.add(userEntityHead);
            mSearchResults.addAll(result.getUsers());
        }

        if (result.getLives() != null && result.getLives().size() > 0) {
            VideoEntity videoEntityHead = new VideoEntity();
            videoEntityHead.setPinned(VideoEntity.IS_PINNED_HEADER);
            videoEntityHead.setLiving(VideoEntity.IS_LIVING);
            mSearchResults.add(videoEntityHead);
            mSearchResults.addAll(result.getLives());
        }
        if (result.getVideos() != null && result.getVideos().size() > 0) {
            VideoEntity videoEntityHead = new VideoEntity();
            videoEntityHead.setPinned(VideoEntity.IS_PINNED_HEADER);
            mSearchResults.add(videoEntityHead);
            mSearchResults.addAll(result.getVideos());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideInputMethod();
        return super.onTouchEvent(event);
    }

}
