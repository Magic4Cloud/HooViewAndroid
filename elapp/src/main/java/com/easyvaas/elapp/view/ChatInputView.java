package com.easyvaas.elapp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.db.Preferences;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.easyvaas.elapp.utils.Logger;
import com.hooview.app.R;

import butterknife.BindView;

/**
 * 聊天输入框
 */
public class ChatInputView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = ChatInputView.class.getSimpleName();
    public static final String MSG_TYPE_IMPORTANT = "hl";
    public static final String MSG_TYPE_STICK = "st";
    public static final String MSG_TYPE_NORMAL = "nor";

    @BindView(R.id.chat_view_input_panel)
    RelativeLayout mPanelInput;
    @BindView(R.id.chat_view_input_et)
    EditText mInputEt;
    @BindView(R.id.chat_view_gift_iv)
    ImageView mGiftIv;
    @BindView(R.id.chat_view_anchor_panel)
    RelativeLayout mPanelAnchor;
    private RadioGroup mRgMsgType;
    private ImageView mIvImage;
    private String mMsgType = MSG_TYPE_NORMAL;
    private InputMethodManager mInputMethodManager;
    private boolean mIsKeyboardActive;
    private Activity mActivity;
    private RelativeLayout mRlOption;
    private InputViewListener mInputViewListener;
    private KeyboardOnGlobalChangeListener mKeyboardOnGlobalChangeListener;
    private boolean hasOptionBar = true;
    private String mReplyTips;
    private ReplyModel mReplyModel;


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
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_input, this, true);
        mInputEt = (EditText) findViewById(R.id.editText);
        mGiftIv = (ImageView) findViewById(R.id.iv_send);
        mRgMsgType = (RadioGroup) findViewById(R.id.rgMsgType);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mRlOption = (RelativeLayout) findViewById(R.id.rl_option);
        mGiftIv.setEnabled(false);
//        mEditText.setOnClickListener(this);
        mInputEt.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Preferences.getInstance(v.getContext()).isLogin() && EVApplication.isLogin()) {
                        mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
                    } else {
                        LoginActivity.start(getContext());
                    }
                }
                return false;
            }
        });
        mIvImage.setOnClickListener(this);
        mGiftIv.setOnClickListener(this);
        findViewById(R.id.rl_option).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        findViewById(R.id.rl_input).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mRgMsgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.tvImportant) {
                    mMsgType = MSG_TYPE_IMPORTANT;
                } else if (checkedId == R.id.tvNormal) {
                    mMsgType = MSG_TYPE_NORMAL;
                } else if (checkedId == R.id.tvStick) {
                    mMsgType = MSG_TYPE_STICK;
                }
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
                if (s.length() == 0) {
                    mGiftIv.setEnabled(false);
                    mGiftIv.setImageResource(R.drawable.btn_send_n);
                } else {
                    mGiftIv.setEnabled(true);
                    mGiftIv.setImageResource(R.drawable.btn_send_s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        getViewTreeObserver().addOnGlobalLayoutListener(mKeyboardOnGlobalChangeListener = new KeyboardOnGlobalChangeListener());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText:
                if (Preferences.getInstance(v.getContext()).isLogin() && EVApplication.isLogin()) {
                    mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
                } else {
                    LoginActivity.start(getContext());
                }

                break;
            case R.id.iv_image:
                if (mInputViewListener != null) {
                    hideKeyboard();
                    mInputViewListener.onImageButtonClick();
                }
                break;
            case R.id.iv_send:
                if (mInputViewListener != null) {
                    String content = mInputEt.getText().toString().trim();
                    if (!TextUtils.isEmpty(mReplyTips) && mReplyModel != null) {
                        mInputViewListener.onReply(content.replace(mReplyTips, ""), mReplyModel);
                    } else {
                        mInputViewListener.sendMessage(mMsgType, mInputEt.getText().toString().trim());
                    }
                    hideKeyboard();
                    mInputEt.setText("");
                }
                break;
        }
    }

    public void showInput() {
        if (mInputEt.isFocused()) {
            mInputMethodManager.showSoftInput(mInputEt, InputMethodManager.SHOW_FORCED);
        } else {
            InputMethodManager mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mInputManager != null) {
                mInputManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    public void hideKeyboard() {
        if (mActivity.getCurrentFocus() != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            Log.d(TAG, "hideKeyboard: ");

        }
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
//            // 获取当前页面窗口的显示范围
            getWindowVisibleDisplayFrame(mRect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - mRect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
            }
            mIsKeyboardActive = isActive;
            if (isActive) {
                if (hasOptionBar) {
                    mRlOption.setVisibility(VISIBLE);
                } else {
                    mRlOption.setVisibility(GONE);
                }
                setBackground(new ColorDrawable(getResources().getColor(R.color.input_bg)));
            } else {
                mRlOption.setVisibility(GONE);
                setBackground(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            }
        }
    }

    public void setInputViewListener(InputViewListener inputViewListener) {
        this.mInputViewListener = inputViewListener;
    }

    public void hideOptionBar() {
        this.hasOptionBar = false;
        mRlOption.setVisibility(GONE);
    }

    public void replySomebody(String nickname, String content) {
        Logger.d(TAG, "replySomebody: nickname=" + nickname + "   content=" + content);
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(content)) {
            return;
        }
        mReplyModel = new ReplyModel(nickname, content);
        mReplyTips = getResources().getString(R.string.reply_somebody, nickname);
        mInputEt.setText(mReplyTips);
        mInputEt.setSelection(mReplyTips.length());
        showInput();
    }

    public interface InputViewListener {
        public void sendMessage(String msgType, String content);

        public void onImageButtonClick();

        public void onReply(String content, ReplyModel replyModel);
    }

    public class ReplyModel {
        public String replyNickName;
        public String replyContent;

        public ReplyModel(String replyNickName, String replyContent) {
            this.replyNickName = replyNickName;
            this.replyContent = replyContent;
        }
    }

}
