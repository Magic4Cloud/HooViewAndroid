package com.easyvaas.elapp.bean.news;

import com.easyvaas.elapp.bean.news.TopRatedModel.HomeNewsBean;
import com.easyvaas.elapp.bean.user.UserPageCommentModel.PostsBean;
import com.easyvaas.elapp.bean.user.UserPageInfo.TagBean;

import java.util.List;

/**
 * Date   2017/5/9
 * Editor  Misuzu
 * 新闻详情信息
 */

public class NewsDetailModel {


    private String id;
    private AuthorBean author;
    private String time;
    private String title;
    private String subTitle;
    private String digest;
    private String source;
    private int like;
    private int favorite;
    private String cover;
    private int likeCount;
    private int viewCount;
    private int postCount;
    private String content;
    private RecommendPersonBean recommendPerson;
    private List<StockBean> stock;
    private List<TagBean> tag;
    private List<HomeNewsBean> recommendNews;
    private List<PostsBean> posts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthorBean getAuthor() {
        return author;
    }

    public void setAuthor(AuthorBean author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RecommendPersonBean getRecommendPerson() {
        return recommendPerson;
    }

    public void setRecommendPerson(RecommendPersonBean recommendPerson) {
        this.recommendPerson = recommendPerson;
    }

    public List<StockBean> getStock() {
        return stock;
    }

    public void setStock(List<StockBean> stock) {
        this.stock = stock;
    }

    public List<TagBean> getTag() {
        return tag;
    }

    public void setTag(List<TagBean> tag) {
        this.tag = tag;
    }

    public List<HomeNewsBean> getRecommendNews() {
        return recommendNews;
    }

    public void setRecommendNews(List<HomeNewsBean> recommendNews) {
        this.recommendNews = recommendNews;
    }

    public List<PostsBean> getPosts() {
        return posts;
    }

    public void setPosts(List<PostsBean> posts) {
        this.posts = posts;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public static class AuthorBean {
        /**
         * id : 作者ID
         * name : 火眼财经
         * avatar : 作者头像
         */

        private String id;
        private String name;
        private String avatar;
        private int bind;
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getBind() {
            return bind;
        }

        public void setBind(int bind) {
            this.bind = bind;
        }

    }


    public static class RecommendPersonBean {
        /**
         * id : uuid
         * avatar : http://rs.hooview.com/img.png
         * name : 杜斯基
         * introduction : 杜斯基的个人简介
         */

        private String id;
        private String avatar;
        private String name;
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class StockBean {
        /**
         * code : 000586
         * market : 股票市场
         * name : 汇源通信
         * persent : +0.17
         */

        private String code;
        private String market;
        private String name;
        private String persent;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPersent() {
            return persent;
        }

        public void setPersent(String persent) {
            this.persent = persent;
        }
    }



}
