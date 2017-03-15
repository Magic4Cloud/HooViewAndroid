package com.easyvaas.common.gift.workers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.gift.action.type.AnimType;
import com.easyvaas.common.gift.action.Action;
import com.easyvaas.common.gift.action.NotificationAction;
import com.easyvaas.common.gift.utils.GiftUtility;
import com.easyvaas.common.gift.R;

public class NotificationWorker extends Worker {
    private ImageView ivAvatar, ivGift;
    private TextView tvIndex, tvName;

    private boolean isGiftPipBottom = false;
    private boolean isEnterAnimDone = true;
    private boolean isOutAnimDone = true;

    public NotificationWorker(ViewGroup host, boolean isGiftPipBottom) {
        super(host);
        this.isGiftPipBottom = isGiftPipBottom;
        initMock();
    }

    @Override
    protected void init() {

    }

    private void initMock() {
        super.init();
    }

    @Override
    protected boolean isMine(AnimType type) {
        return type == AnimType.NOTIFICATION || type == AnimType.EMOJI;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_gift_notification;
    }

    @Override
    protected void onPreparing(ViewGroup container) {
        if (isGiftPipBottom) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) container.getLayoutParams();
            layoutParams.bottomMargin = container.getResources().getDimensionPixelSize(R.dimen.live_gift_pipe_bottom);
            container.setLayoutParams(layoutParams);
        }
        attachToHost(container);
        ivAvatar = (ImageView) container.findViewById(R.id.avatar_iv);
        ivGift = (ImageView) container.findViewById(R.id.gift_iv);
        tvIndex = (TextView) container.findViewById(R.id.index_tv);
        tvName = (TextView) container.findViewById(R.id.name_tv);
    }

    @Override
    protected Animator doAnimation(final ViewGroup host, final ViewGroup container, Action action) {
        NotificationAction ntfAction = (NotificationAction) action;
        setViews(ntfAction);

        if (!container.isShown()) {
            createNotificationEnterAnimator(container).start();
        }
        if (ntfAction.isEndAlignment()) {
            createNotificationOutAnimator(container).start();
        }
        return createIndexAnimator();
    }

    @Override
    public void cancelAnimation() {
        if (isEnterAnimDone && isOutAnimDone) {
            super.cancelAnimation();
        }
    }

    private Animator createNotificationEnterAnimator(final ViewGroup container) {
        float source = GiftUtility.dp2Px(container.getContext(), -200);
        Animator enter = ObjectAnimator.ofFloat(container, "x", source, 0);
        enter.setDuration(500);
        enter.addListener(new ListenerHolder() {
            @Override
            public void onAnimationStart(Animator animation) {
                isEnterAnimDone = false;
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isEnterAnimDone = true;
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(enter).before(createIndexAnimator());
        return set;
    }

    private Animator createNotificationOutAnimator(final ViewGroup container) {
        float destination = GiftUtility.dp2Px(container.getContext(), -200);
        Animator out = ObjectAnimator.ofFloat(container, "x", destination);
        out.setDuration(500);
        out.addListener(new ListenerHolder() {
            @Override
            public void onAnimationStart(Animator animation) {
                isOutAnimDone = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOutAnimDone = true;
                container.setVisibility(View.INVISIBLE);
            }
        });
        return out;
    }

    private Animator createIndexAnimator() {
        PropertyValuesHolder scaleXValue = PropertyValuesHolder.ofFloat("scaleX", 0f, 1.0f);
        PropertyValuesHolder scaleYValue = PropertyValuesHolder.ofFloat("scaleY", 0f, 1.0f);
        PropertyValuesHolder alphaValue = PropertyValuesHolder.ofFloat("alpha", 0.5f, 1);
        Animator animator = ObjectAnimator.ofPropertyValuesHolder(tvIndex, scaleXValue, scaleYValue, alphaValue);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
        return animator;
    }

    @SuppressLint("SetTextI18n")
    private void setViews(NotificationAction action) {
        GiftUtility.showUserPhoto(host.getContext(), action.getSenderIcon().url(), ivAvatar);
        GiftUtility.setGoodsCacheImage(action.getGiftIcon().url(), ivGift);
        tvIndex.setText("x " + (action.getIndex() + 1));
        tvName.setText(action.getSenderName());
    }

    class ListenerHolder implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
