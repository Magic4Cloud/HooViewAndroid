package com.easyvaas.common.emoji.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easyvaas.common.emoji.utils.Utils;

public class AutoHeightLayout extends RelativeLayout {

    private static final int ID_CHILD = 1;
    public static final int KEYBOARD_STATE_NONE = 100;
    public static final int KEYBOARD_STATE_FUNC = 102;
    public static final int KEYBOARD_STATE_BOTH = 103;

    protected Context mContext;
    protected int mAutoHeightLayoutId;
    protected int mAutoViewHeight;
    protected View mAutoHeightLayoutView;
    protected int mKeyboardState = KEYBOARD_STATE_NONE;

    private int mMaxParentHeight = 0;
    private ArrayList<Integer> heightList = new ArrayList<Integer>();

    public AutoHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mAutoViewHeight = Utils.getDefKeyboardHeight(mContext);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int childSum = getChildCount();
        if (getChildCount() > 1) {
            throw new IllegalStateException("can host only one direct child");
        }
        super.addView(child, index, params);

        if (childSum == 0) {
            mAutoHeightLayoutId = child.getId();
            if (mAutoHeightLayoutId < 0) {
                child.setId(ID_CHILD);
                mAutoHeightLayoutId = ID_CHILD;
            }
            RelativeLayout.LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            child.setLayoutParams(paramsChild);
        } else if (childSum == 1) {
            RelativeLayout.LayoutParams paramsChild = (LayoutParams) child.getLayoutParams();
            paramsChild.addRule(RelativeLayout.ABOVE, mAutoHeightLayoutId);
            child.setLayoutParams(paramsChild);
        }
    }

    public void setAutoHeightLayoutView(View view) {
        mAutoHeightLayoutView = view;
    }

    public void setAutoViewHeight(final int height) {
        int heightDp = Utils.px2dip(mContext, height);
        if (heightDp > 0 && heightDp != mAutoViewHeight) {
            mAutoViewHeight = heightDp;
            Utils.setDefKeyboardHeight(mContext, mAutoViewHeight);
        }

        if (mAutoHeightLayoutView != null) {
            mAutoHeightLayoutView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAutoHeightLayoutView
                    .getLayoutParams();
            params.height = height;
            mAutoHeightLayoutView.setLayoutParams(params);
        }
    }

    public void hideAutoView() {
        this.post(new Runnable() {
            @Override
            public void run() {
                Utils.closeSoftKeyboard(mContext);
                setAutoViewHeight(0);
                if (mAutoHeightLayoutView != null) {
                    mAutoHeightLayoutView.setVisibility(View.GONE);
                }
            }
        });
        mKeyboardState = KEYBOARD_STATE_NONE;
    }

    public void showAutoView() {
        if (mAutoHeightLayoutView != null) {
            mAutoHeightLayoutView.setVisibility(VISIBLE);
            setAutoViewHeight(Utils.dip2px(mContext, mAutoViewHeight));
        }
        mKeyboardState = mKeyboardState == KEYBOARD_STATE_NONE ? KEYBOARD_STATE_FUNC : KEYBOARD_STATE_BOTH;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureHeight = measureHeight(heightMeasureSpec);
        heightList.add(measureHeight);
        if (mMaxParentHeight != 0) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (heightList.size() >= 2) {
            int oldh = heightList.get(0);
            int newh = heightList.get(heightList.size() - 1);
            int softHeight = mMaxParentHeight - newh;
             /*
             newh         oldh
             500            max         -> 弹起                softKeyboard = max - 500
             600            500         -> 缩小                softKeyboard = max - 600
             500            600         -> 拉伸               softKeyboard = max - 500
             max           500         -> 关闭               softKeyboard = 0
             */
            /**
             * 弹出软键盘
             */
            if (oldh == mMaxParentHeight) {
                OnSoftPop(softHeight);
            }
            /**
             * 隐藏软键盘
             */
            else if (newh == mMaxParentHeight) {
                OnSoftClose(softHeight);
            }
            /**
             * 调整软键盘高度
             */
            else {
                OnSoftChangeHeight(softHeight);
            }
            heightList.clear();
        } else {
            heightList.clear();
        }
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    public void OnSoftPop(final int height) {
        mKeyboardState = KEYBOARD_STATE_BOTH;
        setAutoViewHeight(height);
    }

    public void OnSoftClose(int height) {
        mKeyboardState = mKeyboardState == KEYBOARD_STATE_BOTH ? KEYBOARD_STATE_FUNC : KEYBOARD_STATE_NONE;
    }

    public void OnSoftChangeHeight(final int height) {
        setAutoViewHeight(height);
    }
}
