package com.easyvaas.common.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.easemob.chat.EMMessage;

import com.easyvaas.common.chat.base.BaseActivity;
import com.easyvaas.common.chat.R;

public class ContextMenuActivity extends BaseActivity {
    public static final String EXTRA_KEY_POSITION = "position";
    public static final String EXTRA_KEY_TYPE = "type";
    public static final String EXTRA_KEY_SYSTEM_TYPE = "system_type";
    public static final int SYSTEM_TYPE = 7;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getIntent().getIntExtra(EXTRA_KEY_POSITION, -1);
        int type = getIntent().getIntExtra(EXTRA_KEY_TYPE, -1);
        int systemType = getIntent().getIntExtra(EXTRA_KEY_SYSTEM_TYPE, -1);

        if (type == EMMessage.Type.TXT.ordinal()) {
            setContentView(R.layout.activity_context_menu_for_text);
            if (systemType == SYSTEM_TYPE) {
                findViewById(R.id.delete_tv).setVisibility(View.GONE);
                findViewById(R.id.delete_line).setVisibility(View.GONE);
            }
        } else if (type == EMMessage.Type.LOCATION.ordinal()) {
            setContentView(R.layout.activity_context_menu_for_location);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            setContentView(R.layout.activity_context_menu_for_image);
        } else if (type == EMMessage.Type.VOICE.ordinal()) {
            setContentView(R.layout.activity_context_menu_for_voice);
        } else if (type == EMMessage.Type.VIDEO.ordinal()) {
            setContentView(R.layout.activity_context_menu_for_video);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void copy(View view) {
        setResult(ChatActivity.RESULT_CODE_COPY, new Intent().putExtra(EXTRA_KEY_POSITION, position));
        finish();
    }

    public void delete(View view) {
        setResult(ChatActivity.RESULT_CODE_DELETE, new Intent().putExtra(EXTRA_KEY_POSITION, position));
        finish();
    }

    public void forward(View view) {
        setResult(ChatActivity.RESULT_CODE_FORWARD, new Intent().putExtra(EXTRA_KEY_POSITION, position));
        finish();
    }

    public void open(View v) {
        setResult(ChatActivity.RESULT_CODE_OPEN, new Intent().putExtra(EXTRA_KEY_POSITION, position));
        finish();
    }

}
