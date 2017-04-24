package com.easyvaas.elapp.ui.user.newuser.fragment;

import com.easyvaas.elapp.adapter.news.NormalNewsAdapter;
import com.easyvaas.elapp.ui.base.mybase.MyBaseListFragment;

/**
 * Date   2017/4/24
 * Editor  Misuzu
 * 普通用户主页 评论列表
 */

public class UserPageCommentFragment extends MyBaseListFragment<NormalNewsAdapter> {

    @Override
    protected NormalNewsAdapter initAdapter() {
        return null;
    }

    @Override
    protected void changeRecyclerView() {
        setPaddingTop(4);
    }

    @Override
    protected void getListData(Boolean isLoadMore) {

    }

}
