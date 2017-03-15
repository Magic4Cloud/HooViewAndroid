/*
 * Copyright (c) 2016 EasyVaas.
 * http://www.easyvaas.com
 * All rights reserved.
 */

package com.easyvaas.elapp.bean;

import java.util.List;

public class TopicEntityArray extends BaseEntityArray {

    private List<TopicEntity> topics;

    public void setTopics(List<TopicEntity> topics) {
        this.topics = topics;
    }

    public List<TopicEntity> getTopics() {
        return topics;
    }
}
