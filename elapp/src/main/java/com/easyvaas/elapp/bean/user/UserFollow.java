package com.easyvaas.elapp.bean.user;

/**
 * Date   2017/4/21
 * Editor  Misuzu
 * 关注和粉丝
 */

public class UserFollow {


    /**
     * name : 17726098
     * nickname : 火眼财经4987
     * logourl : http://appgwdev.hooview.com/resource/user/man.png
     * location : 中国
     * birthday : 1990-01-01
     * signature : 火眼助你成为财经大师
     * level : 0
     * vip : 1
     * gender : male
     * followed : 1
     * imuser : 17726098
     * impwd : 1ec7f737870da5de7a95b444d5356750
     * faned : 0
     */

    private String name;
    private String nickname;
    private String logourl;
    private String location;
    private String birthday;
    private String signature;
    private int level;
    private int vip;
    private String gender;
    private int followed; // 0 未关注 1 关注
    private String imuser;
    private String impwd;
    private int faned;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
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

    public int getFaned() {
        return faned;
    }

    public void setFaned(int faned) {
        this.faned = faned;
    }
}
