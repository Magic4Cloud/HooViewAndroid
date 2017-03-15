package com.easyvaas.common.gift.workers;

import java.io.File;
import java.lang.ref.SoftReference;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.utils.GiftUtility;

import com.easyvaas.common.gift.R;

public class PlaneWorker extends Worker {
    private Activity context;
    private RelativeLayout mPlaneRl;
    private RelativeLayout mStarRl;
    private ImageView mPlaneIv;
    private ImageView mStar1;
    private ImageView mStar2;
    private ImageView mStar3;
    private ImageView mStar4;
    private ImageView mStar5;
    private ImageView mSmokeIv;
    private ImageView mSmokeIv2;
    private ImageView mColorBar;
    private ImageView mColorBar2;
    private LinearLayout mNicknameLl;
    private TextView tvSenderName, tvGiftName;
    private MyHandler mHandler;
    protected static class MyHandler extends Handler {
        private SoftReference<PlaneWorker> softReference;

        public MyHandler(PlaneWorker planeAnimation) {
            softReference = new SoftReference<PlaneWorker>(planeAnimation);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final PlaneWorker planeAnimation = softReference.get();
            if (planeAnimation == null) {
                return;
            }
            switch (msg.what) {
                case 111:
                    planeAnimation.mColorBar.setVisibility(View.VISIBLE);
                    //                    createColorBarAni2().start();
                    break;
                case 112:
                    planeAnimation.mColorBar.animate().alpha(0).setDuration(500).start();
                    planeAnimation.mStarRl.setVisibility(View.VISIBLE);
                    planeAnimation.mStar1.animate().alpha(1).setDuration(100).start();
                    planeAnimation.mStar2.animate().alpha(1).setDuration(100).start();
                    planeAnimation.mStar3.animate().alpha(1).setDuration(100).start();
                    planeAnimation.mStar4.animate().alpha(1).setDuration(100).start();
                    planeAnimation.mStar5.animate().alpha(1).setDuration(100).start();
                    break;
                case 113:
                    planeAnimation.mStar1.animate().alpha(0).setDuration(1000).start();
                    planeAnimation.mStar2.animate().alpha(0).setDuration(1000).start();
                    planeAnimation.mStar3.animate().alpha(0).setDuration(1000).start();
                    planeAnimation.mStar4.animate().alpha(0).setDuration(1000).start();
                    planeAnimation.mStar5.animate().alpha(0).setDuration(1000).start();
                    break;
                case 114:
                    planeAnimation.mSmokeIv.setVisibility(View.VISIBLE);
                    planeAnimation.mSmokeIv2.setVisibility(View.VISIBLE);
                    planeAnimation.mColorBar2.setVisibility(View.VISIBLE);
                    planeAnimation.mSmokeIv.animate().alpha(1).setDuration(1).start();
                    planeAnimation.mSmokeIv2.animate().alpha(1).setDuration(1).start();
                    planeAnimation.mColorBar2.animate().alpha(1).setDuration(1).start();
                    break;
                case 115:
                    //                    mSmokeIv.animate().alpha(0).setDuration(1000).start();
                    //                    mSmokeIv2.animate().alpha(0).setDuration(1000).start();
                    //                    mColorBar2.animate().alpha(0).setDuration(1000).start();
                    planeAnimation.mSmokeIv.setVisibility(View.GONE);
                    planeAnimation.mSmokeIv2.setVisibility(View.GONE);
                    planeAnimation.mColorBar2.setVisibility(View.GONE);
                    break;
            }

        }
    }

