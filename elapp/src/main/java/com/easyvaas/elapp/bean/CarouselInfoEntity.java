/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean;

public class CarouselInfoEntity {
    public static final int TYPE_NOTICE = 0;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_WEB = 2;

    private String thumb;
    private ContentEntity content;

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setContent(ContentEntity content) {
        this.content = content;
    }

    public String getThumb() {
        return thumb;
    }

    public ContentEntity getContent() {
        return content;
    }

    public static class ContentEntity {
        private int type;
        private ContentEntity.DataEntity data;

        public void setType(int type) {
            this.type = type;
        }

        public void setData(ContentEntity.DataEntity data) {
            this.data = data;
        }

        public int getType() {
            return type;
        }

        public ContentEntity.DataEntity getData() {
            return data;
        }

        public static class DataEntity {
            private String notice_id;
            private String activity_id;
            private String web_url;
            private String title;

            public String getNotice_id() {
                return notice_id;
            }

            public String getActivity_id() {
                return activity_id;
            }

            public String getWeb_url() {
                return web_url;
            }

            public void setNotice_id(String notice_id) {
                this.notice_id = notice_id;
            }

            public void setActivity_id(String activity_id) {
                this.activity_id = activity_id;
            }

            public void setWeb_url(String web_url) {
                this.web_url = web_url;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
