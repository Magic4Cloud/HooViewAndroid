package com.hooview.app.activity.account;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hooview.app.R;
import com.hooview.app.activity.home.fragment.TabMyFragment;
import com.hooview.app.base.BaseActivity;

public class AccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TabMyFragment fragment =new TabMyFragment();

        fragmentTransaction.add(R.id.account_fragment, fragment);
        fragmentTransaction.commit();
    }
}
