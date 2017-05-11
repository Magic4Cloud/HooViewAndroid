package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.bean.BaseListBean;

import java.util.List;

/**
 * Date   2017/4/24
 * Editor  Misuzu
 * 用户界面评论列表
 */

public class UserPageCommentModel extends BaseListBean{


    /**
     * next : 2
     * posts : [{"id":"7","time":"2017-03-15 00:17:01","heats":"0","content":"good","topic":{"id":"4352","type":0,"title":"火眼看盘|反弹只是耍流氓？重要支撑位上就看放不放量"},"user":{"id":"15985580","nickname":"李传","avatar":"http://dev.yizhibo.tv/output/easyvaas/resource/user/2f/0e/5f5a9c950632c136faee3706d0172e76.jpeg"}},{"id":"199","time":"2017-04-20 16:44:47","heats":0,"content":"开机","topic":{"id":"5pkvLb8kHp6F1oKD","type":"1","title":"测试测试环境上传"},"user":{"id":"18271157","nickname":"旋转の八音盒","avatar":"http://wx.qlogo.cn/mmopen/Q3auHgzwzM4ZTMZwGJqaXXIwQwiblVfdSPC59arBDpFXRGUrictRXCLA7tbmQ5L2LO1GuIibTFvTiahKibFyJ0ydsYw/0"}},{"id":"372","time":"2017-03-07 18:46:32","heats":0,"content":"健健康康","topic":{"id":"SH000001","type":"2","title":"上证指数（SH000001）"},"user":{"id":"16616325","nickname":"火眼财经6864","avatar":"http://dev.yizhibo.tv/output/easyvaas/resource//user/8b/4c/9619d02f9f73324ebed0d290fafc17f3.jpeg"}}]
     */

    private List<PostsBean> total;

    public List<PostsBean> getPosts() {
        return total;
    }

    public void setPosts(List<PostsBean> total) {
        this.total = total;
    }

    public static class PostsBean {
        /**
         * id : 7
         * time : 2017-03-15 00:17:01
         * heats : 0
         * content : good
         * topic : {"id":"4352","type":0,"title":"火眼看盘|反弹只是耍流氓？重要支撑位上就看放不放量"}
         * user : {"id":"15985580","nickname":"李传","avatar":"http://dev.yizhibo.tv/output/easyvaas/resource/user/2f/0e/5f5a9c950632c136faee3706d0172e76.jpeg"}
         */

        private String id;
        private String time;
        private String heats;
        private int like;
        private String content;
        private TopicBean topic;
        private UserBean user;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }


        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public String getHeats() {
            return heats;
        }

        public void setHeats(String heats) {
            this.heats = heats;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public TopicBean getTopic() {
            return topic;
        }

        public void setTopic(TopicBean topic) {
            this.topic = topic;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class TopicBean {
            /**
             * id : 4352
             * type : 0
             * title : 火眼看盘|反弹只是耍流氓？重要支撑位上就看放不放量
             */

            private String id;
            private int type;  //话题类型（0，新闻；1，视频；2，股票）
            private String title;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class UserBean {
            /**
             * id : 15985580
             * nickname : 李传
             * avatar : http://dev.yizhibo.tv/output/easyvaas/resource/user/2f/0e/5f5a9c950632c136faee3706d0172e76.jpeg
             */

            private String id;
            private String nickname;
            private String avatar;
            private int vip;

            public int getVip() {
                return vip;
            }

            public void setVip(int vip) {
                this.vip = vip;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
