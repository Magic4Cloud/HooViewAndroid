package com.easyvaas.common.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class RoundImageView extends ImageView {
    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private WeakReference<Bitmap> mWeakBitmap;
    private WeakReference<Bitmap> mMaskWeakBitmap;
    private WeakReference<Canvas> mWeakCanvas;

    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    private static final int BORDER_RADIUS_DEFAULT = 10;
    private static final int BORDER_WIDTH_DEFAULT = 0;
    private static final int BORDER_COLOR_DEFAULT = R.color.black_alpha_percent_10;

    private int mShapeType;
    private int mBorderRadius;
    private int mBorderWidth;
    private int mBorderColor;
    private int mOverLayColor;

    public RoundImageView(Context context, int BorderWidth, int BorderColor) {
        this(context, null);
        this.mBorderWidth = BorderWidth;
        this.mBorderColor = BorderColor;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_border_radius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BORDER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));// Default 10dp
        mShapeType = a.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLE);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundImageView_border_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BORDER_WIDTH_DEFAULT, getResources().getDisplayMetrics()));
        mBorderColor = a.getColor(R.styleable.RoundImageView_border_color,
                getResources().getColor(BORDER_COLOR_DEFAULT));
        mOverLayColor = a.getColor(R.styleable.RoundImageView_overlay,
                getResources().getColor(android.R.color.transparent));

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mShapeType == TYPE_CIRCLE) {
            int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            // setMeasuredDimension(width, width);
        }

    }

    @Override
    public void invalidate() {
        mWeakBitmap = null;
        mMaskWeakBitmap = null;
        mWeakCanvas = null;
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas drawCanvas = mWeakCanvas == null ? null : mWeakCanvas.get();
        if (drawCanvas == null) {
            drawCanvas = new Canvas(bitmap);
        }

        float scale = 1.0f;
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (mShapeType == TYPE_ROUND) {
            scale = Math.max(getWidth() * 1.0f / drawableWidth, getHeight() * 1.0f / drawableHeight);
        } else {
            scale = getWidth() * 1.0f / Math.min(drawableWidth, drawableHeight);
        }
        drawable.setBounds(0, 0, (int) (scale * drawableWidth), (int) (scale * drawableHeight));
        drawable.draw(drawCanvas);

        mPaint.reset();
        mPaint.setColor(mOverLayColor);
        drawCanvas.drawColor(mOverLayColor);

        mPaint.reset();
        mPaint.setFilterBitmap(false);
        mPaint.setXfermode(mXfermode);

        Bitmap maskBitmap = mMaskWeakBitmap == null ? null : mMaskWeakBitmap.get();
        if (maskBitmap == null || maskBitmap.isRecycled()) {
            maskBitmap = getBitmap();
            mMaskWeakBitmap = new WeakReference<Bitmap>(maskBitmap);
        }
        drawCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        if (mBorderWidth > 0) {
            drawCircleBorder(drawCanvas);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (mShapeType == TYPE_ROUND) {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                    mBorderRadius, mBorderRadius, paint);
        } else {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - mBorderWidth, paint);
        }

        return bitmap;
    }

    private void drawCircleBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(mBorderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBorderWidth);
        if (mShapeType == TYPE_ROUND) {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                    mBorderRadius, mBorderRadius, paint);
        } else {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, (getWidth() - mBorderWidth) / 2, paint);
        }
    }

    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = mBorderColor;
    }
}
