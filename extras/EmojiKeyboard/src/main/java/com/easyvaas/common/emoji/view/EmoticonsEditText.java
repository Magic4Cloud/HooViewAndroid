package com.easyvaas.common.emoji.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EmoticonsEditText extends EditText {


    public EmoticonsEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EmoticonsEditText(Context context) {
        super(context);
    }

    public EmoticonsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onTextChanged(CharSequence arg0, int start, int lengthBefore, int after) {
        super.onTextChanged(arg0, start, lengthBefore, after);
        if (onTextChangedInterface != null) {
            onTextChangedInterface.onTextChanged(arg0);
        }
    }

    public interface OnTextChangedInterface {
        void onTextChanged(CharSequence argo);
    }

    OnTextChangedInterface onTextChangedInterface;

    public void setOnTextChangedInterface(OnTextChangedInterface i) {
        onTextChangedInterface = i;
    }


    public interface OnSizeChangedListener {
        void onSizeChanged();
    }

    OnSizeChangedListener onSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener i) {
        onSizeChangedListener = i;
    }
}
