/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.view.bubble;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hooview.app.db.Preferences;
import com.hooview.app.utils.DateTimeUtil;
import com.hooview.app.utils.Logger;
import com.hooview.app.utils.Utils;

public class BubbleView extends View {
    private static final String TAG = BubbleView.class.getSimpleName();
    private static final int MSG_STOP_CREATE_BUBBLE = 1;
    private static final int MSG_DRAW_NEXT_BUBBLE = 2;

    private static final int TYPE_CIRCLE = 0;    //圆形
    private static final int TYPE_STAR = 1;      //五角形
    private static final int TYPE_TRIANGLE = 2;  //三角形
    private static final int TYPE_HEART = 3;     //心形
    private static final int TYPE_RHOMBUS = 4;   //菱形
    private static final int TYPE_ORIGINAL = 5;  //原图
    private static final int BUBBLE_TYPE_LIKE_COMMON = 0;
    private static final int BUBBLE_TYPE_LIKE_CUSTOM = 1;
    private static final int BUBBLE_TYPE_LIKE_EMOJI = 2;
    private static final int BUBBLE_MAX_SCALE_NUMBER = 1;

    private static final int MAX_SHAPE_IN_SCREEN = 120;
    private static final int INTERVAL_CREATE_BUBBLE_WHEN_ADDED = 80;
    private static final int INTERVAL_CREATE_BUBBLE_DEFAULT = 100;
    private static final float MIN_SHAPE_RADIUS = 1.0f;
    private static final float MAX_SHAPE_RADIUS = 42.0f;
    private static final float MAX_EMOJI_RADIUS = 2 * MAX_SHAPE_RADIUS;
    private static final int BORDER_WIDTH = 2;
    private static final int SHAPE_ALPHA = 220;
    private static final float RADIUS_SIZE_STEP = 12f;
    private static final float ALPHA_REDUCE_STEP = 1.2f;
    private float BASE_SPEED_X = 2.0f;
    private float BASE_SPEED_Y = 4.0f;

    private List<Bubble> mBubbles = new ArrayList<Bubble>();
    private int mCreateBubbleInterval = INTERVAL_CREATE_BUBBLE_DEFAULT;
    private int mCreateBubbleDuration;
    private int mCurrentBubbleScaleNumber = 0;
    private Random mRandom = new Random();
    private CreateBubbleRunnable mCreateBubbleThread = new CreateBubbleRunnable();
    private List<Bubble> mBubblesCopy = new ArrayList<Bubble>();
    private MyHandler mHandler;

    private int width, height;
    private Bitmap mShapeBitmap;
    private int mLikeCount;
    private int[] mLikeDrawableIds;
    private int mShapeIndex;
    private Paint mPaint;

    // Custom attribute
    private Drawable src;
    private int mBorderColor;
    private int mShapeColor;
    private int mBorderWidth;
    private int mShapeType;
    private int mBubbleType;
    private float mMaxShapeRadius;
    private float mMinShapeRadius;

    private GestureDetector mGestureDetector;

    private ArrayList<String> mServerLikeImageBitmaps;
    private LinkedHashMap<String, Integer> mEmojiCountMap;

    private static class MyHandler extends Handler {
        private SoftReference<BubbleView> softReference;

        public MyHandler(BubbleView bubbleView) {
            softReference = new SoftReference<BubbleView>(bubbleView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BubbleView bubbleView = softReference.get();
            if (bubbleView == null) {
                return;
            }
            switch (msg.what) {
                case MSG_STOP_CREATE_BUBBLE:
                    bubbleView.mCreateBubbleThread.stop();
                    bubbleView.mCreateBubbleInterval = INTERVAL_CREATE_BUBBLE_DEFAULT;
                    break;
            }
        }
    }

    public BubbleView(Context context) {
        super(context);
        init(null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, com.hooview.app.R.styleable.BubbleView);
        init(attributes);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, com.hooview.app.R.styleable.BubbleView);
        init(attributes);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
    }

