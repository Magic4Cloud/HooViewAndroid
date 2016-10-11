/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean.message;

public class MessageItemEntity {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_FOLLOW = 2;

    private ContentEntity content;
    private String time;

    public void setContent(ContentEntity content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ContentEntity getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public static class ContentEntity {
        private MessageFollowUser data;
        private int type;

        public void setData(MessageFollowUser data) {
            this.data = data;
        }

        public void setType(int type) {
            this.type = type;
        }

        public MessageFollowUser getData() {
            return data;
        }

        public int getType() {
            return type;
        }

    }

}
