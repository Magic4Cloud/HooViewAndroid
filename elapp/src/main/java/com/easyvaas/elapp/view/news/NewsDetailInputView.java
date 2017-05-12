package com.easyvaas.elapp.view.news;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.bean.news.NewsDetailModel;
import com.easyvaas.elapp.ui.user.LoginActivity;
import com.hooview.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date   2017/5/10
 * Editor  Misuzu
 * 新闻详情底部操作和输入栏
 */

public class NewsDetailInputView extends FrameLayout {

    @BindView(R.id.tv_news_comment)
    ImageView mTvNewsComment;
    @BindView(R.id.tv_news_comment_count)
    TextView mTvNewsCommentCount;
    @BindView(R.id.tv_news_share)
    ImageView mTvNewsShare;
    @BindView(R.id.tv_news_collect)
    ImageView mTvNewsCollect;
    @BindView(R.id.iv_news_back)
    ImageView mIvNewsBack;
    @BindView(R.id.tv_news_comment_hint)
    TextView mTvNewsCommentHint;
    @BindView(R.id.bottom_layout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.bottom_edittext)
    EditText mBottomEdittext;
    @BindView(R.id.bottom_send_button)
    TextView mBottomSendButton;
    @BindView(R.id.news_detail_comment_layout)
    LinearLayout mNewsDetailCommentLayout;
    NewsDetailBottomListener mNewsDetailBottomListener;

    public NewsDetailInputView(@NonNull Context context) {
        super(context);
        initView();
    }

    public NewsDetailInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.news_detail_input_layout, this);
        ButterKnife.bind(this, this);
        setListener();
    }

    /**
     * 初始化收藏状态
     */
    public void initCollectStatus(NewsDetailModel data)
    {
        if (data.getFavorite() == 0)
            mTvNewsCollect.setSelected(false);
        else
            mTvNewsCollect.setSelected(true);
        mTvNewsCommentCount.setText(String.valueOf(data.getPostCount()));
    }

    /**
     * 评论+1
     */
    public void setCommentAdd()
    {
        mTvNewsCommentCount.setText(String.valueOf(Integer.parseInt(mTvNewsCommentCount.getText().toString())+1));
    }

    /**
     * 设置操作监听
     */
    public void setNewsDetailBottomListener(NewsDetailBottomListener newsDetailBottomListener) {
        mNewsDetailBottomListener = newsDetailBottomListener;
        if (mNewsDetailBottomListener instanceof  Activity)
        {
            Activity activity = (Activity) mNewsDetailBottomListener;
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            View view = content.getChildAt(0);
            view.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
        }
    }

    /**
     * View 点击事件
     */
    @OnClick({R.id.tv_news_comment, R.id.tv_news_comment_count, R.id.tv_news_share, R.id.tv_news_collect, R.id.tv_news_comment_hint, R.id.bottom_send_button})
    public void onViewClicked(View view) {
        if (mNewsDetailBottomListener != null) {
            if (EVApplication.isLogin()) {
                switch (view.getId()) {
                    case R.id.tv_news_comment:
                    case R.id.tv_news_comment_count:
                        mNewsDetailBottomListener.showComment();  // 跳转评论
                        break;
                    case R.id.tv_news_share:
                        mNewsDetailBottomListener.showShare();  // 跳转分享
                        break;
                    case R.id.tv_news_collect:
                        mNewsDetailBottomListener.collectClick(mTvNewsCollect.isSelected()); //点击收藏
                        break;
                    case R.id.tv_news_comment_hint:  //显示评论框
                        mNewsDetailCommentLayout.setVisibility(VISIBLE);
                        mBottomEdittext.postDelayed(new Runnable() {//给他个延迟时间
                            @Override
                            public void run() {
                                mBottomEdittext.requestFocus();
                                toggleKeyBoard();
                            }
                        }, 100);
                        break;
                    case R.id.bottom_send_button:
                        mNewsDetailBottomListener.sendComment(mBottomEdittext.getText().toString()); // 发送评论
                        mBottomEdittext.setText("");
                        toggleKeyBoard();
                        break;
                }
            }else
                LoginActivity.start(getContext());
        }
    }

    /**
     * 底部操作栏接口
     */
    public interface NewsDetailBottomListener {

        /**
         * 跳转评论
         */
        public void showComment();

        /**
         * 跳转分享
         */
        public void showShare();

        /**
         * 点击收藏
         */
        public void collectClick(boolean isSelected);

        /**
         * 关闭界面
         */
        public void backFinish();

        /**
         * 发送评论
         */
        public void sendComment(String msg);

    }

    /**
     * 显示或者隐藏输入法
     */
    public void toggleKeyBoard()
    {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setListener()
    {
        // 软键盘 发送监听
        mBottomEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND )
                    if (mNewsDetailBottomListener != null) {
                        if (EVApplication.isLogin())
                        {
                            mNewsDetailBottomListener.sendComment(mBottomEdittext.getText().toString());
                            mBottomEdittext.setText("");
                        } else
                            LoginActivity.start(getContext());
                    }
                return false;
            }
        });
       // 输入监听
        mBottomEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s))
                    mBottomSendButton.setEnabled(false);
                else
                    mBottomSendButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //关闭界面
        mIvNewsBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewsDetailBottomListener != null)
                    mNewsDetailBottomListener.backFinish();
            }
        });
    }

    public ImageView getTvNewsCollect() {
        return mTvNewsCollect;
    }

    /**
     * 全局Layout监听
     */
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
                if (isActive)
                    mNewsDetailCommentLayout.setVisibility(VISIBLE);
                else
                    mNewsDetailCommentLayout.setVisibility(GONE);
        }
    }

}