    private void init(TypedArray attr) {
        mPaint = new Paint();
        mHandler = new MyHandler(this);
        mEmojiCountMap = new LinkedHashMap<String, Integer>();
        mServerLikeImageBitmaps = new ArrayList<String>();
        mLikeDrawableIds = getResources().getIntArray(com.hooview.app.R.array.like_heart_drawable);

        mBorderColor = getResources().getColor(com.hooview.app.R.color.bubble_border);
        mBorderWidth = BORDER_WIDTH;
        mShapeType = TYPE_CIRCLE;
        mShapeColor = getResources().getColor(com.hooview.app.R.color.bubble_internal);
        mMinShapeRadius = MIN_SHAPE_RADIUS;
        mMaxShapeRadius = MAX_SHAPE_RADIUS;

        if (attr != null) {
            src = attr.getDrawable(com.hooview.app.R.styleable.BubbleView_src);
            mBorderColor = attr.getInt(com.hooview.app.R.styleable.BubbleView_border_color, mBorderColor);
            mBorderWidth = attr.getDimensionPixelSize(com.hooview.app.R.styleable.BubbleView_border_width, mBorderWidth);
            mShapeType = attr.getInt(com.hooview.app.R.styleable.BubbleView_shape_type, mShapeType);
            mShapeColor = attr.getColor(com.hooview.app.R.styleable.BubbleView_shape_color, mShapeColor);
            mMaxShapeRadius = attr
                    .getDimensionPixelSize(com.hooview.app.R.styleable.BubbleView_shape_radius, (int) mMaxShapeRadius);
            attr.recycle();
        }

        Bitmap srcBitmap;
        if (src != null) {
            srcBitmap = drawable2Bitmap(src, (int) mMinShapeRadius);
            mShapeBitmap = initShapeBitmap(mShapeType, srcBitmap, 255, mMinShapeRadius);
            srcBitmap.recycle();
        } else {
            //mShapeBitmap = initShapeBitmap(mShapeType, mShapeColor, 255, mMinShapeRadius);
            mShapeBitmap = initLikeBitmap(mShapeType, mShapeIndex, 255, mMinShapeRadius);
        }

        initServerLikeImage();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        mPaint.reset();
        /*Shader shader = new LinearGradient(0, 0, 0, height, new int[] {
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_blue_dark)
        }, null, Shader.TileMode.REPEAT);
        paint.setShader(shader);
        canvas.drawRect(0, 0, width, height, paint);*/
        mBubblesCopy.clear();
        mBubblesCopy.addAll(mBubbles);
        for (Bubble bubble : mBubblesCopy) {
            if (bubble == null) {
                continue;
            }
            if (bubble.getY() - bubble.getSpeedY() <= 0) { // Remove top
                mBubbles.remove(bubble);
                continue;
            } else if (bubble.getX() + bubble.getSpeedX() <= 0) { // Bound Left
                bubble.setSpeedX(-bubble.getSpeedX());
            } else if (bubble.getX() + bubble.getRadius() >= width) { // Bound right
                bubble.setSpeedX(-bubble.getSpeedX());
            }
            int i = mBubbles.indexOf(bubble);
            if (i < 0 || i == mBubbles.size()) {
                continue;
            }

            bubble.setX(bubble.getX() + bubble.getSpeedX());
            if (bubble.getRadius() < mMaxShapeRadius) {
                bubble.setRadius(bubble.getRadius() + RADIUS_SIZE_STEP);
                //bubble.setBitmap(initShapeBitmap(mShapeType, bubble.getColor(), 255, bubble.getRadius()));
                //bubble.setBitmap(resizeBitmap(bubble.getBitmap(), bubble.getRadius()));
                bubble.setBitmap(initLikeBitmap(mShapeType, bubble.getShapeIndex(), 255, bubble.getRadius()));
                mCurrentBubbleScaleNumber ++;
            }
            if (bubble.getRadius() > bubble.getSpeedY() && bubble.getRadius() < mMaxShapeRadius) {
                bubble.setY(bubble.getY() - RADIUS_SIZE_STEP);
            } else {
                bubble.setY(bubble.getY() - bubble.getSpeedY());
            }

            mPaint.setAlpha((int) bubble.getAlpha());
            bubble.setAlpha(bubble.getAlpha() > ALPHA_REDUCE_STEP
                    ? bubble.getAlpha() - ALPHA_REDUCE_STEP
                    : bubble.getAlpha());
            mBubbles.set(i, bubble);

            canvas.drawBitmap(bubble.getBitmap(), bubble.getX(), bubble.getY(), mPaint);
            //canvas.drawCircle(bubble.getX(), bubble.getY(), bubble.getRadius(), paint);
            if (mCurrentBubbleScaleNumber == BUBBLE_MAX_SCALE_NUMBER) {
                mCurrentBubbleScaleNumber = 0;
                break;
            }
        }

        invalidate();
    }

