package com.easyvaas.common.chat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.common.chat.R;
import com.easyvaas.common.widget.FullScreenDialogHUD;

public class DialogUtil {

    public static final int TYPE_RED_PACK_RECEIVE = 0;
    public static final int TYPE_RED_PACK_RESULT_0 = 1;
    public static final int TYPE_RED_PACK_RESULT_1 = 2;
    public static final int TYPE_RED_PACK_SENDER = 3;

    public static Dialog getProcessDialog(Activity activity, CharSequence message,
            boolean dismissTouchOutside, boolean cancelable) {
        final LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.progress_hud, null);
        Dialog dialog = getCustomDialog(activity, v, dismissTouchOutside, cancelable, -1);
        if (dismissTouchOutside) {
            dialog.setCanceledOnTouchOutside(true);
        } else {
            dialog.setCanceledOnTouchOutside(false);
        }

        ImageView spinner = (ImageView) v.findViewById(R.id.spinnerImageView);
        ((AnimationDrawable) spinner.getBackground()).start();
        TextView messageTv = (TextView) v.findViewById(R.id.message);
        if (TextUtils.isEmpty(message)) {
            messageTv.setVisibility(View.GONE);
        } else {
            messageTv.setText(message);
            messageTv.setVisibility(View.VISIBLE);
        }

        return dialog;
    }

    public static Dialog getCustomDialog(final Activity activity, View view, boolean dismissTouchOutside,
            boolean cancelable, int theme) {
        Dialog dialog = theme > 0 ? new FullScreenDialogHUD(activity, theme)
                : new Dialog(activity, android.R.style.Theme_Dialog);
        //        Dialog dialog = new Dialog(activity, R.style.Dialog_FullScreen);
        dialog.setContentView(view);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        if (!cancelable) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        activity.finish();
                    }
                    return false;
                }
            });
        }
        return dialog;
    }

    public static Dialog getOneButtonDialog(final Activity activity, int resId,
            boolean dismissTouchOutside, boolean cancelable,
            DialogInterface.OnClickListener confirmOnClickListener) {
        return getOneButtonDialog(activity, activity.getString(resId), dismissTouchOutside, cancelable,
                confirmOnClickListener);
    }

    public static Dialog getOneButtonDialog(final Activity activity, String content,
            boolean dismissTouchOutside, boolean cancelable,
            DialogInterface.OnClickListener confirmOnClickListener) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setPositiveButton(R.string.confirm, confirmOnClickListener)
                .setCancelable(cancelable)
                .setMessage(content)
                .create();
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        if (!cancelable) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        activity.finish();
                    }
                    return false;
                }
            });
        }
        return dialog;
    }

    public static Dialog getButtonsDialog(Activity activity, int resId,
            DialogInterface.OnClickListener confirmOnClickListener) {
        return getButtonsDialog(activity, activity.getString(resId), true, true, confirmOnClickListener,
                null);
    }

    public static Dialog getButtonsDialog(Activity activity, String content,
            DialogInterface.OnClickListener confirmOnClickListener) {
        return getButtonsDialog(activity, content, true, true, confirmOnClickListener, null);
    }

    public static Dialog getButtonsDialog(Activity activity, String content, boolean dismissTouchOutside,
            boolean cancelable, DialogInterface.OnClickListener confirmOnClickListener,
            DialogInterface.OnClickListener cancelOnClickListener) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setNegativeButton(R.string.cancel, cancelOnClickListener)
                .setPositiveButton(R.string.confirm, confirmOnClickListener)
                .setCancelable(cancelable)
                .setMessage(content)
                .create();
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        return dialog;
    }

    public static Dialog getButtonsDialog(Activity activity, String content, boolean dismissTouchOutside,
            boolean cancelable, DialogInterface.OnClickListener confirmOnClickListener,
            DialogInterface.OnClickListener cancelOnClickListener, int leftResId, int rightResId) {
        Dialog dialog = new AlertDialog.Builder(activity)
                .setNegativeButton(leftResId, cancelOnClickListener)
                .setPositiveButton(rightResId, confirmOnClickListener)
                .setCancelable(cancelable)
                .setMessage(content)
                .create();
        dialog.setCanceledOnTouchOutside(dismissTouchOutside);
        return dialog;
    }


}
