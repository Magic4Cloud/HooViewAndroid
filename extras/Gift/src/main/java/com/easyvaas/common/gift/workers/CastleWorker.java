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
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.utils.GiftUtility;

import com.easyvaas.common.gift.R;

/**
 * 城堡动画
 * Created by LiFZhe on 4/13/16.
 */
public class CastleWorker extends Worker {
    private Activity context;
    private ImageView ivCastle, ivLight1, ivLight2, ivLight3, ivLight4, ivWay1, ivWay2, ivWay3, ivWay4, ivFire1, ivFire2, ivFire3, ivFire4;
    private LinearLayout llCastle;
    private TextView tvSenderName, tvGiftName;

    public CastleWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        setViews((ZipAnimAction) action);

        Animator lighterAni = getLightersAnimator();
        Animator castleAni = getCastleShowingAnimator();
        Animator fireworks = getFireworksAnimator();
        Animator fireworks2 = getFireworksAnimator();

        AnimatorSet set = new AnimatorSet();
        set.play(lighterAni).with(castleAni);
        set.play(castleAni).after(1100).before(fireworks);
        set.play(fireworks).after(1400).before(fireworks2);
        set.play(fireworks2).after(2200);
        set.start();
        return set;
    }


    private Animator getLightersAnimator() {
        Animator light1 = getLighterAnimator(ivLight1, -15, 20, -30, 50);
        Animator light2 = getLighterAnimator(ivLight2, 0, -50, 30, 30);
        Animator light3 = getLighterAnimator(ivLight3, 0, 50, -30, -30);
        Animator light4 = getLighterAnimator(ivLight4, -15, -30, 10, -55);
        AnimatorSet set = new AnimatorSet();
        set.play(light1).with(light4);
        set.play(light4);
        set.play(light2).with(light3).after(200);
        return set;
    }

    private Animator getLighterAnimator(ImageView lighter, int rotation1, int rotation2, int rotation3, int rotation4) {
        Animator ani1 = getLighterAnimator(lighter, rotation1, rotation2);
        Animator ani2 = getLighterAnimator(lighter, rotation2, rotation3);
        Animator ani3 = getLighterAnimator(lighter, rotation3, rotation4);
        AnimatorSet set = new AnimatorSet();
        set.addListener(new ViewShowListener(lighter));
        set.play(ani1).before(ani2);
        set.play(ani2).before(ani3);
        return set;
    }

    private Animator getLighterAnimator(ImageView lighter, int origin, int destination) {
        ObjectAnimator lighterAni = ObjectAnimator.ofFloat(lighter, "rotation", origin, destination);
        lighterAni.setDuration(800);
        return lighterAni;
    }

    private Animator getCastleShowingAnimator() {
        float castleSY = GiftUtility.getScreenHeight(context) + GiftUtility.dp2Px(context, 240);
        float castleDY = GiftUtility.getScreenHeight(context) - GiftUtility.dp2Px(context, 240) - GiftUtility
                .dp2Px(context, 30);
        PropertyValuesHolder castleX1 = PropertyValuesHolder.ofFloat("y", castleSY, castleDY);

        PropertyValuesHolder castleScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f);
        PropertyValuesHolder castleScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1.0f);

        ObjectAnimator castleAni = ObjectAnimator.ofPropertyValuesHolder(llCastle, castleX1, castleScaleX, castleScaleY);
        castleAni.addListener(new ViewShowListener(ivCastle));
        castleAni.addListener(new ViewShowListener(llCastle));
        castleAni.setInterpolator(new OvershootInterpolator());
        castleAni.setDuration(1000);
        return castleAni;
    }

    private Animator getFireworksAnimator() {
        Animator fireworkAni1 = getFireWorkAnimator(ivWay1, 155, ivFire1);
        Animator fireworkAni2 = getFireWorkAnimator(ivWay2, 162, ivFire2);
        Animator fireworkAni3 = getFireWorkAnimator(ivWay3, 157, ivFire3);
        Animator fireworkAni4 = getFireWorkAnimator(ivWay4, 111, ivFire4);
        AnimatorSet set = new AnimatorSet();
        set.play(fireworkAni1).with(fireworkAni2);
        set.play(fireworkAni2).after(200);
        set.play(fireworkAni4).with(fireworkAni3);
        set.play(fireworkAni3).after(200);
        return set;
    }

    private Animator getFireWorkAnimator(final ImageView way, final int wayHeight, final ImageView fire) {
        ValueAnimator wayAni = ValueAnimator.ofFloat(0f, GiftUtility.dp2Px(way.getContext(), wayHeight));
        wayAni.setDuration(300);
        wayAni.addListener(new ViewShowListener(way));
        wayAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastHeight;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                way.getLayoutParams().height = value.intValue();
                lastHeight = value.intValue();
                way.requestLayout();
            }
        });

        ObjectAnimator wayScaleAni = ObjectAnimator.ofFloat(way, "alpha", 1, 0.2f);
        wayScaleAni.setDuration(500);

        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 0, 1);
        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        ObjectAnimator fireAni = ObjectAnimator.ofPropertyValuesHolder(fire, alphaValue, scaleXValue, scaleYValue);
        fireAni.setDuration(600);
        fireAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fire.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        way.setVisibility(View.GONE);
                    }
                }, 300);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fire.setVisibility(View.GONE);
                    }
                }, 300);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(wayAni).with(wayScaleAni);
        set.play(wayScaleAni).before(fireAni);
        set.play(fireAni);
        return set;
    }


    class ViewShowListener implements Animator.AnimatorListener {
        private View view;

        public ViewShowListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private void setViews(ZipAnimAction action) {
        Resource res = getResource(action.getAnimResDir());
        ivCastle.setImageBitmap(res.castle);
        tvSenderName.setText(action.getSenderName());
        tvGiftName.setText(action.getGiftName());
        ivWay1.setImageBitmap(res.way1);
        ivWay2.setImageBitmap(res.way2);
        ivWay3.setImageBitmap(res.way3);
        ivWay4.setImageBitmap(res.way4);
        ivFire1.setImageBitmap(res.fire1);
        ivFire2.setImageBitmap(res.fire2);
        ivFire3.setImageBitmap(res.fire3);
        ivFire4.setImageBitmap(res.fire4);
        ivLight1.setImageBitmap(res.light1);
        ivLight2.setImageBitmap(res.light2);
        ivLight3.setImageBitmap(res.light3);
        ivLight4.setImageBitmap(res.light4);
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.CASTLE;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_castle;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        ivCastle = (ImageView) container.findViewById(R.id.iv_castle);
        llCastle = (LinearLayout) container.findViewById(R.id.ll_castle);
        tvSenderName = (TextView) container.findViewById(R.id.sender_name_tv);
        tvGiftName = (TextView) container.findViewById(R.id.gift_name_tv);
        ivLight1 = (ImageView) container.findViewById(R.id.iv_light1);
        ivLight2 = (ImageView) container.findViewById(R.id.iv_light2);
        ivLight3 = (ImageView) container.findViewById(R.id.iv_light3);
        ivLight4 = (ImageView) container.findViewById(R.id.iv_light4);
        ivWay1 = (ImageView) container.findViewById(R.id.iv_way1);
        ivWay2 = (ImageView) container.findViewById(R.id.iv_way2);
        ivWay3 = (ImageView) container.findViewById(R.id.iv_way3);
        ivWay4 = (ImageView) container.findViewById(R.id.iv_way4);
        ivFire1 = (ImageView) container.findViewById(R.id.iv_fire1);
        ivFire2 = (ImageView) container.findViewById(R.id.iv_fire2);
        ivFire3 = (ImageView) container.findViewById(R.id.iv_fire3);
        ivFire4 = (ImageView) container.findViewById(R.id.iv_fire4);
    }

    private Resource getResource(File animationDir) {
        File castle = new File(animationDir.getAbsoluteFile() + File.separator + "cb.png");
        File way1 = new File(animationDir.getAbsoluteFile() + File.separator + "f1.png");
        File way2 = new File(animationDir.getAbsoluteFile() + File.separator + "f2.png");
        File way3 = new File(animationDir.getAbsoluteFile() + File.separator + "f3.png");
        File way4 = new File(animationDir.getAbsoluteFile() + File.separator + "f4.png");
        File fire1 = new File(animationDir.getAbsoluteFile() + File.separator + "y1.png");
        File fire2 = new File(animationDir.getAbsoluteFile() + File.separator + "y2.png");
        File fire3 = new File(animationDir.getAbsoluteFile() + File.separator + "y3.png");
        File fire4 = new File(animationDir.getAbsoluteFile() + File.separator + "y4.png");
        File light1 = new File(animationDir.getAbsoluteFile() + File.separator + "g1.png");
        File light2 = new File(animationDir.getAbsoluteFile() + File.separator + "g2.png");
        File light3 = new File(animationDir.getAbsoluteFile() + File.separator + "g3.png");
        File light4 = new File(animationDir.getAbsoluteFile() + File.separator + "g4.png");

        Resource res = new Resource();
        res.castle = BitmapFactory.decodeFile(castle.getAbsolutePath());
        res.way1 = BitmapFactory.decodeFile(way1.getAbsolutePath());
        res.way2 = BitmapFactory.decodeFile(way2.getAbsolutePath());
        res.way3 = BitmapFactory.decodeFile(way3.getAbsolutePath());
        res.way4 = BitmapFactory.decodeFile(way4.getAbsolutePath());
        res.fire1 = BitmapFactory.decodeFile(fire1.getAbsolutePath());
        res.fire2 = BitmapFactory.decodeFile(fire2.getAbsolutePath());
        res.fire3 = BitmapFactory.decodeFile(fire3.getAbsolutePath());
        res.fire4 = BitmapFactory.decodeFile(fire4.getAbsolutePath());
        res.light1 = BitmapFactory.decodeFile(light1.getAbsolutePath());
        res.light2 = BitmapFactory.decodeFile(light2.getAbsolutePath());
        res.light3 = BitmapFactory.decodeFile(light3.getAbsolutePath());
        res.light4 = BitmapFactory.decodeFile(light4.getAbsolutePath());
        return res;
    }

    class Resource {
        Bitmap castle;
        Bitmap light1;
        Bitmap light2;
        Bitmap light3;
        Bitmap light4;
        Bitmap way1;
        Bitmap way2;
        Bitmap way3;
        Bitmap way4;
        Bitmap fire1;
        Bitmap fire2;
        Bitmap fire3;
        Bitmap fire4;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        host.removeView(container);
        for (int i = 0; i < container.getChildCount(); i ++) {
            container.getChildAt(i).setVisibility(View.GONE);
        }
    }
}
