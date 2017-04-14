/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.utils.blur;

import android.graphics.Bitmap;

interface BlurProcess {
    /**
     * Process the given image, blurring by the supplied radius.
     * If radius is 0, this will return original
     *
     * @param original the bitmap to be blurred
     * @param radius   the radius in pixels to blur the image
     * @return the blurred version of the image.
     */
    public Bitmap blur(Bitmap original, float radius);
}
