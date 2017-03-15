package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.bean.imageTextLive.CheckImageTextLiveModel;
import com.easyvaas.elapp.bean.video.TextLiveListModel;
import com.hooview.app.R;


public class MyImageTextLiveRoomActivity extends BaseImageTextLiveActivity implements View.OnClickListener {
    private static final String TAG = "MyImageTextLiveRoomActi";
    public static final String EXTRA_CHECK_IMAGE_TEXT_LIVE_MODEL = "extra_check_image_text_live_model";
    private CheckImageTextLiveModel mCheckImageTextLiveModel;
    private LinearLayout mLlRoot;

    public static void start(Context context, CheckImageTextLiveModel model) {
        Intent starter = new Intent(context, MyImageTextLiveRoomActivity.class);
        starter.putExtra(EXTRA_CHECK_IMAGE_TEXT_LIVE_MODEL, model);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckImageTextLiveModel = (CheckImageTextLiveModel) getIntent().getSerializableExtra(EXTRA_CHECK_IMAGE_TEXT_LIVE_MODEL);
        if (mCheckImageTextLiveModel == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_my_image_live_room);
        mLlRoot = (LinearLayout) findViewById(R.id.root);
        setupView();
    }

    private void setupView() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.my_image_text_room);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMyAdapter.getItem(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String setRoomId() {
        return mCheckImageTextLiveModel.getData().getId();
    }

    @Override
    public boolean isAnchor() {
        return true;
    }

    @Override
    public int watchCount() {
        return mCheckImageTextLiveModel.getData().getViewcount();
    }

    @Override
    public TextLiveListModel.StreamsEntity streamEntity() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_share:
                share();
                break;
        }
    }

}
