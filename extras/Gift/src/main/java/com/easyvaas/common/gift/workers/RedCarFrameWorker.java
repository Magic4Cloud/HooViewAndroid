package com.easyvaas.common.gift.workers;

import java.io.File;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;

import com.easyvaas.common.gift.R;

public class RedCarFrameWorker extends WorkerFrame {
    private Activity context;
    private ImageView ivRedCar;
    private TextView tvSenderName, tvGiftName;

    public RedCarFrameWorker(ViewGroup host) {
        super(host);
        this.context = (Activity) host.getContext();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.CAR_RED;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_redcar;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        ivRedCar = (ImageView) container.findViewById(R.id.iv_red_car);
        tvSenderName = (TextView) container.findViewById(R.id.sender_name_tv);
        tvGiftName = (TextView) container.findViewById(R.id.gift_name_tv);
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        return null;
    }

    @Override
    protected AnimationDrawable doFrameAnimation(ViewGroup host, ViewGroup container, Action action) {
        attachToHost(container);
        setViews((ZipAnimAction) action);
        ZipAnimAction actionNew = (ZipAnimAction) action;
        File file = new File(actionNew.getAnimResDir().getAbsoluteFile().getPath());
        File[] files = file.listFiles();
        ivRedCar.setImageResource(R.drawable.myloading);
        AnimationDrawable drawable = (AnimationDrawable) ivRedCar.getDrawable();
        File boat = null;
        if (drawable.getNumberOfFrames() < 50) {
            for (int i = 0; i < files.length - 3; i++) {
                boat = new File(
                        actionNew.getAnimResDir().getAbsoluteFile() + File.separator + "img" + i + ".png");
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 1;
                opt.inPreferredConfig = Bitmap.Config.ALPHA_8;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                drawable.addFrame(new BitmapDrawable(context.getResources(),
                        BitmapFactory.decodeFile(boat.getAbsolutePath(), opt)), 80);
            }
        }
        return drawable;
    }

    @Override
    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) {
        super.onAnimationEnd(host, container, animator);
        host.removeView(container);
    }

    @Override
    protected void onFrameAnimationEnd(ViewGroup host, ViewGroup container) {
        super.onFrameAnimationEnd(host, container);
        host.removeView(container);
    }

    private void setViews(ZipAnimAction action) {
        tvSenderName.setText(action.getSenderName());
        tvGiftName.setText(action.getGiftName());
    }
}

