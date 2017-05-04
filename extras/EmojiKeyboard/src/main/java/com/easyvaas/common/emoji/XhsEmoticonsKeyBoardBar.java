package com.easyvaas.common.emoji;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyvaas.common.emoji.bean.EmoticonBean;
import com.easyvaas.common.emoji.utils.EmoticonsKeyboardBuilder;
import com.easyvaas.common.emoji.utils.FaceConversionUtil;
import com.easyvaas.common.emoji.utils.Utils;
import com.easyvaas.common.emoji.view.AutoHeightLayout;
import com.easyvaas.common.emoji.view.EmoticonsEditText;
import com.easyvaas.common.emoji.view.EmoticonsIndicatorView;
import com.easyvaas.common.emoji.view.EmoticonsPageView;
import com.easyvaas.common.emoji.view.EmoticonsToolBarView;
import com.easyvaas.common.emoji.view.I.IEmoticonsKeyboard;
import com.easyvaas.common.emoji.view.I.IView;

import static com.easyvaas.common.emoji.R.id.btn_send;


public class XhsEmoticonsKeyBoardBar extends AutoHeightLayout
        implements IEmoticonsKeyboard, View.OnClickListener, EmoticonsToolBarView.OnToolBarItemClickListener {
    private static final String TAG = "XhsEmoticonsKeyBoardBar";
    public static int FUNC_CHILD_VIEW_EMOTICON = 0;
    public static int FUNC_CHILD_VIEW_APPS = 1;
    public int mChildViewPosition = -1;
    public boolean isSearch = false;

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;

    private EmoticonsEditText et_chat;
    private LinearLayout ly_foot_func;
    private int editTextLength;

    public void setFirst2(boolean first2) {
        isFirst2 = first2;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    private boolean isFirst = true;
    private boolean isFirst2 = true;

    private KeyBoardBarViewListener mKeyBoardBarViewListener;
    private boolean mIsMultimediaVisibility = false;
    private Context context;

    public static final int MAX_WORD_NUMBER_DEFAULT = 140;
    public static final int MAX_WORD_NUMBER_BARRAGE = 30;
    private int mMaxWordNum = MAX_WORD_NUMBER_DEFAULT;

    public XhsEmoticonsKeyBoardBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_keyboardbar_new, this);
        initView();
    }

    private void initView() {
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
        mEmoticonsPageView = (EmoticonsPageView) findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) findViewById(R.id.view_eiv);
        ly_foot_func = (LinearLayout) findViewById(R.id.ly_foot_func);
        et_chat = (EmoticonsEditText) findViewById(R.id.bottom_edittext);
        et_chat.setFocusable(true);
        et_chat.setFocusableInTouchMode(true);
        et_chat.requestFocus();
        setAutoHeightLayoutView(ly_foot_func);

        et_chat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener
                            .OnSendBtnClick(et_chat.getText().toString().trim(), isSearch);
                    et_chat.setText("");
                }}
                return false;
            }
        });

        mEmoticonsPageView.setOnIndicatorListener(new EmoticonsPageView.OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
            }
        });

        mEmoticonsPageView.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (et_chat != null) {
                    // 删除
                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }
                    // 用户自定义
                    else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    SpannableString spannableString = FaceConversionUtil.getInstace()
                            .addFace(getContext(), bean.getIconUri(), bean.getContent());
                    int index = et_chat.getSelectionStart();
                    if (spannableString.length() + et_chat.getText().toString().length() <= mMaxWordNum) {
                        Editable editable = et_chat.getEditableText();
                        if (index < 0) {
                            editable.append(spannableString);
                        } else {
                            editable.insert(index, spannableString);
                        }
                    }
                }
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
            }
        });

        et_chat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!et_chat.isFocused()) {
                    et_chat.setFocusable(true);
                    et_chat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        et_chat.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
        et_chat.setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mKeyBoardBarViewListener != null) {
                            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
                        }
                    }
                });
            }
        });

    }

    private void setEditableState(boolean b) {
        if (b) {
            et_chat.setFocusable(true);
            et_chat.setFocusableInTouchMode(true);
            et_chat.requestFocus();
//            rl_input.setBackgroundResource(R.drawable.input_bar_bg_active);
        } else {
            et_chat.setFocusable(false);
            et_chat.setFocusableInTouchMode(false);
//            rl_input.setBackgroundResource(R.drawable.input_bar_bg_normal);
        }
    }

    public void del() {
        if (et_chat != null) {
            int action = KeyEvent.ACTION_DOWN;
            int code = KeyEvent.KEYCODE_DEL;
            KeyEvent event = new KeyEvent(action, code);
            et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        }
    }

    @Override
    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        mEmoticonsPageView.setBuilder(builder);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: " + event.getKeyCode());
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (findViewById(R.id.emotion_key_board_br_ll).isShown()) {
                    hideAutoView();
                    hideInput();
                    return true;
                } else {

                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == btn_send) {
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener
                        .OnSendBtnClick(et_chat.getText().toString().trim(), isSearch);
                et_chat.setText("");
            }
        }
    }

    public void add(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ly_foot_func.addView(view, params);
    }

    public void show(int position) {
        int childCount = ly_foot_func.getChildCount();
        if (position < childCount) {
            for (int i = 0; i < childCount; i++) {
                if (i == position) {
                    ly_foot_func.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition = i;
                } else {
                    ly_foot_func.getChildAt(i).setVisibility(GONE);
                }
            }
        }
        post(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
                }
            }
        });
    }

    public void hideInput() {
        findViewById(R.id.emotion_key_board_br_ll).setVisibility(View.GONE);
        Utils.closeSoftKeyboard(context);
        if (mKeyBoardBarViewListener != null) {
            mKeyBoardBarViewListener.onKeyBoardHide();
        }
    }

    public void showInput(boolean isSearch) {
        this.isSearch = isSearch;
        if (isSearch) {
            et_chat.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            et_chat.setHint(R.string.search_prompt);
        } else {
            et_chat.setImeOptions(EditorInfo.IME_ACTION_SEND);
            et_chat.setHint(R.string.barrage_hint_input);

        }
        findViewById(R.id.emotion_key_board_br_ll).setVisibility(View.VISIBLE);
        Utils.openSoftKeyboard(et_chat);
    }

    public void setInputMaxLength(int maxLength) {
        mMaxWordNum = maxLength;
        et_chat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public boolean isInputShow() {
        return findViewById(R.id.emotion_key_board_br_ll).isShown();
    }

    public void setHint(String hint) {
        et_chat.setHint(hint);
    }

    public void clearText() {
        et_chat.setText("");
    }

    @Override
    public void OnSoftPop(final int height) {
        super.OnSoftPop(height);
        post(new Runnable() {
            @Override
            public void run() {
                if (mKeyBoardBarViewListener != null) {
                    mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
                }
            }
        });
    }

    @Override
    public void OnSoftClose(int height) {
        super.OnSoftClose(height);
        if (mKeyBoardBarViewListener != null) {
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
        }
    }

    @Override
    public void OnSoftChangeHeight(int height) {
        super.OnSoftChangeHeight(height);
        if (mKeyBoardBarViewListener != null) {
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, height);
        }
    }

    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) {
        this.mKeyBoardBarViewListener = l;
    }

    @Override
    public void onToolBarItemClick(int position) {

    }

    public interface KeyBoardBarViewListener {
        void OnKeyBoardStateChange(int state, int height);

        void OnSendBtnClick(String msg, boolean isSearch);

        void onKeyBoardHide();
    }

    public void setTextListener(TextListener textListener) {
        String text = textListener.setTextString();
        int index = et_chat.getSelectionStart();
        Editable editable = et_chat.getEditableText();
        if (index < 0) {
            editable.append(text);
        } else {
            editable.insert(index, text);
        }
    }

    public interface TextListener {
        String setTextString();
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
            if (isActive) {
                ly_foot_func.setVisibility(VISIBLE);
            } else {
                ly_foot_func.setVisibility(GONE);
            }
        }
    }
}
