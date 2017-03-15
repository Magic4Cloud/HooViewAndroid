package com.easyvaas.elapp.bean.user;


import java.io.Serializable;

public class UserInfoModel extends BaseUserEntity implements Serializable {

    /**
     * id : 225
     * phone : 86_15811021081
     * freeze : 0
     * level : 0
     * impwd : 5333c094142025d1222c3e1a1ed1179a
     * register_time : 2016-09-23 16:06:33
     * tags : [{"id":1,"name":"90后"},{"id":2,"name":"二次元"},{"id":8,"name":"电影狂人"}]
     */

    private int id;
    private String phone;
    private String freeze;
    private int level;
    private String impwd;
    private String register_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImpwd() {
        return impwd;
    }

    public void setImpwd(String impwd) {
        this.impwd = impwd;
    }

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }
}
