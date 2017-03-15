package com.easyvaas.elapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hooview.app.R;


/**
 * Created by guojun on 2016/12/19 23:58.
 */

public class BaseRightDialog extends Dialog {
    public BaseRightDialog(Context context) {
        this(context, 0);
    }

    public BaseRightDialog(Context context, int themeResId) {
        super(context, R.style.MenuDialogRight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = this.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        window.getDecorView().setPadding(0, 0, 0, 0);
        wl.gravity = Gravity.RIGHT;
        wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.onWindowAttributesChanged(wl);
    }
}
