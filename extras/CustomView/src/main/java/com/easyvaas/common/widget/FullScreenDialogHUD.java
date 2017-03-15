package com.easyvaas.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class FullScreenDialogHUD extends Dialog {
    private Context context;
    private OnClickListener mConfirmClickListener;

    public FullScreenDialogHUD(Context context) {
        super(context);
    }

    public FullScreenDialogHUD(Context context, int theme) {
        super(context, theme);
        setOwnerActivity((Activity) context);
        this.context = context;
    }
}
