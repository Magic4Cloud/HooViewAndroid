package com.easyvaas.common.chat.activity;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.util.ImageUtils;

import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.task.DownloadImageTask;
import com.easyvaas.common.chat.utils.ImageCache;
import com.easyvaas.common.chat.R;

public class AlertDialogActivity extends BaseActivity {
    private int position;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        TextView titleTv = (TextView) findViewById(R.id.title);
        Button cancelBtn = (Button) findViewById(R.id.btn_cancel);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        mEditText = (EditText) findViewById(R.id.edit);

        String msg = getIntent().getStringExtra("msg");
        String title = getIntent().getStringExtra("title");
        position = getIntent().getIntExtra("position", -1);
        boolean isCancelTitle = getIntent().getBooleanExtra("titleIsCancel", false);
        boolean isCancelShow = getIntent().getBooleanExtra("cancel", false);
        boolean isEditTextShow = getIntent().getBooleanExtra("editTextShow", false);
        String path = getIntent().getStringExtra("forwardImage");
        String edit_text = getIntent().getStringExtra("edit_text");

        if (msg != null) {
            ((TextView) findViewById(R.id.alert_message)).setText(msg);
        }
        if (title != null) {
            titleTv.setText(title);
        }
        if (isCancelTitle) {
            titleTv.setVisibility(View.GONE);
        }
        if (isCancelShow) {
            cancelBtn.setVisibility(View.VISIBLE);
        }
        if (path != null) {
            //Large image first
            if (!new File(path).exists())
                path = DownloadImageTask.getThumbnailImagePath(path);
            imageView.setVisibility(View.VISIBLE);
            findViewById(R.id.alert_message).setVisibility(View.GONE);
            if (ImageCache.getInstance().get(path) != null) {
                imageView.setImageBitmap(ImageCache.getInstance().get(path));
            } else {
                Bitmap bm = ImageUtils.decodeScaleImage(path, 150, 150);
                imageView.setImageBitmap(bm);
                ImageCache.getInstance().put(path, bm);
            }

        }
        if (isEditTextShow) {
            mEditText.setVisibility(View.VISIBLE);
            mEditText.setText(edit_text);
        }
    }

    public void ok(View view) {
        Intent intent = new Intent().putExtra("position", position);
        intent.putExtra("edittext", mEditText.getText().toString());
        setResult(RESULT_OK, intent);
        if (position != -1) {
            ChatActivity.resendPos = position;
        }
        finish();
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }
}
