/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.net;

import java.util.UUID;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.easyvaas.elapp.utils.Logger;

public class UploadThumbAsyncTask extends AsyncTask<Object, Void, Integer> {
    public static final String TAG = "UploadThumbAsyncTask";

    private static final int STATE_UPLOAD_SUCCESS = 1;
    private static final int STATE_UPLOAD_FAILED = 0;

    private UploadListener uploadListener;

    public UploadThumbAsyncTask() {

    }

    public UploadThumbAsyncTask(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    @Override
    protected Integer doInBackground(Object... params) {
        String fileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + ".jpg";
        String uploadUrl;
        Bitmap uploadBitMap;
        if (params != null && params.length > 1) {
            uploadUrl = (String) params[0];
            uploadBitMap = (Bitmap) params[1];
            ApiHelper.getInstance().uploadThumb(
                    uploadUrl, null, uploadBitMap, fileName, new MyRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Logger.d(TAG, "upload response: " + result);
                        }

                        @Override
                        public void onError(String errorInfo) {
                            super.onError(errorInfo);
                        }

                        @Override
                        public void onFailure(String msg) {
                            RequestUtil.handleRequestFailed(msg);
                        }
                    }
            );
            return STATE_UPLOAD_SUCCESS;
        }
        return STATE_UPLOAD_FAILED;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (uploadListener == null) {
            return;
        }
        if (integer == STATE_UPLOAD_SUCCESS) {
            uploadListener.uploadSuccess();
        } else {
            uploadListener.uploadFailed();
        }
    }

    public interface UploadListener {
        void uploadSuccess();
        void uploadFailed();
    }
}
