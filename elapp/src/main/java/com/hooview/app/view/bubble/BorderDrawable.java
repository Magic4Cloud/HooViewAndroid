/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.view.bubble;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

public class BorderDrawable extends ShapeDrawable{
    private Paint fillPaint;
    private Paint strokePaint;

    public BorderDrawable(float borderWidth, int color) {
        super();
        fillPaint = this.getPaint();
        strokePaint = new Paint(fillPaint);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(borderWidth);
        strokePaint.setColor(color);
    }

    public BorderDrawable(Shape shape, float borderWidth, int color) {
        super(shape);
        fillPaint = this.getPaint();
        strokePaint = new Paint(fillPaint);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(borderWidth);
        strokePaint.setColor(color);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint fillPaint) {
        shape.draw(canvas, fillPaint);
        if (strokePaint.getStrokeWidth() > 0) {
            shape.draw(canvas, strokePaint);
        }
    }

    public void setFillColour(int color){
        fillPaint.setColor(color);
    }

    public void setBorderColor(int color) {
        strokePaint.setColor(color);
    }
}
