package com.easyvaas.common.gift.workers;

import java.lang.ref.SoftReference;
import java.util.concurrent.CountDownLatch;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;

import com.easyvaas.common.gift.action.Action;

public abstract class WorkerFrame extends Worker {
    private static final int MSG_RED_CAR_GIFT_ANIM_REMOVE = 100;

    private AnimationDrawable animationDrawable;

    private CountDownLatch mLatch;
    private MyHandler mHandler;

    private static class MyHandler extends Handler {
        private SoftReference<WorkerFrame> softReference;

        public MyHandler(WorkerFrame worker) {
            softReference = new SoftReference<WorkerFrame>(worker);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final WorkerFrame worker = softReference.get();
            if (worker == null) {
                return;
            }
            switch (msg.what) {
                case MSG_RED_CAR_GIFT_ANIM_REMOVE:
                    if (worker.host != null && worker.container != null && worker.mLatch != null
                            && worker.animationDrawable != null) {
                        worker.onFrameAnimationEnd(worker.host, worker.container);
                        worker.mLatch.countDown();
                        worker.animationDrawable.stop();
                        worker.animationDrawable = null;
                    }
                    break;
            }

        }
    }

    public WorkerFrame(ViewGroup host) {
        super(host);
        mHandler = new MyHandler(this);
    }

    protected AnimationDrawable doFrameAnimation(ViewGroup host, ViewGroup container, Action action) {
        return null;
    }

    protected void onFrameAnimationEnd(ViewGroup host, ViewGroup container) {
    }

    public final void workOnBackground(final Action action, final CountDownLatch latch) {
        this.mLatch = latch;
        if (isMine(action.getAnimType())) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    animationDrawable = doFrameAnimation(host, container, action);
                    if (animationDrawable != null) {
                        animationDrawable.start();
                        mHandler.sendEmptyMessageDelayed(MSG_RED_CAR_GIFT_ANIM_REMOVE, 4320);
                    }
                }
            });
        } else {
            latch.countDown();
        }
    }
}
