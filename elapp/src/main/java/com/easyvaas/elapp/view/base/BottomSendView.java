package com.easyvaas.elapp.view.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/5/3
 * Editor  Misuzu
 * 底部发送栏
 */

public class BottomSendView extends LinearLayout {

    public static final int TYPE_CHAT = 0; //聊天

    public static final int TYPE_COMMENT = 1; // 评论

    public static final int TYPE_SEARCH = 2; //搜索股票

    public static final int TYPE_NONE = 3; //隐藏

    @BindView(R.id.bottom_edittext)
    EditText mBottomEdittext;

    @BindView(R.id.bottom_gift)
    ImageView mBottomGift;

    OnBottomInputListener mOnBottomInputListener;

    private int type;

    public BottomSendView(Context context) {
        super(context);
        initView(context);
    }

    public BottomSendView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.bottom_send_layout, this);
        ButterKnife.bind(this, this);
        mBottomEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH)
                    if (mOnBottomInputListener != null) {
                        if (EVApplication.isLogin())
                        {
                            mOnBottomInputListener.sendText(mBottomEdittext.getText().toString(), type); // 根据不同type 发送输入内容
                            if (type != TYPE_SEARCH)
                                mBottomEdittext.setText("");
                        } else
                            LoginActivity.start(getContext());
                    }
                return false;
            }
        });

        mBottomGift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBottomInputListener != null)
                    if (EVApplication.isLogin())
                        mOnBottomInputListener.openGiftWindow();  //打开礼物窗口
                    else
                        LoginActivity.start(getContext());
            }
        });



    }

    public void setOnBottomInputListener(OnBottomInputListener onBottomInputListener) {
        mOnBottomInputListener = onBottomInputListener;
        Activity activity = (Activity) mOnBottomInputListener;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        View view = content.getChildAt(0);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
    }

    /**
     * 设置类型
     */
    public void setType(int type) {
        this.type = type;
        mBottomEdittext.setText("");
        switch (type) {
            case TYPE_CHAT:
                mBottomEdittext.setHint(getContext().getString(R.string.video_input_chat));
                mBottomGift.setVisibility(VISIBLE);
                setVisibility(VISIBLE);
                break;
            case TYPE_COMMENT:
                mBottomEdittext.setHint(getContext().getString(R.string.video_input_comment));
                mBottomGift.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case TYPE_SEARCH:
                mBottomEdittext.setHint(getContext().getString(R.string.please_enter_code));
                mBottomGift.setVisibility(GONE);
                setVisibility(VISIBLE);
                break;
            case TYPE_NONE:
                setVisibility(GONE);
                break;
        }

    }

    /**
     * 输出 输入结果
     */
    public interface OnBottomInputListener {

        public void sendText(String inputString, int type);

        public void openGiftWindow();

    }


    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int mScreenHeight = 0;
        Rect mRect = new Rect();

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(mRect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - mRect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
            }
           if (type == TYPE_CHAT)
           {
               if (isActive)
                   mBottomGift.setVisibility(GONE);
               else
                   mBottomGift.setVisibility(VISIBLE);
           }
        }
    }

}
