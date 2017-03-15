package com.easyvaas.common.widget;

import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 鉴于经常用到获取验证码倒计时按钮 在网上也没有找到理想的 自己写一个
 *
 * @author yung
 *         2015年1月14日[佛祖保佑 永无BUG]
 *         PS: 由于发现timer每次cancle()之后不能重新schedule方法,所以计时完毕只恐timer.
 *         每次开始计时的时候重新设置timer, 没想到好办法初次下策
 *         注意把该类的onCreate()onDestroy()和activity的onCreate()onDestroy()同步处理
 */
public class TimeButton extends Button {
    private static final String TAG = TimeButton.class.getSimpleName();

    private final String TIME = "time";
    private final String CTIME = "ctime";
    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mCurrentTime;
    private Handler mHandler;

    private long mLength;
    private String mText;
    private String mTextRunning;
    private ColorStateList mTextColorRunning;
    private ColorStateList mTextColor;
    private int mBackgroundRunningResId;
    private Drawable mBackgroundDrawable;

    public TimeButton(Context context) {
        super(context);
        init(null);
    }

    public TimeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeButton);
        init(a);
        a.recycle();
    }

    private void init(TypedArray tArray) {
        mHandler = new MyHandler(this);

        mLength = 60 * 1000;// 倒计时长度,这里给了默认60秒
        mTextColorRunning = getTextColors();
        mTextColor = getTextColors();
        mBackgroundRunningResId = -1;
        mBackgroundDrawable = getBackground();
        mText = getText().toString();
        if (tArray != null) {
            mLength = tArray.getInt(R.styleable.TimeButton_countdown_length, (int) mLength);
            mTextRunning = tArray.getString(R.styleable.TimeButton_text_running);
            mTextColorRunning = tArray.getColorStateList(R.styleable.TimeButton_text_color_running);
            mBackgroundRunningResId = tArray.getResourceId(R.styleable.TimeButton_background_running,
                    mBackgroundRunningResId);
        }
        if (TextUtils.isEmpty(mTextRunning)) {
            mTextRunning = getText().toString();
        }
    }

    private void initTimer() {
        mCurrentTime = mLength;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };
    }

    public void clearTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
        this.setEnabled(true);
    }

    public void startTime() {
        initTimer();
        if (mTextColorRunning != null) {
            this.setTextColor(mTextColorRunning);
        }
        if (mBackgroundRunningResId > 0) {
            this.setBackgroundResource(mBackgroundRunningResId);
        }
        this.setText(mCurrentTime / 1000 + mText);
        this.setEnabled(false);
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    public boolean isTiming() {
        return mCurrentTime > 0;
    }

    /**
     * 设置计时时候显示的文本
     */
    public TimeButton setTextAfter(String text) {
        this.mText = text;
        return this;
    }

    /**
     * 设置点击之前的文本
     */
    public TimeButton setTextBefore(String text) {
        this.mTextRunning = text;
        this.setText(mTextRunning);
        return this;
    }

    /**
     * 设置到计时长度
     *
     * @param length 时间 默认毫秒
     */
    public TimeButton setLength(long length) {
        this.mLength = length;
        return this;
    }

    private static class MyHandler extends Handler {

        private SoftReference<TimeButton> softReference;

        public MyHandler(TimeButton timeButton) {
            softReference = new SoftReference<TimeButton>(timeButton);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TimeButton timeButton = softReference.get();
            if (timeButton == null) {
                return;
            }

            timeButton.setText(timeButton.mCurrentTime / 1000 + timeButton.mTextRunning);
            timeButton.mCurrentTime -= 1000;
            if (timeButton.mCurrentTime < 0) {
                timeButton.setText(timeButton.mText);
                timeButton.setTextColor(timeButton.mTextColor);
                if (timeButton.mBackgroundRunningResId > 0) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        timeButton.setBackground(timeButton.mBackgroundDrawable);
                    } else {
                        timeButton.setBackgroundDrawable(timeButton.mBackgroundDrawable);
                    }
                }
                timeButton.setEnabled(true);
                timeButton.clearTimer();
            }
        }
    }
}
