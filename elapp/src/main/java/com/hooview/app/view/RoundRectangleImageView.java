package com.hooview.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hooview.app.R;


/**
 * Created by Steve on 1/20/16.
 *
 * @author Steve
 */
public class RoundRectangleImageView extends ImageView {
    private float topLeftRadius = 0.0f;
    private float topRightRadius = 0.0f;
    private float bottomRightRadius = 0.0f;
    private float bottomLeftRadius = 0.0f;

    public RoundRectangleImageView(Context context) {
        super(context);
    }

    public RoundRectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundRectangleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Method that initializes all fields and sets listeners
     *
     * @param context Context used to create this view
     * @param attrs   Attribute used to initialize fields
     */
    private void init(final Context context, final AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundRectangleImageView);
        topLeftRadius = typedArray.getDimension(R.styleable.RoundRectangleImageView_round_top_left_radius, topLeftRadius);
        topRightRadius = typedArray.getDimension(R.styleable.RoundRectangleImageView_round_top_right_radius, topRightRadius);
        bottomRightRadius = typedArray.getDimension(R.styleable.RoundRectangleImageView_round_bottom_right_radius, bottomRightRadius);
        bottomLeftRadius = typedArray.getDimension(R.styleable.RoundRectangleImageView_round_bottom_left_radius, bottomLeftRadius);

//        content = typedArray.getString(R.styleable.RoundRectangleImageView_content);
//        contentSize = typedArray.getDimension(R.styleable.RoundRectangleImageView_contentSize, contentSize);
//        typedArray.getColor(R.styleable.RoundRectangleImageView_contentColor, context.getResources().getColor(R.color.white));
        typedArray.recycle();

//        textPaint.setColor(contentColor);
//        textPaint.setTextSize(contentSize);
//        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect,
                new float[]{
                        topLeftRadius,
                        topLeftRadius,
                        topRightRadius,
                        topRightRadius,
                        bottomRightRadius,
                        bottomRightRadius,
                        bottomLeftRadius,
                        bottomLeftRadius
                }, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
