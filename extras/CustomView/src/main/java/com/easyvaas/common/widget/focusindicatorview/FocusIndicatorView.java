package com.easyvaas.common.widget.focusindicatorview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.easyvaas.common.widget.R;

public class FocusIndicatorView extends View implements FocusIndicator {
    public FocusIndicatorView(Context context) {
        super(context);
    }

    public FocusIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable (int resid) {
        setBackgroundDrawable(getResources().getDrawable(resid));
    }

    @Override
    public void showStart() {
        setDrawable(R.drawable.focusicon);
    }

    @Override
    public void showSuccess() {
        setDrawable(R.drawable.focusicon_success);
    }

    @Override
    public void showFail() {
        setDrawable(R.drawable.focusicon_failure);
    }

    @Override
    public void clear() {
        setBackgroundDrawable(null);
    }
}
