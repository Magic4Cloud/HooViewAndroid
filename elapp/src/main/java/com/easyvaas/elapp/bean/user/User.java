/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import com.easyvaas.elapp.app.EVApplication;
import com.easyvaas.elapp.db.Preferences;

import java.io.Serializable;
import java.util.List;

public class User extends BaseUserEntity implements Serializable {
    public static final String AUTH_TYPE_PHONE = "phone";
    public static final String AUTH_TYPE_SINA = "sina";
    public static final String AUTH_TYPE_QQ = "qq";
    public static final String AUTH_TYPE_WEIXIN = "weixin";

    private String userid;
    public String sessionid;
    private List<AuthEntity> auth;
    private String authtype;
    private int followed_flag;
    private String invite_url;
    private String impwd;
    private String share_url;
    private int living;
    private String introduce; // 详细介绍

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getLiving() {
        return living;
    }

    public void setLiving(int living) {
        this.living = living;
    }

    // Aya : 2017/4/26 等后台改完后 恢复到原来状态
    public String getSessionid() {
        return Preferences.getInstance(EVApplication.getApp()).getSessionId();
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public void setAuth(List<AuthEntity> auth) {
        this.auth = auth;
    }

    public void setAuthtype(String authtype) {
        this.authtype = authtype;
    }

    public void setFollowed_flag(int followed_flag) {
        this.followed_flag = followed_flag;
    }

    public List<AuthEntity> getAuth() {
        return auth;
    }

    public String getAuthtype() {
        return authtype;
    }

    public int getFollowed_flag() {
        return followed_flag;
    }

    public String getInvite_url() {
        return invite_url;
    }

    public void setInvite_url(String invite_url) {
        this.invite_url = invite_url;
    }

    public String getImpwd() {
        return impwd;
    }

    public void setImpwd(String impwd) {
        this.impwd = impwd;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url){
        this.share_url = share_url;
    }

    public class AuthEntity implements Serializable {
        private String uid;
        private String expire_time;
        private String type;
        private int login;
        private String token;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setExpire_time(String expire_time) {
            this.expire_time = expire_time;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setLogin(int login) {
            this.login = login;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpire_time() {
            return expire_time;
        }

        public String getType() {
            return type;
        }

        public int getLogin() {
            return login;
        }

        public String getToken() {
            return token;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "sessionid='" + sessionid + '\'' +
                ", auth=" + auth +
                ", authtype='" + authtype + '\'' +
                ", followed_flag=" + followed_flag +
                ", invite_url='" + invite_url + '\'' +
                ", impwd='" + impwd + '\'' +
                ", share_url='" + share_url + '\'' +
                ", living=" + living +
                ", introduce='" + introduce + '\'' +
                '}' + super.toString();
    }
}
