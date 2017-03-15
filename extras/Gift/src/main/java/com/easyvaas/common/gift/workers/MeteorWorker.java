package com.easyvaas.common.gift.workers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.ZipAnimAction;

import com.easyvaas.common.gift.R;

/**
 * 流星雨动画
 *
 * Created by LiFZhe on 4/11/16.
 */
public class MeteorWorker extends Worker {
    private List<ImageView> views;

    public MeteorWorker(ViewGroup host) {
        super(host);
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.METEOR;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_meteor;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        views = new ArrayList<ImageView>();
        for (int i = 0; i < container.getChildCount(); i++) {
            views.add((ImageView) container.getChildAt(i));
        }
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        setViews((ZipAnimAction) action);
        return createAnimatorSet(views);
    }

    private AnimatorSet createAnimatorSet(List<ImageView> views) {
        AnimatorSet set = new AnimatorSet();
        for (int i = 0; i < views.size(); i++) {
            set.play(createAnimation(views.get(i))).after(i * 180);
        }
        return set;
    }

    private Animator createAnimation(final ImageView view) {
        final float[] xy = new float[2];

        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("x", view.getX() + 1500);
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat("y", view.getY() + 1500);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, x, y);
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                xy[0] = view.getX();
                xy[1] = view.getY();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                view.setX(xy[0]);
                view.setY(xy[1]);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                view.setX(xy[0]);
                view.setY(xy[1]);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }

    private void setViews(ZipAnimAction action) {
        List<Bitmap> bitmaps = getResource(action.getAnimResDir());
        setRandomBitmapToImage(views, bitmaps);
    }

    private void setRandomBitmapToImage(List<ImageView> views, List<Bitmap> bitmaps) {
        for (int i = 0; i < views.size(); i++) {
            ImageView view = views.get(i);
            view.setVisibility(View.VISIBLE);
            Bitmap bitmap = bitmaps.get(i % 6);
            view.setImageBitmap(bitmap);
        }
    }

    private List<Bitmap> getResource(File animationDir) {
        File start1 = new File(animationDir.getAbsoluteFile() + File.separator + "star1-a0.png");
        File start2 = new File(animationDir.getAbsoluteFile() + File.separator + "star1-a1.png");
        File start3 = new File(animationDir.getAbsoluteFile() + File.separator + "star2-a0.png");
        File start4 = new File(animationDir.getAbsoluteFile() + File.separator + "star2-a1.png");
        File start5 = new File(animationDir.getAbsoluteFile() + File.separator + "star3-a0.png");
        File start6 = new File(animationDir.getAbsoluteFile() + File.separator + "star3-a1.png");

        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeFile(start1.getAbsolutePath()));
        bitmaps.add(BitmapFactory.decodeFile(start2.getAbsolutePath()));
        bitmaps.add(BitmapFactory.decodeFile(start3.getAbsolutePath()));
        bitmaps.add(BitmapFactory.decodeFile(start4.getAbsolutePath()));
        bitmaps.add(BitmapFactory.decodeFile(start5.getAbsolutePath()));
        bitmaps.add(BitmapFactory.decodeFile(start6.getAbsolutePath()));
        return bitmaps;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        host.removeView(container);
    }
}
