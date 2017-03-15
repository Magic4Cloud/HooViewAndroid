package com.easyvaas.elapp.ui.market;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import com.easyvaas.elapp.db.RealmHelper;
import com.hooview.app.R;
import com.easyvaas.elapp.bean.market.ExponentModel;
import com.easyvaas.elapp.ui.base.BaseActivity;

public class GlobalContentEditActivity extends BaseActivity implements View.OnClickListener,GlobalContentListFragment.CallBack {
    private List<ExponentModel> selectModel = new ArrayList<ExponentModel>();
    private String type = "";
    public static void start(Context context,String type) {
        Intent starter = new Intent(context, GlobalContentEditActivity.class);
        starter.putExtra("type",type);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_exponent_edit);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        findViewById(R.id.tv_complete).setOnClickListener(this);
        List<ExponentModel> globalRecordList = RealmHelper.getInstance().getGlobalRecordList();
        selectModel.addAll(globalRecordList);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment,new GlobalContentListFragment()).commit();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        String[] titles = getResources().getStringArray(R.array.global_content_manager_tabs);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_complete:
                RealmHelper.getInstance().deleteGlobalAllRecord();
                insertDb();
                EventBus.getDefault().post(selectModel);
                finish();
                break;
        }
    }

    private void insertDb() {
        for (ExponentModel exponentModel : selectModel) {
            exponentModel.setId(exponentModel.getSymbol());
            if (!TextUtils.isEmpty(exponentModel.getId())
                    && !RealmHelper.getInstance()
                    .queryGlobalRecordId(exponentModel.getId())) {
                RealmHelper.getInstance().insertGlobalRecord(exponentModel);
            }
        }
    }

    @Override
    public void getSelectList(ExponentModel list) {
        list.setAddOrDelete(false);
        selectModel.add(list);
    }

    @Override
    public void removeSelectList(ExponentModel list) {
        list.setAddOrDelete(true);
        for(int i = 0; i < selectModel.size();i++){
            if(selectModel.get(i).getSymbol().equals(list.getSymbol())){
                selectModel.remove(i);
            }
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {
        String[] tabsTitles;

        public MyAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.tabsTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return new GlobalContentListFragment();
        }

        @Override
        public int getCount() {
            return tabsTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabsTitles[position];
        }
    }


}
