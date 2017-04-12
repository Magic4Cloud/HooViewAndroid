package com.easyvaas.elapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created on 2017/4/13.
 * Editor Misuzu.
 * Picasso 转换圆图
 */
public class CircleImageTransformation implements Transformation {

    private static final String KEY = "circleImageTransformation";

    @Override
    public Bitmap transform(Bitmap source) {

        int minEdge = Math.min(source.getWidth(), source.getHeight());
        int dx = (source.getWidth() - minEdge) / 2;
        int dy = (source.getHeight() - minEdge) / 2;

        Shader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setTranslate(-dx, -dy);
        shader.setLocalMatrix(matrix);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);

        Bitmap output = Bitmap.createBitmap(minEdge, minEdge, source.getConfig());
        Canvas canvas = new Canvas(output);
        canvas.drawOval(new RectF(0, 0, minEdge, minEdge), paint);

        source.recycle();

        return output;
    }

    @Override
    public String key() {
        return KEY;
    }
}
