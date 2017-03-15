package com.easyvaas.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LetterSideBar extends View {

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    public static String[] mLetters = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private int mChooseIndex = -1;
    private Paint mPaint = new Paint();

    private TextView mTextDialog;

    public LetterSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LetterSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterSideBar(Context context) {
        super(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mLetters.length;

        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(Color.rgb(33, 65, 98));
            // mPaint.setColor(Color.WHITE);
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(30);
            // Selected state
            if (i == mChooseIndex) {
                mPaint.setColor(getResources().getColor(R.color.text_common));
                mPaint.setFakeBoldText(true);
            }

            float x = width / 2 - mPaint.measureText(mLetters[i]) / 2;
            float y = singleHeight * i + singleHeight;

            canvas.drawText(mLetters[i], x, y, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChooseIndex = mChooseIndex;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        // 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
        final int chooseIndex = (int) (y / getHeight() * mLetters.length);

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundResource(android.R.color.transparent);
                mChooseIndex = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                setBackgroundResource(R.color.black_alpha_percent_30);
                if (oldChooseIndex != chooseIndex) {
                    if (chooseIndex >= 0 && chooseIndex < mLetters.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(mLetters[chooseIndex]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(mLetters[chooseIndex]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        mChooseIndex = chooseIndex;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String letter);
    }
}