    public PlaneWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.PLANE;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_plane;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        mPlaneRl = (RelativeLayout) container.findViewById(R.id.plane_rl);
        mStarRl = (RelativeLayout) container.findViewById(R.id.star_rl);
        mPlaneIv = (ImageView) container.findViewById(R.id.plane_iv);
        mStar1 = (ImageView) container.findViewById(R.id.star_one_iv);
        mStar2 = (ImageView) container.findViewById(R.id.star_two_iv);
        mStar3 = (ImageView) container.findViewById(R.id.star_three_iv);
        mStar4 = (ImageView) container.findViewById(R.id.star_four_iv);
        mStar5 = (ImageView) container.findViewById(R.id.star_five_iv);
        mSmokeIv = (ImageView) container.findViewById(R.id.smoke_one_iv);
        mSmokeIv2 = (ImageView) container.findViewById(R.id.smoke_two_iv);
        mColorBar = (ImageView) container.findViewById(R.id.color_bar_iv);
        mColorBar2 = (ImageView) container.findViewById(R.id.color_bar_iv2);
        mNicknameLl = (LinearLayout) container.findViewById(R.id.view_gift_name_ll);
        tvSenderName = (TextView) container.findViewById(R.id.sender_name_tv);
        tvGiftName = (TextView) container.findViewById(R.id.gift_name_tv);
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        mHandler = new MyHandler(this);
        attachToHost(container);
        ZipAnimAction zipAnimAction = (ZipAnimAction) action;
        Resource resource = getResource(zipAnimAction.getAnimResDir());
        setViews(resource, (ZipAnimAction) action);

        AnimatorSet set = new AnimatorSet();
        set.play(createPlaneAnim1()).before(createPlaneAnim2());
        set.play(createNicknameAnim());
        set.play(createColorBarAni2()).after(1000);
        set.play(createStarAnim1()).after(2500);
        set.play(createStarAnim2()).after(2500);
        set.play(createStarAnim3()).after(2500);
        set.play(createStarAnim4()).after(2500);
        set.play(createStarAnim5()).after(2500);
        mHandler.sendEmptyMessageDelayed(111,1500);
        mHandler.sendEmptyMessageDelayed(112,2500);
        mHandler.sendEmptyMessageDelayed(113,4500);
        mHandler.sendEmptyMessageDelayed(114,6000);
        mHandler.sendEmptyMessageDelayed(115,7000);
        set.play(createPlaneAnim3()).after(6000);
        set.play(createSmokeAnim1()).after(5750);
        set.play(createSmokeAnim2()).after(5750);
        set.play(createColorBarAni()).after(5750);
        return set;
    }
    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        host.removeView(container);
    }
    private Animator createNicknameAnim() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", -GiftUtility.dp2Px(context, 200), GiftUtility
                .getScreenWidth(context)+ GiftUtility.dp2Px(context, 200));

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mNicknameLl, xValue);
        anim.setDuration(6000);
        return anim;
    }
    private Animator createPlaneAnim1() {
        float sx = GiftUtility.dp2Px(context, 200);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", -sx, -dx / 2);

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 50);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 55);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.4f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.4f);

        PropertyValuesHolder rotationXValue = PropertyValuesHolder.ofFloat("rotationX", -10.0f);
        PropertyValuesHolder rotationYValue = PropertyValuesHolder.ofFloat("rotationY", 10.0f);
        //        PropertyValuesHolder rotationYValue = PropertyValuesHolder.ofFloat("rotationY", 1.0f, 1.4f);
        //        mPlaneIv.animate().rotationY(100).setDuration(1000).start();

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mPlaneRl, xValue, yValue, scaleXValue, scaleYValue, rotationXValue,
                        rotationYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createPlaneAnim2() {
        float sx =
                GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 100);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder
                .ofFloat("x", -dx / 2, -dx / 2 + GiftUtility.dp2Px(context, 32));

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 55);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 60);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.4f, 1.6f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.4f, 1.6f);

        PropertyValuesHolder rotationXValue = PropertyValuesHolder.ofFloat("rotationX", -4.0f);
        PropertyValuesHolder rotationYValue = PropertyValuesHolder.ofFloat("rotationY", 4.0f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mPlaneRl, xValue, yValue, scaleXValue, scaleYValue, rotationXValue,
                        rotationYValue);
        anim.setDuration(4000);
//        anim.addListener(new CarAnim2PrepareListener());
        return anim;
    }

    private Animator createPlaneAnim3() {
        float sx =
                GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 100);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder
                .ofFloat("x", -dx / 2 + GiftUtility.dp2Px(context, 32),
                        -dx + GiftUtility.dp2Px(context, 100));

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 60);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 65);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.6f, 2.0f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.6f, 2.0f);

        PropertyValuesHolder rotationXValue = PropertyValuesHolder.ofFloat("rotationX", 0.0f, -39.0f);
        //        PropertyValuesHolder rotationYValue = PropertyValuesHolder.ofFloat("rotationY", 29.0f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mPlaneRl, xValue, yValue, scaleXValue, scaleYValue,
                        rotationXValue);
        anim.setDuration(1000);
