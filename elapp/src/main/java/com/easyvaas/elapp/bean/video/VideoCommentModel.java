package com.easyvaas.elapp.bean.video;

import com.easyvaas.elapp.bean.BaseListBean;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;

import java.util.List;

/**
 * Date   2017/5/5
 * Editor  Misuzu
 * 视频评论
 */

public class VideoCommentModel extends BaseListBean{

    public List<PostsBean> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsBean> posts) {
        this.posts = posts;
    }

    private List<PostsBean> posts;
}
