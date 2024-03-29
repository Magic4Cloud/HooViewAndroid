package com.easyvaas.common.sharelogin.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import junit.framework.Assert;

public class ShareUtil {
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(Bitmap bitmap) {
        boolean needRecycle = false;
        if (bitmap == null) {
            return null;
        }
        /*byte[] b = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            b = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;*/

        int i, j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle) {
                bitmap.recycle();
            }
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                bitmap.recycle();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
                bitmap.recycle();
            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }

    public static Bitmap extractThumbNail(final String path, final int height, final int width,
            final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                return null;
            }

            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap
                        .createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width,
                                height);
                if (cropped == null) {
                    return bm;
                }
                bm = cropped;
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            options = null;
        }

        return null;
    }

}
