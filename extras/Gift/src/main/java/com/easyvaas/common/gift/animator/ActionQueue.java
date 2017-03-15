package com.easyvaas.common.gift.animator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.workers.NotificationWorker;
import com.easyvaas.common.gift.action.NotificationAction;

public interface ActionQueue {

    void offerLocalAction(Action action);

    void offerRemoteAction(Action action);

    Action take() throws InterruptedException;

    int size();

    int size(FromType type);

    Action getLast(FromType type);

    void clear();

    abstract class Base implements ActionQueue {
        protected Queue<Action> localQueue = new LinkedList<Action>();
        protected Queue<Action> remoteQueue = new LinkedList<Action>();

        private Action lastLocalAction;
        private Action lastRemoteAction;
        protected Action lastAction;

        @Override
        public void offerLocalAction(Action action) {
            if (localQueue.size() < getMaxSize(FromType.LOCAL)) {
                doOfferLocalAction(action);
                lastLocalAction = action;
                lastAction = action;
            }
        }

        @Override
        public void offerRemoteAction(Action action) {
            if (remoteQueue.size() < getMaxSize(FromType.REMOTE)) {
                doOfferRemoteAction(action);
                lastRemoteAction = action;
                lastAction = action;
            }
        }

        protected abstract int getMaxSize(FromType type);

        protected abstract void doOfferLocalAction(Action action);

        protected abstract void doOfferRemoteAction(Action action);

        @Override
        public abstract Action take() throws InterruptedException;

        @Override
        public int size() {
            return localQueue.size() + remoteQueue.size();
        }

        @Override
        public int size(FromType type) {
            if (type == FromType.LOCAL) {
                return localQueue.size();
            }
            if (type == FromType.REMOTE) {
                return remoteQueue.size();
            }
            return 0;
        }

        @Override
        public Action getLast(FromType type) {
            if (type == FromType.LOCAL) {
                return lastLocalAction;
            }
            if (type == FromType.REMOTE) {
                return lastRemoteAction;
            }
            return null;
        }

        @Override
        public synchronized void clear() {
            localQueue.clear();
            remoteQueue.clear();
            notify();
        }
    }

    class AnimationQueue extends Base {

        @Override
        protected int getMaxSize(FromType type) {
            return Integer.MAX_VALUE;
        }

        @Override
        protected synchronized void doOfferLocalAction(Action action) {
            localQueue.offer(action);
            notify();
        }

        @Override
        protected synchronized void doOfferRemoteAction(Action action) {
            remoteQueue.offer(action);
            notify();
        }

        @Override
        public synchronized Action take() throws InterruptedException {
            Action action = doTake();
            if (action == null) {
                wait();
                return doTake();
            } else {
                return action;
            }
        }

        private Action doTake() {
            if (!localQueue.isEmpty()) {
                return localQueue.poll();
            }
            if (!remoteQueue.isEmpty()) {
                return remoteQueue.poll();
            }
            return null;
        }
    }

    class NotificationQueue extends Base {
        private NotificationWorker attachedWorker;
        private boolean isLocalAlignmentsEnd = true;

        private Lock lock = new ReentrantLock();
        private Condition localCondition = lock.newCondition();
        private Condition allCondition = lock.newCondition();

        @Override
        protected int getMaxSize(FromType type) {
            return Integer.MAX_VALUE;
        }

        @Override
        protected void doOfferLocalAction(Action a) {
            lock.lock();
            try {
                NotificationAction action = (NotificationAction) a;
                localQueue.offer(action);
                doLocalAnimationBreak(action);
                isLocalAlignmentsEnd = action.isEndAlignment();
                localCondition.signal();
                allCondition.signal();
            } finally {
                lock.unlock();
            }
        }

        private void doLocalAnimationBreak(Action action) {
            if (lastAction != null && lastAction.getFromType() == FromType.LOCAL) {
                if (attachedWorker != null && action.getFromType() == FromType.LOCAL) {
                    attachedWorker.cancelAnimation();
                }
            }
        }

        @Override
        protected void doOfferRemoteAction(Action action) {
            lock.lock();
            try {
                remoteQueue.offer(action);
                allCondition.signal();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Action take() throws InterruptedException {
            lock.lock();
            try {
                if (localQueue.isEmpty()) {
                    if (isLocalAlignmentsEnd) {
                        if (remoteQueue.isEmpty()) {
                            allCondition.await();
                            return take();
                        } else {
                            return remoteQueue.poll();
                        }
                    } else {
                        localCondition.await();
                        return localQueue.poll();
                    }
                } else {
                    return localQueue.poll();
                }
            } finally {
                lock.unlock();
            }
        }

        public void setAttachedWorker(NotificationWorker attachedWorker) {
            this.attachedWorker = attachedWorker;
        }
    }
}
