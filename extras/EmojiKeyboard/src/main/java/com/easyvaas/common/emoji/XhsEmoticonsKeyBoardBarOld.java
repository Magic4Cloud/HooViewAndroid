package com.easyvaas.common.emoji;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

public class XhsEmoticonsKeyBoardBarOld extends AutoHeightLayout
        implements IEmoticonsKeyboard, View.OnClickListener, EmoticonsToolBarView.OnToolBarItemClickListener {

    public static int FUNC_CHILD_VIEW_EMOTICON = 0;
    public static int FUNC_CHILD_VIEW_APPS = 1;
    public int mChildViewPosition = -1;

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;

    private EmoticonsEditText et_chat;
    private RelativeLayout rl_input;
    private LinearLayout ly_foot_func;
    private ImageView btn_face;
    private ImageView btn_multimedia;
    private Button btn_send;
    private CheckBox btn_barrage;
    private int editTextLength;

    public void setFirst2(boolean first2) {
        isFirst2 = first2;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    private boolean isFirst=true;
    private boolean isFirst2=true;

    private KeyBoardBarViewListener mKeyBoardBarViewListener;
    private boolean mIsMultimediaVisibility = false;
    private Context context;

    public static final int MAX_WORD_NUMBER_DEFAULT = 60;
    public static final int MAX_WORD_NUMBER_BARRAGE = 30;
    private int mMaxWordNum = MAX_WORD_NUMBER_DEFAULT;

    public XhsEmoticonsKeyBoardBarOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_keyboardbar_new, this);
        initView();
    }

    private void initView() {
        mEmoticonsPageView = (EmoticonsPageView) findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) findViewById(R.id.view_eiv);

        rl_input = (RelativeLayout) findViewById(R.id.rl_input);
        ly_foot_func = (LinearLayout) findViewById(R.id.ly_foot_func);
        btn_face = (ImageView) findViewById(R.id.btn_face);
        btn_multimedia = (ImageView) findViewById(R.id.btn_multimedia);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_chat = (EmoticonsEditText) findViewById(R.id.et_chat);
        et_chat.setFocusable(true);
        et_chat.setFocusableInTouchMode(true);
        et_chat.requestFocus();
        btn_barrage = (CheckBox) findViewById(R.id.cb_barrage);
        btn_barrage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMaxWordNum = MAX_WORD_NUMBER_BARRAGE;
                    et_chat.setHint(getResources().getString(R.string.barrage_hint_fee));
                    et_chat.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_WORD_NUMBER_BARRAGE) });
                } else {
                    mMaxWordNum = MAX_WORD_NUMBER_DEFAULT;
                    et_chat.setHint(getResources().getString(R.string.barrage_hint_input));
                    et_chat.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_WORD_NUMBER_DEFAULT) });
                }
            }
        });
        setAutoHeightLayoutView(ly_foot_func);
        btn_multimedia.setOnClickListener(this);
        btn_face.setOnClickListener(this);
        btn_send.setOnClickListener(this);

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
        et_chat.setOnTextChangedInterface(new EmoticonsEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                String str = arg0.toString();
                if(!isFirst){
                    isFirst=false;
                    if(editTextLength-1>=str.length()){
                        isFirst=true;
                        clearText();
                    }
                }
                if(isFirst2){
                    isFirst=false;
                    isFirst2=false;
                    editTextLength=str.length();
                }
                if (TextUtils.isEmpty(str)) {
                    if (mIsMultimediaVisibility) {
                        btn_multimedia.setVisibility(VISIBLE);
                        btn_send.setVisibility(GONE);
                    } else {
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg_disable);
                    }
                }
                // -> 发送
                else {
                    if (mIsMultimediaVisibility) {
                        btn_multimedia.setVisibility(GONE);
                        btn_send.setVisibility(VISIBLE);
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg);
                    } else {
                        btn_send.setBackgroundResource(R.drawable.btn_send_bg);
                    }
                }
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            et_chat.setFocusable(true);
            et_chat.setFocusableInTouchMode(true);
            et_chat.requestFocus();
            rl_input.setBackgroundResource(R.drawable.input_bar_bg_active);
        } else {
            et_chat.setFocusable(false);
            et_chat.setFocusableInTouchMode(false);
            rl_input.setBackgroundResource(R.drawable.input_bar_bg_normal);
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
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (ly_foot_func != null && ly_foot_func.isShown()) {
                    hideAutoView();
                    btn_face.setImageResource(R.drawable.icon_face_normal);
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
        if (id == R.id.btn_face) {
            switch (mKeyboardState) {
                case KEYBOARD_STATE_NONE:
                    show(FUNC_CHILD_VIEW_EMOTICON);
                    btn_face.setImageResource(R.drawable.icon_face_pop);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_BOTH:
                    show(FUNC_CHILD_VIEW_EMOTICON);
                    btn_face.setImageResource(R.drawable.icon_face_pop);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if (mChildViewPosition == FUNC_CHILD_VIEW_EMOTICON) {
                        btn_face.setImageResource(R.drawable.icon_face_normal);
                        Utils.openSoftKeyboard(et_chat);
                    } else {
                        show(FUNC_CHILD_VIEW_EMOTICON);
                        btn_face.setImageResource(R.drawable.icon_face_pop);
                    }
                    break;
            }
        } else if (id == R.id.btn_send) {
            if (mKeyBoardBarViewListener != null) {
                mKeyBoardBarViewListener
                        .OnSendBtnClick(et_chat.getText().toString().trim(), btn_barrage.isChecked());
                et_chat.setText("");
            }
        } else if (id == R.id.btn_multimedia) {
            switch (mKeyboardState) {
                case KEYBOARD_STATE_NONE:
                case KEYBOARD_STATE_BOTH:
                    show(FUNC_CHILD_VIEW_APPS);
                    btn_face.setImageResource(R.drawable.icon_face_normal);
                    rl_input.setVisibility(VISIBLE);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_FUNC:
                    btn_face.setImageResource(R.drawable.icon_face_normal);
                    if (mChildViewPosition == FUNC_CHILD_VIEW_APPS) {
                        hideAutoView();
                    } else {
                        show(FUNC_CHILD_VIEW_APPS);
                    }
                    break;
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
    }

    public void showInput() {
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
                btn_face.setImageResource(R.drawable.icon_face_normal);
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

        void OnSendBtnClick(String msg, boolean isBarrage);

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
}
