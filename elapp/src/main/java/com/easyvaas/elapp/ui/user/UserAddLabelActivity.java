package com.easyvaas.elapp.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyvaas.elapp.base.BaseActivity;
import com.easyvaas.elapp.bean.user.BaseUserEntity;
import com.easyvaas.elapp.net.ApiHelper;
import com.easyvaas.elapp.net.MyRequestCallBack;
import com.easyvaas.elapp.utils.Constants;
import com.easyvaas.elapp.utils.SingleToast;
import com.easyvaas.elapp.view.flowlayout.FlowLayout;
import com.hooview.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghai on 2017/3/18.
 * 用户添加标签页面（我的标签）
 */

public class UserAddLabelActivity extends BaseActivity implements View.OnClickListener{

    private FlowLayout labelAllFl;
    private FlowLayout labelSelectedFl;
    private TextView labelCount;
    private ImageView closeIv;
    private TextView  addOptionIv;

    private List<BaseUserEntity.TagsEntity> addList = new ArrayList<>();
    private List<TextView> selectedTextList = new ArrayList<>();

    public static void startActivityForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, UserAddLabelActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_label);
        initViews();
    }

    private void initViews() {
        labelCount = (TextView) findViewById(R.id.label_count);
        labelAllFl = (FlowLayout) findViewById(R.id.label_all_fl);
        labelSelectedFl = (FlowLayout) findViewById(R.id.label_selected_fl);
        closeIv = (ImageView) findViewById(R.id.close_iv);
        addOptionIv = (TextView) findViewById(R.id.add_option_iv);
        addOptionIv.setVisibility(View.VISIBLE);
        addOptionIv.setText("完成");
        closeIv.setOnClickListener(this);
        findViewById(R.id.add_option_view).setOnClickListener(this);
        getList();
    }

    private void addClickLabel(final BaseUserEntity.TagsEntity entity) {
        final TextView textView = getTextView(true);
        textView.setText(entity.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getTag() == null) {
                    if (selectedTextList.size() == 3) {
                        SingleToast.show(UserAddLabelActivity.this, "最多选3个标签哦~");
                        return;
                    }
                    textView.setTag(1);
                    textView.setTextColor(ContextCompat.getColor(UserAddLabelActivity.this, R.color.base_text_white));
                    textView.setBackgroundResource(R.drawable.bg_user_add_tag_selected);
                    addLabel(entity);
                } else {
                    textView.setTag(null);
                    textView.setTextColor(ContextCompat.getColor(UserAddLabelActivity.this, R.color.base_gray));
                    textView.setBackgroundResource(R.drawable.bg_user_add_tag);
                    removeLabel(entity.getId());
                }
            }
        });
        labelAllFl.addView(textView);
    }

    private void addLabel(final BaseUserEntity.TagsEntity entity) {
        TextView textView = getTextView(false);
        textView.setText(entity.getName());
        textView.setTag(entity.getId());
        labelSelectedFl.addView(textView);
        selectedTextList.add(textView);
        updateCount(selectedTextList.size());
    }

    private void removeLabel(int id) {
        for (int i = 0; i < selectedTextList.size(); i++) {
            TextView textView = selectedTextList.get(i);
            if (textView.getTag() != null && (Integer) textView.getTag() == id) {
                labelSelectedFl.removeView(textView);
                selectedTextList.remove(textView);
                break;
            }
        }
        updateCount(selectedTextList.size());
    }

    private TextView getTextView(boolean isClick) {
        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.layout_use_add_tag, null);
        if (isClick) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.base_gray));
            textView.setBackgroundResource(R.drawable.bg_user_add_tag);
        }
        //        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.rightMargin = (int) ViewUtil.dp2Px(getContext(), 12);
//        layoutParams.topMargin = (int) ViewUtil.dp2Px(getContext(), 16);
//        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private void updateCount(int count){
        labelCount.setText(count+"");
    }

    private void getList() {
        ApiHelper.getInstance().getTagList(new MyRequestCallBack<BaseUserEntity>() {
            @Override
            public void onSuccess(BaseUserEntity result) {
                if (result != null && result.getTags() != null && result.getTags().size() > 0) {
                    addList.clear();
                    addList.addAll(result.getTags());
                    for (int j = 0; j < addList.size(); j++) {
                        addClickLabel(addList.get(j));
                    }
                }
            }

            @Override
            public void onError(String errorInfo) {
                super.onError(errorInfo);
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    private void saveLabel(){
        if (selectedTextList.size() > 0){
            final List<String> entities = new ArrayList<>();
            String stringList = "";
            for (int i = 0; i < selectedTextList.size(); i++){
                stringList = stringList + (Integer) selectedTextList.get(i).getTag() + (i == selectedTextList.size() - 1 ? "" : ",");
                entities.add(selectedTextList.get(i).getText().toString());
            }

            ApiHelper.getInstance().saveTagList(stringList, new MyRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(Constants.EXTRA_ADD_LABEL, (ArrayList<String>) entities);
                    UserAddLabelActivity.this.setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onError(String errorInfo) {
                    super.onError(errorInfo);
                }

                @Override
                public void onFailure(String msg) {
                    int i =0;

                }
            });
        } else {
            SingleToast.show(this, "请至少选择一个标签~");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_iv:
                finish();
                break;
            case R.id.add_option_view:
                saveLabel();
                break;
        }
    }
}
