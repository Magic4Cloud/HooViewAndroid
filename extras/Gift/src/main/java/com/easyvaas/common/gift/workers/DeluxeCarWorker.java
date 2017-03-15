package com.easyvaas.common.gift.workers;

import java.io.File;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.utils.GiftUtility;

import com.easyvaas.common.gift.R;

/**
 * 敞篷车动画
 *
 * Created by LiFZhe on 4/11/16.
 */
public class DeluxeCarWorker extends Worker {
    private Activity context;
    private ImageView ivCar1, ivCar2, ivTyreLeft, ivTyreRight;
    private RelativeLayout rlCar2;
    private LinearLayout llCar1, llCar2;
    private TextView car1GiftName, car2GiftName, car1SenderName, car2SenderName;

    public DeluxeCarWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.CAR_DELUXE;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_deluxe_car;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        ivCar1 = (ImageView) container.findViewById(R.id.iv_car1);
        rlCar2 = (RelativeLayout) container.findViewById(R.id.rl_car2);
        ivCar2 = (ImageView) container.findViewById(R.id.iv_car2);
        ivTyreLeft = (ImageView) container.findViewById(R.id.iv_tyre_left);
        ivTyreRight = (ImageView) container.findViewById(R.id.iv_tyre_right);
        llCar1 = (LinearLayout) container.findViewById(R.id.ll_car1);
        llCar2 = (LinearLayout) container.findViewById(R.id.ll_car2);
        car1GiftName = (TextView) llCar1.findViewById(R.id.gift_name_tv);
        car2GiftName = (TextView) llCar2.findViewById(R.id.gift_name_tv);
        car1SenderName = (TextView) container.findViewById(R.id.ll_car1).findViewById(R.id.sender_name_tv);
        car2SenderName = (TextView) container.findViewById(R.id.ll_car2).findViewById(R.id.sender_name_tv);
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        ZipAnimAction zipAnimAction = (ZipAnimAction) action;
        Resource resource = getResource(zipAnimAction.getAnimResDir());
        setViews(resource, (ZipAnimAction) action);

        Animator carAnim1 = createCarAnim1();
        Animator carAnim2 = getCarAnim2(resource);

        AnimatorSet set = new AnimatorSet();
        set.play(carAnim1).before(carAnim2);
        return set;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        host.removeView(container);
    }

    private Animator createCarAnim1() {
        float sx = GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 100);
        float dx = -GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", sx, dx);

        float sy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility.dp2Px(context, 50);
        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility.dp2Px(context, 300);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", sy, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(llCar1, xValue, yValue, scaleXValue, scaleYValue);
        anim.setDuration(2000);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.addListener(new CarAnim1PrepareListener());
        return anim;
    }

    private Animator getCarAnim2(Resource resource) {
        float midDX = GiftUtility.getScreenWidth(context) / 2 - GiftUtility.dp2Px(context, 125);

        Animator anim1 = createCarAnim21(midDX);
        Animator anim2 = createCarAnim22(resource);
        Animator anim3 = createCarAnim23(midDX);

        AnimatorSet set = new AnimatorSet();
        set.play(anim1).before(anim2);
        set.play(anim2).before(anim3);
        return set;
    }

    private Animator createCarAnim21(float dx) {
        float sx = -GiftUtility.dp2Px(context, 100);
        PropertyValuesHolder car2XValue = PropertyValuesHolder.ofFloat("x", sx, dx);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(llCar2, car2XValue);
        anim.setDuration(1500);
        anim.setInterpolator(new DecelerateInterpolator());
        return anim;
    }

    private Animator createCarAnim22(final Resource resource) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(1500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (((Float) valueAnimator.getAnimatedValue()) > 0.2f) {
                    ivCar2.setImageBitmap(resource.car2_2);
                }
                if (((Float) valueAnimator.getAnimatedValue()) > 0.8) {
                    ivCar2.setImageBitmap(resource.car2_1);
                }
            }
        });
        return anim;
    }

    private Animator createCarAnim23(float x) {
        float dx = GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", x, dx);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(llCar2, xValue);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateInterpolator());
        return anim;
    }

    class CarAnim1PrepareListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ivTyreLeft.animate().rotationBy(10000).setDuration(7000).start();
            ivTyreRight.animate().rotationBy(10000).setDuration(7000).start();
            llCar2.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private void setViews(Resource resource, ZipAnimAction action) {
        ivCar1.setImageBitmap(resource.car1);
        ivCar2.setImageBitmap(resource.car2_1);
        ivTyreLeft.setImageBitmap(resource.tyre);
        ivTyreRight.setImageBitmap(resource.tyre);
        car1GiftName.setText(action.getGiftName());
        car1SenderName.setText(action.getSenderName());
        car2GiftName.setText(action.getGiftName());
        car2SenderName.setText(action.getSenderName());
    }

    private Resource getResource(File animationDir) {
        File car1File = new File(animationDir.getAbsoluteFile() + File.separator + "newCarImage.png");
        File car2File = new File(animationDir.getAbsoluteFile() + File.separator + "newLeftImage.png");
        File car2DoorOpen = new File(animationDir.getAbsoluteFile() + File.separator + "newCenterImage.png");
        File tyre = new File(animationDir.getAbsoluteFile() + File.separator + "wheel.png");

        Resource resource = new Resource();
        resource.car1 = BitmapFactory.decodeFile(car1File.getAbsolutePath());
        resource.car2_1 = BitmapFactory.decodeFile(car2File.getAbsolutePath());
        resource.car2_2 = BitmapFactory.decodeFile(car2DoorOpen.getAbsolutePath());
        resource.tyre = BitmapFactory.decodeFile(tyre.getAbsolutePath());
        return resource;
    }

    private class Resource {
        Bitmap car1;
        Bitmap car2_1;
        Bitmap car2_2;
        Bitmap tyre;
    }
}
