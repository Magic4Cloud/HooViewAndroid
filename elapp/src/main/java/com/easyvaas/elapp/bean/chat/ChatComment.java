/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.chat;

public class ChatComment {
    public static final int TYPE_INTO_TIPS = 1;
    public static final int TYPE_INTO_GIFT = 2;
    public static final int TYPE_INTO_EXPRESSION = 3;
    public static final int TYPE_INTO_RED_PACK = 4;
    public static final int TYPE_INTO_USER_JOIN = 5;
    public static final int TIME_COUNT_DOWN = 2;  // unit second
    public static final String NAME_SYSTEM_SECRETARY = "0";
    public static final String NAME_SYSTEM_FOLLOW = "1";

    private long id;
    private String vid;
    private String logourl;
    private String name;
    private String nickname;
    private String reply_name;
    private String reply_nickname;
    private String content;
    private String create_time;
    private int create_time_span;
    private int is_guest;
    private int type;
    private int countDown;

    public ChatComment() {
    }

    //XXX来了
    public ChatComment(String name, int type) {
        this.name = name;
        this.type = type;
    }

    //聊天
    public ChatComment(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setReply_name(String reply_name) {
        this.reply_name = reply_name;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setCreate_time_span(int create_time_span) {
        this.create_time_span = create_time_span;
    }

    public void setIs_guest(int is_guest) {
        this.is_guest = is_guest;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setReply_nickname(String reply_nickname) {
        this.reply_nickname = reply_nickname;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVid() {
        return vid;
    }

    public String getReply_name() {
        return reply_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public int getCreate_time_span() {
        return create_time_span;
    }

    public int getIs_guest() {
        return is_guest;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getReply_nickname() {
        return reply_nickname;
    }

    public String getLogourl() {
        return logourl;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ChatComment{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", vid='" + vid + '\'' +
                ", logourl='" + logourl + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", reply_name='" + reply_name + '\'' +
                ", reply_nickname='" + reply_nickname + '\'' +
                ", create_time='" + create_time + '\'' +
                ", create_time_span=" + create_time_span +
                ", is_guest=" + is_guest +
                ", type=" + type +
                ", countDown=" + countDown +
                '}';
    }
}
