/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean.user;

import java.util.List;

import com.easyvaas.elapp.bean.BaseEntityArray;

public class AssetsRankEntityArray extends BaseEntityArray {
    private List<RankUserEntity> send_rank_list;
    private List<RankUserEntity> receive_rank_list;

    public void setSend_rank_list(List<RankUserEntity> send_rank_list) {
        this.send_rank_list = send_rank_list;
    }

    public void setReceive_rank_list(List<RankUserEntity> receive_rank_list) {
        this.receive_rank_list = receive_rank_list;
    }

    public List<RankUserEntity> getSend_rank_list() {
        return send_rank_list;
    }

    public List<RankUserEntity> getReceive_rank_list() {
        return receive_rank_list;
    }

}
