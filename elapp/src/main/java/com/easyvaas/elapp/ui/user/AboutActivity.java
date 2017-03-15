package com.easyvaas.elapp.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hooview.app.R;
import com.easyvaas.elapp.ui.base.BaseTitleActivity;
import com.easyvaas.elapp.utils.Utils;


public class AboutActivity extends BaseTitleActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_new);
        mTvTitle.setText("");
        findViewById(R.id.btn_encourage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoAppMarket(getApplicationContext());
            }
        });
    }
}
