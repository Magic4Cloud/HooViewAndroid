package com.easyvaas.common.gift.workers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.StaticAnimAction;
import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.utils.GiftUtility;
import com.easyvaas.common.gift.R;

public class NormalWorker extends Worker {
    ImageView imageView;

    public NormalWorker(ViewGroup host) {
        super(host);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_normal;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        attachToHost(container);
        imageView = (ImageView) container.findViewById(R.id.anim_iv);
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.NORMAL;
    }

    @Override
    protected Animator doAnimation(ViewGroup host, ViewGroup container, Action action) {
        GiftUtility.setGoodsCacheImage(((StaticAnimAction) action).getGiftPictureUrl().url(), imageView);
        Animator enter = getEnterAnim(imageView);
        Animator out = getOutAnim(imageView);
        AnimatorSet set = new AnimatorSet();
        set.play(enter).before(out);
        set.play(out).after(1000);
        return set;
    }

    private Animator getEnterAnim(View view) {
        float source = - GiftUtility.dp2Px(view.getContext(), 100);
        float destination = GiftUtility.getScreenWidth(view.getContext()) / 2 - GiftUtility
                .dp2Px(view.getContext(), 50);
        Animator animator = ObjectAnimator.ofFloat(view, "x", source, destination);
        animator.setDuration(1000);
        animator.setInterpolator(new OvershootInterpolator());
        return animator;
    }

    private Animator getOutAnim(View view) {
        float destination = GiftUtility.getScreenWidth(view.getContext()) + GiftUtility
                .dp2Px(view.getContext(), 100);
        Animator animator = ObjectAnimator.ofFloat(view, "x", destination);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateInterpolator());
        return animator;
    }
}
