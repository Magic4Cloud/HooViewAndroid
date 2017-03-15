package com.easyvaas.elapp.ui.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooview.app.R;
import com.easyvaas.elapp.ui.base.BaseFragment;


public class BookPlayFragment extends BaseFragment {

    public static BookPlayFragment newInstance() {

        Bundle args = new Bundle();

        BookPlayFragment fragment = new BookPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_feature_no_open, container, false);
    }
}
