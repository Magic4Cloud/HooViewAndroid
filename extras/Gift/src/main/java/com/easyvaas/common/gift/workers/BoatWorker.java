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

/**
 * 豪华游艇动画 Created by LiFZhe on 4/11/16.
 */
public class BoatWorker extends Worker {
    private Activity context;
    private ImageView ivBoat, ivFlag, ivWater;
    private LinearLayout llBoat;
    private TextView tvSenderName, tvGiftName;

    private Subscription flagSubscription;

    public BoatWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.BOAT;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_boat;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        ivBoat = (ImageView) container.findViewById(R.id.iv_boat);
        ivFlag = (ImageView) container.findViewById(R.id.iv_flag);
        ivWater = (ImageView) container.findViewById(R.id.iv_water);
        llBoat = (LinearLayout) container.findViewById(R.id.ll_boat);
        tvSenderName = (TextView) container.findViewById(R.id.sender_name_tv);
        tvGiftName = (TextView) container.findViewById(R.id.gift_name_tv);
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        setViews((ZipAnimAction) action);

        startFlagAnim(ivFlag);

        Animator boatAnim = createBoatAnim();
        Animator waterAnim = createWaterAnim();

        AnimatorSet set = new AnimatorSet();
        set.play(boatAnim).with(waterAnim);
        return set;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        super.onAnimationEnd(host, container, animator);
        flagSubscription.unsubscribe();
        host.removeView(container);
    }

    private Animator createWaterAnim() {
        final float sy = GiftUtility.getScreenHeight(context);
        float dy = GiftUtility.getScreenHeight(context) - GiftUtility.dp2Px(context, 271);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(ivWater, "y", sy, dy);
        anim1.setDuration(500);

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(ivWater, "x", -GiftUtility.dp2Px(context, 50));
        anim2.setDuration(6000);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(ivWater, "x", GiftUtility.dp2Px(context, 50));
        anim3.setDuration(2000);

        AnimatorSet set = new AnimatorSet();
        set.play(anim1).before(anim2);
        set.play(anim2).before(anim3);
        set.play(anim3);
        set.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ivWater.setY(sy);
            }
        });
        return set;
    }

    private Animator createBoatAnim() {
        final float boatSX1 = GiftUtility.getScreenWidth(context) + GiftUtility.dp2Px(context, 200);
        float boatDX1 = GiftUtility.getScreenWidth(context) / 2 - GiftUtility.dp2Px(context, 70);
        ObjectAnimator boatAni1 = ObjectAnimator.ofFloat(llBoat, "x", boatSX1, boatDX1);
        boatAni1.setDuration(4000);

        float boatDX2 = - GiftUtility.dp2Px(context, 200);
        PropertyValuesHolder moveXValue2 = PropertyValuesHolder.ofFloat("x", boatDX2);
        PropertyValuesHolder alphaValue2 = PropertyValuesHolder.ofFloat("alpha", 1, 0);
        ObjectAnimator boatAni2 = ObjectAnimator.ofPropertyValuesHolder(llBoat, moveXValue2, alphaValue2);
        boatAni2.setDuration(4000);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                llBoat.setAlpha(1);
            }
        });
        set.play(boatAni1).before(boatAni2);
        set.play(boatAni2).after(4000);
        return set;
    }

    private void startFlagAnim(View flag) {
        flagSubscription = Observable.interval(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlagAnimator(flag));
    }

    private class FlagAnimator implements Action1<Long> {
        View flag;
        boolean isBy = false;

        public FlagAnimator(View flag) {
            this.flag = flag;
        }

        @Override
        public void call(Long aLong) {
            if (isBy) {
                flag.animate().rotation(3).setDuration(100).start();
            } else {
                flag.animate().rotationBy(3).setDuration(100).start();
            }
            isBy = !isBy;
        }
    }

    private void setViews(ZipAnimAction action) {
        Resource resource = getResource(action.getAnimResDir());
        ivBoat.setImageBitmap(resource.boat);
        ivFlag.setImageBitmap(resource.flag);
        ivWater.setImageBitmap(resource.water);
        ivWater.setY(GiftUtility.getScreenHeight(context));
        tvSenderName.setText(action.getSenderName());
        tvGiftName.setText(action.getGiftName());
    }

    private Resource getResource(File animationDir) {
        File boat = new File(animationDir.getAbsoluteFile() + File.separator + "s_ Ship.png");
        File flag = new File(animationDir.getAbsoluteFile() + File.separator + "s_Flag.png");
        File water = new File(animationDir.getAbsoluteFile() + File.separator + "s_Water.png");

        Resource resource = new Resource();
        resource.boat = BitmapFactory.decodeFile(boat.getAbsolutePath());
        resource.flag = BitmapFactory.decodeFile(flag.getAbsolutePath());
        resource.water = BitmapFactory.decodeFile(water.getAbsolutePath());
        return resource;
    }

    private class Resource {
        Bitmap boat;
        Bitmap flag;
        Bitmap water;
    }
}
