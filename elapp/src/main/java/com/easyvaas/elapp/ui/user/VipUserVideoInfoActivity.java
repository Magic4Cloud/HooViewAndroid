package com.easyvaas.elapp.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.user.UserInfoArrayModel;
import com.easyvaas.elapp.bean.user.UserInfoModel;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

public class VipUserVideoInfoActivity extends BaseActivity {
    private UserInfoModel mUserInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_user_video_info);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(R.string.user_my_live_list);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
        getUserInfos(EVApplication.getUser().getName());

    }

    private void getUserInfos(String name) {
        List<String> names = new ArrayList<>();
        names.add(name);
        ApiHelper.getInstance().getUserInfosNew(names, new MyRequestCallBack<UserInfoArrayModel>() {
            @Override
            public void onSuccess(UserInfoArrayModel result) {
                if (result != null && result.getUsers() != null && result.getUsers().size() > 0) {
                    mUserInfoModel = result.getUsers().get(0);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment,VipLiveListFragment.newInstance(mUserInfoModel)).commit();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
