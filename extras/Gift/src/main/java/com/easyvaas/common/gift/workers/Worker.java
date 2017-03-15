package com.easyvaas.common.gift.workers;

import java.util.concurrent.CountDownLatch;

import android.animation.Animator;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.type.AnimType;

/**
 * The animation worker
 *
 * Created by LiFZhe on 4/19/16.
 */
public abstract class Worker {
    protected abstract boolean isMine(AnimType type);

    @LayoutRes protected abstract int getLayout();

    protected abstract void onPreparing(ViewGroup container);

    protected abstract Animator doAnimation(ViewGroup host, ViewGroup container, Action action);

    protected void onAnimationEnd(ViewGroup host, ViewGroup container, Animator animator) { }

    protected ViewGroup host;
    protected ViewGroup container;
    private Animator animator;

    public Worker(ViewGroup host) {
        this.host = host;
        init();
    }

    protected void init() {
        container = (ViewGroup) LayoutInflater.from(host.getContext()).inflate(getLayout(), host, false);
        onPreparing(container);
    }

    public void workOnBackground(final Action action, final CountDownLatch latch) {
        if (isMine(action.getAnimType())) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    animator = doAnimation(host, container, action);
                    if (animator != null) {
                        animator.addListener(new AnimatorListener(latch));
                        animator.start();
                    } else {
                        latch.countDown();
                    }
                }
            });
        } else {
            latch.countDown();
        }
    }

    public void cancelAnimation() {
        if (animator == null) return;
        animator.cancel();
    }

    class AnimatorListener implements Animator.AnimatorListener {
        private CountDownLatch latch = null;

        public AnimatorListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Worker.this.onAnimationEnd(host, container, animation);
            latch.countDown();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            latch.countDown();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    protected void attachToHost(View view) {
        if (container.getParent() != null) {
            host.removeView(container);
        }
        host.addView(view);
    }
}
