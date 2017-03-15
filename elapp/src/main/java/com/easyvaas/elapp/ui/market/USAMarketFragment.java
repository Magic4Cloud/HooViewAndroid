package com.easyvaas.elapp.ui.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.easyvaas.elapp.ui.base.BaseFragment;

/**
 * Created by guojun on 2016/12/29 10:11.
 */

public class USAMarketFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO 2.1
        View view = View.inflate(getContext(), R.layout.empty_feature_no_open, null);
        return view;
    }
}
