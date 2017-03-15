package com.easyvaas.common.chat.bean;

import java.io.Serializable;
import java.util.List;

public class BaseUser implements Serializable {
    private String name;
    private String nickname;
    private String logourl;
    private String imuser;
    private String impwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getImuser() {
        return imuser;
    }

    public void setImuser(String imuser) {
        this.imuser = imuser;
    }

    public String getImpwd() {
        return impwd;
    }

    public void setImpwd(String impwd) {
        this.impwd = impwd;
    }
}
