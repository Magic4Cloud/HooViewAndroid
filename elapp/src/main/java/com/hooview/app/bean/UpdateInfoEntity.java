/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.hooview.app.bean;

public class UpdateInfoEntity {
    public static final int IS_FORCE_UPDATE = 1;
    public static final int IS_UPDATE = 1;

    private String update_version;
    private String update_url;
    private int force;
    private int update;
    private String update_log;
    private String new_md5;
    private long target_size;

    public String getUpdate_version() {
        return update_version;
    }

    public void setUpdate_version(String update_version) {
        this.update_version = update_version;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getNew_md5() {
        return new_md5;
    }

    public void setNew_md5(String new_md5) {
        this.new_md5 = new_md5;
    }

    public long getTarget_size() {
        return target_size;
    }

    public void setTarget_size(long target_size) {
        this.target_size = target_size;
    }
}
