package com.easyvaas.common.widget;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class MyUserPhoto extends ImageView {
    private static final int BORDER_SMALL_WIDTH_DEFAULT = 10;
    private static final int BORDER_SMALL_HEIGHT_DEFAULT = 10;
    private static final int IS_VIP = 1;

    private static final int BORDER_WIDTH_DEFAULT = 0;
    private static final int BORDER_COLOR_DEFAULT = R.color.border_color_default;

    private int mSmallImageWidth;
    private int mSmallImageHeight;
    private int mIsVip;
    private int mBorderWidth;
    private int mBorderColor;
    private int mSmallIconResId;

    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private WeakReference<Bitmap> mWeakBitmap;
    private WeakReference<Bitmap> mMaskWeakBitmap;
    private WeakReference<Canvas> mWeakCanvas;

    public MyUserPhoto(Context context) {
        super(context);
    }

    public MyUserPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyUserPhoto);
        mSmallImageHeight = a.getDimensionPixelSize(R.styleable.MyUserPhoto_small_height,
                BORDER_SMALL_HEIGHT_DEFAULT);
        mSmallImageWidth = a.getDimensionPixelSize(R.styleable.MyUserPhoto_small_width,
                BORDER_SMALL_WIDTH_DEFAULT);
        mIsVip = a.getInt(R.styleable.MyUserPhoto_is_vip, 0);
        mSmallIconResId = a.getResourceId(R.styleable.MyUserPhoto_small_icon,
                R.drawable.floating_screen_v);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.MyUserPhoto_border_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BORDER_WIDTH_DEFAULT, getResources().getDisplayMetrics()));
        mBorderColor = a.getColor(R.styleable.MyUserPhoto_border_color,
                getResources().getColor(BORDER_COLOR_DEFAULT));

        setWillNotDraw(false);

        a.recycle();
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
            mWeakBitmap = new WeakReference<Bitmap>(bitmap);
        }

        Canvas drawCanvas = mWeakCanvas == null ? null : mWeakCanvas.get();
        if (drawCanvas == null) {
            drawCanvas = new Canvas(bitmap);
            mWeakCanvas = new WeakReference<Canvas>(drawCanvas);
        }

        float scale = 1.0f;
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        scale = getWidth() * 1.0f / Math.min(drawableWidth, drawableHeight);
        drawable.setBounds(0, 0, (int) (scale * drawableWidth), (int) (scale * drawableHeight));
        drawable.draw(drawCanvas);

        mPaint.reset();
        mPaint.setFilterBitmap(false);
        mPaint.setXfermode(mXfermode);

        Bitmap maskBitmap = mMaskWeakBitmap == null ? null : mMaskWeakBitmap.get();
        if (maskBitmap == null || maskBitmap.isRecycled()) {
            maskBitmap = getMaskBitmap();
            mMaskWeakBitmap = new WeakReference<Bitmap>(maskBitmap);
        }
        drawCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        if (mBorderWidth > 0) {
            drawCircleBorder(drawCanvas);
        }
        if (mIsVip == IS_VIP && (mSmallImageWidth > 0 || mSmallImageHeight > 0)) {
            drawSmallIcon(drawCanvas);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
    }


    private Bitmap getMaskBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - mBorderWidth, paint);

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
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, (getWidth() - mBorderWidth) / 2, paint);
    }

    private void drawSmallIcon(Canvas canvas) {
        int smallIconSize = mSmallImageWidth;
        Bitmap smallIconBitmap = BitmapFactory.decodeResource(getResources(), mSmallIconResId);
        smallIconBitmap = scaleImage(smallIconBitmap, smallIconSize, smallIconSize);
        float marginTop = getWidth() / 2.0f + (float)Math.sqrt(2) * (getHeight() / 2.0f) / 2.0f
                - smallIconSize / 2.0f;
        float marginLeft = marginTop;
        canvas.drawBitmap(smallIconBitmap, marginLeft, marginTop, null);
        smallIconBitmap.recycle();
    }

    private Bitmap scaleImage(Bitmap bitmap, float newWidth, float newHeight) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

    public ImageView getRoundImageView() {
        return this;
    }

    public void setIsVip(int isVip) {
        if (mIsVip != isVip) {
            mIsVip = isVip;
            invalidate();
        }
    }
}
