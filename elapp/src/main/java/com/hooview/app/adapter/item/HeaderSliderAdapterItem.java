/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.adapter.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.easyvaas.common.adapter.AdapterItem;
import com.easyvaas.common.imageslider.SliderLayout;
import com.easyvaas.common.imageslider.SliderTypes.BaseSliderView;
import com.easyvaas.common.imageslider.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.hooview.app.activity.WebViewActivity;
import com.hooview.app.bean.CarouselInfoEntity;
import com.hooview.app.bean.CarouselInfoEntityArray;
import com.hooview.app.db.Preferences;
import com.hooview.app.net.ApiHelper;
import com.hooview.app.net.MyRequestCallBack;
import com.hooview.app.net.RequestUtil;
import com.hooview.app.utils.Constants;

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
        return com.hooview.app.R.layout.view_header_discover_slider;
    }

    @Override
    public void onBindViews(View root) {
        mSliderLayout = (SliderLayout) root.findViewById(com.hooview.app.R.id.recommend_header_slider);
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setDuration(5000);
    }

    @Override
    public void onSetViews() {
        String json = Preferences.getInstance(mContext)
                .getString(Preferences.KEY_CACHED_CAROUSEL_INFO_JSON);
        if (TextUtils.isEmpty(json)) {
            loadCarouseInfo();
        } else {
            CarouselInfoEntityArray result = new Gson().fromJson(json, CarouselInfoEntityArray.class);
            assembleSliderView(result.getObjects());
            mSliderLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateViews(Object model, int position) {

    }

    //banner的数据
    private void loadCarouseInfo() {
        ApiHelper.getInstance().getCarouseInfo(new MyRequestCallBack<CarouselInfoEntityArray>() {
            @Override
            public void onSuccess(CarouselInfoEntityArray result) {
                if (result != null && result.getCount() > 0) {
                    mSliderLayout.setVisibility(View.VISIBLE);
                    assembleSliderView(result.getObjects());
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

    private void assembleSliderView(List<CarouselInfoEntity> list) {
        mSliderLayout.removeAllSliders();
        for (int i = 0, n = list.size(); i < n; i++) {
            TextSliderView textSliderView = new TextSliderView(mContext);
            textSliderView
                    .description("")
                    .image(list.get(i).getThumb())
                    .empty(com.hooview.app.R.drawable.banner_default)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(mOnSliderClickListener);
            textSliderView.bundle(new Bundle());
            int type = list.get(i).getContent().getType();
            textSliderView.getBundle().putInt(EXTRA_TYPE, type);
            textSliderView.getBundle().putString(Constants.EXTRA_KEY_TITLE,
                    list.get(i).getContent().getData().getTitle());
            if (type == CarouselInfoEntity.TYPE_ACTIVITY) {
                textSliderView.getBundle().putString(EXTRA_KEY,
                        list.get(i).getContent().getData().getActivity_id());
            } else if (type == CarouselInfoEntity.TYPE_NOTICE) {
                textSliderView.getBundle().putString(EXTRA_KEY,
                        list.get(i).getContent().getData().getNotice_id());
            } else if (type == CarouselInfoEntity.TYPE_WEB) {
                textSliderView.getBundle().putString(EXTRA_KEY,
                        list.get(i).getContent().getData().getWeb_url());
            }
            mSliderLayout.addSlider(textSliderView);
        }
    }
}