//        anim.addListener(new CarAnim3PrepareListener());
        return anim;
    }

    private Animator createColorBarAni() {
        float sx =
                GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 200);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", sx / 4, sx / 2);

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 50);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 60);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 2.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);
        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 0, 1);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mColorBar2, xValue, scaleXValue, scaleYValue, alphaValue);
        anim.setDuration(1000);
        return anim;
    }

    private Animator createColorBarAni2() {
        float sx =
                GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 100);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", -sx, -dx / 2);

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 50);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility
                .dp2Px(context, 60);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);
        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 0, 1);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mColorBar, alphaValue);
        anim.setDuration(1000);
        return anim;
    }

    private Animator createStarAnim1() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", 0, 5);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, 5);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mStar1, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createStarAnim2() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", 0, 10);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, 10);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 2.0f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 2.0f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mStar2, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createStarAnim3() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", 0, 15);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, 15);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mStar3, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createStarAnim4() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", 0, 20);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, 20);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 2.0f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 2.0f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mStar4, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createStarAnim5() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", 0, 30);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, 30);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);

        ObjectAnimator anim = ObjectAnimator
                .ofPropertyValuesHolder(mStar5, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        return anim;
    }

    private Animator createSmokeAnim1() {
        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 0f, 1.5f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mSmokeIv, scaleXValue, scaleYValue);
        anim.setDuration(1000);
        return anim;
    }

    private Animator createSmokeAnim2() {
        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 0f, 1.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 0f, 1.5f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mSmokeIv2, scaleXValue, scaleYValue);
        anim.setDuration(1000);
        return anim;
    }
    private void setViews(Resource resource, ZipAnimAction action) {
        mPlaneIv.setImageBitmap(resource.airplane);
        mStar1.setImageBitmap(resource.onestar);
        mStar2.setImageBitmap(resource.twostar);
        mStar3.setImageBitmap(resource.threestar);
        mStar4.setImageBitmap(resource.twostar);
        mStar5.setImageBitmap(resource.threestar);
        mSmokeIv.setImageBitmap(resource.smoke);
        mSmokeIv2.setImageBitmap(resource.smoketwo);
        mColorBar.setImageBitmap(resource.ribbon);
        mColorBar2.setImageBitmap(resource.ribbon);
        tvSenderName.setText(action.getSenderName());
        tvGiftName.setText(action.getGiftName());
        //        car1GiftName.setText(action.getGiftName());
        //        car1SenderName.setText(action.getSenderName());
        //        car2GiftName.setText(action.getGiftName());
        //        car2SenderName.setText(action.getSenderName());
    }

    private Resource getResource(File animationDir) {
        File airplane = new File(animationDir.getAbsoluteFile() + File.separator + "airplane.png");
        File onestar = new File(animationDir.getAbsoluteFile() + File.separator + "onestar.png");
        File twostar = new File(animationDir.getAbsoluteFile() + File.separator + "twostar.png");
        File threestar = new File(animationDir.getAbsoluteFile() + File.separator + "threestar.png");
        File smoke = new File(animationDir.getAbsoluteFile() + File.separator + "smoke.png");
        File smoketwo = new File(animationDir.getAbsoluteFile() + File.separator + "smoketwo.png");
        File ribbon = new File(animationDir.getAbsoluteFile() + File.separator + "ribbon.png");

        Resource resource = new Resource();
        resource.airplane = BitmapFactory.decodeFile(airplane.getAbsolutePath());
        resource.onestar = BitmapFactory.decodeFile(onestar.getAbsolutePath());
        resource.twostar = BitmapFactory.decodeFile(twostar.getAbsolutePath());
        resource.threestar = BitmapFactory.decodeFile(threestar.getAbsolutePath());
        resource.smoke = BitmapFactory.decodeFile(smoke.getAbsolutePath());
        resource.smoketwo = BitmapFactory.decodeFile(smoketwo.getAbsolutePath());
        resource.ribbon = BitmapFactory.decodeFile(ribbon.getAbsolutePath());
        return resource;
    }

    private class Resource {
        Bitmap airplane;
        Bitmap onestar;
        Bitmap twostar;
        Bitmap threestar;
        Bitmap smoke;
        Bitmap smoketwo;
        Bitmap ribbon;
    }
}

