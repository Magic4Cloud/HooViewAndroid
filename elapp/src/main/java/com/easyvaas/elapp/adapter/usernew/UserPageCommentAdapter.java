package com.easyvaas.elapp.adapter.usernew;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.easyvaas.common.widget.MyUserPhoto;
import com.easyvaas.elapp.ui.base.mybase.MyBaseAdapter;
import com.hooview.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date   2017/4/24
 * Editor  Misuzu
 * 用户主页评论
 */

public class UserPageCommentAdapter extends MyBaseAdapter {

    public UserPageCommentAdapter(List data) {
        super(data);
    }

    @Override
    protected int getItemViewByType(int position) {
        return 0;
    }

    @Override
    protected BaseViewHolder OnCreateViewByHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void initOnItemClickListener() {

    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {

    }

    public class UserCommentViewHolder extends BaseViewHolder {

        @BindView(R.id.user_comment_icon)
        ImageView mUserCommentIcon;
        @BindView(R.id.user_comment_target_text)
        TextView mUserCommentTargetText;
        @BindView(R.id.user_comment_target_bg_layout)
        LinearLayout mUserCommentTargetBgLayout;
        @BindView(R.id.user_comment_avator)
        MyUserPhoto mUserCommentAvator;
        @BindView(R.id.user_comment_name)
        TextView mUserCommentName;
        @BindView(R.id.user_comment_praise_counts)
        TextView mUserCommentPraiseCounts;
        @BindView(R.id.user_comment_praise_icon)
        ImageView mUserCommentPraiseIcon;
        @BindView(R.id.user_comment_content)
        TextView mUserCommentContent;
        @BindView(R.id.user_comment_time)
        TextView mUserCommentTime;

        public UserCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

}
