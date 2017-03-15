package com.easyvaas.common.gift.workers;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.utils.GiftUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import com.easyvaas.common.gift.R;

public class RacingCarWorker extends Worker {
    private Activity context;
    private ImageView ivCar1, ivCar2, ivLight, ivWay;
    private LinearLayout llCar;
    private TextView tvSenderName, tvGiftName;

    private Subscription tyreRepeat;

    public RacingCarWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.CAR_RACING;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_racing_car;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        llCar = (LinearLayout) container.findViewById(R.id.fl_car);
        ivCar1 = (ImageView) container.findViewById(R.id.iv_car1);
        ivCar2 = (ImageView) container.findViewById(R.id.iv_car2);
        ivLight = (ImageView) container.findViewById(R.id.iv_car_lighter);
        ivWay = (ImageView) container.findViewById(R.id.iv_bg);
        tvSenderName = (TextView) container.findViewById(R.id.sender_name_tv);
        tvGiftName = (TextView) container.findViewById(R.id.gift_name_tv);
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        setViews((ZipAnimAction) action);

        Animator car1Anim = createCarAnim1();
        Animator car2Anim = createCarAnim2();
        Animator wayAnim = createWayAnim();

        startTyreAnim();

        AnimatorSet set = new AnimatorSet();
        set.play(car1Anim).with(wayAnim);
        set.play(wayAnim).after(2000).before(car2Anim);
        set.play(car2Anim).after(2000);
        return set;
    }

    private Animator createCarAnim1() {
        float sx = GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 80);
        float dx = GiftUtility.getScreenWidth(context) - GiftUtility.dp2Px(context, 300);
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", sx, dx);

        float dy = GiftUtility.getScreenHeight(context) / 2 - GiftUtility.dp2Px(context, 50);
        PropertyValuesHolder yValue = PropertyValuesHolder.ofFloat("y", 0, dy);

        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(llCar, xValue, yValue, scaleXValue, scaleYValue);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(3000);
        return anim;
    }

    private Animator createCarAnim2() {
        PropertyValuesHolder xValue = PropertyValuesHolder.ofFloat("x", -GiftUtility.dp2Px(context, 305));
        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 5f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 5f);
        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(llCar, xValue, scaleXValue, scaleYValue, alphaValue);
        anim.setDuration(1000);
        anim.addListener(new CarAnim2Listener());
        return anim;
    }

    private Animator createWayAnim() {
        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        final ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(ivWay, alphaValue);
        anim.setDuration(3000);
        anim.addListener(new WayAnimListener());
        return anim;
    }

    private void startTyreAnim() {
        tyreRepeat = Observable.interval(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        ivCar1.setVisibility(ivCar1.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                        ivCar2.setVisibility(ivCar2.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }

    private class CarAnim2Listener implements Animator.AnimatorListener {
        Subscription repeat;

        @Override
        public void onAnimationStart(Animator animation) {
            repeat = Observable.interval(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            ivLight.setVisibility(ivLight.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                        }
                    });
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (tyreRepeat != null && !repeat.isUnsubscribed()) {
                repeat.unsubscribe();
            }
            if (tyreRepeat != null && !tyreRepeat.isUnsubscribed()) {
                tyreRepeat.unsubscribe();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private class WayAnimListener implements Animator.AnimatorListener {
        float carY;

        public WayAnimListener() {
            this.carY = GiftUtility.getScreenHeight(context) / 2 - GiftUtility.dp2Px(context, 20);
        }

        @Override
        public void onAnimationStart(Animator animator) {
            ivWay.setVisibility(View.VISIBLE);
            ivWay.setY(carY);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ivWay.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private void setViews(ZipAnimAction action) {
        Resource res = getResource(action.getAnimResDir());
        ivCar1.setImageBitmap(res.car1);
        ivCar2.setImageBitmap(res.car2);
        llCar.setAlpha(1);
        ivLight.setImageBitmap(res.light);
        ivWay.setImageBitmap(res.way);
        tvSenderName.setText(action.getSenderName());
        tvGiftName.setText(action.getGiftName());
    }

    private Resource getResource(File animationDir) {
        File car1File = new File(animationDir.getAbsoluteFile() + File.separator + "yellowCarImage0.png");
        File car2File = new File(animationDir.getAbsoluteFile() + File.separator + "yellowCarImage1.png");
        File carLightFile = new File(animationDir.getAbsoluteFile() + File.separator + "yellowCarLightImage.png");
        File wayFile = new File(animationDir.getAbsoluteFile() + File.separator + "yellowCarRibbonImage.png");

        Resource resource = new Resource();
        resource.car1 = BitmapFactory.decodeFile(car1File.getAbsolutePath());
        resource.car2 = BitmapFactory.decodeFile(car2File.getAbsolutePath());
        resource.light = BitmapFactory.decodeFile(carLightFile.getAbsolutePath());
        resource.way = BitmapFactory.decodeFile(wayFile.getAbsolutePath());

        return resource;
    }

    private class Resource {
        Bitmap car1;
        Bitmap car2;
        Bitmap light;
        Bitmap way;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        host.removeView(container);
    }
}
