package com.easyvaas.common.gift.animator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.ZipAnimAction;
import com.easyvaas.common.gift.action.type.FromType;
import com.easyvaas.common.gift.bean.GiftEntity;
import com.easyvaas.common.gift.bean.GoodsEntity;
import com.easyvaas.common.gift.utils.GiftUtility;

import rx.functions.Action1;

import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.action.StaticAnimAction;
import com.easyvaas.common.gift.action.type.Type;

public class GiftAnimator {
    public static String prefix, suffix;
    private Context context;

    private Map<Type, Set<ActionQueue>> actionQueues = new HashMap<>();
    private Set<ActionFetcher> fetchers = new HashSet<>();
    private Dispatcher dispatcher;

    private List<GoodsEntity> cachedGifts;

    public GiftAnimator(ViewGroup host, Action1<NotificationAction> onNotificationSend, String name) {
        context = host.getContext();
        ActionQueue topNotificationQueue = new ActionQueue.NotificationQueue();
        NotificationFetcher topNotificationFetcher = new NotificationFetcher(host,
                NotificationFetcher.Type.TOP, onNotificationSend, topNotificationQueue);
        ActionQueue bottomNotificationQueue = new ActionQueue.NotificationQueue();
        NotificationFetcher bottomNotificationFetcher = new NotificationFetcher(host,
                NotificationFetcher.Type.BOTTOM, onNotificationSend, bottomNotificationQueue);

        Set<ActionQueue> notificationQueueSet = new HashSet<>();
        notificationQueueSet.add(topNotificationQueue);
        notificationQueueSet.add(bottomNotificationQueue);
        actionQueues.put(Type.NOTIFICATION, notificationQueueSet);

        topNotificationFetcher.start();
        bottomNotificationFetcher.start();
        fetchers.add(topNotificationFetcher);
        fetchers.add(bottomNotificationFetcher);

        ActionQueue animationQueue = new ActionQueue.AnimationQueue();
        AnimationFetcher animationFetcher = new AnimationFetcher(host, animationQueue);

        Set<ActionQueue> animationQueueSet = new HashSet<>();
        animationQueueSet.add(animationQueue);
        actionQueues.put(Type.ANIMATION, animationQueueSet);

        animationFetcher.start();
        fetchers.add(animationFetcher);

        dispatcher = new Dispatcher(actionQueues, name);
    }

    public void showLocalAnim(GiftEntity entity) throws FileNotFoundException {
        showLocalAnim(entity, false);
    }

    public void showLocalAlignmentsEndAnim(GiftEntity entity) throws FileNotFoundException {
        showLocalAnim(entity, true);
    }

    private void showLocalAnim(GiftEntity entity, boolean isAlignmentsEnd) throws FileNotFoundException {
        Action action = null;
        switch (entity.getAnimationType()) {
            case GoodsEntity.ANI_TYPE_STATIC:
                action = new StaticAnimAction(entity, isAlignmentsEnd);
                break;
            case GoodsEntity.ANI_TYPE_ZIP:
                action = new ZipAnimAction(entity, FromType.LOCAL);
                break;
        }
        if (action != null && !isAlignmentsEnd) {
            dispatcher.dispatch(action);
        }
        dispatcher.dispatch(new NotificationAction(entity, isAlignmentsEnd));
    }

    public void showRemoteAnim(List<GiftEntity> entities) throws FileNotFoundException {
        for (GiftEntity entity : entities) {
            fillChatGiftEntity(entity);
        }
    }

    private void fillChatGiftEntity(GiftEntity entity) throws FileNotFoundException {
        if (cachedGifts == null) {
            cachedGifts = GiftUtility.getAllCachedGoods(context);
        }

        for (GoodsEntity item : cachedGifts) {
            if (entity.getGiftId() == item.getId() && item.getType() == GoodsEntity.TYPE_GIFT) {
                entity.setGiftPicUrl(item.getPic());
                entity.setGiftAniUrl(item.getAni());
                entity.setAnimationType(item.getAnitype());
                for (int i = 0; i < entity.getGiftCount(); i++) {
                    executeRemoteAction(entity, i, entity.getGiftCount());
                }
            }
        }
    }

    private void executeRemoteAction(GiftEntity info, int index, int count) throws FileNotFoundException {
        Action action = null;
        switch (info.getAnimationType()) {
            case GoodsEntity.ANI_TYPE_STATIC:
                action = new StaticAnimAction(info, index);
                break;
            case GoodsEntity.ANI_TYPE_ZIP:
                action = new ZipAnimAction(info, FromType.REMOTE);
                break;
        }
        if (action != null) {
            dispatcher.dispatch(action);
        }
        dispatcher.dispatch(new NotificationAction(info, index, false));
        if (index == (count - 1)) {
            dispatcher.dispatch(new NotificationAction(info, index, true));
        }
    }

    public void stop() {
        for (Set<ActionQueue> queues : actionQueues.values()) {
            for (ActionQueue queue : queues) {
                queue.clear();
            }
        }
        for (ActionFetcher fetcher : fetchers) {
            fetcher.cancel();
        }
        ZipAnimAction.resMap.clear();
        cachedGifts = null;
    }
}
