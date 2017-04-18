/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.adapter.oldItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.imageslider.SliderLayout;
import com.easyvaas.common.imageslider.SliderTypes.BaseSliderView;
import com.easyvaas.common.imageslider.SliderTypes.TextSliderView;
import com.easyvaas.elapp.activity.WebViewActivity;
import com.easyvaas.elapp.bean.BannerModel;
import com.easyvaas.elapp.bean.CarouselInfoEntity;
import com.easyvaas.elapp.net.HooviewApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.net.RequestUtil;
import com.easyvaas.elapp.utils.Constants;
import com.growingio.android.sdk.collection.GrowingIO;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class HeaderSliderAdapterItem implements AdapterItem {
    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_KEY = "extra_key";

    private Context mContext;
    private SliderLayout mSliderLayout;

    private BaseSliderView.OnSliderClickListener mOnSliderClickListener
            = new BaseSliderView.OnSliderClickListener() {
        @Override
        public void onSliderClick(BaseSliderView baseSliderView) {
            Bundle bundle = baseSliderView.getBundle();
            int type = bundle.getInt(EXTRA_TYPE, -1);
            String keyValue = bundle.getString(EXTRA_KEY);
            Intent intent = null;
            if (type == CarouselInfoEntity.TYPE_WEB) {
                intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_KEY_TYPE, WebViewActivity.TYPE_ACTIVITY);
                intent.putExtra(WebViewActivity.EXTRA_KEY_URL, keyValue);
                intent.putExtra(Constants.EXTRA_KEY_TITLE, bundle.getString(Constants.EXTRA_KEY_TITLE));
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    };

    public HeaderSliderAdapterItem(Context context) {
        mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_header_discover_slider;
    }

    @Override
    public void onBindViews(View root) {
        mSliderLayout = (SliderLayout) root.findViewById(R.id.recommend_header_slider);
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setDuration(5000);
    }

    @Override
    public void onSetViews() {
//        String json = Preferences.getInstance(mContext)
//                .getString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON);
//        if (TextUtils.isEmpty(json)) {
//            loadCarouseInfo();
//        } else {
//            BannerModel result = new Gson().fromJson(json, BannerModel.class);
//            assembleSliderView(result.getData());
//            mSliderLayout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onUpdateViews(Object model, int position) {

    }

    private void loadCarouseInfo() {
        HooviewApiHelper.getInstance().getBannerInfo(new MyRequestCallBack<BannerModel>() {
            @Override
            public void onSuccess(BannerModel result) {
                if (result != null && result.getData() != null && result.getData().size() > 0) {
                    mSliderLayout.setVisibility(View.VISIBLE);
                    assembleSliderView(result.getData());
                } else {
                    mSliderLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
                mSliderLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String msg) {
                RequestUtil.handleRequestFailed(msg);
            }
        });
    }

    private void assembleSliderView(List<BannerModel.DataEntity> list) {
        mSliderLayout.removeAllSliders();
        List<String> bannerDescriptions = new ArrayList<>();
        for (int i = 0, n = list.size(); i < n; i++) {
            TextSliderView textSliderView = new TextSliderView(mContext);
            textSliderView
                    .description("")
                    .image(list.get(i).getImg())
                    .empty(R.drawable.account_bitmap_list)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(mOnSliderClickListener);
            textSliderView.bundle(new Bundle());
            int type = list.get(i).getType();
            textSliderView.getBundle().putInt(EXTRA_TYPE, type);
            textSliderView.getBundle().putString(Constants.EXTRA_KEY_TITLE,
                    list.get(i).getTitle());
            textSliderView.getBundle().putString(EXTRA_KEY,
                    list.get(i).getResource());
            mSliderLayout.addSlider(textSliderView);
            bannerDescriptions.add(list.get(i).getImg());
        }
        GrowingIO.getInstance().trackBanner(mSliderLayout, bannerDescriptions);
    }
}
