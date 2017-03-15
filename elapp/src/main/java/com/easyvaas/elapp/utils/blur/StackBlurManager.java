/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */


package com.easyvaas.elapp.utils.blur;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;

public class StackBlurManager {
	static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
	static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

	private static volatile boolean hasRS = true;

	/**
	 * Original image
	 */
	private final Bitmap _image;

	/**
	 * Most recent result of blurring
	 */
	private Bitmap _result;

	/**
	 * Method of blurring
	 */
	private final BlurProcess _blurProcess;

	/**
	 * Constructor method (basic initialization and construction of the pixel array)
	 * @param image The image that will be analyed
	 */
	public StackBlurManager(Bitmap image) {
		_image = image;
		_blurProcess = new JavaBlurProcess();
	}

	/**
	 * Process the image on the given radius. Radius must be at least 1
	 * @param radius
	 */
	public Bitmap process(int radius) {
		_result = _blurProcess.blur(_image, radius);
		return _result;
	}

	/**
	 * Returns the blurred image as a bitmap
	 * @return blurred image
	 */
	public Bitmap returnBlurredImage() {
		return _result;
	}

	/**
	 * Save the image into the file system
	 * @param path The path where to save the image
	 */
	public void saveIntoFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			_result.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the original image as a bitmap
	 * @return the original bitmap image
	 */
	public Bitmap getImage() {
		return this._image;
	}
}