    private Bitmap initLikeBitmap(int shape, int shapeIndex, int alpha, float size) {
        int bound = (int) size;
        Bitmap bitmap = null;
        String bitmapPath;
        Drawable drawable;
        if (mServerLikeImageBitmaps.size() > 0) {
            bitmapPath = mServerLikeImageBitmaps.get(shapeIndex);
            drawable = BitmapDrawable.createFromPath(bitmapPath);
        } else {
            TypedArray typedArray = getResources().obtainTypedArray(com.hooview.app.R.array.like_heart_drawable);
            drawable = getResources().getDrawable(typedArray.getResourceId(shapeIndex, com.hooview.app.R.drawable.heart0));
            typedArray.recycle();
        }
        if (mEmojiCountMap.size() > 0) {
            bitmapPath = "";
            for (Map.Entry<String, Integer> entry : mEmojiCountMap.entrySet()) {
                if (entry.getValue() == 0) {
                    mEmojiCountMap.remove(entry.getKey());
                } else {
                    bitmapPath = entry.getKey();
                    if (size >= mMaxShapeRadius) {
                        entry.setValue(entry.getValue() - 1);
                    }
                }
                break;
            }
            bound += 90;
            drawable = BitmapDrawable.createFromPath(bitmapPath);
        }

        bitmap = Bitmap.createBitmap(bound, bound, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (drawable != null) {
            drawable.setAlpha(alpha);
            drawable.setBounds(0, 0, bound, bound);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    private void initServerLikeImage() {
        String likeImageInfo = Preferences.getInstance(this.getContext())
                .getString(Preferences.KEY_PARAM_SERVER_LIKE_IMAGE_INFO, "");
        if (!TextUtils.isEmpty(likeImageInfo)) {
            if (likeImageInfo.contains("|")) {
                String[] likes = likeImageInfo.split("\\|");
                for (int i = 0; i < likes.length; i++) {
                    checkLikeImageInfo(likes[i]);
                }
            } else {
                checkLikeImageInfo(likeImageInfo);
            }
        }
    }

    private void checkLikeImageInfo(String likeImageInfo) {
        String[] likeInfos = likeImageInfo.split(",");
        if (likeInfos.length < 3) {
            return;
        }
        String startTime = likeInfos[0];
        String endTime = likeInfos[1];
        String likeUrl = likeInfos[2];
        if (DateTimeUtil.isCurrentDateInSpan(startTime, endTime)) {
            String bitmapPath = Utils.getCacheServerLikeImage(likeUrl);
            if (bitmapPath != null) {
                mServerLikeImageBitmaps.add(bitmapPath);
            }
        }
    }

    private Bitmap initShapeBitmap(int shape, int color, int alpha, float size) {
        Bitmap bitmap = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return initShapeBitmap(shape, bitmap, alpha, size);
    }

    private Bitmap initShapeBitmap(int shape, Bitmap srcBitmap, int alpha, float size) {
        BitmapShader bitmapShader = new BitmapShader(srcBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Bitmap bitmap = Bitmap.createBitmap((int) size, (int) size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path path;
        BorderDrawable shapeDrawable = new BorderDrawable(mBorderWidth, mBorderColor);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setShader(bitmapShader);
        shapeDrawable.setBounds(0, 0, (int) size, (int) size);
        shapeDrawable.setAlpha(alpha);
        switch (shape) {
            case TYPE_CIRCLE:
                shapeDrawable.setShape(new OvalShape());
                break;
            case TYPE_STAR:
                path = new Path();
                // The Angle of the pentagram
                float radian = (float) (Math.PI * 36 / 180);
                float radius = size / 2;
                // In the middle of the radius of the pentagon
                float radius_in = (float) (radius * Math.sin(radian / 2) / Math.cos(radian));
                // The starting point of the polygon
                path.moveTo((float) (radius * Math.cos(radian / 2)), 0);
                path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.sin(radian)),
                        (float) (radius - radius * Math.sin(radian / 2)));
                path.lineTo((float) (radius * Math.cos(radian / 2) * 2),
                        (float) (radius - radius * Math.sin(radian / 2)));
                path.lineTo((float) (radius * Math.cos(radian / 2) + radius_in * Math.cos(radian / 2)),
                        (float) (radius + radius_in * Math.sin(radian / 2)));
                path.lineTo((float) (radius * Math.cos(radian / 2) + radius * Math.sin(radian)),
                        (float) (radius + radius * Math.cos(radian)));
                path.lineTo((float) (radius * Math.cos(radian / 2)), radius + radius_in);
                path.lineTo((float) (radius * Math.cos(radian / 2) - radius * Math.sin(radian)),
                        (float) (radius + radius * Math.cos(radian)));
                path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.cos(radian / 2)),
                        (float) (radius + radius_in * Math.sin(radian / 2)));
                path.lineTo(0, (float) (radius - radius * Math.sin(radian / 2)));
                path.lineTo((float) (radius * Math.cos(radian / 2) - radius_in * Math.sin(radian)),
                        (float) (radius - radius * Math.sin(radian / 2)));
                path.close();// Make these points closed polygons
                shapeDrawable.setShape(new PathShape(path, size, size));
                break;
            case TYPE_RHOMBUS:
                path = new Path();
                path.moveTo(size / 2, 0);
                path.lineTo(0, size / 2);
                path.lineTo(size / 2, size);
                path.lineTo(size, size / 2);
                path.close();
                shapeDrawable.setShape(new PathShape(path, size, size));
                break;
            case TYPE_HEART:
                path = new Path();
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(bitmapShader);
                Matrix matrix = new Matrix();
                Region region = new Region();
                RectF ovalRect = new RectF(size / 4, 0, size - (size / 4), size);
                path.addOval(ovalRect, Path.Direction.CW);
                matrix.postRotate(43, size / 2, size / 2);
                path.transform(matrix, path);
                region.setPath(path, new Region((int) size / 2, 0, (int) size, (int) size));
                canvas.drawPath(region.getBoundaryPath(), paint);

                matrix.reset();
                path.reset();
                path.addOval(ovalRect, Path.Direction.CW);
                matrix.postRotate(-43, size / 2, size / 2);
                path.transform(matrix, path);
                region.setPath(path, new Region(0, 0, (int) size / 2, (int) size));
                canvas.drawPath(region.getBoundaryPath(), paint);
                return bitmap;// return <--
                /*path.moveTo(size / 2, size / 5);
                path.quadTo(size, 0, size / 2, size / 1.0f);
                path.quadTo(0, 0, size / 2, size / 5);
                path.close();
                shapeDrawable.setShape(new PathShape(path, size, size));
                break;*/
            case TYPE_TRIANGLE:
                path = new Path();
                path.moveTo(0, 0);
                path.lineTo(size / 2, size);
                path.lineTo(size, 0);
                path.close();
                shapeDrawable.setShape(new PathShape(path, size, size));
                break;
            case TYPE_ORIGINAL:
                break;
            default:
                break;
        }
        shapeDrawable.draw(canvas);
        return bitmap;
    }

    private float degree2Radian(int degree) {
        return (float) (Math.PI * degree / 180);
    }

    private void drawCircleBorder(Canvas canvas, int radius, int color, int borderWidth) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }

