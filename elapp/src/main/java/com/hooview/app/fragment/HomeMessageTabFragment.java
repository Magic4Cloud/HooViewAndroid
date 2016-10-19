package com.hooview.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooview.app.base.BaseFragment;

/**
 * Author:   yyl
 * Description:
 * CreateDate:     2016/10/19
 */
public class HomeMessageTabFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("HomeMesssage");
        return tv;
    }
}
