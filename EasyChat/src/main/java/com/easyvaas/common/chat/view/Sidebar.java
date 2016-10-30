package com.easyvaas.common.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.util.DensityUtil;

import com.easyvaas.common.chat.adapter.ContactAdapter;
import com.easyvaas.common.chat.utils.ChatLogger;
import com.easyvaas.common.chat.R;

public class Sidebar extends View {
    private static final String TAG = Sidebar.class.getSimpleName();

    private Paint paint;
    private TextView header;
    private float height;
    private ListView mListView;
    private Context context;
    private String[] sections;

    public void setListView(ListView listView) {
        mListView = listView;
    }

    public Sidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        String st = context.getString(R.string.search_new);
        sections = new String[] { st, "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#8C8C8C"));
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(DensityUtil.sp2px(context, 10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndScroll(MotionEvent event) {
        if (mListView == null) {
            //check the mListView to avoid NPE. but the mListView shouldn't be null
            //need to check the call stack later
            return;
        }
        String headerString = sections[sectionForPoint(event.getY())];
        header.setText(headerString);
        ContactAdapter contactAdapter;
        if (mListView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) mListView.getAdapter();
            contactAdapter = (ContactAdapter) headerViewListAdapter.getWrappedAdapter();
        } else {
            contactAdapter = (ContactAdapter) mListView.getAdapter();
        }
        String[] adapterSections = (String[]) contactAdapter.getSections();
        try {
            for (int i = adapterSections.length - 1; i > -1; i--) {
                if (adapterSections[i].equals(headerString)) {
                    mListView.setSelection(contactAdapter.getPositionForSection(i));
                    break;
                }
            }
        } catch (Exception e) {
            ChatLogger.e(TAG, "setHeaderTextAndScroll", e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (header == null) {
                    header = (TextView) ((View) getParent()).findViewById(R.id.floating_header_tv);
                }
                setHeaderTextAndScroll(event);
                header.setVisibility(View.VISIBLE);
                setBackgroundResource(R.drawable.sidebar_background_pressed);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                setHeaderTextAndScroll(event);
                return true;
            }
            case MotionEvent.ACTION_UP:
                header.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                header.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }

}
