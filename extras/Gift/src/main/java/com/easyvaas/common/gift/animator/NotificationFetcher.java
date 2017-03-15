package com.easyvaas.common.gift.animator;

import java.util.concurrent.CountDownLatch;

import android.view.ViewGroup;

import com.easyvaas.common.gift.utils.GiftLogger;
import rx.functions.Action1;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.workers.NotificationWorker;

class NotificationFetcher extends Thread implements ActionFetcher {
    private Action1<NotificationAction> onNotificationSend;

    private ActionQueue actionQueue;
    private boolean isRunning = true;

    private NotificationWorker worker;

    public NotificationFetcher(ViewGroup host,
                               Type type,
                               Action1<NotificationAction> onNotificationSend,
                               ActionQueue actionQueue) {
        this.onNotificationSend = onNotificationSend;
        this.actionQueue = actionQueue;
        if (type == Type.TOP) {
            worker = new NotificationWorker(host, false);
        } else {
            worker = new NotificationWorker(host, true);
        }
        if (actionQueue instanceof ActionQueue.NotificationQueue) {
            ((ActionQueue.NotificationQueue) actionQueue).setAttachedWorker(worker);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                doWork();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        super.start();
    }

    private void doWork() throws InterruptedException {
        Action action = actionQueue.take();
        if (action == null) return;
        GiftLogger.d(getClass(), action.toString());
        if (worker != null) {
            onNotificationSend.call((NotificationAction) action);
            CountDownLatch latch = new CountDownLatch(1);
            worker.workOnBackground(action, latch);
            latch.await();
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
        interrupt();
    }

    public enum Type {
        TOP, BOTTOM
    }
}
