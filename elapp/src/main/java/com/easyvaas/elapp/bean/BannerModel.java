package com.easyvaas.elapp.bean;


import java.util.List;

public class BannerModel {

    private List<DataEntity> data;

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public static class DataEntity {
        public static final int TYPE_H5 = 0;
        public static final int TYPE_NEWS = 1;
        public static final int TYPE_GOOD_VIDEO = 2;
        public static final int TYPE_VIDEO_LIVE = 3;
        public static final int TYPE_IMAGE_TEXT_LIVE = 4;
        public static final int TYPE_VIP_INFO = 5;
        /**
         * title : 标题
         * img : http://res.hooview.com/banners/abc.jpg
         * type : 0
         * resource : 资源ID
         */

        private String title;
        private String img;
        private int type;
        private String resource;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }
    }
}