    public Bitmap convertToBitmap(String path, int radioSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > radioSize || height > radioSize) {
            scaleWidth = ((float) width) / radioSize;
            scaleHeight = ((float) height) / radioSize;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), radioSize, radioSize, true);
    }

    private Bitmap createBitmap(int color, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDensity(bitmap.getDensity());
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, 0, 0, null);

        return bitmap;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, float radius) {
        Matrix matrix = new Matrix();
        float scale = radius * 2 / bitmap.getWidth();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap drawable2Bitmap(Drawable drawable, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);
        return bitmap;
    }

    private void createBubble() {
        if (!isShown()) {
            return;
        }
        if (mBubbles.size() > MAX_SHAPE_IN_SCREEN) {
            return;
        }

        Bubble bubble = new Bubble();
        int radius = mRandom.nextInt(30);
        while (radius == 0) {
            radius = mRandom.nextInt(30);
        }
        float speedY = (mRandom.nextFloat() + 1) * BASE_SPEED_Y;
        while (speedY < 1) {
            speedY = (mRandom.nextFloat() + 1) * BASE_SPEED_Y;
        }
        //bubble.setRadius(radius);
        bubble.setRadius(mMinShapeRadius);
        bubble.setSpeedY(speedY);
        bubble.setX(width / 2);
        bubble.setY(height);
        float speedX = mRandom.nextFloat() - BASE_SPEED_X * (mRandom.nextFloat() > 0.5 ? -1 : 1);
        while (speedX == 0) {
            speedX = mRandom.nextFloat() - BASE_SPEED_X;
        }
        bubble.setSpeedX(speedX * 2);
        bubble.setAlpha(SHAPE_ALPHA);
        bubble.setBitmap(mShapeBitmap);
        bubble.setColor(mShapeColor);
        bubble.setShapeIndex(mShapeIndex);
        mBubbles.add(bubble);
    }

    private class Bubble {
        private float radius;
        private float speedY;
        private float speedX;
        private float x;
        private float y;
        private float alpha;
        private Bitmap bitmap;
        private int color;
        private int shapeIndex;
        private int type;

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getSpeedY() {
            return speedY;
        }

        public void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        public float getSpeedX() {
            return speedX;
        }

        public void setSpeedX(float speedX) {
            this.speedX = speedX;
        }

        public float getAlpha() {
            return alpha;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getShapeIndex() {
            return shapeIndex;
        }

        public void setShapeIndex(int shapeIndex) {
            this.shapeIndex = shapeIndex;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    private class CreateBubbleRunnable implements Runnable {
        volatile Thread mTheThread = null;
        protected boolean mStarting = false;

        @Override
        public void run() {
            if (mTheThread != Thread.currentThread()) {
                throw new RuntimeException();// 防止外部调用Thread.start方法启动该线程
            }
            while (!Thread.interrupted() && mTheThread != null && mStarting) {
                if (mCreateBubbleDuration <= 0) {
                    mCreateBubbleInterval = INTERVAL_CREATE_BUBBLE_DEFAULT;
                    mTheThread.interrupt();
                }
                if (!isShown()) {
                    break;
                }
                if (mBubbles.size() > MAX_SHAPE_IN_SCREEN) {
                    continue;
                }
                try {
                    //Thread.sleep(mRandom.nextInt(3) * 200);
                    Thread.sleep(mCreateBubbleInterval);
                    mCreateBubbleDuration -= mCreateBubbleInterval;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mTheThread.interrupt();
                }
                Bubble bubble = new Bubble();
                int radius = mRandom.nextInt(30);
                while (radius == 0) {
                    radius = mRandom.nextInt(30);
                }
                float speedY = (mRandom.nextFloat() + 1) * BASE_SPEED_Y;
                while (speedY < 1) {
                    speedY = (mRandom.nextFloat() + 1) * BASE_SPEED_Y;
                }
                //bubble.setRadius(radius);
                bubble.setRadius(mMinShapeRadius);
                bubble.setSpeedY(speedY);
                bubble.setX(width / 2);
                bubble.setY(height);
                float speedX = mRandom.nextFloat() - BASE_SPEED_X * (mRandom.nextFloat() > 0.5 ? -1 : 1);
                while (speedX == 0) {
                    speedX = mRandom.nextFloat() - BASE_SPEED_X;
                }
                bubble.setSpeedX(speedX * 2);
                bubble.setAlpha(SHAPE_ALPHA);
                bubble.setBitmap(mShapeBitmap);
                bubble.setColor(mShapeColor);
                bubble.setShapeIndex(mShapeIndex);
                bubble.setType(mBubbleType);
                mBubbles.add(bubble);
            }
        }

        public void start() {
            if (!isShown()) {
                return;
            }
            if (mTheThread == null || !mTheThread.isAlive()) {
                mTheThread = new Thread(this);
                mTheThread.start();
            }
            mStarting = true;
        }

        public void stop() {
            mStarting = false;
        }

        public void destroy() {
            if (mTheThread != null) {
                mTheThread.interrupt();
                try {
                    mTheThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mTheThread.interrupt();
                }
            }
        }

        public boolean isRunning() {
            return mTheThread != null && mTheThread.isAlive();
        }
    }

    private void createBubbles(int count) {
        mCreateBubbleInterval = INTERVAL_CREATE_BUBBLE_WHEN_ADDED;
        mCreateBubbleDuration += count * INTERVAL_CREATE_BUBBLE_WHEN_ADDED - 15;
        if (!mCreateBubbleThread.isRunning()) {
            mCreateBubbleThread.start();
        }
    }

    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        public boolean onDown(MotionEvent event) {
            Logger.d(TAG, "mouse down" + " " + event.getX() + "," + event.getY());
            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Logger.d(TAG, "onFling, vx: " + velocityX + ", vy: " + velocityY);
            mCreateBubbleThread.stop();
            return false;
        }

        public void onLongPress(MotionEvent event) {
            mCreateBubbleThread.stop();
            Logger.d(TAG, "on long pressed");
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Logger.d(TAG, "onScroll" + " " + distanceX + "," + distanceY);
            return false;
        }

        public void onShowPress(MotionEvent event) {
            Logger.d(TAG, "pressed");
            mCreateBubbleDuration += mCreateBubbleInterval;
            if (!mCreateBubbleThread.isRunning()) {
                mCreateBubbleThread.start();
            }
            mLikeCount++;
        }

        public boolean onSingleTapUp(MotionEvent event) {
            Logger.d(TAG, "Tap up");
            mCreateBubbleThread.stop();
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            return mGestureDetector.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.d(TAG, "onDetachedFromWindow");
        mLikeCount = 0;
        mCreateBubbleThread.destroy();
    }

    public int getLikeCount() {
        return mLikeCount;
    }

    public void setLikeCount(int likeCount) {
        mLikeCount = likeCount;
    }

    public void addEmoji(String giftAni, int showCount) {
        if (TextUtils.isEmpty(giftAni) || showCount == 0) {
            return;
        }
        showCount = showCount > 20 ? 20 : showCount;
        String bitmapPath = Utils.getCacheServerLikeImage(giftAni);
        if (bitmapPath != null) {
            if (!mEmojiCountMap.containsKey(bitmapPath)) {
                mEmojiCountMap.put(bitmapPath, showCount);
            } else {
                mEmojiCountMap.put(bitmapPath, mEmojiCountMap.get(bitmapPath) + showCount);
            }
        }
        mBubbleType = BUBBLE_TYPE_LIKE_EMOJI;
        createBubbles(showCount);
    }

    public void addLikeCount(int likeCount) {
        if (likeCount <= 0) {
            return;
        } else if (likeCount > 40) {
            likeCount = 40;
        }
        createBubbles(likeCount);
        randomShapeIndex();
    }

    public void randomShapeIndex() {
        if (mServerLikeImageBitmaps.size() > 0) {
            mShapeIndex = mRandom.nextInt(mServerLikeImageBitmaps.size());
            mBubbleType = BUBBLE_TYPE_LIKE_CUSTOM;
        }else {
            mShapeIndex = mRandom.nextInt(mLikeDrawableIds.length);
            mBubbleType = BUBBLE_TYPE_LIKE_COMMON;
        }
    }

    public void setSpeedY(float speedY) {
        BASE_SPEED_Y = speedY;
    }

    public void setSpeedX(float speedX) {
        BASE_SPEED_X = speedX;
    }

    public void setSize(float radius) {
        mMaxShapeRadius = radius;
    }
}
