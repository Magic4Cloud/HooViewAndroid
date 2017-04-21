package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.bean.BaseListBean;

import java.util.List;

/**
 * Date    2017/4/21
 * Author  xiaomao
 * 秘籍
 */

public class CheatsListModel extends BaseListBean {

    private List<CheatsModel> cheats;

    public List<CheatsModel> getCheats() {
        return cheats;
    }

    public void setCheats(List<CheatsModel> cheats) {
        this.cheats = cheats;
    }

    public static class CheatsModel {
        /**
         * id: "000hPZYLUqmY",
         * userid: "19821111",
         * date: "2017年11月12日 11:52:00",
         * title: "秘籍标题",
         * introduce: "秘籍介绍",
         * price: 18000,
         * salesCount: 1371
         */
        private String id;
        private String userid;
        private String date;
        private String title;
        private String introduce;
        private int price;
        private int salesCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getSalesCount() {
            return salesCount;
        }

        public void setSalesCount(int salesCount) {
            this.salesCount = salesCount;
        }
    }
}
