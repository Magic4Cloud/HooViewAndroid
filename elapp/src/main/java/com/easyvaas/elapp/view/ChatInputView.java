package com.easyvaas.elapp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.chat.model.EMMessageWrapper;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date    2017/5/3
 * Author  xiaomao
 * 聊天输入框
 */
public class ChatInputView extends RelativeLayout {

    private static final String TAG = ChatInputView.class.getSimpleName();
    @BindView(R.id.chat_view_input_panel)
    LinearLayout mPanelInput;
    @BindView(R.id.chat_view_input_et)
    EditText mInputEt;
    @BindView(R.id.chat_view_gift_iv)
    ImageView mGiftIv;
    @BindView(R.id.chat_view_send_tv)
    TextView mSendTv;
    private InputMethodManager mInputMethodManager;
    private boolean mIsKeyboardActive;
    private boolean mIsAnchor;
    private Activity mActivity;
    private OnInputListener mOnInputListener;
    private String mReplyTips;
    private ReplyModel mReplyModel;
    private static String MSG_TYPE = EMMessageWrapper.MSG_TYPE_NORMAL;


    public ChatInputView(Context context) {
        this(context, null);
    }

    public ChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mActivity = (Activity) context;
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_chat_input, this, true);
        ButterKnife.bind(this, view);
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
        // input
        mInputEt.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Preferences.getInstance(mActivity).isLogin() && EVApplication.isLogin()) {
                        mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
                    } else {
                        LoginActivity.start(mActivity);
                    }
                }
                return false;
            }
        });
        mInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mReplyTips) && s.length() < mReplyTips.length()) {
                    mReplyTips = "";
                    mReplyModel = null;
                    mInputEt.setText("");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (TextUtils.isEmpty(text)) {
                    mSendTv.setEnabled(false);
                } else {
                    mSendTv.setEnabled(true);
                }
            }
        });
        // input panel
        mPanelInput.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    /**
     * 输入框
     */
    @OnClick(R.id.chat_view_input_et)
    protected void onInputClick() {
        if (Preferences.getInstance(mActivity).isLogin() && EVApplication.isLogin()) {
            mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
        } else {
            LoginActivity.start(mActivity);
        }
    }

    /**
     * 送礼
     */
    @OnClick(R.id.chat_view_gift_iv)
    protected void onGiftClick() {
        if (Preferences.getInstance(mActivity).isLogin() && EVApplication.isLogin()) {
            if (mOnInputListener != null) {
                hideKeyboard();
                mOnInputListener.onSendGift();
            }
        } else {
            LoginActivity.start(mActivity);
        }
    }

    /**
     * 发送消息
     */
    @OnClick(R.id.chat_view_send_tv)
    protected void onSendClick() {
        if (Preferences.getInstance(mActivity).isLogin() && EVApplication.isLogin()) {
            if (mOnInputListener != null) {
                String message = mInputEt.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    if (!TextUtils.isEmpty(mReplyTips) && mReplyModel != null) {
                        mOnInputListener.onReplyMessage(message.replace(mReplyTips, ""), mReplyModel);
                    } else {
                        mOnInputListener.onSendMessage(MSG_TYPE, message);
                    }
                    mInputEt.setText("");
                }
            }
            hideKeyboard();
        } else {
            LoginActivity.start(mActivity);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent   isInputMethodOpen" + mIsKeyboardActive);
        if (mIsKeyboardActive) {
            hideKeyboard();
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 显示输入
     */
    public void showInput() {
        if (mInputEt.isFocused()) {
            mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
        } else {
            InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        if (mActivity.getCurrentFocus() != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int screenHeight = 0;
        Rect rect = new Rect();

        /**
         * 获取屏幕高度
         *
         * @return
         */
        private int getScreenHeight() {
            if (screenHeight > 0) {
                return screenHeight;
            }
            screenHeight = ((WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
            return screenHeight;
        }

        @Override
        public void onGlobalLayout() {
            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            // 软键盘高度
            int keyboardHeight = screenHeight - rect.bottom;
            boolean isActive = false;
            // 超过屏幕四分之一则表示弹出了输入法
            if (Math.abs(keyboardHeight) > screenHeight / 4) {
                isActive = true;
            }
            mIsKeyboardActive = isActive;
            // 发送按钮、礼物按钮
            if (isActive) {
                mGiftIv.setVisibility(GONE);
                mSendTv.setVisibility(VISIBLE);
            } else {
                String text = mInputEt.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    mSendTv.setVisibility(GONE);
                    if (!mIsAnchor) {
                        mGiftIv.setVisibility(VISIBLE);
                    } else {
                        mGiftIv.setVisibility(GONE);
                    }
                } else {
                    mGiftIv.setVisibility(GONE);
                    mSendTv.setVisibility(VISIBLE);
                }
            }
        }
    }

    public void setAnchor(boolean isAnchor) {
        mIsAnchor = isAnchor;
        if (isAnchor) {
            mGiftIv.setVisibility(GONE);
            mInputEt.setHint("大师，加入聊天吧～");
        } else {
            mGiftIv.setVisibility(VISIBLE);
            mInputEt.setHint("加入聊天吧");
        }
        mSendTv.setEnabled(false);
    }

    /**
     * 设置监听
     */
    public void setOnInputListener(OnInputListener listener) {
        mOnInputListener = listener;
    }

    /**
     * 回复某人
     */
    public void replySomebody(String nickname, String message) {
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(message)) {
            return;
        }
        mReplyModel = new ReplyModel(nickname, message);
        mReplyTips = getResources().getString(R.string.reply_somebody, nickname);
        mInputEt.setText(mReplyTips);
        mInputEt.setSelection(mReplyTips.length());
        mInputEt.requestFocus();
        showInput();
    }

    public interface OnInputListener {

        void onSendMessage(String type, String message);

        void onSendGift();

        void onReplyMessage(String message, ReplyModel replyModel);
    }

    public class ReplyModel {
        public String nickname;
        public String message;

        public ReplyModel(String nickname, String message) {
            this.nickname = nickname;
            this.message = message;
        }
    }

}
