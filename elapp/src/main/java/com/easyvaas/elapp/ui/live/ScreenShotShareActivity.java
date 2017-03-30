package com.easyvaas.elapp.ui.live;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.easyvaas.common.sharelogin.data.ShareConstants;
import com.easyvaas.common.sharelogin.model.ShareContent;
import com.easyvaas.elapp.ui.base.BaseActivity;
import com.easyvaas.elapp.utils.ShareHelper;
import com.hooview.app.R;


public class ScreenShotShareActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_TITLE = "title";
    private ImageView mIvShareImage;
    private String imageUrl;
    private ShareHelper mShareHelper;
    private ShareContent shareContentPic;

    public static void start(Context context, String imageUrl) {
        Intent starter = new Intent(context, ScreenShotShareActivity.class);
        starter.putExtra(EXTRA_IMAGE_URL, imageUrl);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        if (TextUtils.isEmpty(imageUrl)) {
            finish();
            return;
        }
        setContentView(R.layout.activity_screen_shot_share);
        mIvShareImage = (ImageView) findViewById(R.id.iv_share_image);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_share).setOnClickListener(this);
        mShareHelper = ShareHelper.getInstance(this, true);
        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
        mIvShareImage.setImageBitmap(bitmap);
        shareContentPic = initShareContentModel();
    }

    @NonNull
    private ShareContent initShareContentModel() {
        return new ShareContent() {
            @Override
            public int getShareWay() {
                return ShareConstants.SHARE_WAY_PIC;
            }

            @Override
            public String getContent() {
                return null;
            }

            @Override
            public String getTitle() {
                return getString(R.string.app_name);
            }

            @Override
            public String getURL() {
                return getString(R.string.app_url);
            }

            @Override
            public String getImageUrl() {
                return imageUrl;
            }

            @Override
            public String getMusicUrl() {
                return null;
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_share:
                mShareHelper.showShareBottomPanel(shareContentPic);
                break;
        }
    }
}






