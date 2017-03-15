package com.easyvaas.common.gift.animator;

import java.util.Map;
import java.util.Set;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.action.StaticAnimAction;
import com.easyvaas.common.gift.action.type.Type;

@SuppressWarnings("unchecked")
class Dispatcher {
    private Map<Type, Set<ActionQueue>> actionQueues;

    private NotificationAction lastLocalAction;

    private String currentUserName;

    public Dispatcher(Map<Type, Set<ActionQueue>> actionQueues, String name) {
        this.actionQueues = actionQueues;
        this.currentUserName = name;
    }

    public void dispatch(Action action) {
        if (!filterAction(action)) return;

        switch (action.getType()) {
            case NOTIFICATION:
                dispatchNotification((NotificationAction) action);
                break;
            case ANIMATION:
                dispatchAnimation(action);
                break;
        }
    }

    private boolean filterAction(Action action) {
        boolean isGoOn = !(action.getFromType() == FromType.REMOTE && action.getSenderID().equals(currentUserName));
        if (action instanceof StaticAnimAction) {
            isGoOn = isGoOn && ((StaticAnimAction) action).isShow();
        }
        return isGoOn;
    }

    @SuppressWarnings("UnnecessaryContinue")
    private void dispatchNotification(NotificationAction action) {
        // 处理通知动画的index
        if (action.getFromType() == FromType.LOCAL) {
            if (lastLocalAction == null) {
                lastLocalAction = action;
            } else {
                if (action.isEndAlignment()) {
                    action.setIndex(lastLocalAction.getIndex());
                    lastLocalAction = null;
                } else {
                    action.setIndex(lastLocalAction.getIndex() + 1);
                    lastLocalAction = action;
                }
            }
        }

        Set<ActionQueue> actionQueueSet = actionQueues.get(Type.NOTIFICATION);
        for (ActionQueue queue : actionQueueSet) {
            NotificationAction lastAction = (NotificationAction) queue.getLast(action.getFromType());
            if (hasOtherInAlignments(actionQueueSet, queue, action)) {
                continue;
            }
            if (lastAction != null && !lastAction.isEndAlignment()) {
                offerAction(queue, action);
                return;
            }
            if (isMinQueue(actionQueueSet, queue)) {
                offerAction(queue, action);
                return;
            }
        }
    }

    private boolean isMinQueue(Set<ActionQueue> queueSet, ActionQueue actionQueue) {
        int minSize = 0;
        for (ActionQueue queue : queueSet) {
            if (queue.size() < minSize) {
                minSize = queue.size();
            }
        }
        return actionQueue.size() == minSize;
    }

    private boolean hasOtherInAlignments(Set<ActionQueue> queueSet, ActionQueue actionQueue, NotificationAction action) {
        boolean hasAlignmentsNotificationQueue = false;
        for (ActionQueue queue : queueSet) {
            NotificationAction lastAction = (NotificationAction) queue.getLast(action.getFromType());
            if (queue != actionQueue && lastAction != null && !lastAction.isEndAlignment()) {
                hasAlignmentsNotificationQueue = true;
            }
        }
        return hasAlignmentsNotificationQueue;
    }

    private void offerAction(ActionQueue queue, Action action) {
        if (queue != null) {
            if (action.getFromType() == FromType.LOCAL) {
                queue.offerLocalAction(action);
            }
            if (action.getFromType() == FromType.REMOTE) {
                queue.offerRemoteAction(action);
            }
        }
    }

    private void dispatchAnimation(Action action) {
        Set<ActionQueue> actionQueueSet = actionQueues.get(Type.ANIMATION);
        for (ActionQueue queue : actionQueueSet) {
            if (action.getFromType() == FromType.LOCAL) {
                queue.offerLocalAction(action);
            }
            if (action.getFromType() == FromType.REMOTE) {
                queue.offerRemoteAction(action);
            }
        }
    }
}
